package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.DefinitionUpdater;
import com.almerys.columbia.api.services.DefinitionService;
import com.almerys.columbia.api.services.Utilities;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
public class ColumbiaDefinitionControllerTest {
  @Mock
  DefinitionService definitionService;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @Mock
  Utilities utilities;

  @InjectMocks
  DefinitionController controller;

  //Teste la récupération de définition selon un terme et un contexte donné.
  @Test
  public void testGetTermDefinitionByContext() {
    //Doit retourner une 404 et vide
    when(definitionService.getByContextIdAndTermId(any(), any())).thenReturn(null);
    assertThat(controller.getTermDefinitionByContext(1L, 1L)
                         .getBody()).isNull();
    assertThat(controller.getTermDefinitionByContext(1L, 1L)
                         .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    //Doit retourner une 200
    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(definitionService.getByContextIdAndTermId(any(), any())).thenReturn(def);
    assertThat(controller.getTermDefinitionByContext(1L, 1L)
                         .getBody()).isEqualToComparingFieldByFieldRecursively(def);
    assertThat(controller.getTermDefinitionByContext(1L, 1L)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 400
    when(definitionService.getByContextIdAndTermId(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getTermDefinitionByContext(1L, 1L)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la suppression de définition selon un terme et un contexte donné.
  @Test
  public void testDeleteTermDefinitionByContext() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

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

    //Doit retourner une 200
    doNothing().when(definitionService)
               .deleteByTermIdAndContextId(any(), any());
    assertThat(controller.deleteTermDefinitionByContext(1L, 1L, auth)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Test user privileges
    Authentication auth2 = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("GLOSSATEUR"));
        auths.add(new SimpleGrantedAuthority("CONTEXT_1"));
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
    assertThat(controller.deleteTermDefinitionByContext(1L, 1L, auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(controller.deleteTermDefinitionByContext(2L, 1L, auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    //Doit retourner une 400
    doThrow(IllegalArgumentException.class).when(definitionService)
                                           .deleteByTermIdAndContextId(any(), any());
    assertThatThrownBy(() -> controller.deleteTermDefinitionByContext(1L, 1L, auth)).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la mise à jour de définition selon un terme et un contexte donné.
  @Test
  public void testUpdateDefinitionInContext() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

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

    //Doit retourner une 200
    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    DefinitionUpdater definitionUpdater = new DefinitionUpdater();

    when(definitionService.update(any(), any(), any())).thenReturn(def);
    assertThat(controller.updateDefinitionInContext(1L, 1L, definitionUpdater, auth)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Test user privileges
    Authentication auth2 = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("GLOSSATEUR"));
        auths.add(new SimpleGrantedAuthority("CONTEXT_1"));
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
    assertThat(controller.updateDefinitionInContext(1L, 1L, definitionUpdater, auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(controller.updateDefinitionInContext(2L, 1L, definitionUpdater, auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    //Doit retourner une 400
    when(definitionService.update(any(), any(), any())).thenReturn(null);
    assertThat(controller.updateDefinitionInContext(1L, 1L, definitionUpdater, auth)
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une 400
    when(definitionService.update(any(), any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.updateDefinitionInContext(1L, 1L, definitionUpdater, auth)).isInstanceOf(
        IllegalArgumentException.class);

  }

  //Teste la création de definition selon un terme et un contexte donné.
  @Test
  public void testCreateDefinitionInContext() {
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

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

    //Doit retourner une 200
    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    DefinitionUpdater definitionUpdater = new DefinitionUpdater();
    when(definitionService.create(any(), any())).thenReturn(def);

    //Test user privileges
    Authentication auth2 = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("GLOSSATEUR"));
        auths.add(new SimpleGrantedAuthority("CONTEXT_1"));
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


    assertThat(controller.createDefinitionInContext(1L, definitionUpdater, UriComponentsBuilder.newInstance(), auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);

    assertThat(controller.createDefinitionInContext(2L, definitionUpdater, UriComponentsBuilder.newInstance(), auth2)
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(controller.createDefinitionInContext(1L, definitionUpdater, UriComponentsBuilder.newInstance(), auth)
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);

    //Doit retourner une 400
    when(definitionService.create(any(), any())).thenReturn(null);
    assertThat(controller.createDefinitionInContext(1L, definitionUpdater, UriComponentsBuilder.newInstance(), auth)
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une 400
    when(definitionService.create(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(
        () -> controller.createDefinitionInContext(1L, definitionUpdater, UriComponentsBuilder.newInstance(), auth)).isInstanceOf(
        IllegalArgumentException.class);

  }

}
