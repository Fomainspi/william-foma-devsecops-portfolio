# Architecture and Data Flow

## Goal

Deliver William Foma's Java/Spring Boot professional portfolio through a repeatable pipeline, while scanning the source, dependencies, build artifact, container image, and Kubernetes configuration.

## Components

| Component | Responsibility |
|---|---|
| GitHub repository | Source control, pull requests, reviews, workflow definitions, documentation |
| GitHub Actions | Repeatable build, test, scanning, artifact publishing, and report retention |
| Maven | Compile, test, resolve dependencies, and package the Java application |
| JFrog Artifactory | Store versioned Maven artifacts and container images |
| SAST/dependency tools | Detect likely source-code and third-party dependency vulnerabilities |
| SBOM generator | Record software components included in the application |
| Docker | Package the built JAR into a runtime image |
| Trivy | Scan container images and optionally other supported targets |
| Helm | Package Kubernetes manifests as a configurable release |
| Minikube | Local Kubernetes target for deployment and runtime verification |

JFrog Xray is an optional integration dependent on the account plan and permissions. Do not assume it is enabled.

## Artifact flow

1. A change is committed or submitted as a pull request.
2. CI compiles the application and runs unit tests.
3. Security checks run and produce machine-readable or human-readable reports.
4. The approved versioned JAR is uploaded to Artifactory.
5. The pipeline retrieves the intended version and verifies its checksum.
6. Docker builds an image using that artifact; the image is scanned before publication.
7. A versioned image is published to the configured JFrog Docker repository.
8. Helm deploys the image to Minikube; rollout and application health are checked.

Use immutable version identifiers (such as a commit SHA plus a release version) to avoid ambiguity. Do not use the mutable `latest` tag as the sole production identity.

## Trust boundaries

- GitHub Actions secrets are available only to workflows and contexts that need them.
- Pull-request code from untrusted forks must not receive publishing credentials.
- Build credentials should not automatically have permission to delete artifacts or administer JFrog.
- Kubernetes deployment access should be separate from artifact publishing access where practical.
- A GitHub-hosted runner cannot normally connect to a developer's local Minikube cluster. Local deployment or a deliberately secured self-hosted runner is required.

## Security gates

The pipeline should fail on agreed conditions such as failed tests, exposed secrets, or vulnerabilities above the configured threshold. Thresholds must be visible in code and documented. Exceptions should have an owner, reason, and expiry.

## Design decisions to record

As implementation proceeds, record:
- tool versions and why they were selected;
- JFrog repository names and repository types;
- authentication method (OIDC or scoped token);
- scanner thresholds and any accepted exceptions;
- image base image and hardening decisions;
- Kubernetes security context and network assumptions;
- deployment limitations and evidence from actual runs.
