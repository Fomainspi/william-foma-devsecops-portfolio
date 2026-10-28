# GitHub Actions Setup

## Purpose

Automate repeatable build, test, security checks, and artifact publishing.

## Required configuration

Use repository Settings → Secrets and variables → Actions. Add the JFrog URL as a variable and the scoped access token as a secret if using token authentication. Never store credentials as plain workflow variables.

## Workflow security principles

- Set workflow and job permissions to the minimum required.
- Pin third-party actions to full commit SHAs where practical.
- Do not expose publish credentials to untrusted fork pull requests.
- Separate pull-request validation from release/publish jobs.
- Avoid printing environment variables or enabling shell tracing around secrets.
- Prefer OIDC over long-lived tokens when the JFrog account and workflow support it.
- Use immutable artifact versions and retain scan reports.

## Expected stages

1. Checkout source.
2. Set up the project JDK.
3. Compile and run tests.
4. Run source and dependency scans.
5. Generate and retain an SBOM.
6. Publish the approved JAR to Artifactory only from a trusted branch/tag.
7. Build, scan, and publish the image after the artifact gate passes.

The exact YAML will be documented alongside the workflow as implementation is added. Do not assume publishing is configured until a real workflow run confirms it.

## Verification

Open the repository Actions tab, select the workflow run, and inspect each step. Confirm failures block later publishing steps. Check that logs do not contain credentials.

## Troubleshooting

- Workflow not triggered: inspect the `on:` events and branch names.
- Secret is empty: check exact secret name and repository/environment scope.
- Permission denied: check workflow permissions and JFrog repository permissions.
- Works on default branch but not PR: confirm that secrets are intentionally unavailable to forked PRs.
- Hosted runner cannot reach Minikube: expected for a local laptop cluster; use the documented local deployment process.