package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.security.CustomUserDetails;
import com.almerys.columbia.api.services.ApplicationContextService;
import com.almerys.columbia.api.services.UserService;
import com.almerys.columbia.api.services.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaUserControllerTest {
  @Mock
  UserService userService;

  @Mock
  Utilities utilities;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  UserController controller;

  //Teste la récupération de la liste utilisateur
  @Test
  public void testGetAllUser() {
    ColumbiaUser user = new ColumbiaUser();
    user.setId("aaaa");
    user.setDomain("Almerys");
    user.setUsername("Bonjour");

    Authentication auth = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
      }

      @Override
      public Object getCredentials() {
        return null;
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return new CustomUserDetails(user);
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    when(userService.getById(any())).thenReturn(null);
    assertThat(controller.getUserList(PageRequest.of(0, 10), auth)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(userService.getById(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getUserList(PageRequest.of(0, 10), auth)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testGetAllUserAdmin() {

    Authentication auth = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("ADMIN"));
        return auths;
      }

      @Override
      public Object getCredentials() {
        return null;
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    when(userService.getAll(any())).thenReturn(null);
    assertThat(controller.getUserList(PageRequest.of(0, 10), auth)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(userService.getAll(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getUserList(PageRequest.of(0, 10), auth)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la suppression d'un utilisateur.
  @Test
  public void testDeleteUser() {
    doNothing().when(userService)
               .delete(any());
    assertThat(controller.deleteUser("ze")
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    doThrow(IllegalArgumentException.class).when(userService)
                                           .delete(any());
    assertThatThrownBy(() -> controller.deleteUser("adz")).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la création d'un utilisateur.
  @Test
  public void testCreateUser() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("Administrateur");
    when(columbiaConfiguration.getUserRoleName()).thenReturn("Utlisitateur");


    Authentication authentication = new Authentication() {

      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(columbiaConfiguration.getAdminRoleName());
        HashSet<SimpleGrantedAuthority> s = new HashSet<>();
        s.add(simpleGrantedAuthority);
        return s;
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return true;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(utilities.getScheme()).thenReturn("http");

    when(userService.createUser(any())).thenReturn(new ColumbiaUser());
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);


    when(userService.createUser(any())).thenReturn(null);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    Authentication authentication2 = new Authentication() {

      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(columbiaConfiguration.getUserRoleName());
        HashSet<SimpleGrantedAuthority> s = new HashSet<>();
        s.add(simpleGrantedAuthority);
        return s;
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return true;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    when(userService.createUser(any())).thenReturn(new ColumbiaUser());

    authentication2.setAuthenticated(false);
    when(columbiaConfiguration.getOpenRegistration()).thenReturn(false);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication2).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), null).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    when(columbiaConfiguration.getOpenRegistration()).thenReturn(true);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), null).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication2).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    authentication2.setAuthenticated(true);
    when(columbiaConfiguration.getOpenRegistration()).thenReturn(false);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication2).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), null).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    when(columbiaConfiguration.getOpenRegistration()).thenReturn(true);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), null).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication2).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    doThrow(IllegalArgumentException.class).when(userService).createUser(any());
    assertThatThrownBy(() -> controller.createUser(new UserUpdater(), UriComponentsBuilder.newInstance(), authentication)).isInstanceOf(
        IllegalArgumentException.class);

  }

  //Teste la récupération d'un utilisateur.
  @Test
  public void testGetUser() {
    Collection<GrantedAuthority> ga = new HashSet<>();
    ga.add(new SimpleGrantedAuthority("GLOSSATEUR"));

    Authentication authentication2 = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return ga;
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");
    when(userService.getById(any())).thenReturn(new ColumbiaUser());

    assertThat(controller.getUser("sfd", authentication2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(controller.getUser("a", authentication2)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
    grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));

    Authentication authentication = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return true;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    when(userService.getById(any())).thenReturn(new ColumbiaUser());
    assertThat(controller.getUser("zed", authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(userService.getById(any())).thenReturn(null);
    assertThat(controller.getUser("d", authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    doThrow(IllegalArgumentException.class).when(userService)
                                           .getById(any());
    assertThatThrownBy(() -> controller.getUser("sd", authentication)).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la récupération d'un utilisateur.
  @Test
  public void testGetSelf() {
    when(userService.getById(any())).thenReturn(new ColumbiaUser());

    Authentication authentication = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };
    assertThat(controller.getSelf(authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(userService.getById(any())).thenReturn(null);
    assertThat(controller.getSelf(authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    when(userService.getById(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getSelf(authentication)).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la mise à jour d'un utilisateur.
  @Test
  public void testUpdateUser() {
    Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
    grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));

    Authentication authentication = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };

    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    when(userService.updateUser(any(), any(), anyBoolean())).thenReturn(new ColumbiaUser());
    assertThat(controller.updateUser("sd", new UserUpdater(), authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    Authentication authentication2 = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
      }

      @Override
      public Object getCredentials() {
        return "a";
      }

      @Override
      public Object getDetails() {
        return null;
      }

      @Override
      public Object getPrincipal() {
        return null;
      }

      @Override
      public boolean isAuthenticated() {
        return false;
      }

      @Override
      public void setAuthenticated(boolean bool) throws IllegalArgumentException {
      }

      @Override
      public String getName() {
        return null;
      }
    };
    assertThat(controller.updateUser("a", new UserUpdater(), authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(userService.updateUser(any(), any(), anyBoolean())).thenReturn(null);
    assertThat(controller.updateUser("sd", new UserUpdater(), authentication)
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    doThrow(IllegalArgumentException.class).when(userService)
                                           .updateUser(any(), any(), anyBoolean());
    assertThatThrownBy(() -> controller.updateUser("sfd", new UserUpdater(), authentication)).isInstanceOf(IllegalArgumentException.class);

    assertThat(controller.updateUser("sfd", new UserUpdater(), authentication2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

  }
}
