package uk.gov.defra.reach.monitoring.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
public class JwtTokenValidatorTest {

  @Value("${test.jwt.token}")
  private String testJwtToken;

  @Value("${reach.monitoring.jwt.secret}")
  private String testJwtKey;

  private JwtTokenValidator jwtTokenValidator;

  @Before
  public void setup() {
    jwtTokenValidator = new JwtTokenValidator(testJwtKey);
  }

  @Test
  public void validateToken_shouldReturnTrue_whenTokenIsValid() {

    assertThat(jwtTokenValidator.validateToken(testJwtToken.substring(7))).isTrue();
  }

  @Test
  public void validateToken_shouldReturnFalse_whenTokenIsInvalid() {

    assertThat(jwtTokenValidator.validateToken("invalidToken")).isFalse();
  }
}
