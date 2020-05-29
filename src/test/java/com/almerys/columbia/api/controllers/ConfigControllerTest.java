package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.dto.DTOConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigControllerTest {
  @Mock
  ColumbiaConfiguration columbiaConfiguration;


  @InjectMocks
  ConfigController configController;

  @Test
  public void testConfigController() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("Admin");
    when(columbiaConfiguration.getModeratorRoleName()).thenReturn("Moderator");
    when(columbiaConfiguration.getUserRoleName()).thenReturn("User");
    when(columbiaConfiguration.getMaxContextLevel()).thenReturn(3);
    when(columbiaConfiguration.getDelegatedAuthentication()).thenReturn(true);
    when(columbiaConfiguration.getOpenRegistration()).thenReturn(true);

    assertThat(configController.getConfiguration().getStatusCode()).isEqualTo(HttpStatus.OK);

    DTOConfig config = new DTOConfig();

    config.adminRole = columbiaConfiguration.getAdminRoleName();
    config.moderatorRole = columbiaConfiguration.getModeratorRoleName();
    config.userRole = columbiaConfiguration.getUserRoleName();
    config.maxContextLevel = columbiaConfiguration.getMaxContextLevel();
    config.delegatedAuth = columbiaConfiguration.getDelegatedAuthentication();
    config.openRegistration = columbiaConfiguration.getOpenRegistration();

    assertThat(configController.getConfiguration().getBody()).isEqualToComparingFieldByFieldRecursively(config);

  }

}
