# Rebuild From Scratch

This runbook is the final end-to-end exercise. Follow the linked guides for the detailed explanation of each step.

## Clean-room checklist

- [ ] Clone the repository on a clean workstation.
- [ ] Install tools and record versions using [Prerequisites and Setup](prerequisites-and-setup.md).
- [ ] Start Minikube and verify the node is Ready.
- [ ] Confirm the JFrog platform URL and create/verify the required repositories.
- [ ] Configure GitHub Actions variables and secrets without committing credentials.
- [ ] Run the Java application and tests locally.
- [ ] Run SAST, dependency scanning, secret detection, and SBOM generation.
- [ ] Confirm the configured security gates block an intentionally failing lab case.
- [ ] Publish a versioned JAR to Artifactory.
- [ ] Retrieve the exact JAR and verify its checksum.
- [ ] Build and scan the container image.
- [ ] Publish the versioned image to JFrog.
- [ ] Validate Helm/Kubernetes manifests.
- [ ] Deploy to Minikube and verify rollout and application health.
- [ ] Review scan reports and retain the evidence for the exact version deployed.

## Completion criteria

The lab is reproducible only when each checklist item has been completed on your machine and the observed output matches the guide. Mark a step complete only after testing it; update any commands or version requirements that have changed.

## Reset safely

Before deleting resources, check `kubectl config current-context`, the target namespace, and the Helm release. Remove only lab resources. Do not delete shared repositories or credentials that other projects use.