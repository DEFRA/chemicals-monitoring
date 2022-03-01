package uk.gov.defra.reach.monitoring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;
import uk.gov.defra.reach.monitoring.util.TestUtils;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppInsightsEventMessagingServiceImplTest {

  private static final String XFF = "192.168.1.1";

  private AppInsightsEventMessagingServiceImpl service;

  private TelemetryClient telemetryClient;

  private MonitoringEventMapper eventMapper;

  @Before
  public void setup() {
    telemetryClient = Mockito.mock(TelemetryClient.class);
    eventMapper = Mockito.mock(MonitoringEventMapper.class);

    service = new AppInsightsEventMessagingServiceImpl(telemetryClient, eventMapper);
  }

  @Test
  public void sendMessage_callsTelemetryClientWithValidMonitoringEvent() {
    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();
    MonitoringEvent monitoringEvent = TestUtils.createMonitoringEvent(monitoringEventRequest);

    Mockito.when(eventMapper.map(monitoringEventRequest, XFF)).thenReturn(monitoringEvent);

    service.sendMessage(monitoringEventRequest, XFF);

    Mockito.verify(telemetryClient).trackEvent("REACH Monitoring", getProperties(monitoringEvent), null);
  }

  private Map<String, String> getProperties(MonitoringEvent monitoringEvent) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String eventMessage;

    try {
      eventMessage = objectMapper.writeValueAsString(monitoringEvent);
    } catch (IOException e) {
      throw new BadRequestException(e);
    }

    Map<String, String> properties = new HashMap<>();
    properties.put("MonitoringEvent", eventMessage);

    return properties;
  }
}

