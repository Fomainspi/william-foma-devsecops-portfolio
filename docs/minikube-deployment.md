# Helm and Minikube Deployment

## Purpose

Deploy the application to a local Kubernetes cluster with a Helm chart and verify runtime health.

## Start and verify Minikube

```bash
minikube start
kubectl config current-context
kubectl cluster-info
kubectl get nodes
helm version
```

Continue only when the Minikube node is `Ready` and the current context is the expected local cluster.

## Build an image available to Minikube

For a first local test, build the image directly into Minikube's Docker daemon:

```bash
eval $(minikube docker-env)
mvn clean package
docker build -t william-foma-portfolio:local .
```

On PowerShell, use the Minikube Docker environment command appropriate to your installed Minikube version, or load the built image using `minikube image load william-foma-portfolio:local`.

## Validate and deploy

Render the chart before installation:

```bash
helm lint ./helm/william-foma-portfolio
helm template portfolio ./helm/william-foma-portfolio
```

Then install it into a dedicated lab namespace:

```bash
kubectl create namespace foma-portfolio --dry-run=client -o yaml | kubectl apply -f -
helm upgrade --install portfolio ./helm/william-foma-portfolio \
  --namespace foma-portfolio \
  --set image.repository=william-foma-portfolio \
  --set image.tag=local \
  --set image.pullPolicy=IfNotPresent
kubectl rollout status deployment/portfolio-william-foma-portfolio -n foma-portfolio --timeout=120s
kubectl get pods,svc -n foma-portfolio
```

If the deployment name differs, use `kubectl get deployments -n foma-portfolio` and the name returned by Helm.

## Smoke test

```bash
kubectl port-forward -n foma-portfolio svc/portfolio-william-foma-portfolio 8080:8080
```

In a second terminal:

```bash
curl --fail http://localhost:8080/
curl --fail http://localhost:8080/actuator-health
```

The pod should become Ready and the health endpoint should return `UP`.

## Security expectations

The chart defaults request a non-root user, seccomp RuntimeDefault, no privilege escalation, dropped capabilities, read-only root filesystem, resource requests/limits, and HTTP probes. Review the rendered YAML and actual pod configuration. NetworkPolicy is not included because enforcement depends on the cluster CNI.

## Runtime hardening

The chart includes a hardened security context and a dedicated writable `/tmp` volume. Review the rendered YAML and then follow the [Kubernetes hardening walkthrough](kubernetes-hardening.md) if you are hardening the separate live lab Deployment in namespace `foma-devsecops`. Record the actual rollout and application test results in the [hardening log](kubernetes-hardening-log.md); chart values alone are not proof that a live Deployment is hardened.

## CI boundary

A GitHub-hosted runner cannot normally reach Minikube on a personal workstation. Deploy locally after CI succeeds unless you deliberately configure a secured self-hosted runner. Do not expose the Kubernetes API publicly.

## Cleanup

```bash
helm uninstall portfolio -n foma-portfolio
kubectl delete namespace foma-portfolio
```

Confirm the current context and namespace before cleanup. Delete only the lab resources.
