package com.almerys.columbia.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public abstract class AbstractRestController {

  public URI getLocation(UriComponentsBuilder ucb, String path, Object... uriVariableValues) {
    return ucb.path(path).buildAndExpand(uriVariableValues).toUri();
  }

  public ResponseEntity forbidden() {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  public ResponseEntity error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  public ResponseEntity badRequest() {
    return ResponseEntity.badRequest().build();
  }

  public ResponseEntity notFound() {
    return ResponseEntity.notFound().build();
  }

  public boolean hasRole(Authentication authentication, String... roles) {
    if (authentication == null) {
      return false;
    }

    for (String role : roles) {
      if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
        return true;
      }
    }
    return false;
  }



}
