# Homepage Routing and Java Build Troubleshooting

## Overview

This document records the local troubleshooting performed on the William Foma DevOps / DevSecOps portfolio application. The work covered Java compiler availability, a local port conflict, and the root route returning JSON instead of the HTML portfolio page.

## Environment

- OS: Ubuntu under WSL
- Java target: Java 21
- Build tool: Maven
- Application framework: Spring Boot
- Static homepage: `src/main/resources/static/index.html`
- Application entry point: `src/main/java/com/williamfoma/portfolio/PortfolioApplication.java`
- Controller: `src/main/java/com/williamfoma/portfolio/web/PortfolioController.java`
- Default port in `application.properties`: `8080`

## 1. Java compiler and Maven configuration

### Symptoms

The initial build failed because Maven's compiler configuration defaulted to an obsolete Java source level. After configuring the compiler for Java 21, Maven reported:

```text
Fatal error compiling: error: release version 21 not supported
```

### Cause and fix

The Java runtime was present, but `javac` was not available. Install the JDK, which includes the compiler:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

Verify the toolchain:

```bash
java -version
javac -version
mvn -version
```

The Maven compiler plugin in `pom.xml` was configured to use Java 21:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
    <configuration>
        <release>21</release>
    </configuration>
</plugin>
```

## 2. Port 8080 conflict

### Symptoms

Spring Boot reported:

```text
Web server failed to start. Port 8080 was already in use.
```

### Cause and fix

Port 8080 was being used by a Java process running as the `jenkins` user. The listener was identified with:

```bash
sudo lsof -i :8080
```

Jenkins was left running. For local testing, the portfolio was started on port 8087:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8087
```

This is a command-line override; `application.properties` still sets the default port to 8080.

## 3. Root URL returned JSON instead of HTML

### Symptoms

A request to `http://localhost:8087/` returned a JSON object with the portfolio name, headline, purpose, and API welcome message. The static HTML file already existed at `src/main/resources/static/index.html`.

### Cause

The search of Java source identified `PortfolioController.java` with `@RestController` and `@GetMapping("/")`. The root mapping returned a JSON response. A `@RestController` writes returned data into the HTTP response rather than rendering the intended static homepage.

### Intended correction

Use an MVC `@Controller` for the root route and forward it to the static HTML page:

```java
package com.williamfoma.portfolio.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PortfolioController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/actuator-health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}
```

This is the intended minimal example for the homepage and simple health endpoint. If the project has other existing controller endpoints, preserve them when applying the change. Confirm the committed controller matches the intended behavior.

## 4. Validation

Start the application on port 8087:

```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments=--server.port=8087
```

Check the homepage response:

```bash
curl -i http://localhost:8087/
```

Expected: HTTP 200 with HTML content, typically `Content-Type: text/html`.

Check the health endpoint, if configured as above:

```bash
curl -i http://localhost:8087/actuator-health
```

Expected: HTTP 200 and body `OK`.

Build the project:

```bash
mvn clean package
```

A successful application startup or a source-code change alone is not sufficient evidence of a successful build or correct browser rendering; inspect the actual results.

## 5. Profile photo status

The homepage references:

`/images/william-foma-profile.jpg`

During troubleshooting, this URL returned HTTP 404. Add the actual profile photo to:

`src/main/resources/static/images/william-foma-profile.jpg`

Create the directory if needed:

```bash
mkdir -p src/main/resources/static/images
```

Then verify the image URL again. Do not treat the photo as installed until the file is committed and the URL returns successfully.

## 6. Git workflow

Review and commit the documentation and related code changes:

```bash
git status
git diff
git add pom.xml src/main/java/com/williamfoma/portfolio/web/PortfolioController.java docs/homepage-routing-and-build-fix.md
git commit -m "Document Java build and homepage routing fixes"
git push origin main
```

If updating the README as well, stage `README.md` too.

## Outcome and remaining checks

The Java compiler issue was addressed by installing the JDK and configuring Maven for Java 21. The port conflict was worked around by running the portfolio on port 8087 rather than stopping Jenkins. The root-route diagnosis identified the controller mapping that returned JSON instead of the static homepage.

Still verify the final controller behavior, a successful `mvn clean package`, and the profile photo asset. Local availability on `localhost` does not mean the website has been deployed publicly.

Repository: https://github.com/Fomainspi/william-foma-devsecops-portfolio
