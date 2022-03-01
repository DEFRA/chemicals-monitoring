package uk.gov.defra.reach.monitoring.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.reach.monitoring.entity.MonitoringEvent;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;
import uk.gov.defra.reach.monitoring.service.EventMessagingService;

@Slf4j
@RestController
public class MonitoringController {

  private final EventMessagingService eventMessagingService;

  @Autowired
  public MonitoringController(EventMessagingService eventMessagingService) {
    this.eventMessagingService = eventMessagingService;
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  public String root() {
    return "ok";
  }

  /**
   * Receives an {@link MonitoringEvent} and passes it to the eventMessagingService
   * @param monitoringEvent the event monitoring json object
   * @param xff the x-forwarded-for header
   */
  @PostMapping(value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void sendEvent(@Valid @NotNull @RequestBody MonitoringEventRequest monitoringEvent, @RequestHeader("x-forwarded-for") String xff) {
    log.debug("Start send event message: " + monitoringEvent.toString());
    eventMessagingService.sendMessage(monitoringEvent, xff);
    log.debug("End send event message:" + monitoringEvent.toString());
  }
}
