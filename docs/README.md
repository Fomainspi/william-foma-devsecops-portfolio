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
8. [Troubleshooting](troubleshooting.md) — diagnose common failures.
9. [Rebuild from scratch](rebuild-from-scratch.md) — repeat the complete lab.
10. [Interview walkthrough](interview-walkthrough.md) — explain architecture, trade-offs, and evidence.

## Current implementation map

- `.github/workflows/devsecops.yml`: Maven validation, Trivy filesystem scanning, CycloneDX SBOM generation, and JFrog JAR upload on a successful push to `main`.
- `Dockerfile` and `compose.yaml`: container runtime foundation.
- `helm/william-foma-portfolio/`: Helm chart foundation.
- `foma-maven-local`: JFrog Maven repository, confirmed in the user's account.
- `foma-docker-local`: JFrog Docker repository, confirmed in the user's account.

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

A green workflow does not prove the application is completely secure. Review scanner findings and document accepted risks.
