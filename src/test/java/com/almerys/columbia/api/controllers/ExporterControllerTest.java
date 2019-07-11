package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.services.ExcelService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExporterControllerTest {
  @Mock
  ExcelService excelService;

  @InjectMocks
  ExporterController exporterController;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @Test
  public void testExport() {
    when(excelService.createExcel(any(), any())).thenReturn(new XSSFWorkbook());
    when(columbiaConfiguration.getAdminRoleName()).thenReturn("ADMIN");

    HttpServletResponse response = mock(HttpServletResponse.class);

    assertThat(exporterController.downloadExcelOutputExl(null, response, 1L)
                                 .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    HashSet<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("CONTEXT_1"));

    Authentication authentication = new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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

    assertThat(exporterController.downloadExcelOutputExl(authentication, response, 1L)
                                 .getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(exporterController.downloadExcelOutputExl(authentication, response, 2L)
                                 .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

  }

}
