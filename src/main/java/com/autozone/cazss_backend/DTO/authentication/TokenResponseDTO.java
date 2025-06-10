package com.autozone.cazss_backend.DTO.authentication;

import java.util.Objects;

public class TokenResponseDTO {
  private String access_token;
  private String token_type;
  private int expires_in;
  private String scope;

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  public int getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(int expires_in) {
    this.expires_in = expires_in;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    TokenResponseDTO that = (TokenResponseDTO) o;
    return expires_in == that.expires_in
        && Objects.equals(access_token, that.access_token)
        && Objects.equals(token_type, that.token_type)
        && Objects.equals(scope, that.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(access_token, token_type, expires_in, scope);
  }

  @Override
  public String toString() {
    return "TokenResponse{"
        + "access_token='"
        + access_token
        + '\''
        + ", token_type='"
        + token_type
        + '\''
        + ", expires_in="
        + expires_in
        + ", scope='"
        + scope
        + '\''
        + '}';
  }
}
