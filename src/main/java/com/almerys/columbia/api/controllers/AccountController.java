package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.AccountService;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api(tags = "Account Controller", description = "Main controller for account management")
@RestController
@RequestMapping("/accounts")
public class AccountController extends AbstractRestController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }


  //Active un utilisateur
  @ApiOperation(value = "Activate user through the token delivered by email")
  @PutMapping(value = "/activate/{token}")
  public ResponseEntity<Void> activateUser(@ApiParam(value = "Token from mail", required = true) @NotNull @PathVariable("token") String token) {
    accountService.activate(token);
    return ResponseEntity.ok().build();
  }

  //Mot de passe perdu
  @ApiOperation(value = "Send a reset-password link to people via email")
  @GetMapping(value = "/lostPassword/{user}")
  public ResponseEntity<Void> lostPassword(@ApiParam(value = "Username", required = true) @NotNull @PathVariable("user") String user) {
    accountService.lostPassword(user);
    return ResponseEntity.ok().build();
  }

  //Mise à jour mot de passe
  @ApiOperation(value = "Set new password through token sent by email")
  @PutMapping(value = "/lostPassword/{token}")
  public ResponseEntity<Void> updatePassword(@ApiParam(value = "Token from mail", required = true) @NotNull @PathVariable("token") String token,
                                             @ApiParam(value = "Here, only the \"password\" field is required (and it will be the only readed)", required = true) @NotNull @Validated @RequestBody UserUpdater user) {
    Assert.notNull(user.getPassword(), "password could not be null !");

    accountService.updatePassword(token, user);
    return ResponseEntity.ok().build();
  }


}
