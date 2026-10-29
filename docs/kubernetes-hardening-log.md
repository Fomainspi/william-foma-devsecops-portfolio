# Kubernetes Hardening Log

This log separates observed baseline facts from planned controls. Update it after each real run; do not infer success from a command being issued.

## Lab identity

- Repository: [william-foma-devsecops-portfolio](https://github.com/Fomainspi/william-foma-devsecops-portfolio)
- Cluster: local Minikube
- Namespace: `foma-devsecops`
- Deployment: `william-foma-portfolio`
- Container: `portfolio-api`
- Service: `william-foma-portfolio`, ClusterIP port 8080
- Work instruction: [Kubernetes Hardening walkthrough](kubernetes-hardening.md)

## Baseline recorded from the troubleshooting session

| Item | Observed result | Interpretation |
|---|---|---|
| Minikube node | `minikube` was `Ready` | Cluster API responded at the time of the check |
| Deployment / pod | Deployment was 1/1 available; pod was Running 1/1 | Application was running before the hardening experiment |
| Service | ClusterIP service on port 8080 existed | Internal service was present |
| Container image | Versioned image in the private JFrog Docker repository | Record the exact tag from the current cluster before each run |
| Explicit pod security context | JSONPath returned `{}` | No explicit pod-level security context was set on the live Deployment at that check |
| Explicit container security context | JSONPath output was blank | No explicit container-level security context was set on the live Deployment at that check |
| Non-root experiment | Pod-level `runAsNonRoot`, `runAsUser: 1000`, and `runAsGroup: 1000` were patched temporarily | Experiment was later rolled back |
| Post-rollback check | Pod security context returned `{}` | The explicit pod-level experiment was removed; full hardening remains unverified |

These are historical observations from a previous lab session, not a claim about the cluster's current state. Re-run the baseline commands before applying changes.

## Controls status

| Control | Status at last recorded check | Evidence required to mark verified |
|---|---|---|
| Non-root identity at Kubernetes pod level | Experiment rolled back | Final pod security context plus process UID/GID |
| `allowPrivilegeEscalation: false` | Not verified on live Deployment | Final container security context and successful rollout |
| Drop all Linux capabilities | Not verified on live Deployment | Final container security context and successful rollout |
| `seccompProfile: RuntimeDefault` | Not verified on live Deployment | Final pod security context and successful rollout |
| Writable `/tmp` volume | Not verified on live Deployment | Pod volume and mount plus successful app startup |
| Read-only root filesystem | Not verified on live Deployment | Final container security context and successful app startup |
| Resource requests/limits and probes | Defined in repository Helm chart; live Deployment needs inspection | Rendered chart and/or live Deployment output |
| Application smoke test after hardening | Pending | Port-forward/curl result and healthy pod |
| Helm chart hardening | Settings present in `values.yaml` and Deployment template | `helm lint`, rendered manifest review, and a successful deployment test |

## Monitoring resource note

To reduce local Minikube resource usage, the following monitoring workloads were scaled to zero during the earlier lab session: Grafana, kube-prometheus-operator, kube-state-metrics, Prometheus, and Alertmanager in namespace `monitoring`. The Helm release was left installed and PVCs were not deleted. The node-exporter DaemonSet remained running.

Consequences:
- Prometheus metrics and alert evaluation are not actively collected while Prometheus and Alertmanager are scaled down.
- Grafana is unavailable while its Deployment is at zero replicas.
- The Helm release still exists; do not assume its workloads are healthy or restored.
- Restore deliberately when resources permit, then verify pods before describing monitoring as operational.

Inspect current state:

```bash
kubectl get deployments,statefulsets,daemonsets,pods -n monitoring
helm list -n monitoring
```

## Per-run evidence template

Copy this section for each hardening attempt.

- **Date/time and timezone:**
- **Git revision / chart version:**
- **Kubernetes context:**
- **Namespace / Deployment / image tag:**
- **Baseline replica readiness:**
- **Control changed:**
- **Command used:**
- **Rollout result:**
- **Pod UID/GID result:**
- **Final security-context output:**
- **Application test and response:**
- **Relevant log output (redacted):**
- **Issues / remediation:**
- **Rollback performed (yes/no) and verified state:**
- **Final status:** Planned / In progress / Verified

Never store tokens, passwords, kubeconfig contents, or secret values in this log.
