package uk.gov.defra.reach.monitoring.util;

import com.microsoft.applicationinsights.extensibility.context.OperationContext;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import com.microsoft.applicationinsights.web.internal.RequestTelemetryContext;
import com.microsoft.applicationinsights.web.internal.ThreadContext;
import org.mockito.Mockito;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventDetails;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequestDetails;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class TestUtils {
  public static final String HEALTH_CHECK_ENDPOINT= "/healthcheck";
  public static final String AUTHORIZATION_HEADER= "Authorization";
  public static final String CONTENT_TYPE_HEADER= "Content-Type";
  public static final String X_FORWARED_FOR_HEADER = "x-forwarded-for";
  public static final String APPLICATION_JSON_HEADER_VALUE= "application/json";
  public static final String X_FORWARED_FOR_HEADER_VALUE = "192.168.1.1";

  public static final String EVENT_ENDPOINT = "/event";

  public static MonitoringEvent createMonitoringEvent() {
    return MonitoringEvent.builder()
            .userId("IDM/495f8904-fb35-43c2-8d07-7e7634b60e3f")
            .sessionId(UUID.randomUUID())
            .dateTime(Instant.now())
            .component("test-component")
            .pmcCode("pmcCode")
            .priority("1")
            .details(
                    new MonitoringEventDetails(
                      "test-transaction-code",
                      "test-message",
                      "test-additional-info"
                    )
            )
            .operationid("fake-operation-id")
            .build();
  }

  public static MonitoringEvent createMonitoringEvent(MonitoringEventRequest monitoringEventRequest) {
    return  MonitoringEvent.builder()
            .userId(monitoringEventRequest.getUserId())
            .sessionId(monitoringEventRequest.getSessionId())
            .dateTime(monitoringEventRequest.getDateTime())
            .component(monitoringEventRequest.getComponent())
            .pmcCode(monitoringEventRequest.getPmcCode())
            .priority(monitoringEventRequest.getPriority().toString())
            .details(
                    new MonitoringEventDetails(
                            monitoringEventRequest.getDetails().getTransactionCode(),
                            monitoringEventRequest.getDetails().getMessage(),
                            monitoringEventRequest.getDetails().getAdditionalInfo()
                    )
            )
            .operationid("fake-operation-id")
            .build();
  }

  public static MonitoringEventRequest createMonitoringEventRequest() {
    return MonitoringEventRequest.builder()
            .userId("IDM/495f8904-fb35-43c2-8d07-7e7634b60e3f")
            .sessionId(UUID.randomUUID())
            .dateTime(Instant.now())
            .component("test-component")
            .pmcCode("pmcCode")
            .priority(1)
            .details(
                    new MonitoringEventRequestDetails(
                            "test-transaction-code",
                            "test-message",
                            "test-additional-info"
                    )
            )
            .build();
  }

  public static void setMockTelemetryContext() {
    RequestTelemetryContext requestTelemetryContext = Mockito.mock(RequestTelemetryContext.class);
    RequestTelemetry requestTelemetry = Mockito.mock(RequestTelemetry.class);
    TelemetryContext telemetryContext = Mockito.mock(TelemetryContext.class);
    OperationContext operationContext = Mockito.mock(OperationContext.class);
    when(requestTelemetryContext.getHttpRequestTelemetry()).thenReturn(requestTelemetry);
    when(requestTelemetry.getContext()).thenReturn(telemetryContext);
    when(telemetryContext.getOperation()).thenReturn(operationContext);
    when(operationContext.getId()).thenReturn("fake-operation-id");
    ThreadContext.setRequestTelemetryContext(requestTelemetryContext);
  }
}
