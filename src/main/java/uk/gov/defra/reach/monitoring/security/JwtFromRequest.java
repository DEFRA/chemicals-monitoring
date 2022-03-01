package uk.gov.defra.reach.monitoring.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

final class JwtFromRequest {

  private JwtFromRequest() {
  }

  static String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
