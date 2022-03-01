package uk.gov.defra.reach.monitoring.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringEvent {

  @NotNull
  @Pattern(regexp = "^(IDM\\/|AAD\\/)[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$")
  @JsonProperty("user")
  private String userId;

  @NotNull
  @JsonProperty("sessionid")
  @JsonDeserialize(using = UUIDDeserializer.class)
  @JsonSerialize(using = UUIDSerializer.class)
  private UUID sessionId;

  @NotNull
  @JsonProperty("datetime")
  private Instant dateTime;

  @JsonProperty("application")
  private String application;

  @NotNull
  @JsonProperty("component")
  private String component;

  @JsonProperty("ip")
  private String sourceIpAddress;

  @NotNull
  @JsonProperty("pmccode")
  private String pmcCode;

  @NotNull
  @JsonProperty("priority")
  private String priority;

  @JsonProperty("environment")
  private String environment;

  @JsonProperty("version")
  private String version;

  @NotNull
  @JsonProperty("details")
  private MonitoringEventDetails details;

  @JsonProperty("operationid")
  private String operationid;
}
