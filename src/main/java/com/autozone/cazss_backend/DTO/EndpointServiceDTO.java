package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.model.StatusModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(
    name = "EndpointServiceDTO",
    description = "DTO representing the result of executing a service",
    example =
        """
        {
          "status": {
            "code": 200,
            "description": "OK"
          },
          "response": {
            "CMSTINVC_locale": ["en_US"],
            "CMSTINVC_revision": ["6"]
          }
        }
        """)
public class EndpointServiceDTO {
  @Schema(description = "Status object containing HTTP code and message")
  private StatusModel status;

  @Schema(description = "Parsed response content from the endpoint")
  private Map<String, List<String>> response;

  public EndpointServiceDTO(StatusModel status, Map<String, List<String>> response) {
    this.status = status;
    this.response = response;
  }

  public StatusModel getStatus() {
    return status;
  }

  public void setStatus(StatusModel status) {
    this.status = status;
  }

  public Map<String, List<String>> getResponse() {
    return response;
  }

  public void setResponse(Map<String, List<String>> response) {
    this.response = response;
  }
}
