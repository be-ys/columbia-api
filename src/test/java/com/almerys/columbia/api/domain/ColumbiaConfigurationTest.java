package com.almerys.columbia.api.domain;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaConfigurationTest {

  @Test
  public void testColumbiaConfiguration() {
    ColumbiaConfiguration columbiaConfiguration = new ColumbiaConfiguration();

    columbiaConfiguration.setMaxContextLevel(3);
    columbiaConfiguration.setCryptPower(13);
    columbiaConfiguration.setCryptPassword("pass");
    columbiaConfiguration.setFrontURL("http://localhost/");
    columbiaConfiguration.setTokenLifetime(3600);
    columbiaConfiguration.setTokenPrefix("Bearer ");
    columbiaConfiguration.setTokenSecret("Secret");
    columbiaConfiguration.setDelegatedAuthentication(true);
    columbiaConfiguration.setOauth2UserinfoUrl("https://oauth/");
    columbiaConfiguration.setEnforceHttps(true);
    columbiaConfiguration.setOpenRegistration(false);

    assertThat(columbiaConfiguration.getAdminRoleName()).isEqualTo("ADMIN");
    assertThat(columbiaConfiguration.getModeratorRoleName()).isEqualTo("GLOSSATEUR");
    assertThat(columbiaConfiguration.getUserRoleName()).isEqualTo("UTILISATEUR");

    assertThat(columbiaConfiguration.getMaxContextLevel()).isEqualTo(3);
    assertThat(columbiaConfiguration.getCryptPower()).isEqualTo(13);
    assertThat(columbiaConfiguration.getCryptPassword()).isEqualTo("pass");
    assertThat(columbiaConfiguration.getFrontURL()).isEqualTo("http://localhost/");
    assertThat(columbiaConfiguration.getTokenLifetime()).isEqualTo(3600);
    assertThat(columbiaConfiguration.getTokenPrefix()).isEqualTo("Bearer ");
    assertThat(columbiaConfiguration.getTokenSecret()).isEqualTo("Secret");
    assertThat(columbiaConfiguration.getDelegatedAuthentication()).isEqualTo(true);
    assertThat(columbiaConfiguration.getOauth2UserinfoUrl()).isEqualTo("https://oauth/");
    assertThat(columbiaConfiguration.getEnforceHttps()).isEqualTo(true);
    assertThat(columbiaConfiguration.getOpenRegistration()).isEqualTo(false);


    columbiaConfiguration.setOpenRegistration(null);
    assertThat(columbiaConfiguration.getOpenRegistration()).isEqualTo(false);

    columbiaConfiguration.setDelegatedAuthentication(null);
    assertThat(columbiaConfiguration.getDelegatedAuthentication()).isEqualTo(false);


  }

}
