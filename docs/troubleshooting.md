# Troubleshooting

Diagnose one failure at a time. Capture the error text with secrets removed, identify which stage failed, and verify the underlying assumption before changing configuration.

| Symptom | Checks | Likely cause |
|---|---|---|
| Java/Maven command not found | `java -version`, `mvn -version` | Missing install or PATH |
| Tests fail | Review the first failing test and stack trace | Code or test fixture issue |
| GitHub secret appears empty | Check exact secret name and scope | Typo, wrong environment, or secrets unavailable to fork PR |
| JFrog 401 | Check base URL, token validity, authentication method | Invalid/expired token |
| JFrog 403 | Check repository permissions | Insufficient privileges |
| JFrog 404 | Check exact repository key and endpoint | Incorrect URL/repository |
| Dependency scan fails | Read report and scanner exit code | Vulnerability threshold, network, or database issue |
| Docker cannot build | `docker info`, inspect build context | Engine unavailable or missing files |
| Image scan blocks release | Review CVE, severity, package, fix | Vulnerable base image or dependency |
| Pod ImagePullBackOff | `kubectl describe pod` | Wrong image name/tag or registry pull credentials |
| Pod CrashLoopBackOff | `kubectl logs`, `kubectl describe pod` | App startup, configuration, or probe issue |
| Pod Pending | `kubectl describe pod`, `kubectl get events` | Resource, scheduling, or storage issue |
| Helm upgrade fails | `helm status`, `helm history` | Chart/configuration or cluster issue |
| Hosted CI cannot access Minikube | Check runner location and kubeconfig context | Laptop cluster is not reachable from hosted runner |

## Safe debugging

- Redact access tokens, passwords, email addresses, and other private data from logs.
- Do not turn off a security gate as a first response.
- If a credential was exposed, revoke it; deleting the line is not enough.
- Make one change at a time and rerun the failed verification step.
- Record the root cause and fix here after resolving a new issue.