package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlobalServiceTest {
  @Mock
  ContextService contextService;

  @Mock
  DefinitionService definitionService;

  @Mock
  NewsletterService newsletterService;

  @Mock
  UserService userService;

  @Mock
  TermService termService;

  @InjectMocks
  GlobalService globalService;

  @Test
  public void testDeleteContext() {
    when(contextService.getById(any())).thenReturn(new ColumbiaContext());
    doNothing().when(userService)
               .removeAllRightsFromSpecificContext(any());
    doNothing().when(definitionService)
               .deleteAllByContextId(any());
    doNothing().when(newsletterService)
               .removeAllNewsletterFromSpecificContext(any());
    doNothing().when(contextService)
               .delete(any());

    globalService.deleteContext(4L);

    when(contextService.isParent(any())).thenReturn(true);
    assertThatThrownBy(() -> globalService.deleteContext(4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> globalService.deleteContext(4L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testDeleteTerm() {
    when(termService.getById(any())).thenReturn(new ColumbiaTerm());
    doNothing().when(definitionService)
               .deleteAllByTermId(any());
    doNothing().when(termService)
               .delete(any());

    globalService.deleteTerm(4L);

    when(termService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> globalService.deleteTerm(4L)).isInstanceOf(IllegalArgumentException.class);

  }
}
