package uk.gov.defra.reach.monitoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.defra.reach.monitoring.mapper.MonitoringEventMapper;

@Configuration
public class MonitoringEventMapperConfig {

  @Value("${reach.monitoring.environment}")
  private String environment;

  @Value("${monitoring.event.version}")
  private String version;

  @Bean
  public MonitoringEventMapper monitoringEventMapper() {
    return new MonitoringEventMapper(version, environment);
  }
}
