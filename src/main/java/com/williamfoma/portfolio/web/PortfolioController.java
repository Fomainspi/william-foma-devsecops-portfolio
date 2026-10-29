
package com.williamfoma.portfolio.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/actuator-health")
    @org.springframework.web.bind.annotation.ResponseBody
    public String health() {
        return "OK";
    }
}
