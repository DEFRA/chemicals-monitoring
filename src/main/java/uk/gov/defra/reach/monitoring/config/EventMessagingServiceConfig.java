package uk.gov.defra.reach.monitoring.config;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.TransportType;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.defra.reach.monitoring.exception.EventMessagingServiceException;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;
import uk.gov.defra.reach.monitoring.service.AppInsightsEventMessagingServiceImpl;
import uk.gov.defra.reach.monitoring.service.EventMessagingService;
import uk.gov.defra.reach.monitoring.service.EventMessagingServiceImpl;

@Slf4j
@Configuration
public class EventMessagingServiceConfig {

  @Value("${event.hub.connection.string}")
  private String eventHubConnectionString;

  @Value("${event.hub.connection.web_sockets:false}")
  private boolean webSockets;

  @Bean
  public EventMessagingService eventMessagingService(TelemetryClient telemetryClient, MonitoringEventMapper eventMapper)
          throws EventMessagingServiceException {
    if (eventHubConnectionString == null) {
      return new AppInsightsEventMessagingServiceImpl(telemetryClient, eventMapper);
    }

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
    EventHubClient eventHubClient;
    try {
      ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(eventHubConnectionString);
      connectionStringBuilder.setTransportType(webSockets ? TransportType.AMQP_WEB_SOCKETS : TransportType.AMQP);
      eventHubClient = EventHubClient.createSync(connectionStringBuilder.toString(), executorService);
    } catch (IOException | EventHubException e) {
      throw new EventMessagingServiceException("An error occurred while initialising EventMessagingService", e);
    }

    return new EventMessagingServiceImpl(eventHubClient, new AppInsightsEventMessagingServiceImpl(telemetryClient, eventMapper), eventMapper);
  }
}
