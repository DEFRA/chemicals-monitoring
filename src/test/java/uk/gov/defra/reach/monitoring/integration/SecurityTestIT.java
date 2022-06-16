package uk.gov.defra.reach.monitoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.gov.defra.reach.monitoring.util.TestUtils;

public class SecurityTestIT extends IntegrationCommon {

  @Test
  public void healthCheck_shouldReturn200_withoutAuthentication() {

    ResponseEntity<String> response = REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + TestUtils.HEALTH_CHECK_ENDPOINT, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void emptyEndpointForAzurePing_shouldReturn200_withoutAuthentication() {

    ResponseEntity<String> response = REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + "/", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void event_shouldReturn201_withCorrectHeaders() throws JsonProcessingException {

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(TEST_JWT_TOKEN);
    authHeaders.add(TestUtils.CONTENT_TYPE_HEADER, TestUtils.APPLICATION_JSON_HEADER_VALUE);
    authHeaders.add(TestUtils.X_FORWARED_FOR_HEADER, TestUtils.X_FORWARED_FOR_HEADER_VALUE);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    String requestBody = objectMapper.writeValueAsString(TestUtils.createMonitoringEventRequest());

    ResponseEntity<String> response = REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + TestUtils.EVENT_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestBody, authHeaders), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }
}
