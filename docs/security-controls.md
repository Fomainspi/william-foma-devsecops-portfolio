# Security Controls and Release Gates

## Purpose

Explain which checks are used, what they can and cannot prove, and when the pipeline must stop.

## Planned controls

| Control | Purpose | Gate expectation |
|---|---|---|
| Unit tests | Catch functional regressions | Fail on test failure |
| SAST (Semgrep or CodeQL) | Flag suspicious source-code patterns | Fail on policy-defined high-risk findings |
| Maven dependency scan | Identify known vulnerable libraries | Fail on policy-defined severity threshold |
| Secret scanning | Detect likely committed credentials | Block release; rotate any exposed credential |
| SBOM (CycloneDX) | Inventory included components | Generate and retain with build evidence |
| Trivy image scan | Identify image OS/package vulnerabilities | Block on agreed threshold |
| Manifest scan (Checkov/Kubescape) | Flag risky Kubernetes configuration | Block on policy-defined findings |
| Optional JFrog Xray | Analyze artifacts and dependencies | Only if the account plan and permissions support it |

## Policy and thresholds

Choose and document the exact severity threshold when configuring each tool. A scanner may report a finding without failing the job unless the workflow explicitly enforces the policy. Test that each gate really blocks the next release stage.

Do not suppress a finding simply to obtain a green pipeline. If a finding is a false positive or must be temporarily accepted, document:
- finding identifier and affected component;
- technical justification;
- compensating control;
- responsible owner;
- expiry/review date.

## SBOM

The SBOM is an inventory, not a guarantee of safety. Store it with the corresponding build version and use it to support vulnerability review and future component-impact analysis.

## Evidence checklist

For a completed demonstration, capture:
- a successful test run;
- one safe, controlled demonstration of a scanner finding in a lab branch;
- evidence that the configured gate blocked publishing;
- remediation commit and successful rerun;
- SBOM and scan reports;
- image scan and Kubernetes manifest validation output.

Do not deploy intentionally vulnerable builds to a public endpoint.