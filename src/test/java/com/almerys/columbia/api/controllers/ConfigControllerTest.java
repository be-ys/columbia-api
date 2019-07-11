package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
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

    Map<String, Object> content = new HashMap<>();

    content.put("adminRole", "Admin");
    content.put("moderatorRole", "Moderator");
    content.put("userRole", "User");
    content.put("maxContextLevel", 3);
    content.put("delegatedAuth", true);
    content.put("openRegistration", true);

    assertThat(configController.getConfiguration().getBody()).isEqualToComparingFieldByFieldRecursively(content);

  }

}
