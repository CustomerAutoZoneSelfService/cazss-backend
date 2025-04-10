package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "Endpoints", schema = "cazss")
public class EndpointsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endpoint_id")
    private Integer endpointId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EndpointMethodEnum method;

    @Column(nullable = false, length = 2048)
    private String url;

    public EndpointsEntity(Integer endpointId, CategoryEntity category, UserEntity user, Boolean active, String name, String description, EndpointMethodEnum method, String url) {
        this.endpointId = endpointId;
        this.category = category;
        this.user = user;
        this.active = active;
        this.name = name;
        this.description = description;
        this.method = method;
        this.url = url;
    }

    public EndpointsEntity() {
    }

    public Integer getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Integer endpointId) {
        this.endpointId = endpointId;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EndpointMethodEnum getMethod() {
        return method;
    }

    public void setMethod(EndpointMethodEnum method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
