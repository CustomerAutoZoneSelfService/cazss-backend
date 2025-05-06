package com.autozone.cazss_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/** Main runner of Web App. */
@SpringBootApplication
public class CazssBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(CazssBackendApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
