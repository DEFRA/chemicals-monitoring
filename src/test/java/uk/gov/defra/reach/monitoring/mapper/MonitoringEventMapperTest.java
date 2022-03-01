package uk.gov.defra.reach.monitoring.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.applicationinsights.web.internal.RequestTelemetryContext;
import com.microsoft.applicationinsights.web.internal.ThreadContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.util.TestUtils;

public class MonitoringEventMapperTest {

  private static final String APPLICATION = "ER001";
  private static final String ENVIRONMENT = "test-env";
  private static final String VERSION = "1.1";
  private static final String XFF = "192.168.1:8080, 1.1.1.1:80";
  private static RequestTelemetryContext originalRequestTelemetryContext;

  private MonitoringEventMapper eventMapper;


  @BeforeClass
  public static void setupClass() { originalRequestTelemetryContext = ThreadContext.getRequestTelemetryContext();   }

  @Before
  public void setup() {
    eventMapper = new MonitoringEventMapper(VERSION, ENVIRONMENT);
  }

  @AfterClass
  public static void tearDown() { ThreadContext.setRequestTelemetryContext(originalRequestTelemetryContext); }

  @Test
  public void addCommonEventValues_shouldAddCorrectFieldsToMonitoringEvent() {
    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();

    TestUtils.setMockTelemetryContext();

    MonitoringEvent monitoringEvent = eventMapper.map(monitoringEventRequest, XFF);

    assertThat(monitoringEvent.getApplication()).isEqualTo(APPLICATION);
    assertThat(monitoringEvent.getEnvironment()).isEqualTo(ENVIRONMENT);
    assertThat(monitoringEvent.getVersion()).isEqualTo(VERSION);
    assertThat(monitoringEvent.getSourceIpAddress()).isEqualTo("192.168.1");
    assertThat(monitoringEvent.getOperationid()).isEqualTo("fake-operation-id");
  }

  @Test
  public void convertsNullPriority() {
    MonitoringEventRequest monitoringEventRequest = TestUtils.createMonitoringEventRequest();
    monitoringEventRequest.setPriority(null);

    TestUtils.setMockTelemetryContext();
    
    MonitoringEvent monitoringEvent = eventMapper.map(monitoringEventRequest, XFF);

    assertThat(monitoringEvent.getPriority()).isEqualTo("");
  }
}
