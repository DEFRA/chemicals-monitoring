package uk.gov.defra.reach.monitoring.security;

import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenValidator {

  private final JwtConsumer jwtConsumer;

  public JwtTokenValidator(
      @Value("${protective.monitoring.jwt.secret}") final String jwtSecret) {

    this.jwtConsumer = new JwtConsumerBuilder()
        .setAllowedClockSkewInSeconds(30)
        .setRequireExpirationTime()
        .setVerificationKey(new HmacKey(jwtSecret.getBytes()))
        .setRelaxVerificationKeyValidation()
        .build();
  }

  boolean validateToken(String authToken) {
    try {
      jwtConsumer.process(authToken);

      return true;
    } catch (InvalidJwtException e) {
      log.error("Invalid JWT token", e);
    }
    return false;
  }
}
