package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateRequestVariableDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import com.autozone.cazss_backend.repository.RequestVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestVariableService {
    @Autowired
    private RequestVariableRepository requestVariableRepository;

    public RequestVariableEntity createRequestVariable(EndpointsEntity endpoint, CreateRequestVariableDTO requestVariableDTO) {
        RequestVariableEntity reqVar = new RequestVariableEntity();
        reqVar.setEndpoint(endpoint);
        reqVar.setType(requestVariableDTO.getType());
        reqVar.setKeyName(requestVariableDTO.getKey());
        reqVar.setDefaultValue(requestVariableDTO.getDefaultValue());
        reqVar.setCustomizable(requestVariableDTO.getCustomizable());
        reqVar.setDescription(requestVariableDTO.getDescription());

        return requestVariableRepository.save(reqVar);
    }
}
