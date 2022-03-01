package uk.gov.defra.reach.monitoring.mapper;

import com.microsoft.applicationinsights.web.internal.ThreadContext;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventDetails;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;

public class MonitoringEventMapper {

  private static final String APPLICATION = "ER001";

  private final String eventVersion;
  private final String environment;

  public MonitoringEventMapper(String eventVersion, String environment) {
    this.eventVersion = eventVersion;
    this.environment  = environment;
  }

  /**
   * Adds constants and universal fields to an {@link MonitoringEvent}
   * @param monitoringEventRequest without constants
   * @param xff the x-forwarded-for header
   * @return {@link MonitoringEvent} with constants
   */
  public MonitoringEvent map(MonitoringEventRequest monitoringEventRequest, String xff) {
    return  MonitoringEvent.builder()
            .userId(monitoringEventRequest.getUserId())
            .sessionId(monitoringEventRequest.getSessionId())
            .dateTime(monitoringEventRequest.getDateTime())
            .application(APPLICATION)
            .component(monitoringEventRequest.getComponent())
            .sourceIpAddress(extractNetworkOrigin(xff))
            .pmcCode(monitoringEventRequest.getPmcCode())
            .priority(monitoringEventRequest.getPriority() != null ? monitoringEventRequest.getPriority().toString() : "")
            .environment(environment)
            .version(eventVersion)
            .details(
                    new MonitoringEventDetails(
                            monitoringEventRequest.getDetails().getTransactionCode(),
                            monitoringEventRequest.getDetails().getMessage(),
                            monitoringEventRequest.getDetails().getAdditionalInfo()
                    )
            )
            .operationid(getOperationId())
            .build();
  }

  private String getOperationId() {
    return ThreadContext.getRequestTelemetryContext().getHttpRequestTelemetry().getContext().getOperation().getId();
  }

  /**
   * Extracts the source ip address from an xff header. This will work for IPv4 and IPv6, hence the weird splitting and substringing. This works under the
   * assumption that we are using Microsoft Application Gateway and therefore the xff header will always include port numbers, if we are not this will
   * likely cause problems with IPv6 ips.
   * @param xff the x-forwarded-for header
   * @return the source ip address without the port number
   */
  private static String extractNetworkOrigin(String xff) {
    String sourceIpAddress = xff.split(",")[0];

    // If there is no port number on an IPv4 address - this can occur locally
    if (sourceIpAddress.split(":").length == 1) {
      return sourceIpAddress;
    }

    // Remove the port number from the ip address
    String[] parts = sourceIpAddress.split(":");
    String portNumber = parts[parts.length - 1];
    return sourceIpAddress.substring(0, sourceIpAddress.length() - portNumber.length() - 1);
  }
}
