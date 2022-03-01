package uk.gov.defra.reach.monitoring.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringEventRequestDetails {

  @NotNull
  @JsonProperty("transactionCode")
  private String transactionCode;

  @NotNull
  @JsonProperty("message")
  private String message;

  @NotNull
  @JsonProperty("additionalInfo")
  private String additionalInfo;
}
