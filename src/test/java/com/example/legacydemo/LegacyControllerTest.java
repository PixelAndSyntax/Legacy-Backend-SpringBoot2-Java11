package com.example.legacydemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LegacyControllerTest {
  @Autowired MockMvc mockMvc;

  @Test void helloWorks() throws Exception {
    mockMvc.perform(get("/api/hello"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Hello from Boot 2.x on Java 11"));
  }

  @Test void xmlEndpointProducesXml() throws Exception {
    mockMvc.perform(get("/api/xml/customer"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/xml"));
  }

  @Test void customersReturnsData() throws Exception {
    mockMvc.perform(get("/api/customers"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"));
  }
}
