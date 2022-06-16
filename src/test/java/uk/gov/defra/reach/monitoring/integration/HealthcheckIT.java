package uk.gov.defra.reach.monitoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class HealthcheckIT extends IntegrationCommon {

  @Test
  public void testHealthcheck_shouldReturnHealthyState_WhenAppIsRunningCorrectly() {
    ResponseEntity<String> response = REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + "/healthcheck", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    JSONObject healthcheckDetails = new JSONObject(response.getBody().toString());

    assertThat(healthcheckDetails.get("health")).isEqualTo("HEALTHY");
    assertThat(healthcheckDetails.get("version")).isNotNull();
  }

}

