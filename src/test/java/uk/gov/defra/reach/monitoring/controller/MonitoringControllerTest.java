package uk.gov.defra.reach.monitoring.controller;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.service.EventMessagingService;
import uk.gov.defra.reach.monitoring.util.TestUtils;

public class MonitoringControllerTest {

  private static final String XFF = "192.168.1.1";

  private MonitoringController monitoringController;

  private EventMessagingService eventMessagingService;

  @Before
  public void setup() {
    eventMessagingService = Mockito.mock(EventMessagingService.class);
    monitoringController = new MonitoringController(eventMessagingService);
  }

  @Test
  public void sendEvent_callsEventMessagingServiceWithValidMonitoringEvent() {
    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();

    monitoringController.sendEvent(monitoringEventRequest, XFF);

    Mockito.verify(eventMessagingService).sendMessage(monitoringEventRequest, XFF);
  }

  @Test
  public void root() {
    Assertions.assertThat(monitoringController.root()).isEqualTo("ok");
  }
}
