package com.almerys.columbia.api.controllers.conf;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({ IllegalArgumentException.class })
  public ResponseEntity<Object> handleIllegalArgumentException(Exception ex, WebRequest request) {
    return new ResponseEntity<>(
        StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "Bad request : some values are invalid", new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({Exception.class, NullPointerException.class})
  public ResponseEntity<Object> exception(Exception ex, WebRequest request) {
    return new ResponseEntity<>(
    StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "Something went wrong. Very wrong.", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
