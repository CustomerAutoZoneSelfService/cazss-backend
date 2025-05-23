package com.autozone.cazss_backend.DTO;

import java.util.List;

public class UserFilterListDTO {
  private List<UserFilterDTO> userFilters;

  public UserFilterListDTO() {}

  public UserFilterListDTO(List<UserFilterDTO> userFilters) {
    this.userFilters = userFilters;
  }

  public List<UserFilterDTO> getUserFilters() {
    return userFilters;
  }

  public void setUserFilters(List<UserFilterDTO> userFilters) {
    this.userFilters = userFilters;
  }
}
