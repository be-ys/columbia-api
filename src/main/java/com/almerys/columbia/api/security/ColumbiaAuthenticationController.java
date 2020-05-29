package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.controllers.AbstractRestController;
import com.almerys.columbia.api.services.Utilities;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Login Controller", description = "Controller to login to local server or OAuth2")
@RestController
public class ColumbiaAuthenticationController extends AbstractRestController {

  private AuthenticationService authenticationService;
  private ColumbiaConfiguration columbiaConfiguration;

  public ColumbiaAuthenticationController(AuthenticationService authenticationService, ColumbiaConfiguration columbiaConfiguration) {
    this.authenticationService = authenticationService;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  @ApiOperation(value = "Login")
  @PostMapping(path = "/login")
  public ResponseEntity login(
          @ApiParam(value = "username (for local connexion)") @RequestParam(name = "username", required = false) String username,
          @ApiParam(value = "password (for local connexion)")  @RequestParam(name = "password", required = false) String password,
          @ApiParam(value = "token (for OAuth2 connexion)") @RequestParam(name = "token", required = false) String token,
          @ApiParam(value = "user domain", required = true) @RequestParam(name = "domain") String domain) {

    Assert.notNull(domain, "Domain could not be null");

    HttpHeaders httpHeaders = new HttpHeaders();

    if (!Utilities.isEmptyOrNull(username) &&  !Utilities.isEmptyOrNull(password)) {
      httpHeaders.set("Authorization", authenticationService.getBearer(username, password, domain));
    } else if (!Utilities.isEmptyOrNull(token) && columbiaConfiguration.getDelegatedAuthentication()) {
      httpHeaders.set("Authorization", authenticationService.getBearer(token, domain));
    } else {
      return badRequest();
    }

    return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).build();
  }

}
