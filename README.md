# William Foma — DevSecOps Portfolio

A hands-on Java/Spring Boot portfolio and reproducible DevSecOps lab. The repository demonstrates a controlled software-delivery path: test, scan, create an SBOM, publish versioned artifacts to JFrog, containerize the application, scan the image, and deploy locally to Kubernetes with Helm.

**Owner:** William Foma  
**Repository:** https://github.com/Fomainspi/william-foma-devsecops-portfolio  
**JFrog Platform:** https://williamdev.jfrog.io  
**CI evidence:** See the [CI incident/resolution report](docs/workflow-incident-report.md) for the recorded workflow evidence and status caveats.

## Project goals

1. Build a professional portfolio application using verified career information only.
2. Demonstrate DevOps and DevSecOps engineering practices in a public, documented repository.
3. Make the setup reproducible from a clean workstation with commands, expected results, troubleshooting, and cleanup.
4. Record evidence honestly: configured controls are not called verified until a real run confirms them.

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
  Versioned JAR + SHA-256
  Versioned Docker image
        |
        v
Helm chart -> local Minikube -> rollout + health check
```

The workflow builds and scans a container image in CI. Publication happens through the configured main-branch workflow after validation. Local Minikube deployment is a separate step: a GitHub-hosted runner cannot normally access Minikube on a personal workstation. Deploy locally unless a secured self-hosted runner is deliberately configured.

## Implemented repository components

- `.github/workflows/devsecops.yml` — CI checks, SBOM generation, Trivy scans, and configured JFrog publication.
- `pom.xml` and `src/` — Spring Boot application and tests.
- `Dockerfile` and `compose.yaml` — container packaging with a non-root user and additional runtime restrictions.
- `helm/william-foma-portfolio/` — Helm chart with non-root execution, RuntimeDefault seccomp, no privilege escalation, dropped capabilities, read-only root filesystem, a dedicated `/tmp` volume, resources, and probes.
- `docs/` — architecture, setup, CI, JFrog, security, deployment, troubleshooting, and operational guides.

## Security controls and verification status

| Control | Repository configuration | What must be verified |
|---|---|---|
| Unit/integration tests | Maven `clean verify` in CI | Workflow logs and test reports |
| Filesystem scan | Trivy scans vulnerabilities, secrets, and misconfigurations | Actual scan output and release-gate behavior |
| SBOM | CycloneDX Maven plugin | Generated `target/bom.json` and retained artifact |
| Artifact traceability | Versioned artifact and SHA-256 sidecar | JFrog repository contents and checksum |
| Container hardening | Docker non-root user; Compose read-only root, tmpfs, no-new-privileges, dropped capabilities | Local container test |
| Kubernetes hardening | Helm chart declares non-root, no privilege escalation, dropped capabilities, RuntimeDefault seccomp, read-only root, `/tmp`, resources, and probes | Rendered Helm manifest and live pod security context |
| JFrog Xray | Optional; account/plan dependent | Only claim after configuration and a real scan |

**Important:** The Helm chart's hardened settings are configuration, not proof that every running Deployment is using them. The live Deployment in namespace `foma-devsecops` had its explicit pod-level security context checked during a previous session; a non-root patch experiment was subsequently rolled back. Its complete runtime hardening remains unverified until the walkthrough is applied and evidence is recorded. See [Kubernetes hardening](docs/kubernetes-hardening.md) and the [hardening log](docs/kubernetes-hardening-log.md).

A scan report is not proof of complete security. Review findings, test that release gates block as intended, and document any accepted risk. Do not expose secrets or intentionally vulnerable builds to a public endpoint.

## Start here

1. Follow [Prerequisites and setup](docs/prerequisites-and-setup.md).
2. Follow [JFrog setup](docs/jfrog-setup.md).
3. Review [GitHub Actions setup](docs/github-actions.md), especially required variables and secret handling.
4. Read [Security controls](docs/security-controls.md).
5. Follow [Docker and image security](docs/docker-and-image-security.md).
6. Follow [Helm and Minikube deployment](docs/minikube-deployment.md).
7. Work through the [Kubernetes hardening walkthrough](docs/kubernetes-hardening.md), recording results in the [hardening log](docs/kubernetes-hardening-log.md).
8. Use [Rebuild from scratch](docs/rebuild-from-scratch.md) to repeat the lab from a clean environment.
9. Use [Interview walkthrough](docs/interview-walkthrough.md) to present the design and evidence.
10. Review [Homepage routing and Java build troubleshooting](docs/homepage-routing-and-build-fix.md) for the Java 21 compiler setup, local port conflict, Spring Boot root routing, and validation commands.

## Safe credential handling

- Never commit access tokens, passwords, private keys, kubeconfig files, or production data.
- Use GitHub Actions secrets for credentials and a least-privilege JFrog token.
- Do not expose publishing credentials to untrusted pull requests.
- If a credential leaks, revoke it immediately; deleting the line later is not enough.
- Do not include private contact details in the public portfolio.

## Completion rule

A stage is **verified** only after a real run succeeds and the evidence is inspected. CI validation, artifact publication, image publication, and Kubernetes runtime hardening each need their own evidence. Use the [CI incident/resolution report](docs/workflow-incident-report.md), [troubleshooting guide](docs/troubleshooting.md), and the Kubernetes hardening log to keep status accurate.
