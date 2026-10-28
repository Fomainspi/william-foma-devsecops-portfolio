# William Foma — DevSecOps Portfolio

A hands-on Java/Spring Boot portfolio and reproducible DevSecOps lab. The repository is designed to demonstrate a controlled software-delivery path: test, scan, create an SBOM, publish a versioned artifact to JFrog, containerize it, scan the image, and deploy locally to Kubernetes with Helm.

**Owner:** William Foma  
**Repository:** https://github.com/Fomainspi/william-foma-devsecops-portfolio  
**JFrog Platform:** https://williamdev.jfrog.io  
**Current status:** Foundation and CI workflow committed; the first real workflow run and JFrog publication still need verification.

## Project goals

1. Build a professional portfolio application using verified career information only.
2. Demonstrate DevOps and DevSecOps engineering practices in a public, documented repository.
3. Make the setup reproducible from a clean workstation with commands, expected results, troubleshooting, and cleanup.
4. Record evidence honestly: planned controls are not called complete until a run confirms them.

## Delivery architecture

```text
Pull request / push to main
        |
        v
GitHub Actions
  Java 21 + Maven tests
  Trivy filesystem vulnerability/secret/misconfiguration scan
  CycloneDX SBOM
        |
        | main branch only, after gates pass
        v
JFrog Artifactory
  foma-maven-local (versioned JAR + SHA-256)
        |
        v
Docker image + Trivy image scan
        |
        v
Helm chart -> local Minikube -> rollout + health check
```

The workflow builds and scans a container image in CI and publishes the versioned JAR to JFrog after validation. Pushing the image to JFrog is intentionally held until the exact Docker registry login method and required username are confirmed from the account's Set Me Up page. Local Minikube deployment is a separate step. A GitHub-hosted runner cannot normally access Minikube on a personal computer; deploy locally unless a secured self-hosted runner is deliberately configured.

## Implemented repository components

- `.github/workflows/devsecops.yml` — CI checks, SBOM generation, Trivy filesystem scan, and main-branch JFrog artifact publication.
- `pom.xml` and `src/` — Spring Boot application and tests.
- `Dockerfile` and `compose.yaml` — local container packaging with a non-root user and additional runtime restrictions.
- `helm/william-foma-portfolio/` — Helm chart foundation for local Kubernetes.
- `docs/` — setup, architecture, security, JFrog, deployment, troubleshooting, and clean rebuild guides.

## JFrog repositories

The repositories have been created in the JFrog account and confirmed in the screenshots shared during setup:

| Repository key | Package type | Purpose |
|---|---|---|
| `foma-maven-local` | Maven | Versioned application JAR and checksum |
| `foma-docker-local` | Docker | Intended destination for container images |

The setup steps and screenshot evidence are documented in [JFrog setup](docs/jfrog-setup.md). The screenshots need to be added as tracked image assets before they can render inline from GitHub; the repository documentation currently describes the verified screens without claiming that image assets have already been committed.

## Security controls and limitations

| Control | Current implementation | Evidence to verify |
|---|---|---|
| Unit/integration tests | Maven `clean verify` | GitHub Actions log and Surefire reports |
| Filesystem scan | Trivy: vulnerabilities, secrets, misconfigurations; HIGH/CRITICAL gate | Workflow scan result |
| SBOM | CycloneDX Maven plugin | `target/bom.json` artifact |
| Artifact traceability | Versioned path and SHA-256 sidecar in JFrog | Repository contents and checksum |
| Container hardening | Non-root user; Compose read-only root, tmpfs, dropped capabilities | Local container test |
| Kubernetes hardening | Non-root, no privilege escalation, dropped capabilities, read-only root FS, resource limits, probes | Helm rendering and pod security context |
| JFrog Xray | Optional; depends on plan/configuration | Only claim if visible and tested in the account |

A scan report is not proof of complete security. Review findings, test that release gates block as intended, and document any accepted risk. Do not expose secrets or intentionally vulnerable builds to a public endpoint.

## Start here

1. Follow [Prerequisites and setup](docs/prerequisites-and-setup.md).
2. Follow [JFrog setup](docs/jfrog-setup.md).
3. Review [GitHub Actions setup](docs/github-actions.md), especially the required `JF_URL` variable and `JF_ACCESS_TOKEN` secret.
4. Read [Security controls](docs/security-controls.md).
5. Follow [Docker and image security](docs/docker-and-image-security.md).
6. Follow [Helm and Minikube deployment](docs/minikube-deployment.md).
7. Use [Rebuild from scratch](docs/rebuild-from-scratch.md) to repeat the lab from a clean environment.
8. Use [Interview walkthrough](docs/interview-walkthrough.md) to present the design and evidence.

## Safe credential handling

- Never commit access tokens, passwords, private keys, kubeconfig files, or production data.
- Use GitHub Actions secrets for credentials and a least-privilege JFrog token.
- Do not expose publishing credentials to untrusted pull requests.
- If a credential leaks, revoke it immediately; deleting the line later is not enough.
- Do not include private contact details in the public portfolio.

## Completion rule

A stage is **verified** only after a real run succeeds and the evidence is inspected. CI workflow YAML and documentation are committed, but that does not by itself mean the pipeline, image publication, or Kubernetes deployment has been tested. Check the Actions tab for the first run and use the troubleshooting guide for failures.
