package com.williamfoma.portfolio.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeForwardsToPortfolioHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void healthEndpointReturnsOkText() throws Exception {
        mockMvc.perform(get("/actuator-health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
