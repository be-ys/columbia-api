package com.almerys.columbia.api.services;

import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatsServiceTest {
  @Mock
  UserRepository userRepository;

  @Mock
  TermRepository termRepository;

  @Mock
  ContextRepository contextRepository;

  @Mock
  DefinitionRepository definitionRepository;

  @InjectMocks
  StatsService statsService;

  @Test
  public void testGetStats() {
    when(userRepository.count()).thenReturn(4L);
    when(termRepository.count()).thenReturn(12L);
    when(contextRepository.count()).thenReturn(6L);
    when(definitionRepository.count()).thenReturn(5L);
    when(definitionRepository.countDefinitionByContextId(any())).thenReturn(2L);

    Map<String, Long> map = statsService.getStats();
    assertThat(map.get("definitionsNumber")).isEqualTo(5L);
    assertThat(map.get("termsNumber")).isEqualTo(12L);
    assertThat(map.get("usersNumber")).isEqualTo(4L);
    assertThat(map.get("contextsNumber")).isEqualTo(6L);

    Map<String, Long> map2 = statsService.getStatsForContext(20L);
    assertThat(map2.get("definitionsNumber")).isEqualTo(2L);

  }
}
