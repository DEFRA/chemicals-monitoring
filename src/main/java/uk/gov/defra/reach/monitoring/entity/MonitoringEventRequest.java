package uk.gov.defra.reach.monitoring.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringEventRequest {

  @NotNull
  @JsonProperty("user")
  private String userId;

  @NotNull
  @JsonProperty("sessionId")
  @JsonSerialize(using = UUIDSerializer.class)
  private UUID sessionId;

  @NotNull
  @JsonProperty("dateTime")
  private Instant dateTime;

  @NotNull
  @JsonProperty("component")
  private String component;

  @NotNull
  @JsonProperty("pmcCode")
  private String pmcCode;

  @NotNull
  @JsonProperty("priority")
  private Integer priority;

  @NotNull
  @JsonProperty("details")
  private MonitoringEventRequestDetails details;
}
