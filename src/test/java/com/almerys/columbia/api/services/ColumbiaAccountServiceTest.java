package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.services.mailer.SendLostPasswordMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaAccountServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  SendLostPasswordMail sendLostPasswordMail;

  @Mock
  Utilities utilities;

  @InjectMocks
  AccountService service;

  @Test
  public void testActivate() {
    //Doit échouer
    when(userRepository.findByActivationKey(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.activate("token")).isInstanceOf(IllegalArgumentException.class);

    //Doit passer
    when(userRepository.save(any())).thenReturn(new ColumbiaUser());
    when(userRepository.findByActivationKey(any())).thenReturn(Optional.of(new ColumbiaUser()));
    service.activate("token");
  }

  @Test
  public void testLostPassword() {
    when(userRepository.findByUsernameAndDomain(any(), any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.lostPassword("user")).isInstanceOf(IllegalArgumentException.class);

    ColumbiaUser columbiaUser = new ColumbiaUser();
    columbiaUser.setActivationKey("activ");

    when(userRepository.findByUsernameAndDomain(any(), any())).thenReturn(Optional.of(columbiaUser));
    assertThatThrownBy(() -> service.lostPassword("user")).isInstanceOf(IllegalArgumentException.class);

    columbiaUser.setActivationKey(null);
    service.lostPassword("user");
  }

  @Test
  public void testUpdatePassword() {
    assertThatThrownBy(() -> service.updatePassword(null, new UserUpdater())).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.updatePassword("token", new UserUpdater())).isInstanceOf(IllegalArgumentException.class);


    UserUpdater columbiaUser = new UserUpdater();
    columbiaUser.setPassword("pass");

    when(userRepository.save(any())).thenReturn(new ColumbiaUser());
    when(userRepository.findByActivationKey(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.updatePassword("token", columbiaUser)).isInstanceOf(IllegalArgumentException.class);

    when(userRepository.findByActivationKey(any())).thenReturn(Optional.of(new ColumbiaUser()));
    service.updatePassword("token", columbiaUser);
  }

}
