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
| Trivy image scan reports Jackson 3.1.2 CVEs | Check effective Maven dependency versions and the packaged JAR/image; review run #29 and fix commit 04fb323 | Jackson 3 dependencies resolved to a vulnerable version; fixed by managing tools.jackson Jackson BOM/core/databind at 3.1.8 and verified in run #30 |
| Spring Boot 4 test import cannot be resolved | Verify test import package and Maven test dependencies; run mvn clean verify | Framework test module/package changed; align the test import and add spring-boot-starter-webmvc-test in test scope |
| SBOM file missing | Inspect CycloneDX Maven plugin logs and target directory | Plugin failure or changed output path |
| Docker cannot build | `docker info`, inspect `target/portfolio.jar` | Engine unavailable or JAR missing |
| Container healthcheck fails | `docker logs <container>`, call `/actuator-health` | Startup error or health endpoint mismatch |
| Image scan blocks | Review CVE, severity, package, and fix | Vulnerable base image or dependency |
| Pod ImagePullBackOff | `kubectl describe pod` | Wrong image/tag or registry pull credentials |
| Pod CrashLoopBackOff | `kubectl logs`, `kubectl describe pod` | App startup, config, or probe issue |
| Pod Pending | `kubectl describe pod`, `kubectl get events` | Resource/scheduling/storage issue |
| Helm template/lint fails | `helm lint`, `helm template` | Chart syntax or values issue |
| Hosted CI cannot access Minikube | Check runner location and kubeconfig context | Laptop cluster is not reachable from hosted runner |

## Recent incident: successful run #30

Run #30 passed both the security validation job and JFrog publication. Earlier runs were blocked by HIGH/CRITICAL dependency findings, followed by test compilation errors during the Spring Boot 4 migration. The Jackson 3.1.8 dependency management change fixed the blocking scan findings. See the [CI workflow incident and resolution report](workflow-incident-report.md) and [successful Actions run](https://github.com/Fomainspi/william-foma-devsecops-portfolio/actions/runs/40770703468).

The run page shows two warnings and one notice even though both jobs passed. Expand the Annotations panel to read the actual messages; do not assume their cause from the summary count alone.

## Safe debugging

- Redact tokens, passwords, email addresses, and private data from logs.
- Do not turn off a security gate as the first response.
- If a credential was exposed, revoke it; deleting the line is not enough.
- Make one change at a time and rerun the failed verification step.
- Record the root cause and the fix here after resolving a new issue.
