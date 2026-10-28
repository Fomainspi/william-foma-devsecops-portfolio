# Rebuild From Scratch

This runbook is the clean-room exercise. Use the linked guides for details and record the actual outputs from your own machine.

## Clean-room checklist

- [ ] Clone the repository.
- [ ] Install Java 21, Maven, Git, Docker, kubectl, Helm, Minikube, and Trivy; verify versions in [Prerequisites and Setup](prerequisites-and-setup.md).
- [ ] Run `mvn clean verify` and confirm all tests pass.
- [ ] Run the local Trivy filesystem scan and review findings.
- [ ] Generate the CycloneDX SBOM.
- [ ] Confirm GitHub Actions variable `JF_URL` and secret `JF_ACCESS_TOKEN` are set without exposing the token.
- [ ] Confirm JFrog repositories `foma-maven-local` (Maven) and `foma-docker-local` (Docker) exist.
- [ ] Push a harmless documentation or code change and inspect the workflow run.
- [ ] On a successful push to `main`, confirm a versioned JAR and SHA-256 sidecar are present in Artifactory.
- [ ] Download the artifact and verify its SHA-256 checksum.
- [ ] Build the Docker image and inspect it.
- [ ] Run Trivy image scan and review the results.
- [ ] Start Minikube, lint/render the Helm chart, deploy the local image, and verify rollout.
- [ ] Run smoke tests against `/` and `/actuator-health`.
- [ ] Capture scan reports, SBOM, artifact path, checksum, and deployment evidence.

## Checksum verification

Download the JAR and its corresponding `.sha256` file from the exact versioned Artifactory path. From the directory containing the downloaded JAR, run:

```bash
sha256sum -c portfolio.jar.sha256
```

If the sidecar names a versioned filename instead of `portfolio.jar`, either rename the downloaded file to match the sidecar or run `sha256sum` on the JAR and compare the values manually.

## Completion criteria

A stage is complete only when its command has been run successfully and evidence has been inspected. Committing workflow YAML or chart files does not prove that CI, artifact publication, image scanning, or Kubernetes deployment passed.

## Reset safely

Before deleting resources, check `kubectl config current-context`, the target namespace, and the Helm release. Remove only the lab release and namespace. Do not delete shared JFrog repositories or credentials used by other projects.
