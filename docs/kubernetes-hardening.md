# Kubernetes Hardening: Complete Practical Walkthrough

This guide hardens the portfolio API running in the local Minikube lab. It is written as a practical, observable change: record the baseline, apply one control at a time, wait for the rollout, test the application, and record evidence.

> **Important status distinction:** The Helm chart in this repository already defines a hardened target configuration. The live Deployment in namespace `foma-devsecops` was inspected separately; its explicit pod-level security context was empty at the last recorded check. A non-root Deployment patch was tested and then rolled back. Do not describe the live Deployment as fully hardened until the steps below have been applied and verified on the target cluster.

## Scope and current resources

| Item | Value |
|---|---|
| Local cluster | Minikube |
| Namespace | `foma-devsecops` |
| Deployment | `william-foma-portfolio` |
| Container | `portfolio-api` |
| Service | `william-foma-portfolio` |
| Service type / port | ClusterIP / 8080 |
| Image | Private JFrog image, versioned tag |
| Application | Spring Boot API, Java 21 |

The private image reference used in the lab has the form `williamdev.jfrog.io/foma-docker-local/william-foma-portfolio:<version-tag>`. Do not copy credentials, tokens, or image-pull secrets into documentation.

## Security goals

- Run the process as a non-root UID/GID (1000:1000).
- Block privilege escalation.
- Drop Linux capabilities the application does not need.
- Use the container runtime's default seccomp profile.
- Mount a dedicated writable `/tmp` volume before enabling a read-only root filesystem.
- Keep resource requests/limits and health probes.
- Verify the rollout and application behavior after every meaningful change.

These controls reduce risk; they do not replace image scanning, patch management, RBAC, network controls, secret management, or runtime monitoring.

## 0. Pre-flight and baseline evidence

Run one command at a time. Confirm the Kubernetes context before modifying anything.

```bash
kubectl config current-context
kubectl cluster-info
kubectl get nodes
kubectl get deployment william-foma-portfolio -n foma-devsecops
kubectl get pods -n foma-devsecops -o wide
kubectl get service william-foma-portfolio -n foma-devsecops
kubectl get deployment william-foma-portfolio -n foma-devsecops -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
```

Expected: the context is the intended local Minikube cluster, the node is `Ready`, and the Deployment/Pod/Service exist. Record the exact image tag and replica readiness.

Capture the starting security settings:

```bash
kubectl get deployment william-foma-portfolio -n foma-devsecops -o jsonpath='podSecurityContext={.spec.template.spec.securityContext}{"\n"}containerSecurityContext={.spec.template.spec.containers[0].securityContext}{"\n"}'
kubectl get deployment william-foma-portfolio -n foma-devsecops -o yaml > deployment-before-hardening.yaml
```

Keep the YAML snapshot in a local, private working directory if it contains environment-specific details. Review it before committing anything; do not publish secrets or cluster-specific credentials.

## 1. Enforce a non-root identity

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"securityContext":{"runAsNonRoot":true,"runAsUser":1000,"runAsGroup":1000}}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
kubectl get pods -n foma-devsecops
kubectl exec deployment/william-foma-portfolio -n foma-devsecops -- id
```

Expected: rollout succeeds and the process identity reports UID 1000 and GID 1000. The image's Dockerfile also declares `USER 1000:1000`; the pod setting adds an explicit Kubernetes enforcement layer.

If the pod does not become Ready, inspect `kubectl describe pod` and `kubectl logs` before continuing.

## 2. Prevent privilege escalation

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"containers":[{"name":"portfolio-api","securityContext":{"allowPrivilegeEscalation":false}}]}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
```

Expected: the new ReplicaSet becomes available and `allowPrivilegeEscalation` is `false`.

## 3. Drop Linux capabilities

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"containers":[{"name":"portfolio-api","securityContext":{"capabilities":{"drop":["ALL"]}}}]}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
```

Expected: the container security context lists `drop: [ALL]`. If a later application requirement genuinely needs a capability, document the reason and add only that specific capability rather than restoring the default set.

## 4. Apply the RuntimeDefault seccomp profile

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"securityContext":{"seccompProfile":{"type":"RuntimeDefault"}}}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
```

Expected: pod-level `seccompProfile.type` is `RuntimeDefault` and the rollout succeeds.

## 5. Prepare writable temporary storage, then use a read-only root filesystem

