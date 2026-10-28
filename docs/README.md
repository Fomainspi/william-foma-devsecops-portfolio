# Documentation Index

This documentation is designed to let you rebuild the project independently. Follow the guides in order; each guide explains the goal, commands, expected output, verification, troubleshooting, and cleanup.

## Learning path

1. [Architecture](architecture.md) — understand the system before building it.
2. [Prerequisites and setup](prerequisites-and-setup.md) — install tools and verify versions.
3. [JFrog setup](jfrog-setup.md) — configure repositories and credentials safely.
4. [GitHub Actions](github-actions.md) — configure workflow variables, secrets, and permissions.
5. [Security controls](security-controls.md) — understand scanners, the SBOM, and release gates.
6. [Docker and image security](docker-and-image-security.md) — package and scan the application.
7. [Minikube deployment](minikube-deployment.md) — deploy with Helm and verify the rollout.
8. [Troubleshooting](troubleshooting.md) — diagnose common failures.
9. [Rebuild from scratch](rebuild-from-scratch.md) — repeat the complete lab on a clean environment.
10. [Interview walkthrough](interview-walkthrough.md) — explain the decisions and demonstrate evidence.

## How to use each guide

- Read the purpose and prerequisites first.
- Run commands one at a time; compare your output with the expected result.
- Do not proceed past a failed verification step.
- Never paste secrets into a terminal transcript, screenshot, Git commit, or this documentation.
- Record the tool versions and actual results from your own run. A sample result is not proof that your environment passed.

## Status convention

- **Planned:** design exists, implementation not yet verified.
- **In progress:** being configured or tested.
- **Verified:** run successfully and the steps/evidence were recorded.

A green workflow alone does not prove the application is secure. Review the findings and document accepted risks.
