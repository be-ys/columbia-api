package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.mailer.SendRegistrationMail;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Service
public class UserService {

  private final ContextService contextService;
  private final UserRepository userRepository;
  private final ColumbiaConfiguration columbiaConfiguration;
  private final SendRegistrationMail sendRegistrationMail;
  private final Utilities utilities;

  public UserService(ContextService contextService, UserRepository userRepository, ColumbiaConfiguration columbiaConfiguration,
      Utilities utilities, SendRegistrationMail sendRegistrationMail) {
    this.contextService = contextService;
    this.userRepository = userRepository;
    this.sendRegistrationMail = sendRegistrationMail;
    this.columbiaConfiguration = columbiaConfiguration;
    this.utilities = utilities;
  }

  public void removeAllRightsFromSpecificContext(Long contextId) {
    Assert.notNull(contextId, "contextId cannot be null");

    ColumbiaContext columbiaContextToDelete = contextService.getById(contextId);

    if (columbiaContextToDelete == null) {
      throw new IllegalArgumentException("ColumbiaContext does not exists");
    }

    if (contextService.isParent(contextId)) {
      throw new IllegalArgumentException("ColumbiaContext is parent, cannot delete.");
    }

    Iterable<ColumbiaUser> users = userRepository.findByGrantedContextsContains(columbiaContextToDelete);

    users.forEach(a -> {
      Collection<ColumbiaContext> old = a.getGrantedContexts();
      old.remove(columbiaContextToDelete);
      a.setGrantedContexts(old);
    });

    userRepository.saveAll(users);
  }

