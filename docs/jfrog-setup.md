# JFrog Setup

## Purpose

Use JFrog Artifactory to store the versioned application JAR and, after registry access is verified, container images.

## Platform URL

The platform base URL is `https://williamdev.jfrog.io`. Use the base URL in automation, not the browser's `/ui/get_started` page.

## Repositories created

The JFrog repository list shared during setup shows these local repositories:

| Repository key | Package type | Use |
|---|---|---|
| `foma-maven-local` | Maven | Store the versioned Spring Boot JAR and checksum |
| `foma-docker-local` | Docker | Intended container image repository |

### Create or verify them in a fresh account

1. Open **Administration → Repositories**.
2. Choose **Create a Repository → Local**.
3. Select **Maven** and use key `foma-maven-local`.
4. Repeat and select **Docker** with key `foma-docker-local`.
5. Return to **Repositories** and verify both keys and package types.

Why two repositories? Maven artifacts and Docker images use different package formats and registry interactions. Do not create either repository as a generic repository.

### Screenshot evidence

Two screenshots were provided during the project setup: one shows the local repository package-type selection page (including Maven and Docker), and the other shows the repository list with both keys. They are the source of the repository names above. The screenshot files still need to be committed as repository assets to display inline on GitHub.

## Authentication

The repository has been configured with:
- GitHub Actions variable `JF_URL` = `https://williamdev.jfrog.io`
- GitHub Actions secret `JF_ACCESS_TOKEN` = the user's JFrog access token

Do not copy the token into source code, logs, this guide, or chat. Use a least-privilege token that can upload to the required repository and publish build information. Prefer OIDC if you configure a JFrog identity integration later.

## Verify connection and artifact

The workflow uses `jf rt ping` before upload. A successful ping confirms connectivity/authentication, but not upload permission. After the first successful run:

1. Open the GitHub Actions run and inspect the JFrog steps.
2. Open Artifactory and browse `foma-maven-local`.
3. Confirm a versioned path under `com/williamfoma/william-foma-devsecops-portfolio/`.
4. Download the JAR and its `.sha256` sidecar and verify the checksum locally.
5. Confirm the build record is available under JFrog Builds if build-info publication is enabled for the account.

## Docker registry note

The repository key alone does not prove which Docker registry hostname/access method your JFrog instance requires. Before pushing an image, inspect the repository's **Set Me Up** instructions and use the exact Docker login/registry format shown there. Do not guess a subdomain or path-style endpoint. Registry access may depend on the trial plan and repository configuration.

## Troubleshooting

- **401 Unauthorized:** token expired/invalid or wrong authentication method.
- **403 Forbidden:** identity lacks deploy or build-info permission.
- **404 Not Found:** verify base URL and exact repository key.
- **Artifact upload rejected:** confirm repository type, path, and permissions.
- **Docker login/push fails:** use the registry URL from JFrog's Set Me Up instructions; the platform URL may not equal the Docker registry endpoint.

Never expose the access token in screenshots. If it leaks, revoke it immediately.
