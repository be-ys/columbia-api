package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.DTOSetup;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "Setup Controller", description = "Controller to setup the server on first boot")
@RestController
public class SetupController extends AbstractRestController {
  private ColumbiaConfiguration columbiaConfiguration;
  private UserService userService;

  public SetupController(ColumbiaConfiguration columbiaConfiguration, UserService userService) {
    this.columbiaConfiguration = columbiaConfiguration;
    this.userService = userService;
  }

  @ApiOperation(value = "Setup initial administrator user (only if database is empty)")
  @GetMapping("/setup")
  public ResponseEntity<DTOSetup> initialization() {
    if (!userService.getAll(PageRequest.of(0, 2)).isEmpty()) {
      return forbidden();
    }

    UserUpdater user = new UserUpdater();
    user.setUsername("columbialocal_admin");
    String password = RandomStringUtils.randomAlphanumeric(25);
    user.setPassword(password);
    user.setEmail("admin@localhost");
    user.setDomain("local");
    user.setActiv(true);
    user.setRole(columbiaConfiguration.getAdminRoleName());

    ColumbiaUser us =  userService.createUser(user);

    if (us == null) {
      return error();
    }

    DTOSetup setup = new DTOSetup();
    setup.password = password;
    setup.user = us.getUsername();

    return ResponseEntity.ok(setup);
  }

}
