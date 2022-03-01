package uk.gov.defra.reach.monitoring.model;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.util.TestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class MonitoringEventTest {

  private Validator validator;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void monitoringEvent_hasNoConstraintViolationsForValidData() {
    MonitoringEvent monitoringEvent = TestUtils.createMonitoringEvent();

    Set<ConstraintViolation<MonitoringEvent>> violations = validator.validate(monitoringEvent);

    Assertions.assertThat(violations.isEmpty()).isTrue();
  }

  @Test
  public void monitoringEvent_hasConstraintViolationsForUserIDWithWrongPattern() {
    MonitoringEvent monitoringEvent = TestUtils.createMonitoringEvent();
    monitoringEvent.setUserId("not a valid user id");

    Set<ConstraintViolation<MonitoringEvent>> violations = validator.validate(monitoringEvent);

    Assertions.assertThat(violations.isEmpty()).isFalse();
  }
}
