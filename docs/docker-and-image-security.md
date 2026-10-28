# Docker and Image Security

## Purpose

Package the approved Java artifact into a repeatable container image and scan it before publication.

## Build principles

- Use a maintained, minimal Java runtime base image compatible with the app.
- Copy in the versioned JAR retrieved from Artifactory rather than silently rebuilding a different artifact.
- Run as a non-root user.
- Include a `.dockerignore` file.
- Avoid embedding secrets or build credentials in image layers.
- Prefer immutable version tags, such as a release version and commit SHA.
- Pin base images by digest when practical and document how they are updated.

## Build and inspect

After the Dockerfile and workflow are committed, follow the exact commands in the implementation. Typical local checks include:

```bash
docker build -t william-foma-portfolio:local .
docker image inspect william-foma-portfolio:local
```

Confirm the image was created. The final project guide will specify the exact runtime port and health endpoint after the application is implemented.

## Scan

Run Trivy against the built image using the configured policy. Review package, severity, and fix-availability details. Do not treat “no findings” as proof that the image is completely secure; scanners have coverage and database limitations.

## Publish

Authenticate using a scoped CI identity, tag with an immutable version, and push only after required gates pass. Never publish a release from an untrusted pull request.

## Troubleshooting

- Docker daemon unavailable: start the engine and verify with `docker info`.
- Build context too large: review `.dockerignore`.
- Registry authentication failure: confirm the JFrog Docker repository endpoint and token permissions.
- Scanner database failure: inspect network/connectivity logs and retry; do not silently skip the scan.