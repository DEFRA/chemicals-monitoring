package uk.gov.defra.reach.monitoring.healthcheck;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-int.properties")
public class HealthcheckTest {

  @Value("${spring.application.version}")
  private String appVersion;

  @Autowired
  private TestRestTemplate template;

  @Test
  public void testHealthcheck_shouldReturnHealthyState_WhenAppIsRunningCorrectly() {
    ResponseEntity<String> response = template.exchange("/healthcheck", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    JSONObject healthcheckDetails = new JSONObject(response.getBody().toString());

    assertThat(healthcheckDetails.get("health")).isEqualTo("HEALTHY");
    assertThat(healthcheckDetails.get("version")).isEqualTo(appVersion);
  }

}