  public void updateLastLoginDate(String userId) {
    ColumbiaUser user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      throw new IllegalArgumentException("User does not exists.");
    }
    user.setLastLogin(new Date());
    userRepository.save(user);
  }

  public ColumbiaUser getById(String userId) {
    Assert.notNull(userId, "userId cannot be null");
    ColumbiaUser user = userRepository.findById(userId).orElse(null);

    if (user != null) {
      updateLastLoginDate(userId);
      user.setEmail(utilities.decryptEmail(user.getEmail()));
      if (user.getRole().equals(columbiaConfiguration.getAdminRoleName())) {
        ArrayList<ColumbiaContext> list = new ArrayList<>();
        contextService.getAll().forEach(list::add);
        user.setGrantedContexts(list);
      }
    }

    return user;
  }

  public ColumbiaUser getByUsernameAndDomain(String username, String domain) {
    Assert.notNull(username, "username cannot be empty");
    Assert.notNull(domain, "domain cannot be empty");

    ColumbiaUser user = userRepository.findByUsernameAndDomain(username, domain).orElse(null);
    if (user != null) {
      updateLastLoginDate(user.getId());
    }
    return user;
  }

  public Page getAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Transactional
  public void delete(String userId) {
    Assert.notNull(userId, "userId cannot be null");
    ColumbiaUser user = userRepository.findById(userId).orElse(null);
    if (user != null) {
      userRepository.destroyEnversHistory(user.getUsername());
      userRepository.deleteById(userId);
    }
  }

  public ColumbiaUser createUser(UserUpdater userUpdater) {
    Assert.notNull(userUpdater, "userUpdater could not be null");
    Assert.notNull(userUpdater.getUsername(), "userUpdater username could not be null");
    Assert.notNull(userUpdater.getDomain(), "userUpdater domain could not be null");
    Assert.notNull(userUpdater.getRole(), "userUpdater role could not be null");

    if (userUpdater.getDomain().equalsIgnoreCase("local")) {
      Assert.notNull(userUpdater.getPassword(), "userUpdater password could not be null");
      Assert.notNull(userUpdater.getEmail(), "userUpdater email could not be null");
    }

    if (userRepository.findByUsername(userUpdater.getUsername()).isPresent()) {
      throw new IllegalArgumentException("New username already exist in database");
    }

    if (userUpdater.getDomain().equalsIgnoreCase("local") && userRepository.findByEmail(utilities.cryptEmail(userUpdater.getEmail())).isPresent()) {
      throw new IllegalArgumentException("Email is already used.");
    }

    if (!userUpdater.getRole().equals(columbiaConfiguration.getAdminRoleName())
        && !userUpdater.getRole().equals(columbiaConfiguration.getModeratorRoleName())
        && !userUpdater.getRole().equals(columbiaConfiguration.getUserRoleName())) {
      throw new IllegalArgumentException("Invalid role.");
    }

    ColumbiaUser columbiaUser = new ColumbiaUser();
    columbiaUser.setDomain(userUpdater.getDomain().toLowerCase());

    if (userUpdater.getDomain().equalsIgnoreCase("local")) {
      columbiaUser.setPassword(utilities.cryptPassword(userUpdater.getPassword()));
      columbiaUser.setEmail(utilities.cryptEmail(userUpdater.getEmail()));
    }
    columbiaUser.setUsername(userUpdater.getUsername());
    columbiaUser.setRole(userUpdater.getRole());

    if (userUpdater.getGrantedContexts() != null) {
      Collection<ColumbiaContext> grantedColumbiaContexts = new HashSet<>();
      userUpdater.getGrantedContexts().forEach(e -> {
        if (contextService.getById(e.getId()) == null) {
          throw new IllegalArgumentException("ColumbiaContext " + e.getId() + " does not exists");
        }
        grantedColumbiaContexts.add(contextService.getById(e.getId()));
      });
      columbiaUser.setGrantedContexts(grantedColumbiaContexts);
    }

    columbiaUser.setActiv((userUpdater.getActiv() == null) ? Boolean.FALSE : userUpdater.getActiv());

    if (userUpdater.getActiv() == null || !userUpdater.getActiv()) {
      columbiaUser.setActivationKey(RandomStringUtils.randomAlphanumeric(50));
    }

    ColumbiaUser finalUser =  userRepository.save(columbiaUser);
    sendRegistrationMail.prepareAndSend(finalUser);
    return finalUser;
  }

  public ColumbiaUser updateUser(String userId, UserUpdater userUpdater, boolean isAdmin) {
    Assert.notNull(userId, "userId cannot be null");
    Assert.notNull(userUpdater, "userUpdater cannot be null");

    ColumbiaUser updatingColumbiaUser = userRepository.findById(userId).orElse(null);

    if (updatingColumbiaUser == null) {
      throw new IllegalArgumentException("ColumbiaUser does not exist.");
    }

    //Mise à jour des champs réservés aux admins : username, contextes, rôle
    if (isAdmin) {

      if (userUpdater.getActiv() != null) {
        updatingColumbiaUser.setActiv(userUpdater.getActiv());
        if (!userUpdater.getActiv()) {
          updatingColumbiaUser.setActivationKey(RandomStringUtils.randomAlphanumeric(50));
        }
      }

      //Pseudo
      if (!Utilities.isEmptyOrNull(userUpdater.getUsername())) {
        if (userRepository.findByUsername(userUpdater.getUsername()).isPresent()) {
          throw new IllegalArgumentException("New username already exist in database");
        }
        updatingColumbiaUser.setUsername(userUpdater.getUsername());
      }

      //Rôle
      if (userUpdater.getRole() != null
          && (userUpdater.getRole().equals(columbiaConfiguration.getAdminRoleName())
              || userUpdater.getRole().equals(columbiaConfiguration.getModeratorRoleName())
              || userUpdater.getRole().equals(columbiaConfiguration.getUserRoleName()))) {
        updatingColumbiaUser.setRole(userUpdater.getRole());
      }

      //Contextes
      if (userUpdater.getGrantedContexts() != null) {
        Collection<ColumbiaContext> grantedColumbiaContexts = new HashSet<>();
        userUpdater.getGrantedContexts().forEach(e -> {
          if (contextService.getById(e.getId()) == null) {
            throw new IllegalArgumentException("ColumbiaContext " + e.getId() + " does not exists");
          }
          grantedColumbiaContexts.add(contextService.getById(e.getId()));
        });
        updatingColumbiaUser.setGrantedContexts(grantedColumbiaContexts);
      }

    }

    //Mise à jour des champs modifiables par l'utilisateur : Mot de passe, email.
    if (!Utilities.isEmptyOrNull(userUpdater.getEmail()) && updatingColumbiaUser.getDomain().equals("local")) {
      if (userRepository.findByEmail(utilities.cryptEmail(userUpdater.getEmail())).isPresent()) {
        throw new IllegalArgumentException("Email is already used.");
      }

      updatingColumbiaUser.setEmail(utilities.cryptEmail(userUpdater.getEmail()));
    }

    if (!Utilities.isEmptyOrNull(userUpdater.getPassword())  && updatingColumbiaUser.getDomain().equals("local")) {
      updatingColumbiaUser.setPassword(utilities.cryptPassword(userUpdater.getPassword()));
    }

    return userRepository.save(updatingColumbiaUser);
  }


  //Cron de suppression des utilisateurs inactifs depuis > 12 mois.
  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void deleteOldUsers() {
    long timestamp = new Date().getTime();
    long msInYear = 1000L * 60L * 60L * 24L * 365L;
    timestamp = timestamp - msInYear;
    Iterable<ColumbiaUser> users = userRepository.findAllByLastLoginBefore(new Date(timestamp));
    for (ColumbiaUser user: users) {
      if (user.getRole().equalsIgnoreCase(columbiaConfiguration.getAdminRoleName())) {
        userRepository.destroyEnversHistory(user.getUsername());
        userRepository.deleteById(user.getId());
      }
    }
  }


  //Cron de suppression des utilisateurs jamais activés.
  @Transactional
  @Scheduled(cron = "0 0 4 * * 0")
  public void deleteNeverActivatedUsers() {
    Iterable<ColumbiaUser> users = userRepository.findAllByLastLoginIsNull();
    for (ColumbiaUser user: users) {
      if (!user.getActiv()) {
        userRepository.destroyEnversHistory(user.getUsername());
        userRepository.deleteById(user.getId());
      }
    }
  }
}
