package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.View;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.UserService;
import com.almerys.columbia.api.services.Utilities;
import com.fasterxml.jackson.annotation.JsonView;
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

import javax.validation.constraints.NotNull;
import java.util.HashSet;

@RestController
@RequestMapping("/users")
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
  public ResponseEntity getUser(@NotNull @PathVariable("userId") String userId, Authentication authentication) {

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
  public ResponseEntity getSelf(Authentication authentication) {

    String userId = (String) authentication.getCredentials();

    ColumbiaUser columbiaUser = userService.getById(userId);


    return (columbiaUser == null)
        ? notFound()
        : ResponseEntity.ok(columbiaUser);
  }

  //Récupère la liste des utilisateurs
  @JsonView(View.MinimalDisplay.class)
  @GetMapping(value = "")
  public ResponseEntity getUserList(Pageable page, Authentication authentication) {
    if (hasRole(authentication, columbiaConfiguration.getAdminRoleName())) {
      return ResponseEntity.ok(userService.getAll(page));
    } else {
      return ResponseEntity.ok(userService.getById((String) (authentication.getCredentials())));
    }
  }

  //---------- PUT
  // Met à jour un utilisateur
  @PutMapping(value = "/{userId}")
  public ResponseEntity updateUser(@NotNull @PathVariable("userId") String userId,
      @NotNull @Validated @RequestBody UserUpdater userUpdater, Authentication authentication) {

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
  public ResponseEntity<Void> deleteUser(@NotNull @PathVariable("userId") String userId) {
    userService.delete(userId);

    return ResponseEntity.ok().build();
  }

  //----------- POST
  // Créer un utilisateur
  @PostMapping
  public ResponseEntity createUser(@NotNull @Validated @RequestBody UserUpdater userUpdater, UriComponentsBuilder ucb, Authentication authentication) {
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
