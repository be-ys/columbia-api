package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.mailer.SendRegistrationMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaUserServiceTest {
  @Mock
  UserRepository userRepository;

  @Mock
  ContextService contextService;

  @InjectMocks
  UserService userService;

  @Mock
  Utilities utilities;

  @Mock
  SendRegistrationMail sendRegistrationMail;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @Test
  public void testGetAllByUsername() {
    assertThatThrownBy(() -> userService.getByUsernameAndDomain(null, null)).isInstanceOf(IllegalArgumentException.class);

    when(userRepository.findByUsernameAndDomain(any(), any())).thenReturn(Optional.empty());
    assertThat(userService.getByUsernameAndDomain("coucou", "local")).isNull();

    ColumbiaUser us = new ColumbiaUser();

    when(userRepository.findByUsernameAndDomain(any(), any())).thenReturn(Optional.of(us));
    when(userRepository.findById(anyString())).thenReturn(Optional.of(us));
    assertThat(userService.getByUsernameAndDomain("coucou", "local")).isEqualToComparingFieldByFieldRecursively(us);

  }

  @Test
  public void testGetById() {
    assertThatThrownBy(() -> userService.getById(null)).isInstanceOf(IllegalArgumentException.class);
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThat(userService.getById("ze")).isNull();

    ColumbiaUser columbiaUser = new ColumbiaUser();
    columbiaUser.setRole("fzed");

    when(userRepository.findById(anyString())).thenReturn(Optional.of(columbiaUser));
    assertThat(userService.getById("re")).isEqualToComparingFieldByFieldRecursively(columbiaUser);

    ColumbiaContext columbiaContextA = new ColumbiaContext(1L, "coucou", null, null);
    ColumbiaContext columbiaContextB = new ColumbiaContext(2L, "hello", null, null);
    Collection<ColumbiaContext> columbiaContexts = new HashSet<>();
    columbiaContexts.add(columbiaContextA);
    columbiaContexts.add(columbiaContextB);

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    when(contextService.getAll()).thenReturn(columbiaContexts);
    columbiaUser.setRole("ADMIN");
    columbiaUser.setGrantedContexts(columbiaContexts);
    when(userRepository.findById("fzeze")).thenReturn(Optional.of(columbiaUser));

    assertThat(userService.getById("fzeze")).isEqualToComparingFieldByFieldRecursively(columbiaUser);
  }

  @Test
  public void testDeleteById() {
    doNothing().when(userRepository)
               .deleteById(anyString());
    doNothing().when(userRepository)
               .destroyEnversHistory(anyString());
    ColumbiaUser user = new ColumbiaUser();
    user.setId("ezdza-edzzazd");
    user.setUsername("coucou");

    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> userService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    userService.delete("fzfe");

    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    userService.delete("fzfe");


  }

  @Test
  public void testGetAll() {

    Page<ColumbiaUser> users = new PageImpl<>(new ArrayList<>());
    when(userRepository.findAll((Pageable) any())).thenReturn(users);
    assertThat(userService.getAll(PageRequest.of(0, 10))).isNullOrEmpty();
    assertThat(userService.getAll(PageRequest.of(0, 10))).isNullOrEmpty();
    assertThat(userService.getAll(PageRequest.of(0, 10))).isNullOrEmpty();
  }

  @Test
  public void RemoveAllRightsFromSpecificContext() {
    when(contextService.getById(any())).thenReturn(new ColumbiaContext(1L, "coucou", null, null));

    //Construction des objets pour le test
    ColumbiaContext columbiaContextA = new ColumbiaContext(1L, "coucou", null, null);
    ColumbiaContext columbiaContextB = new ColumbiaContext(2L, "hello", null, null);
    Collection<ColumbiaContext> columbiaContexts = new HashSet<>();
    columbiaContexts.add(columbiaContextA);
    columbiaContexts.add(columbiaContextB);

    ColumbiaUser columbiaUserA = new ColumbiaUser();
    columbiaUserA.setGrantedContexts(columbiaContexts);
    Collection<ColumbiaUser> columbiaUsers = new HashSet<>();
    columbiaUsers.add(columbiaUserA);

    when(userRepository.findByGrantedContextsContains(any())).thenReturn(columbiaUsers);
    when(userRepository.saveAll(any())).thenReturn(new ArrayList<>());

    //Doit passer
    userService.removeAllRightsFromSpecificContext(1L);

    //Doit planter
    when(contextService.isParent(any())).thenReturn(true);
    assertThatThrownBy(() -> userService.removeAllRightsFromSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> userService.removeAllRightsFromSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> userService.removeAllRightsFromSpecificContext(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testCreateUser() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");
    when(columbiaConfiguration.getModeratorRoleName()).thenReturn("GLOSSATEUR");
    when(columbiaConfiguration.getUserRoleName()).thenReturn("USER");

    //Création qui doit fonctionner.
    UserUpdater userUpdater = new UserUpdater();
    userUpdater.setUsername("columbialocal_essai");
    userUpdater.setRole("ADMIN");
    userUpdater.setEmail("hello@almerys.com");
    userUpdater.setPassword("aaaa");
    userUpdater.setDomain("local");

    doNothing().when(sendRegistrationMail).prepareAndSend(any());

    Collection<ContextUpdater> contexts = new HashSet<>();
    contexts.add(new ContextUpdater(4L, "coucou", null, null));
    contexts.add(new ContextUpdater(5L, "coucou2", null, null));
    userUpdater.setGrantedContexts(contexts);

    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(contextService.getById(any())).thenReturn(new ColumbiaContext(1L, "coucou", null, null));

    //Doit passer
    userService.createUser(userUpdater);
    userUpdater.setGrantedContexts(null);
    userUpdater.setActiv(false);
    userService.createUser(userUpdater);
    userUpdater.setGrantedContexts(contexts);

    userUpdater.setActiv(true);
    userUpdater.setRole("GLOSSATEUR");
    userService.createUser(userUpdater);

    userUpdater.setRole("USER");
    userService.createUser(userUpdater);

    //Doit planter.
    //1 - Contexte inconnu
    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> userService.createUser(userUpdater)).isInstanceOf(IllegalArgumentException.class);

    //2 - Mauvais rôle
    userUpdater.setRole("FAIL");
    assertThatThrownBy(() -> userService.createUser(userUpdater)).isInstanceOf(IllegalArgumentException.class);

    //3 - Mais déjà utilisé
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(new ColumbiaUser()));
    assertThatThrownBy(() -> userService.createUser(userUpdater)).isInstanceOf(IllegalArgumentException.class);

    //4 - Pseudo déjà utilisé
    when(userRepository.findByUsername(any())).thenReturn(Optional.of(new ColumbiaUser()));
    assertThatThrownBy(() -> userService.createUser(userUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testCreateUserDelegated() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    UserUpdater userUpdater = new UserUpdater();
    userUpdater.setUsername("ldapaccount");
    userUpdater.setRole("ADMIN");
    userUpdater.setEmail("hello@almerys.com");
    userUpdater.setPassword("aaaa");
    userUpdater.setDomain("almerys");

    userService.createUser(userUpdater);

  }

  @Test
  public void testUpdateUser() {

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");
    when(columbiaConfiguration.getModeratorRoleName()).thenReturn("GLOSSATEUR");
    when(columbiaConfiguration.getUserRoleName()).thenReturn("USER");

    when(utilities.cryptEmail(any())).thenReturn("cyphered_mail");
    when(utilities.cryptPassword(any())).thenReturn("cyphered_pass");

    UserUpdater userUpdater = new UserUpdater();
    userUpdater.setUsername("nouveau");
    userUpdater.setRole("GLOSSATEUR");
    userUpdater.setPassword("aaaa");
    userUpdater.setDomain("local");
    userUpdater.setEmail("coucou@almerys.com");

    Collection<ContextUpdater> contexts = new HashSet<>();
    contexts.add(new ContextUpdater(4L, "coucou", null, null));
    contexts.add(new ContextUpdater(5L, "coucou2", null, null));
    userUpdater.setGrantedContexts(contexts);

    ColumbiaUser user = new ColumbiaUser();
    user.setDomain("local");

    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, null));
    when(userRepository.save(any())).thenReturn(new ColumbiaUser());

    //doit passer
    userUpdater.setActiv(true);
    userService.updateUser("f", userUpdater, true);
    userUpdater.setGrantedContexts(null);

    userUpdater.setActiv(false);
    userUpdater.setRole("ADMIN");
    userService.updateUser("f", userUpdater, true);

    userUpdater.setActiv(null);
    userUpdater.setRole("USER");
    userService.updateUser("f", userUpdater, true);

    userUpdater.setRole(null);
    userService.updateUser("f", userUpdater, true);

    userUpdater.setGrantedContexts(contexts);

    userUpdater.setRole("Echec");
    userService.updateUser("f", userUpdater, true);
    userUpdater.setRole("USER");

    userUpdater.setUsername(null);
    userService.updateUser("f", userUpdater, true);

    userUpdater.setUsername("");
    userService.updateUser("f", userUpdater, true);
    userUpdater.setUsername("nouveau");

    userUpdater.setPassword(null);
    userService.updateUser("f", userUpdater, true);
    userUpdater.setPassword("");
    userService.updateUser("f", userUpdater, true);
    userUpdater.setPassword("aaaaa");


    user.setDomain("aa");
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

    userUpdater.setEmail("bonjour@localhost.com");
    userUpdater.setPassword("bonjour");
    userUpdater.setDomain("fieur");
    userService.updateUser("f", userUpdater, true);

    userUpdater.setEmail(null);
    userUpdater.setPassword(null);
    userService.updateUser("f", userUpdater, true);



    user.setDomain("local");
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));


    userService.updateUser("f", userUpdater, true);
    user.setDomain("local");

    userUpdater.setEmail(null);
    userService.updateUser("f", userUpdater, true);
    userUpdater.setEmail("");
    userService.updateUser("f", userUpdater, true);
    userUpdater.setEmail("bonjour@localhost.com");

    //Tout les fails.

    //1 - Admin et context inconnu
    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> userService.updateUser("sfd", userUpdater, true)).isInstanceOf(IllegalArgumentException.class);
    userService.updateUser("sfd", userUpdater, false);

    //2 - Mail déjà utilisé
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(new ColumbiaUser()));
    assertThatThrownBy(() -> userService.updateUser("sfd", userUpdater, false)).isInstanceOf(IllegalArgumentException.class);

    //3 - Username déjà utilisé
    when(userRepository.findByUsername(any())).thenReturn(Optional.of(new ColumbiaUser()));
    assertThatThrownBy(() -> userService.updateUser("sfd", userUpdater, true)).isInstanceOf(IllegalArgumentException.class);

    //4 - Utilisateur inexistant
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.updateUser("sfd", userUpdater, false)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void testUpdateLogin() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.updateLastLoginDate("a")).isInstanceOf(IllegalArgumentException.class);
    when(userRepository.findById(anyString())).thenReturn(Optional.of(new ColumbiaUser()));
    userService.updateLastLoginDate("a");
  }

  @Test
  public void testUserCrontask() {
    long timestamp = new Date().getTime();
    long msInYear = 1000L * 60L * 60L * 24L * 365L;
    timestamp = timestamp - msInYear;

    ColumbiaUser user1 = new ColumbiaUser();
    user1.setId("o-delete");
    user1.setUsername("o-delete");
    user1.setLastLogin(new Date());
    user1.setRole("User");

    ColumbiaUser user2 = new ColumbiaUser();
    user2.setId("to-delete");
    user2.setUsername("to-delete");
    user2.setRole("Admin");
    user2.setLastLogin(new Date(500));

    Collection<ColumbiaUser> columbiaUsers = new HashSet<>();
    columbiaUsers.add(user1);
    columbiaUsers.add(user2);

    when(userRepository.findAllByLastLoginBefore(any())).thenReturn(columbiaUsers);
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("Admin");
    doNothing().when(userRepository).destroyEnversHistory(any());
    doNothing().when(userRepository).deleteById(anyString());

    userService.deleteOldUsers();

  }



  @Test
  public void testDeleteNeverLoggedCrontask() {
    ColumbiaUser user1 = new ColumbiaUser();
    user1.setId("o-delete");
    user1.setUsername("o-delete");
    user1.setActiv(false);
    user1.setLastLogin(new Date());

    ColumbiaUser user2 = new ColumbiaUser();
    user2.setId("to-delete");
    user2.setActiv(true);
    user2.setUsername("to-delete");
    user2.setLastLogin(new Date(500));

    Collection<ColumbiaUser> columbiaUsers = new HashSet<>();
    columbiaUsers.add(user1);
    columbiaUsers.add(user2);

    when(userRepository.findAllByLastLoginIsNull()).thenReturn(columbiaUsers);

    doNothing().when(userRepository).destroyEnversHistory(any());
    doNothing().when(userRepository).deleteById(anyString());

    userService.deleteNeverActivatedUsers();

    doThrow(IllegalArgumentException.class).when(userRepository).deleteById("o-delete");
    assertThatThrownBy(() -> userService.deleteNeverActivatedUsers()).isInstanceOf(IllegalArgumentException.class);
  }
}
