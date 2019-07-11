package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetupControllerTest {
  @Mock
  UserService userService;

  @InjectMocks
  SetupController setupController;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @Test
  public void testSetup() {
    when(userService.getAll(PageRequest.of(0, 2))).thenReturn(new PageImpl(new ArrayList()));
    when(userService.createUser(any())).thenReturn(new ColumbiaUser());

    assertThat(setupController.initialization()
                              .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Erreurs
    when(userService.createUser(any())).thenReturn(null);
    assertThat(setupController.initialization()
                              .getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    when(userService.createUser(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> setupController.initialization()
                                            .getStatusCode()).isInstanceOf(IllegalArgumentException.class);

    ArrayList users = new ArrayList();
    users.add(new ColumbiaUser());

    when(userService.getAll(PageRequest.of(0, 2))).thenReturn(new PageImpl(users));
    assertThat(setupController.initialization()
                              .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

  }

}
