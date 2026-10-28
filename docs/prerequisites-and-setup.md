# Prerequisites and Local Setup

## Purpose

Prepare a clean workstation for building, scanning, containerizing, and deploying the Java portfolio. Commands below are checks; installation methods depend on your operating system.

## Required tools

- Git
- A supported JDK (use the project version once the Maven configuration is committed)
- Maven, unless the project uses the Maven Wrapper (preferred for repeatability)
- Docker Engine/Desktop
- kubectl
- Minikube
- Helm
- A GitHub account with repository access
- A JFrog Cloud account with permission to create/use repositories and access tokens

Security tooling planned for the pipeline includes a source-code scanner, Maven dependency scanner, SBOM generator, and Trivy. The exact tool versions will be pinned or recorded in the implementation.

## Verify installations

Run each command and inspect the output:

```bash
git --version
java -version
mvn -version
docker --version
kubectl version --client
minikube version
helm version
```

If a command is not found, install that tool using its official documentation and reopen the terminal so PATH changes take effect. Do not continue until required commands work.

## Start Minikube

Start a local cluster using a driver supported by your machine:

```bash
minikube start
kubectl cluster-info
kubectl get nodes
```

Expected result: the cluster responds and a node is listed as Ready. If startup fails, inspect `minikube status` and `minikube logs` before retrying.

## Docker verification

```bash
docker info
```

Expected result: Docker client can contact a running daemon. If the daemon is unavailable, start Docker Desktop/Engine and retry.

## Safety notes

- Never place a JFrog token in a shell command that may be saved in shell history.
- Do not commit local environment files containing secrets.
- Use a lab cluster and test data, not a production Kubernetes cluster.
- Record your operating system and actual tool versions in your project notes so the procedure can be reproduced.
