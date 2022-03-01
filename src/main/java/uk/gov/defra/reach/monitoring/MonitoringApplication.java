package uk.gov.defra.reach.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = "uk.gov.defra.reach")
public class MonitoringApplication {

  public static void main(String[] args) {
    SpringApplication.run(MonitoringApplication.class, args);
  }
}
