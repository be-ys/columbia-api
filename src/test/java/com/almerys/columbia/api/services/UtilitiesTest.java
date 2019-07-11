package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtilitiesTest {
  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  Utilities utilities;

  @Test
  public void testGetScheme() {
    when(columbiaConfiguration.getEnforceHttps()).thenReturn(true);
    assertThat(utilities.getScheme()).isEqualTo("https");


    when(columbiaConfiguration.getEnforceHttps()).thenReturn(false);
    assertThat(utilities.getScheme()).isEqualTo("http");

  }

  @Test
  public void testCryptEmail() {
    when(columbiaConfiguration.getCryptPassword()).thenReturn("pass");
    String pass = utilities.cryptEmail("bonjour@localhost");

    assertThat(utilities.decryptEmail(pass)).isEqualTo("bonjour@localhost");
  }

  @Test
  public void testCryptPassword() {
    when(columbiaConfiguration.getCryptPower()).thenReturn(13);
    String pass = utilities.cryptPassword("coucou");

    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(columbiaConfiguration.getCryptPower());

    assertThat(bcrypt.matches("coucou", pass)).isEqualTo(true);
  }

  @Test
  public void testCheckPassword() {
    when(columbiaConfiguration.getCryptPower()).thenReturn(13);


    String password = "ours";
    String cypheredPassword = utilities.cryptPassword(password);

    assertThat(utilities.checkPassword(password, cypheredPassword)).isTrue();
    assertThat(utilities.checkPassword("ananas", cypheredPassword)).isFalse();

  }
}
