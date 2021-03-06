package uk.gov.defra.reach.monitoring.integration;

import static java.util.concurrent.CompletableFuture.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.eventhubs.TransportType;

import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.util.TestUtils;

@Slf4j
public class EventMessageIT extends IntegrationCommon {

  private static final String EVENT_HUB_CONNECTION_STRING = "Endpoint=sb://sndchminfens001.servicebus.windows.net/;SharedAccessKeyName=PreviewDataPolicy;SharedAccessKey=3TOO//eIasJRmadtliAAJQ2PxqaHLwXcKjtKzn1NXB0=;EntityPath=sndchmeventhubtest";

  private ObjectMapper objectMapper = new ObjectMapper()
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .registerModule(new JavaTimeModule());

  private EventHubClient eventHubClient;

  private ScheduledExecutorService executorService;

  @BeforeEach
  public void setup() throws IOException, EventHubException {
    if (eventHubClient != null) {
      eventHubClient.close();
    }

    if (executorService != null) {
      executorService.shutdown();
    }

    executorService = Executors.newScheduledThreadPool(4);

    ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(EVENT_HUB_CONNECTION_STRING);
    connectionStringBuilder.setTransportType(TransportType.AMQP_WEB_SOCKETS);
    eventHubClient = EventHubClient.createSync(connectionStringBuilder.toString(), executorService);
  }

  @Test
  public void healthCheck_shouldReturn200_withoutAuthentication() {
    ResponseEntity<String> response = REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + TestUtils.HEALTH_CHECK_ENDPOINT, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void eventEndpoints_shouldPostMessageToEventHub() throws EventHubException, InterruptedException, ExecutionException, TimeoutException {
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setBearerAuth(TEST_JWT_TOKEN);
    authHeaders.add(TestUtils.CONTENT_TYPE_HEADER, TestUtils.APPLICATION_JSON_HEADER_VALUE);
    authHeaders.add(TestUtils.X_FORWARED_FOR_HEADER, TestUtils.X_FORWARED_FOR_HEADER_VALUE);

    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();

    Instant startTime = Instant.now();

    REST_TEMPLATE.exchange(MONITORING_SERVICE_URL + TestUtils.EVENT_ENDPOINT, HttpMethod.POST, new HttpEntity<>(monitoringEventRequest, authHeaders), String.class);

    assertThat(findEvent(monitoringEventRequest, startTime)).isTrue();
  }

  private boolean findEvent(MonitoringEventRequest monitoringEventRequest, Instant startTime) throws EventHubException, InterruptedException, ExecutionException, TimeoutException {
    final EventHubRuntimeInformation eventHubInfo = eventHubClient.getRuntimeInformation().get();

    boolean matched = false;

    List<PartitionReceiver> receivers = new ArrayList<>();
    for (String partitionId : eventHubInfo.getPartitionIds()) {
      receivers.add(eventHubClient.createReceiverSync(
              EventHubClient.DEFAULT_CONSUMER_GROUP_NAME,
              partitionId,
              EventPosition.fromEnqueuedTime(startTime)));
    }

    try {
      @SuppressWarnings("unchecked")
      Iterable<EventData> receivedEvents = (Iterable<EventData>) anyOf(receivers.stream()
              .map(r -> r.receive(500))
              .toArray(CompletableFuture[]::new))
              .get(10, TimeUnit.SECONDS);

      if (receivedEvents != null) {
        for (EventData receivedEvent : receivedEvents) {
          if (receivedEvent.getBytes() != null) {
            try {
              final MonitoringEvent request = objectMapper.readValue(receivedEvent.getObject().toString(), MonitoringEvent.class);
              // Here we just compare the sessionIds as they are unique and these two objects are not of the same type
              if (request.getSessionId().equals(monitoringEventRequest.getSessionId())) {
                matched = true;
                break;
              }
            } catch (IOException e) {
              log.error("An error occurred while retrieving events" + e);
            }
          }
        }
      }
    } finally {
      CompletableFuture.allOf(receivers.stream()
              .map(PartitionReceiver::close)
              .toArray(CompletableFuture[]::new))
              .thenComposeAsync(aVoid -> eventHubClient.close(), executorService)
              .whenCompleteAsync((t, u) -> {
                if (u != null) {
                  log.error(String.format("closing failed with error: %s", u.toString()));
                }
              }, executorService).get();
      executorService.shutdown();
    }

    return matched;
  }
}
