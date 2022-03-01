package uk.gov.defra.reach.monitoring.config;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestClientConfiguration {
  @Bean
  RestTemplate restTemplate() {
    RestTemplate restClient = new RestTemplate(
            new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    restClient.setInterceptors(Collections.singletonList((request, body, execution) -> {

      log.debug(request.toString());
      return execution.execute(request, body);
    }));
    return restClient;
  }
}
