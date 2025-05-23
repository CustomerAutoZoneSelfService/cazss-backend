package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestBodyEntity;
import com.autozone.cazss_backend.repository.RequestBodyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestBodyService {
  @Autowired private RequestBodyRepository requestBodyRepository;

  @Transactional
  public RequestBodyEntity createRequestBody(EndpointsEntity endpoint, String template) {
    RequestBodyEntity requestBody = new RequestBodyEntity();
    requestBody.setEndpoint(endpoint);
    requestBody.setTemplate(template);

    return requestBodyRepository.save(requestBody);
  }
}
