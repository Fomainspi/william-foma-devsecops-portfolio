# JFrog Setup

## Purpose

Configure JFrog Artifactory as the trusted store for the versioned Maven JAR and container image.

## Platform URL

The platform URL for this lab is `https://williamdev.jfrog.io`. Use the base URL in automation—not the browser's `/ui/get_started` path.

## Repository setup

In the JFrog UI, open Artifactory and create repositories suitable for:
- Maven releases (for versioned application artifacts);
- Docker images (use the repository type and access method supported by your JFrog instance).

Record each exact repository key in your lab notes and use the same keys in the workflow. Do not guess repository keys: confirm them in the UI. If the UI does not offer repository creation, check the account permissions and trial capabilities.

## Authentication

Prefer GitHub OIDC if supported by the current JFrog plan and integration. Otherwise, create a scoped access token for CI. Grant only the repositories and operations the workflow needs. Avoid administrator tokens.

For the token approach, add these in GitHub repository Settings → Secrets and variables → Actions:
- Variable `JF_URL` = `https://williamdev.jfrog.io`
- Secret `JF_ACCESS_TOKEN` = your JFrog access token

Never paste the token into this document, a GitHub issue, source code, or chat. Rotate/revoke it immediately if exposed.

## Verify

After the workflow integration is added, run a connection check that does not print the token. Then publish a test versioned artifact and confirm it appears in the expected repository. Record the repository key, timestamp, and result—not the credential.

## Troubleshooting

- **401 Unauthorized:** token expired/invalid or wrong authentication method.
- **403 Forbidden:** identity exists but lacks repository permission.
- **404 Not Found:** check the base URL and exact repository key.
- **Artifact upload rejected:** check repository type, Maven coordinates, and version policy.

Account plans and menus change; document the options actually visible in your JFrog account.