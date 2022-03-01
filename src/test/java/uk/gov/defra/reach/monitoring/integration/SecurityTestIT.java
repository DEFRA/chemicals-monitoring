package uk.gov.defra.reach.monitoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.reach.monitoring.util.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Here we specifically define the connection string as null so that only EventMessageIT instantiates an instance of
// EventHubClient otherwise we run into conflicts - do this for all other integration tests or face the wrath of EventHubClient
@TestPropertySource(locations="classpath:application-int.properties", properties="event.hub.connection.string=#{null}")
public class SecurityTestIT {

  @MockBean
  private TelemetryClient telemetryClient;

  @Autowired
  private TestRestTemplate template;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Value("${test.jwt.token}")
  private String testJwtToken;

  @Before
  public void setup() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Test
  public void healthCheck_shouldReturn200_withoutAuthentication() {

    ResponseEntity<String> response = template.exchange(TestUtils.HEALTH_CHECK_ENDPOINT, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void emptyEndpointForAzurePing_shouldReturn200_withoutAuthentication() {

    ResponseEntity<String> response = template.exchange("/", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void event_shouldReturn201_withCorrectHeaders() throws JsonProcessingException {

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.add(TestUtils.AUTHORIZATION_HEADER, testJwtToken);
    authHeaders.add(TestUtils.CONTENT_TYPE_HEADER, TestUtils.APPLICATION_JSON_HEADER_VALUE);
    authHeaders.add(TestUtils.X_FORWARED_FOR_HEADER, TestUtils.X_FORWARED_FOR_HEADER_VALUE);

    String requestBody = objectMapper.writeValueAsString(TestUtils.createMonitoringEventRequest());

    ResponseEntity<String> response = template.exchange(TestUtils.EVENT_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestBody, authHeaders), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }
}
