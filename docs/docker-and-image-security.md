# Docker and Image Security

## Purpose

Package the tested Java artifact into a repeatable container image and harden the runtime.

## Current files

- `Dockerfile` uses a Java 21 JRE base, copies `target/portfolio.jar`, and runs as UID 1000.
- `compose.yaml` applies read-only root filesystem, a temporary filesystem for `/tmp`, dropped Linux capabilities, and `no-new-privileges`.
- `.dockerignore` reduces unnecessary build context.

These files are committed but still require a real local build and runtime verification.

## Build and inspect

From the repository root, after Java and Maven are installed:

```bash
mvn clean package
docker build -t william-foma-portfolio:local .
docker image inspect william-foma-portfolio:local
docker run --rm -p 8080:8080 william-foma-portfolio:local
```

In another terminal, verify the application:

```bash
curl --fail http://localhost:8080/
curl --fail http://localhost:8080/actuator-health
```

Expected health response includes `UP`. Stop the foreground container with Ctrl+C.

You can also use Docker Compose:

```bash
docker compose up --build
docker compose down
```

If the read-only filesystem causes a startup issue, diagnose the required writable paths before changing the hardening settings. Do not remove security options without documenting why.

## Scan

After building, install Trivy using its official installation instructions and run:

```bash
trivy image --severity HIGH,CRITICAL william-foma-portfolio:local
```

Review findings by package, severity, fix availability, and whether the finding is in the base OS or application dependency. Do not treat an empty report as proof that the image is completely secure.

## Publish

The JFrog Maven artifact publish stage is implemented first. The Docker repository exists, but the exact registry endpoint/access method must be confirmed from JFrog's **Set Me Up** instructions before adding an image push step. Do not guess a registry hostname. A future image-publish job must:
1. use the same approved JAR or otherwise prove artifact identity;
2. tag images immutably (commit SHA or release version);
3. run Trivy and enforce the agreed threshold;
4. push only after the security gate passes;
5. retain scan evidence.

## Troubleshooting

- Docker daemon unavailable: start Docker and verify with `docker info`.
- Missing `target/portfolio.jar`: run `mvn clean package` and inspect the build error.
- Port 8080 busy: use another host port, e.g. `-p 8081:8080`.
- Healthcheck fails: inspect `docker logs` and check the health endpoint.
- Registry authentication fails: follow JFrog's exact Docker registry setup instructions.
- Scanner database fails: inspect connectivity and retry; do not silently skip the scan.