Java is configured to use `/tmp` for temporary files. Mount a dedicated `emptyDir` there before enabling the read-only root filesystem.

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"volumes":[{"name":"tmp-storage","emptyDir":{"sizeLimit":"64Mi"}}],"containers":[{"name":"portfolio-api","volumeMounts":[{"name":"tmp-storage","mountPath":"/tmp"}]}]}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
```

Check the pod and logs. Confirm that the application starts and its temporary-file use works. Then enable read-only root filesystem:

```bash
kubectl patch deployment william-foma-portfolio -n foma-devsecops --type=strategic -p '{"spec":{"template":{"spec":{"containers":[{"name":"portfolio-api","securityContext":{"readOnlyRootFilesystem":true}}]}}}}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
kubectl get pods -n foma-devsecops
kubectl logs deployment/william-foma-portfolio -n foma-devsecops --tail=100
```

Expected: rollout succeeds, pod becomes Ready, and logs show normal startup. If the application requires another writable path, identify it from the error and mount only that path deliberately; do not disable read-only mode without documenting why.

## 6. Verify the final configuration

```bash
kubectl get deployment william-foma-portfolio -n foma-devsecops -o jsonpath='podSecurityContext={.spec.template.spec.securityContext}{"\n"}containerSecurityContext={.spec.template.spec.containers[0].securityContext}{"\n"}volumeMounts={.spec.template.spec.containers[0].volumeMounts}{"\n"}volumes={.spec.template.spec.volumes}{"\n"}'
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
kubectl get deployment william-foma-portfolio -n foma-devsecops
kubectl get pods -n foma-devsecsecops
```

If the last command reports that the namespace does not exist, use the correct namespace command below (the intended namespace is `foma-devsecops`):

```bash
kubectl get pods -n foma-devsecops
```

Review the output for `runAsNonRoot: true`, UID/GID 1000, `seccompProfile: RuntimeDefault`, `allowPrivilegeEscalation: false`, dropped `ALL` capabilities, `readOnlyRootFilesystem: true`, and a `/tmp` volume mount.

## 7. Test application behavior

Use a second terminal for the port-forward while leaving the command running in the first terminal:

```bash
kubectl port-forward -n foma-devsecops service/william-foma-portfolio 8080:8080
```

Then test from the second terminal:

```bash
curl -i http://127.0.0.1:8080/
curl -i http://127.0.0.1:8080/actuator-health
```

The exact root response depends on the application's routes. The configured health path in the Helm chart is `/actuator-health`; confirm the endpoint is enabled in the running application before treating a 404 as a hardening failure. Verify that the service responds, logs are normal, and the pod remains Ready.

## Rollback

Rollback is for lab recovery, not a substitute for diagnosing the cause. Check the rollout history first:

```bash
kubectl rollout history deployment/william-foma-portfolio -n foma-devsecops
kubectl rollout undo deployment/william-foma-portfolio -n foma-devsecops
kubectl rollout status deployment/william-foma-portfolio -n foma-devsecops --timeout=120s
```

A rollout undo restores a previous Deployment revision; it does not restore external resources, data, or changes made outside that Deployment. Verify the final state and application health after rollback. If there is no suitable prior revision, patch the specific setting back only after understanding the failure.

## Repository Helm chart

The repository chart at `helm/william-foma-portfolio/` already declares non-root execution, RuntimeDefault seccomp, no privilege escalation, dropped capabilities, a read-only root filesystem, a 64 MiB memory-backed `/tmp` volume, resource requests/limits, and HTTP probes. Validate its rendered output before use:

```bash
helm lint ./helm/william-foma-portfolio
helm template portfolio ./helm/william-foma-portfolio
```

The chart release in the `foma-portfolio` namespace is separate from the live Deployment described at the top of this guide. Do not assume hardening the chart changes a Deployment installed or patched independently in `foma-devsecops`.

## Evidence checklist

Record the actual date, cluster context, image tag, command output, rollout result, pod identity, final security context, application response, and any remediation. Never mark a control verified solely because the patch command returned successfully.

- [ ] Baseline captured
- [ ] Non-root identity verified
- [ ] Privilege escalation disabled
- [ ] All Linux capabilities dropped
- [ ] RuntimeDefault seccomp confirmed
- [ ] Dedicated writable `/tmp` mounted
- [ ] Read-only root filesystem tested
- [ ] Rollout and application health verified
- [ ] Evidence added to [the hardening log](kubernetes-hardening-log.md)
