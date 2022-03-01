package uk.gov.defra.reach.monitoring.security;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private JwtTokenValidator jwtTokenValidator;

  @Autowired
  public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
    this.jwtTokenValidator = jwtTokenValidator;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {

      String jwt = JwtFromRequest.getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && jwtTokenValidator.validateToken(jwt)) {

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(null, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }
}
