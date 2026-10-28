# Interview Walkthrough

## 60-second summary

“I built a Java-based professional portfolio and used it as the workload for an end-to-end DevSecOps delivery pipeline. GitHub Actions automates build and tests, source and dependency checks, SBOM generation, and controlled publishing. JFrog Artifactory stores versioned build artifacts and container images. The image is scanned before deployment, and Helm deploys it to a local Minikube cluster with Kubernetes hardening and rollout verification.”

Only describe stages that have actually been implemented and tested. Label planned features honestly.

## Questions to prepare for

### Why use Artifactory?

It provides a controlled location for versioned artifacts and images, making the artifact promoted through the pipeline explicit and traceable.

### Why scan both dependencies and the container?

Dependency scanning focuses on known vulnerabilities in application libraries. Image scanning also examines operating-system packages and other components in the container. The coverage overlaps but is not identical.

### What is an SBOM?

A Software Bill of Materials lists software components included in a build. It helps inventory dependencies and investigate newly disclosed vulnerabilities; it does not certify that the application is secure.

### Why verify the artifact checksum?

It helps confirm that the downloaded artifact matches the expected bytes. It is an integrity check, not a substitute for trusted publishing, access control, or artifact provenance.

### Why deploy to Minikube?

It provides a repeatable local Kubernetes environment for demonstrating deployment configuration, security contexts, probes, resource limits, and troubleshooting without requiring a production cluster.

### How do you prevent secrets from leaking?

Use GitHub secrets or supported OIDC, narrow token scope, avoid printing secret values, restrict publishing jobs to trusted events, and rotate exposed credentials.

### What are the pipeline's limitations?

Scanners can produce false positives and miss issues. JFrog Xray and advanced platform features depend on entitlement. Local Minikube is not automatically reachable from hosted GitHub runners. Explain these limitations and the controls used to mitigate them.

## Demo sequence

1. Show the architecture and documentation.
2. Show a passing build and test run.
3. Show security reports and the SBOM.
4. Demonstrate a controlled security gate failure in a lab branch.
5. Show the remediation and passing rerun.
6. Show the versioned artifact and image in JFrog.
7. Show the Helm release, Kubernetes security settings, and health verification.
8. Explain one troubleshooting issue you encountered and how you resolved it.