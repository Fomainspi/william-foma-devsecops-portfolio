# William Foma — DevSecOps Portfolio

> A hands-on portfolio application and reproducible DevSecOps lab. The goal is to build, test, scan, publish, containerize, and deploy a Java application using GitHub Actions, JFrog Artifactory, Docker, Trivy, Helm, and Kubernetes (Minikube).

**Owner:** William Foma  
**GitHub:** https://github.com/Fomainspi/william-foma-devsecops-portfolio  
**JFrog Platform:** https://williamdev.jfrog.io  
**Status:** Initial project setup and documentation in progress.

---

## 1. Why this project exists

This repository has two purposes:

1. **Professional portfolio:** present William Foma's DevOps/DevSecOps skills, selected projects, and professional background.
2. **Learning lab:** document every major implementation step so another engineer—or William working from a clean machine—can reproduce the setup without relying on undocumented knowledge.

This is an educational portfolio project, not a claim that every control automatically guarantees a secure production system. Security findings, limitations, and any exceptions must be documented honestly.

## 2. What we are building

The portfolio will be a Java application using Spring Boot. It will contain professional sections such as an introduction, skills, experience, projects, FOMA training, and contact links. Only verified information will be published. Do not add private contact details, secrets, or unverified employment dates.

### Target delivery flow

```text
Developer change / Pull Request
          |
          v
GitHub Actions: compile + unit tests
          |
          +--> Source-code security scan (SAST)
          +--> Maven dependency scan
          +--> Secret detection
          +--> SBOM generation
          |
          v
JFrog Artifactory: publish versioned Maven artifact
          |
          v
Download exact artifact + verify checksum
          |
          v
Build Docker image from the artifact
          |
          +--> Trivy image scan
          +--> Optional JFrog Xray scan, if enabled by the account plan
          |
          v
Publish versioned image to JFrog
          |
          v
Validate Helm/Kubernetes manifests
          |
          v
Deploy to Minikube + rollout and smoke tests
```

**Important:** a standard GitHub-hosted runner cannot normally reach a Minikube cluster running on a personal computer. The first implementation will run the deployment step from your local machine, or we will document how to configure a secured self-hosted runner if remote deployment is later required.

## 3. Security controls

| Layer | Planned controls | Evidence |
|---|---|---|
| Source | Pull-request checks, SAST, secret detection | Workflow logs and scan reports |
| Dependencies | Vulnerability scan and SBOM | Dependency report and SBOM file |
| Build | Tests, pinned actions where practical, minimal permissions | Test results and workflow configuration |
| Artifact | Versioned JAR, repository permissions, checksum verification | Artifactory build/artifact record |
| Container | Minimal runtime image, non-root user, Trivy scan | Dockerfile and scan report |
| Kubernetes | Non-root security context, resource limits, RBAC, manifest validation | Helm chart and validation output |
| Runtime | Readiness/liveness checks, rollout and smoke tests | Deployment commands and results |

Security gates will have explicit severity thresholds. A scanner producing a report is not the same as a gate blocking a release; both configuration and test evidence must be documented. JFrog Xray and advanced GitHub features will only be claimed as enabled after account entitlement and configuration are verified.

## 4. Documentation map

The project will be documented in small, task-focused guides:

- [Documentation index](docs/README.md) — learning order and links to every guide.
- [Architecture](docs/architecture.md) — components, data flow, trust boundaries, and design decisions.
- [Prerequisites and setup](docs/prerequisites-and-setup.md) — required tools, accounts, versions, and installation checks.
- [JFrog setup](docs/jfrog-setup.md) — repositories, permissions, token handling, and connection checks.
- [GitHub Actions and secrets](docs/github-actions.md) — workflow setup, variables, secrets, least privilege, and troubleshooting.
- [Security controls](docs/security-controls.md) — scanners, policies, thresholds, SBOM, and how to interpret findings.
- [Docker and image scanning](docs/docker-and-image-security.md) — image build, hardening, scan, and publish.
- [Minikube deployment](docs/minikube-deployment.md) — local cluster, Helm, rollout verification, and cleanup.
- [Troubleshooting](docs/troubleshooting.md) — symptoms, diagnostic commands, likely causes, and fixes.
- [Rebuild from scratch](docs/rebuild-from-scratch.md) — clean-machine runbook.
- [Interview walkthrough](docs/interview-walkthrough.md) — how to explain architecture, trade-offs, security gates, and evidence.

Each hands-on guide should include: **purpose, prerequisites, steps, commands, expected result, verification, common errors, cleanup, and a short explanation of why the step matters.**

## 5. Quick start

The complete quick start will be finalized as the application and workflows are added. The intended order is:

1. Install and verify Java, Maven, Git, Docker, kubectl, Helm, and Minikube.
2. Configure the JFrog platform and create the required repositories.
3. Add the required GitHub Actions variables and secrets.
4. Run the application and unit tests locally.
5. Run the security scans and review their reports.
6. Publish the versioned JAR to Artifactory.
7. Build and scan the container image.
8. Deploy locally to Minikube and run smoke tests.

Follow the [documentation index](docs/README.md) rather than skipping setup steps.

## 6. Secrets and safe usage

- Never commit passwords, API keys, access tokens, private keys, kubeconfig files, or production data.
- Store CI credentials in GitHub Actions secrets or use OIDC when the integration supports it.
- Use a narrowly scoped JFrog identity and separate build/publish permissions from deployment permissions where practical.
- If a token is accidentally exposed, revoke it immediately and replace it. Removing it from a later commit does not make an exposed credential safe.
- Use test data only. Do not publish private personal information in the portfolio.
- Review scan results; do not ignore findings solely to make a pipeline green.

## 7. Reproducibility and versioning

Commands and configuration should be tested against the versions documented in the prerequisites guide. Where practical, pin dependencies and CI actions. Record known platform-specific differences (Windows, macOS, Linux) rather than assuming every command behaves identically.

A successful run should leave evidence of:
- passing application tests;
- generated security reports and SBOM;
- a versioned artifact in Artifactory;
- an image scan result;
- a successful Kubernetes rollout and application health check.

## 8. Project status and contribution approach

The repository is being built incrementally. A section should be marked **complete** only after its implementation has been tested and its steps have been recorded. Do not treat planned features as already enabled.

See the documentation index for the step-by-step implementation path.
