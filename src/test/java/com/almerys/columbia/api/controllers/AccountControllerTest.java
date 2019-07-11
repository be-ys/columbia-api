package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.AccountService;
import com.almerys.columbia.api.services.ContextService;
import com.almerys.columbia.api.services.GlobalService;
import com.almerys.columbia.api.services.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {
  @Mock
  AccountService accountService;


  @InjectMocks
  AccountController accountController;

  //Teste l'activation des utilisateurs.
  @Test
  public void testActivateUser() {
    doNothing().when(accountService).activate(any());
    accountController.activateUser("token");

    doThrow(IllegalArgumentException.class).when(accountService).activate(any());
    assertThatThrownBy(() -> accountController.activateUser("token")).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la perte de mot de passe.
  @Test
  public void testLostPassword() {
    doNothing().when(accountService).lostPassword(any());
    accountController.lostPassword("token");

    doThrow(IllegalArgumentException.class).when(accountService).lostPassword(any());
    assertThatThrownBy(() -> accountController.lostPassword("token")).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste le changement de mdp.
  @Test
  public void testUpdatePassword() {
    UserUpdater userUpdater = new UserUpdater();
    userUpdater.setPassword("pass");

    doNothing().when(accountService).updatePassword(any(), any());
    accountController.updatePassword("token", userUpdater);

    assertThatThrownBy(() -> accountController.updatePassword("token", new UserUpdater())).isInstanceOf(IllegalArgumentException.class);


    doThrow(IllegalArgumentException.class).when(accountService).updatePassword(any(), any());
    assertThatThrownBy(() -> accountController.updatePassword("token", userUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

}
