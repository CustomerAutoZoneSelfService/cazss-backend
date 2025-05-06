package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.RequestBodyEntity;
import com.autozone.cazss_backend.model.BodyModel;
import com.autozone.cazss_backend.repository.RequestBodyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateFiller {

  @Autowired private RequestBodyRepository requestBodyRepository;

  public String returnFilledTemplate(List<BodyModel> bodyList, Integer endpointId) {

    RequestBodyEntity requestBodyEntity =
        requestBodyRepository.findByEndpoint_EndpointId(endpointId).orElse(null);

    if (requestBodyEntity == null) return "";

    String template = requestBodyEntity.getTemplate();

    for (BodyModel bodyItem : bodyList) {
      String placeholder = "{{" + bodyItem.getKey() + "}}";
      template = template.replace(placeholder, bodyItem.getValue());
    }

    System.out.println(template);
    return template;
  }
}
