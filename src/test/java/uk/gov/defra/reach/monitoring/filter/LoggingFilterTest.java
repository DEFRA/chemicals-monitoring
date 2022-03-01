package uk.gov.defra.reach.monitoring.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.slf4j.MDC;

public class LoggingFilterTest {

  private static final String USER_ID_KEY = "userid";

  private static final String X_REMOTE_USER = "x-remote-user";

  private static final String CORRELATION_ID = "correlation-id";

  private LoggingFilter loggingFilter = new LoggingFilter();

  @Test
  public void doFilterSetsValuesInMdcAndRemovesThemAfterwards() throws Exception {
    String userValue = "user value";
    String correlationIdValue = "corr value";

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(X_REMOTE_USER)).thenReturn(userValue);
    when(request.getHeader(CORRELATION_ID)).thenReturn(correlationIdValue);

    loggingFilter.doFilter(request, null, (servletRequest, servletResponse) -> {
      assertThat(MDC.get(CORRELATION_ID)).isEqualTo(correlationIdValue);
      assertThat(MDC.get(USER_ID_KEY)).isEqualTo(userValue);
    });

    assertThat(MDC.get(CORRELATION_ID)).isNull();
    assertThat(MDC.get(USER_ID_KEY)).isNull();
  }

}
