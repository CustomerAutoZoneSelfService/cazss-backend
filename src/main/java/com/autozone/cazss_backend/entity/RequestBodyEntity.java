package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Request_body", schema = "cazss")
public class RequestBodyEntity {
    @Id
    @Column(name = "endpoint_id")
    private Integer endpointId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "endpoint_id")
    private EndpointsEntity endpoint;

    @Column(nullable = false)
    @Lob
    private String template;

    public RequestBodyEntity(Integer endpointId, EndpointsEntity endpoint, String template) {
        this.endpointId = endpointId;
        this.endpoint = endpoint;
        this.template = template;
    }

    public RequestBodyEntity() {
    }

    public Integer getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Integer endpointId) {
        this.endpointId = endpointId;
    }

    public EndpointsEntity getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointsEntity endpoint) {
        this.endpoint = endpoint;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
