# Helm and Minikube Deployment

## Purpose

Deploy the scanned container image to a local Kubernetes cluster and verify the rollout.

## Start and verify the cluster

```bash
minikube start
kubectl cluster-info
kubectl get nodes
```

Continue only when the node is Ready.

## Security expectations

The Helm chart should configure a dedicated namespace, resource requests/limits, readiness and liveness probes, a non-root security context, no privilege escalation, dropped Linux capabilities, and a read-only root filesystem where the application supports it. Apply least-privilege RBAC. Add NetworkPolicy only after confirming the cluster CNI enforces it.

Validate manifests before applying them. A manifest policy tool can detect common misconfigurations, but results still need review.

## Deploy and verify

The exact Helm release name, namespace, chart path, image repository, and application port will be documented once implemented. After deployment, verify:

```bash
kubectl get pods -A
kubectl get deployments -A
kubectl get events -A --sort-by=.metadata.creationTimestamp
```

Use `kubectl describe pod`, `kubectl logs`, and `helm status` to troubleshoot. A successful command is not sufficient: confirm the pod is Ready and the app's health endpoint responds.

## Local cluster limitation

A GitHub-hosted runner cannot normally reach Minikube running on your workstation. Run the deployment stage locally after CI publishes the image, or set up a secured self-hosted runner deliberately. Do not expose the Kubernetes API publicly just to make hosted CI deployment easier.

## Cleanup

Delete only the lab release and namespace you created. Confirm the current Kubernetes context and namespace before running destructive commands.