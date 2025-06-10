package com.autozone.cazss_backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  @Mock private JwtUtil jwtUtil;

  @Mock private UserDetailsServiceImpl userDetailsService;

  @InjectMocks private JwtFilter jwtFilter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  private UserDetails mockUserDetails;
  private final String testUserId = "1";

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    mockUserDetails =
        new User(
            testUserId,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  void doFilterInternal_noAuthorizationHeader_shouldContinueFilterChain()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_authorizationHeaderNotStartingWithBearer_shouldContinueFilterChain()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Basic somecredentials");

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_validToken_shouldSetAuthentication() throws ServletException, IOException {
    String jwt = "valid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtUtil.extractUserId(jwt)).thenReturn(testUserId);
    when(userDetailsService.loadUserById(Integer.parseInt(testUserId))).thenReturn(mockUserDetails);
    when(jwtUtil.validateToken(jwt, mockUserDetails)).thenReturn(true);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(testUserId, SecurityContextHolder.getContext().getAuthentication().getName());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_invalidToken_shouldNotSetAuthenticationAndContinueFilterChain()
      throws ServletException, IOException {
    String jwt = "invalid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtUtil.extractUserId(jwt)).thenReturn(testUserId);
    when(userDetailsService.loadUserById(Integer.parseInt(testUserId))).thenReturn(mockUserDetails);
    when(jwtUtil.validateToken(jwt, mockUserDetails)).thenReturn(false);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_jwtUtilThrowsException_shouldClearContextAndContinueFilterChain()
      throws ServletException, IOException {
    String jwt = "exception.causing.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtUtil.extractUserId(jwt)).thenThrow(new RuntimeException("JWT parsing error"));

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_userIdNullFromToken_shouldNotSetAuthentication()
      throws ServletException, IOException {
    String jwt = "token.with.null.userid";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtUtil.extractUserId(jwt)).thenReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(userDetailsService, never()).loadUserById(anyInt());
  }

  @Test
  void doFilterInternal_authenticationAlreadySet_shouldNotReloadAuthentication()
      throws ServletException, IOException {
    SecurityContextHolder.getContext()
        .setAuthentication(mock(org.springframework.security.core.Authentication.class));

    String jwt = "valid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtUtil.extractUserId(jwt)).thenReturn(testUserId);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(userDetailsService, never()).loadUserById(anyInt());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(filterChain).doFilter(request, response);
  }
}
