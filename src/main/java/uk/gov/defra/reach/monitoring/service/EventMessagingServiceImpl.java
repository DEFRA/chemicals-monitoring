package uk.gov.defra.reach.monitoring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;

@Slf4j
public class EventMessagingServiceImpl implements EventMessagingService {

  private final EventHubClient eventHubClient;

  private final MonitoringEventMapper eventMapper;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  // In the case the event hub is mis-configured we log to app insights using this other implementation of EventMessagingService
  private final EventMessagingService appInsightsEventMessagingService;

  @Autowired
  public EventMessagingServiceImpl(EventHubClient eventHubClient, EventMessagingService eventMessagingService, MonitoringEventMapper eventMapper) {
    this.eventHubClient = eventHubClient;
    this.appInsightsEventMessagingService = eventMessagingService;
    this.eventMapper = eventMapper;
  }

  /**
   * Serializes an {@link MonitoringEvent} and sends it to the event hub. In the cases that this fails for an unexpected
   * reason {@link AppInsightsEventMessagingServiceImpl} is called to log the event to app insights
   * @param eventRequest the monitoring event request
   * @param xff the x-forwarded-for header
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

    byte[] eventMessageBytes = eventMessage.getBytes(Charset.defaultCharset());
    EventData sendEvent = EventData.create(eventMessageBytes);

    try {
      eventHubClient.sendSync(sendEvent);
    } catch (EventHubException e) {
      log.error("An error occurred while attempting to send a message to Microsoft event-hub: ", e);
      appInsightsEventMessagingService.sendMessage(eventRequest, xff);
    }
  }
}
