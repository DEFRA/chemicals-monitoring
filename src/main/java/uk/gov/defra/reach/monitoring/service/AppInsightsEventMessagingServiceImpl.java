package uk.gov.defra.reach.monitoring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.applicationinsights.TelemetryClient;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;

@Slf4j
public class AppInsightsEventMessagingServiceImpl implements EventMessagingService {

  private final TelemetryClient telemetryClient;

  private final MonitoringEventMapper eventMapper;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Autowired
  public AppInsightsEventMessagingServiceImpl(TelemetryClient telemetryClient, MonitoringEventMapper eventMapper) {
    this.telemetryClient = telemetryClient;
    this.eventMapper = eventMapper;
  }

  /**
   * Serializes an {@link MonitoringEvent} and sends it as a custom event to app insights
   * @param eventRequest the monitoring event request
   * @param xff x-forwarded-for header
   */
  @Override
  public void sendMessage(MonitoringEventRequest eventRequest, String xff) {
    MonitoringEvent event = eventMapper.map(eventRequest, xff);

    String eventMessage;
    try {
      eventMessage = objectMapper.writeValueAsString(event);
    } catch (IOException e) {
      throw new BadRequestException("An error occurred while serialising a MonitoringEventRequest: ", e);
    }

    Map<String, String> properties = Map.of("MonitoringEvent", eventMessage);
    telemetryClient.trackEvent("REACH Monitoring", properties, null);
  }
}
