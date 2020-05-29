package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.controllers.conf.ApiPageable;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.View;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.UserService;
import com.almerys.columbia.api.services.Utilities;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.HashSet;

@RestController
@RequestMapping("/users")
@Api(tags = "User Controller", description = "Controller to manipulate users")
public class  UserController extends AbstractRestController {

  private final UserService userService;
  private final ColumbiaConfiguration columbiaConfiguration;
  private final Utilities utilities;

  public UserController(UserService userService, Utilities utilities, ColumbiaConfiguration columbiaConfiguration) {
    this.columbiaConfiguration = columbiaConfiguration;
    this.userService = userService;
    this.utilities = utilities;
  }

  //---------- GET
  //Récupère les informations de l'utilisateur courant.
  @JsonView(View.DefaultDisplay.class)
  @GetMapping(value = "/{userId}")
  @ApiOperation(value = "Retrieve user information", authorizations = @Authorization(value = "Authentication", scopes = {}))
  public ResponseEntity<ColumbiaUser> getUser(@ApiParam(value = "Id of the user", required = true) @NotNull @PathVariable("userId") String userId, @ApiIgnore Authentication authentication) {

    if (!authentication.getCredentials().equals(userId) && !hasRole(authentication, columbiaConfiguration.getAdminRoleName())) {
      return forbidden();
    }

    ColumbiaUser columbiaUser = userService.getById(userId);


    return (columbiaUser == null)
        ? notFound()
        : ResponseEntity.ok(columbiaUser);
  }

  @JsonView(View.DefaultDisplay.class)
  @GetMapping(value = "/self")
  @ApiOperation(value = "Retrieve self information", authorizations = @Authorization(value = "Authentication", scopes = {}))
  public ResponseEntity<ColumbiaUser> getSelf(@ApiIgnore Authentication authentication) {

    String userId = (String) authentication.getCredentials();

    ColumbiaUser columbiaUser = userService.getById(userId);


    return (columbiaUser == null)
        ? notFound()
        : ResponseEntity.ok(columbiaUser);
  }

  //Récupère la liste des utilisateurs
  @JsonView(View.MinimalDisplay.class)
  @GetMapping(value = "")
  @ApiOperation(value = "Get all users", authorizations = @Authorization(value = "Authentication", scopes = {}))
  @ApiPageable
  public ResponseEntity getUserList(@ApiIgnore Pageable page, @ApiIgnore Authentication authentication) {
    if (hasRole(authentication, columbiaConfiguration.getAdminRoleName())) {
      return ResponseEntity.ok(userService.getAll(page));
    } else {
      return ResponseEntity.ok(userService.getById((String) (authentication.getCredentials())));
    }
  }

  //---------- PUT
  // Met à jour un utilisateur
  @PutMapping(value = "/{userId}")
  @ApiOperation(value = "Update user information", authorizations = @Authorization(value = "Authentication", scopes = {}))
  public ResponseEntity<Void> updateUser(@ApiParam(value = "User Id", required = true) @NotNull @PathVariable("userId") String userId,
      @NotNull @Validated @RequestBody UserUpdater userUpdater, @ApiIgnore Authentication authentication) {

    boolean isAdmin = hasRole(authentication, columbiaConfiguration.getAdminRoleName());

    if (!(authentication.getCredentials()).equals(userId) && !isAdmin) {
      return forbidden();
    }

    ColumbiaUser updatedColumbiaUser = userService.updateUser(userId, userUpdater, isAdmin);

    return (updatedColumbiaUser == null)
        ? badRequest()
        : ResponseEntity.ok().build();
  }

  //---------- DELETE
  // Supprime un utilisateur
  @DeleteMapping(value = "/{userId}")
  @ApiOperation(value = "Delete user", authorizations = @Authorization(value = "Authentication", scopes = {}))
  public ResponseEntity<Void> deleteUser(@ApiParam(value = "User ID", required = true) @NotNull @PathVariable("userId") String userId) {
    userService.delete(userId);

    return ResponseEntity.ok().build();
  }

  //----------- POST
  // Créer un utilisateur
  @PostMapping
  @ApiOperation(value = "Create a new user")
  public ResponseEntity<Void> createUser(@NotNull @Validated @RequestBody UserUpdater userUpdater,
                                         @ApiIgnore UriComponentsBuilder ucb,
                                         @ApiIgnore Authentication authentication) {
    ucb = ucb.scheme(utilities.getScheme());

    ColumbiaUser savedColumbiaUser;

    if ( (authentication == null || !authentication.isAuthenticated()) && columbiaConfiguration.getOpenRegistration()) {
      userUpdater.setDomain("local");
      userUpdater.setActiv(false);
      userUpdater.setRole(columbiaConfiguration.getUserRoleName());
      userUpdater.setGrantedContexts(new HashSet<>());
      savedColumbiaUser = userService.createUser(userUpdater);
    } else if (hasRole(authentication, columbiaConfiguration.getAdminRoleName())) {
      savedColumbiaUser = userService.createUser(userUpdater);
    } else {
      return forbidden();
    }


    return (savedColumbiaUser == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/user/{id}", savedColumbiaUser.getId())).build();
  }

}
