package com.almerys.columbia.api.controllers;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ErrorController extends AbstractErrorController {

  public ErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
  }

  @GetMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> handleGetError(HttpServletRequest request) {
    return super.getErrorAttributes(request, false);
  }

  @PostMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> handlePostError(HttpServletRequest request) {
    return super.getErrorAttributes(request, false);
  }

  @PutMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> handlePutError(HttpServletRequest request) {
    return super.getErrorAttributes(request, false);
  }

  @DeleteMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> handleDeleteError(HttpServletRequest request) {
    return super.getErrorAttributes(request, false);
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }

}
