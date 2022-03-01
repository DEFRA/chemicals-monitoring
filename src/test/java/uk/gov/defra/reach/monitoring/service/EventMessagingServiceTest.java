package uk.gov.defra.reach.monitoring.service;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;
import uk.gov.defra.reach.monitoring.util.TestUtils;

public class EventMessagingServiceTest {

  private static final String XFF = "192.168.1.1";

  private EventHubClient eventHubClient;

  private EventMessagingServiceImpl service;

  private MonitoringEventMapper eventMapper;

  @Test
  public void sendMessage_callsEventHubClient() throws EventHubException {
    eventHubClient = Mockito.mock(EventHubClient.class);
    eventMapper = Mockito.mock(MonitoringEventMapper.class);
    service = new EventMessagingServiceImpl(eventHubClient, null, eventMapper);

    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();

    service.sendMessage(monitoringEventRequest, XFF);

    Mockito.verify(eventHubClient).sendSync(Mockito.any(EventData.class));
  }

  @Test
  public void sendMessage_callsAppInsightsMessagingServiceOnEHClientException() throws EventHubException {
    EventMessagingService appInsightsEventMessagingService = Mockito.mock(AppInsightsEventMessagingServiceImpl.class);
    eventHubClient = Mockito.mock(EventHubClient.class);
    eventMapper = Mockito.mock(MonitoringEventMapper.class);

    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();
    MonitoringEvent monitoringEvent = TestUtils.createMonitoringEvent(monitoringEventRequest);

    Mockito.doThrow(Mockito.mock(EventHubException.class)).when(eventHubClient).sendSync(Mockito.any(EventData.class));
    Mockito.when(eventMapper.map(monitoringEventRequest, XFF)).thenReturn(monitoringEvent);

    service = new EventMessagingServiceImpl(eventHubClient, appInsightsEventMessagingService, eventMapper);

    service.sendMessage(monitoringEventRequest, XFF);

    Mockito.verify(eventHubClient).sendSync(Mockito.any(EventData.class));
    Mockito.verify(appInsightsEventMessagingService).sendMessage(monitoringEventRequest, XFF);
  }
}
