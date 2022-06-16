package uk.gov.defra.reach.monitoring.integration;

import org.springframework.web.client.RestTemplate;

public abstract class IntegrationCommon {

  protected static final String TEST_JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjM1NjkzMjU2MTYsImxlZ2FsRW50aXR5Um9sZSI6IlJFR1VMQVRPUiIsInNvdXJjZSI6ImIyYyIsInVzZXJJZCI6IjRlMDJiZDUwLWUxMTEtNDA0OS1iODYxLWY5NjE1OTZlZTZhOSIsImNvbnRhY3RJZCI6bnVsbCwiZW1haWwiOiJyZWd1bGF0b3IxQGRvbWFpbi5jb20iLCJncm91cHMiOlsiYjQyNTAwYzctODBiZS00MjUxLWEwMjgtZDE3ZjQ1ODdiYjQ0Il0sInJvbGUiOiJSRUdVTEFUT1IiLCJ1c2VyIjpudWxsfQ.3IeggA_pQaP56nvfae6DWqUSnNzlHMLFxSdquGgqdBI";
  
  protected static final String MONITORING_SERVICE_URL = System.getProperty("MONITORING_SERVICE_URL", "http://localhost:8096");
  
  protected static final RestTemplate REST_TEMPLATE = new RestTemplate();


}