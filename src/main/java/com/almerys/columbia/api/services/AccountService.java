package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.services.mailer.SendLostPasswordMail;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AccountService {
  private final UserRepository userRepository;
  private final SendLostPasswordMail sendLostPasswordMail;
  private final Utilities utilities;

  public AccountService(UserRepository userRepository, SendLostPasswordMail sendLostPasswordMail, Utilities utilities) {
    this.userRepository = userRepository;
    this.utilities = utilities;
    this.sendLostPasswordMail = sendLostPasswordMail;
  }

  public void activate(String token) {
    ColumbiaUser columbiaUser = userRepository.findByActivationKey(token).orElseThrow(IllegalArgumentException::new);
    columbiaUser.setActiv(true);
    columbiaUser.setActivationKey(null);
    userRepository.save(columbiaUser);
  }

  public void lostPassword(String user) {
    ColumbiaUser columbiaUser = userRepository.findByUsernameAndDomain(user, "local").orElseThrow(IllegalArgumentException::new);
    if (!Utilities.isEmptyOrNull(columbiaUser.getActivationKey())) {
      throw new IllegalArgumentException("User is already pending for activation or password reset.");
    }

    columbiaUser.setActivationKey(RandomStringUtils.randomAlphanumeric(50));
    userRepository.save(columbiaUser);
    sendLostPasswordMail.prepareAndSend(columbiaUser);
  }

  public void updatePassword(String token, UserUpdater user) {
    Assert.notNull(user.getPassword(), "user password could not be null");
    Assert.notNull(token, "token could not be null");

    ColumbiaUser columbiaUser = userRepository.findByActivationKey(token).orElseThrow(IllegalArgumentException::new);
    columbiaUser.setPassword(utilities.cryptPassword(user.getPassword()));
    columbiaUser.setActivationKey(null);
    userRepository.save(columbiaUser);

  }

}

