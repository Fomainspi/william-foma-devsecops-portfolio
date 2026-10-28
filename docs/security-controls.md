# Security Controls and Release Gates

## Implemented in the current workflow

The `.github/workflows/devsecops.yml` workflow currently runs:
- Maven `clean verify` for compile and tests.
- CycloneDX Maven plugin to generate an SBOM at `target/bom.json`.
- Trivy filesystem scanning for vulnerabilities, secrets, and misconfigurations, with HIGH and CRITICAL findings configured to fail the job.
- JFrog upload only after the validation job passes and only on a push to `main`.

Review the first real workflow run before calling these controls verified. Tool installation, database availability, policy behavior, and the actual findings must be checked in the logs.

## Control inventory

| Control | Purpose | Gate |
|---|---|---|
| Maven tests | Catch functional regressions | Any test failure stops the job |
| Trivy filesystem scan | Detect supported dependency vulnerabilities, secrets, and misconfigurations | HIGH/CRITICAL finding fails the step |
| CycloneDX SBOM | Inventory project components | SBOM generation step must succeed |
| JFrog artifact upload | Store a traceable versioned JAR and SHA-256 sidecar | Runs only after validation on trusted main branch |
| Trivy image scan | Planned for the container stage | Must be implemented and tested before image release |
| Kubernetes manifest scan | Planned for Helm manifests | Add/test a manifest-policy gate before claiming it is active |
| JFrog Xray | Optional and account-plan dependent | Claim only after enablement and a real scan are confirmed |

## Interpreting results

A scanner report is not a guarantee of security. Scanners have coverage limits, may report false positives, and rely on current vulnerability databases. Investigate the package, severity, exploitability, and fixed version. Do not suppress a finding simply to obtain a green pipeline.

For an accepted exception, record:
- finding identifier and affected component;
- technical justification and compensating control;
- responsible owner;
- expiry or review date.

## SBOM

The CycloneDX SBOM is an inventory of components and metadata, not a certificate of safety. Retain it with the same workflow run and artifact version. Check the uploaded workflow evidence for `bom.json`.

## Demonstration evidence

For a complete interview demonstration, collect:
- successful test run;
- Trivy output and any findings;
- generated SBOM;
- JFrog artifact and checksum;
- evidence that a failing test or controlled scanner finding blocks publishing;
- remediation commit and successful rerun;
- image scan and Kubernetes manifest validation output after those stages are implemented and tested.

Do not deploy intentionally vulnerable builds to a public endpoint.
