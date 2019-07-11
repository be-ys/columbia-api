package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaAuthenticationControllerTest {
  @Mock
  AuthenticationService authenticationService;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  ColumbiaAuthenticationController columbiaAuthenticationController;

  //Teste la récupération de la liste utilisateur
  @Test
  public void testGetAllUser() {

    when(columbiaConfiguration.getDelegatedAuthentication()).thenReturn(true);

    assertThatThrownBy(() -> columbiaAuthenticationController.login("a", "b", null, null)).isInstanceOf(IllegalArgumentException.class);

    assertThat(columbiaAuthenticationController.login(null, "b", null, "local").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(columbiaAuthenticationController.login("a", null, null, "local").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(columbiaAuthenticationController.login(null, null, null, "local").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    when(authenticationService.getBearer(any(), any())).thenReturn("token");
    assertThat(columbiaAuthenticationController.login(null, null, "rawToken", "local").getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(columbiaAuthenticationController.login(null, null, "rawToken", "local").getHeaders().get("Authorization")).containsExactlyInAnyOrder("token");

    when(authenticationService.getBearer(any(), any(), any())).thenReturn("localtoken");
    assertThat(columbiaAuthenticationController.login("user", "pass", null, "local").getHeaders().get("Authorization")).containsExactlyInAnyOrder("localtoken");
    assertThat(columbiaAuthenticationController.login("user", "pass", "rawToken", "local").getHeaders().get("Authorization")).containsExactlyInAnyOrder("localtoken");


    when(columbiaConfiguration.getDelegatedAuthentication()).thenReturn(false);
    assertThat(columbiaAuthenticationController.login(null, null, "rawToken", "local").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

}
