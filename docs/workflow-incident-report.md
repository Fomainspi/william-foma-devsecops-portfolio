# CI Workflow Incident and Resolution Report

**Project:** William Foma — DevSecOps Portfolio  
**Workflow:** DevSecOps CI  
**Successful run:** [Run #30 — Upgrade Jackson 3 dependencies to patched 3.1.8](https://github.com/Fomainspi/william-foma-devsecops-portfolio/actions/runs/40770703468)  
**Commit:** [04fb323](https://github.com/Fomainspi/william-foma-devsecops-portfolio/commit/04fb323f47a225f30645d366c21ee59071743e04)  
**Outcome:** Both workflow jobs passed. The JAR was uploaded to JFrog and build evidence was published.

## Executive summary

Run #30 is green end-to-end for the tested main-branch commit. Earlier runs uncovered dependency vulnerabilities and framework/test compatibility issues. The pipeline stopped release publication when its HIGH/CRITICAL image vulnerability gate failed. Updating the effective Jackson 3 dependencies to 3.1.8 allowed both Trivy scans to pass and the dependent JFrog publication job to complete.

## What run #30 verified

### Job 1 — Test and security checks

All steps shown in the supplied screenshot passed:
- Checkout source and set up Java 21
- Maven build and tests
- CycloneDX SBOM generation
- Docker image build for scanning
- Trivy container-image scan
- Trivy repository vulnerability/secret scan
- Upload of test reports and SBOM

### Job 2 — Publish versioned JAR to JFrog

All steps shown in the supplied screenshot passed:
- Checkout source and set up Java 21
- Build release candidate
- Set up JFrog CLI and verify the connection
- Upload JAR using Maven repository layout
- Publish build information
- Upload published build evidence

This verifies configured GitHub Actions-to-JFrog authentication and the permissions needed by these workflow steps at the time of the run. It does not verify Docker image publication or Kubernetes deployment.

## Issues discovered and fixes

### 1. Trivy blocked the image due to vulnerable dependencies

**Observed:** Earlier image scans reported HIGH and CRITICAL vulnerabilities in packaged dependencies, including Jackson and Spring/Tomcat components. The scan exited non-zero and prevented artifact publication.

**Why it mattered:** A successful compile does not prove that packaged dependencies are safe. The release gate correctly stopped publication when findings met the configured severity threshold.

**Fix:** Dependency versions were updated, the image rebuilt, and scans rerun. The pipeline's blocking scan policy was retained rather than disabling the scanner.

### 2. Spring Boot 4 migration caused test compilation problems

**Observed:** After upgrading Spring Boot, a previous Spring MVC test import/package was unavailable with the original test dependency set. Changing the import alone did not restore the test classpath.

**Fix:** The test import and dependency setup were aligned with the Spring Boot 4 test arrangement, including the spring-boot-starter-webmvc-test test dependency. A subsequent run passed Maven build and tests.

**Lesson:** Framework major-version upgrades can change module boundaries and test packages. Validate dependency declarations, imports, and the test runtime together.

### 3. Jackson 3.1.2 findings persisted in run #29

**Observed:** Run #29 passed build, tests, SBOM generation, and image build but failed Trivy image scanning with eight HIGH findings tied to tools.jackson.core jackson-core and jackson-databind 3.1.2.

**Root cause:** The effective Jackson 3 dependencies inside the packaged application remained at 3.1.2; the previous dependency management did not move the resolved versions to the patched level.

**Fix:** Commit 04fb323 removed the obsolete Jackson 2 BOM override and imported the Jackson 3 BOM tools.jackson:jackson-bom:3.1.8. Explicit dependency management also set tools.jackson.core:jackson-core and tools.jackson.core:jackson-databind to 3.1.8.

**Verification:** In run #30, both the Trivy container-image scan and repository scan passed, followed by successful JFrog publication. This is the evidence that the findings blocking run #29 no longer prevented the pipeline.

### 4. JFrog publication depended on the security job

**Observed:** The publish job requires the validation/security job to pass. Earlier Trivy failures therefore prevented publication.

**Fix/status:** No bypass was added. Once run #30 passed the security gates, the JFrog connection check, versioned JAR upload, build-info publication, and evidence upload all succeeded.

## Warnings and limitations

The GitHub Actions page shows **2 warnings and 1 notice**, while both jobs are green. The screenshots do not show the expanded annotation messages, so their exact causes cannot be reliably identified from the evidence provided. Expand **Annotations** in run #30 and inspect each message before deciding whether it is harmless or needs action.

Other limitations:
- CI scans the container image but currently publishes the JAR to JFrog; it does not yet push the Docker image to foma-docker-local.
- A green CI build does not prove the Helm chart has been deployed to Minikube or that runtime behavior has been tested.
- Scanner results reflect the database and policy at the time of the run; continue scanning on future changes.
- Keep JF_ACCESS_TOKEN in GitHub Actions secrets. Never place it in source code, docs, or screenshots.

## Evidence

- [Successful GitHub Actions run #30](https://github.com/Fomainspi/william-foma-devsecops-portfolio/actions/runs/40770703468)
- [Jackson dependency fix commit](https://github.com/Fomainspi/william-foma-devsecops-portfolio/commit/04fb323f47a225f30645d366c21ee59071743e04)
- The companion report document includes the two screenshots supplied by William: the green security-check job and the green JFrog publication job.

## Follow-up checklist

- [ ] Expand and record the exact text of the two warnings and one notice.
- [ ] Confirm the uploaded JAR and checksum are visible in foma-maven-local.
- [ ] Review the published JFrog build-info record.
- [ ] Add and verify Docker image publication to foma-docker-local only after confirming the exact registry endpoint in JFrog Set Me Up.
- [ ] Render the Helm chart, deploy to Minikube, and record rollout/health-check evidence separately.
- [ ] Keep the Trivy HIGH/CRITICAL gate enabled and review future findings rather than suppressing them by default.
