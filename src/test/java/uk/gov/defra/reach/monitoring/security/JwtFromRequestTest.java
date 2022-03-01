package uk.gov.defra.reach.monitoring.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.reach.monitoring.util.TestUtils;

@RunWith(SpringRunner.class)
public class JwtFromRequestTest {

  @Test
  public void getJwtFromRequest_returnsToken_whenValid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(TestUtils.AUTHORIZATION_HEADER, "Bearer token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo("token123");
  }

  @Test
  public void getJwtFromRequest_returnsNull_whenBearerMissing() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(TestUtils.AUTHORIZATION_HEADER, "token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo(null);
  }

  @Test
  public void getJwtFromRequest_returnsNull_whenInvalid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(TestUtils.AUTHORIZATION_HEADER, "Bear token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo(null);
  }
}
