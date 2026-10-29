# Documentation Index

This project is designed to be rebuilt from a clean workstation. Follow the guides in order and verify each stage before proceeding.

## Learning path

1. [Architecture](architecture.md) — components, data flow, and trust boundaries.
2. [Prerequisites and setup](prerequisites-and-setup.md) — install tools and check versions.
3. [JFrog setup](jfrog-setup.md) — configure the two local repositories and CI credentials.
4. [GitHub Actions](github-actions.md) — understand the workflow, permissions, variables, and secret scope.
5. [Security controls](security-controls.md) — scan policy, SBOM, and release gates.
6. [Docker and image security](docker-and-image-security.md) — build, harden, scan, and inspect the container.
7. [Minikube deployment](minikube-deployment.md) — validate the Helm chart and deploy locally.
8. [Kubernetes hardening walkthrough](kubernetes-hardening.md) — apply runtime security controls one at a time, verify rollouts, test the API, and recover safely.
9. [Kubernetes hardening log](kubernetes-hardening-log.md) — historical baseline, status of controls, monitoring resource notes, and a template for evidence.
10. [Troubleshooting](troubleshooting.md) — diagnose common failures.
11. [CI workflow incident and resolution report](workflow-incident-report.md) — timeline of failures, fixes, and evidence from successful run #30.
12. [Rebuild from scratch](rebuild-from-scratch.md) — repeat the complete lab.
13. [Interview walkthrough](interview-walkthrough.md) — explain architecture, trade-offs, and evidence.

## Current implementation map

- `.github/workflows/devsecops.yml`: Maven validation, Trivy filesystem and container-image scanning, CycloneDX SBOM generation, and JFrog publication stages.
- `Dockerfile` and `compose.yaml`: container runtime foundation.
- `helm/william-foma-portfolio/`: Helm chart with a hardened pod/container security context, resource requests/limits, probes, and a dedicated `/tmp` volume.
- `docs/kubernetes-hardening.md`: practical hardening procedure for the live Minikube lab.
- `docs/kubernetes-hardening-log.md`: separates historical observations from controls that still need live verification.
- `foma-maven-local`: JFrog Maven repository, confirmed during setup.
- `foma-docker-local`: JFrog Docker repository, confirmed during setup.

## How to use each guide

- Read purpose and prerequisites first.
- Run commands one at a time and compare results with the expected result.
- Never proceed past a failed verification step.
- Never paste secrets into a terminal transcript, screenshot, Git commit, or documentation.
- Record the versions and actual results from your own run. Sample output is not proof your environment passed.

## Status convention

- **Planned:** design exists; implementation or verification is incomplete.
- **In progress:** currently being configured or tested.
- **Verified:** successfully run and evidence recorded.

The previously recorded CI run verified its described checks and JFrog JAR publication. See the [incident and resolution report](workflow-incident-report.md) for historical evidence and remaining verification work.

A green workflow does not prove the application is completely secure. Review scanner findings and document accepted risks.
