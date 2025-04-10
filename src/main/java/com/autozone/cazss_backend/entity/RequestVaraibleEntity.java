package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "Request_variables", schema = "cazss")
public class RequestVaraibleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_variable_id")
    private Integer requestVariableId;

    @ManyToOne
    @JoinColumn(name = "endpoint_id")
    private EndpointsEntity endpoint;

    @Enumerated(EnumType.STRING)
    private RequestVariableTypeEnum type;

    @Column(name = "key_name", nullable = false)
    private String keyName;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(nullable = false)
    private Boolean customizable = true;

    @Column(nullable = false)
    private String description;

    public RequestVaraibleEntity() {
    }

    public RequestVaraibleEntity(Integer requestVariableId, EndpointsEntity endpoint, RequestVariableTypeEnum type, String keyName, String defaultValue, Boolean customizable, String description) {
        this.requestVariableId = requestVariableId;
        this.endpoint = endpoint;
        this.type = type;
        this.keyName = keyName;
        this.defaultValue = defaultValue;
        this.customizable = customizable;
        this.description = description;
    }

    public Integer getRequestVariableId() {
        return requestVariableId;
    }

    public void setRequestVariableId(Integer requestVariableId) {
        this.requestVariableId = requestVariableId;
    }

    public EndpointsEntity getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointsEntity endpoint) {
        this.endpoint = endpoint;
    }

    public RequestVariableTypeEnum getType() {
        return type;
    }

    public void setType(RequestVariableTypeEnum type) {
        this.type = type;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getCustomizable() {
        return customizable;
    }

    public void setCustomizable(Boolean customizable) {
        this.customizable = customizable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
