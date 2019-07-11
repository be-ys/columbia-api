package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.services.UserService;
import com.almerys.columbia.api.services.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

  @InjectMocks
  AuthenticationService authenticationService;

  @Mock
  Utilities utilities;

  @Mock
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  //Teste la récupération de la liste utilisateur
  @Test
  public void testLocalLogin() {

    //Bad domain
    assertThatThrownBy(() -> authenticationService.getBearer("a", "b", "dist")).isInstanceOf(IllegalArgumentException.class);

    //User not found
    when(userService.getByUsernameAndDomain(any(), any())).thenReturn(null);
    assertThatThrownBy(() -> authenticationService.getBearer("a", "b", "local")).isInstanceOf(IllegalArgumentException.class);

    //Bad password
    ColumbiaUser user = new ColumbiaUser();
    user.setUsername("test");
    user.setRole("ADMIN");
    user.setActiv(true);


    when(userService.getByUsernameAndDomain(any(), any())).thenReturn(user);
    when(utilities.checkPassword(any(), any())).thenReturn(false);
    assertThatThrownBy(() -> authenticationService.getBearer("a", "b", "local")).isInstanceOf(IllegalArgumentException.class);

    //Must login
    when(columbiaConfiguration.getTokenSecret()).thenReturn("veryLongKeyForTesting");

    when(utilities.checkPassword(any(), any())).thenReturn(true);
    authenticationService.getBearer("a", "b", "local");




  }
}
