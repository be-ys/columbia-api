package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.AccountService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/accounts")
public class AccountController extends AbstractRestController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  //Active un utilisateur
  @PutMapping(value = "/activate/{token}")
  public ResponseEntity activateUser(@NotNull @PathVariable("token") String token) {
    accountService.activate(token);
    return ResponseEntity.ok().build();
  }

  //Mot de passe perdu
  @GetMapping(value = "/lostPassword/{user}")
  public ResponseEntity lostPassword(@NotNull @PathVariable("user") String user) {
    accountService.lostPassword(user);
    return ResponseEntity.ok().build();
  }

  //Mise à jour mot de passe
  @PutMapping(value = "/lostPassword/{token}")
  public ResponseEntity updatePassword(@NotNull @PathVariable("token") String token, @NotNull @Validated @RequestBody UserUpdater user) {
    Assert.notNull(user.getPassword(), "password could not be null !");

    accountService.updatePassword(token, user);
    return ResponseEntity.ok().build();
  }


}
