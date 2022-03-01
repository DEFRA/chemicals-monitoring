package uk.gov.defra.reach.monitoring.service;

import org.springframework.stereotype.Service;
import uk.gov.defra.reach.monitoring.entity.MonitoringEventRequest;

@Service
public interface EventMessagingService {

  void sendMessage(MonitoringEventRequest event, String xff);
}
