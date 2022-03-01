package uk.gov.defra.reach.monitoring.filter;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AppInsightsFilter implements Filter {

  private final List<String> excludedUrlPatterns;
  private final WebRequestTrackingFilter webRequestTrackingFilter;

  public AppInsightsFilter(String appName, List<String> excludedUrlPatterns) {
    this.excludedUrlPatterns = Collections.unmodifiableList(excludedUrlPatterns);
    webRequestTrackingFilter = new WebRequestTrackingFilter(appName);
  }

  @Override
  public void init(FilterConfig filterConfig) {
    webRequestTrackingFilter.init(filterConfig);
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // completely idiotic cast as this is the only servlet type (thanks Java)
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      String servletPath = httpServletRequest.getServletPath();
      String requestURI = httpServletRequest.getRequestURI();
      if (shouldFilter(servletPath, requestURI)) {
        webRequestTrackingFilter.doFilter(request, response, chain);
        return;
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // NO-OP
  }

  private boolean shouldFilter(String servletPath, String requestURI) {
    return (!this.excludedUrlPatterns.contains(servletPath) || !this.excludedUrlPatterns.contains(requestURI));
  }
}
