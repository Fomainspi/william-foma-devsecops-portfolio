# Troubleshooting

Diagnose one failure at a time. Capture the error with secrets removed, identify the failing stage, and verify the underlying assumption before changing configuration.

| Symptom | Checks | Likely cause |
|---|---|---|
| Java/Maven command not found | `java -version`, `mvn -version` | Missing install or PATH |
| Maven tests fail | Inspect first failing test and Surefire report under `target/surefire-reports` | Code or test fixture issue |
| GitHub workflow not triggered | Actions tab, workflow events, branch names | Event/branch mismatch or Actions disabled |
| Secret appears empty | Check exact secret name and scope | Typo, environment scope, or fork PR secret restriction |
| JFrog 401 | Check base URL and token validity without printing it | Invalid/expired token |
| JFrog 403 | Check repository deploy/build-info permissions | Insufficient privileges |
| JFrog 404 | Check exact repository key and endpoint | Incorrect URL/repository |
| Artifact upload rejected | Inspect repository type and path | Wrong package type, path, or permission |
| Trivy scan blocks | Review vulnerability, secret, or misconfiguration finding | Real finding, policy threshold, or stale dependency |
| SBOM file missing | Inspect CycloneDX Maven plugin logs and target directory | Plugin failure or changed output path |
| Docker cannot build | `docker info`, inspect `target/portfolio.jar` | Engine unavailable or JAR missing |
| Container healthcheck fails | `docker logs <container>`, call `/actuator-health` | Startup error or health endpoint mismatch |
| Image scan blocks | Review CVE, severity, package, and fix | Vulnerable base image or dependency |
| Pod ImagePullBackOff | `kubectl describe pod` | Wrong image/tag or registry pull credentials |
| Pod CrashLoopBackOff | `kubectl logs`, `kubectl describe pod` | App startup, config, or probe issue |
| Pod Pending | `kubectl describe pod`, `kubectl get events` | Resource/scheduling/storage issue |
| Helm template/lint fails | `helm lint`, `helm template` | Chart syntax or values issue |
| Hosted CI cannot access Minikube | Check runner location and kubeconfig context | Laptop cluster is not reachable from hosted runner |

## Safe debugging

- Redact tokens, passwords, email addresses, and private data from logs.
- Do not turn off a security gate as the first response.
- If a credential was exposed, revoke it; deleting the line is not enough.
- Make one change at a time and rerun the failed verification step.
- Record the root cause and the fix here after resolving a new issue.
