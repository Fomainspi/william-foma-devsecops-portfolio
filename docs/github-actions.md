# GitHub Actions Setup

## Purpose

Automate repeatable testing, security checks, SBOM generation, and versioned JAR publication.

## Required configuration

In GitHub, open **Settings → Secrets and variables → Actions** and confirm:
- Variable `JF_URL` is set to `https://williamdev.jfrog.io`.
- Secret `JF_ACCESS_TOKEN` contains the scoped JFrog token.

Never store the token as a plain variable or print it to logs. The workflow should not publish artifacts for pull requests.

## Workflow file

The workflow is `.github/workflows/devsecops.yml`. It currently contains two jobs:

1. **Test and security checks** — checkout, Java 21, Maven `clean verify`, CycloneDX SBOM generation, and a Trivy filesystem scan for vulnerabilities, secrets, and misconfigurations. HIGH and CRITICAL findings fail the scan step.
2. **Publish versioned JAR** — runs only after validation succeeds on a push to `main`; sets up JFrog CLI, pings the platform, uploads the JAR and SHA-256 sidecar to `foma-maven-local`, and publishes build information.

The artifact version includes the workflow run number and commit SHA so repeated builds do not overwrite one fixed version.

## First-run verification

1. Open the repository **Actions** tab.
2. Select the `DevSecOps CI` run created by the commit.
3. Inspect the Maven test and Trivy scan steps.
4. On a push to `main`, inspect the JFrog ping and upload steps.
5. Confirm the JAR and checksum are visible in Artifactory.
6. Confirm the SBOM and test evidence are attached to the workflow run.

A failed required check must stop the dependent publish job. A successful YAML commit is not proof the workflow itself passed; use the actual run result.

## Security principles

- Workflow-level permissions are read-only by default.
- Publishing only runs on the trusted `main` branch.
- Never run publishing with untrusted fork pull-request code.
- Prefer pinning third-party actions to full commit SHAs for high-assurance production workflows; the current examples use version tags for readability and should be upgraded before production use.
- Do not enable shell tracing or print environment variables around secrets.
- Prefer OIDC over long-lived tokens when the account supports it.

## Minikube boundary

GitHub-hosted runners cannot normally connect to Minikube on a personal workstation. Deployment is a separate local operation unless a secured self-hosted runner is deliberately configured. Never expose the Kubernetes API publicly just to make a hosted runner reach a laptop cluster.

## Troubleshooting

- Workflow not triggered: inspect the `on:` events and branch names.
- Secret appears empty: check exact name and scope; secrets are not available to forked PRs.
- JFrog 401: validate token and base URL without revealing the token.
- JFrog 403: grant only the needed deploy/build-info permissions.
- Trivy blocks: inspect the finding and fix/update the affected dependency or base image; don't disable the gate to get a green run.
