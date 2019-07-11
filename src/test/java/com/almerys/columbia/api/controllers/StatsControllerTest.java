package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.StatsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatsControllerTest {
  @Mock
  StatsService statsService;

  @InjectMocks
  StatsController statsController;

  @Test
  public void testGetAtom() {
    when(statsService.getStats()).thenReturn(new HashMap<>());
    assertThat(statsController.getStatsForSpecificContext()
                              .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(statsService.getStats()).thenReturn(null);
    assertThat(statsController.getStatsForSpecificContext()
                              .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    when(statsService.getStats()).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> statsController.getStatsForSpecificContext()).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testGetAtomForSpecificContext() {
    when(statsService.getStatsForContext(any())).thenReturn(new HashMap<>());
    assertThat(statsController.getStatsForSpecificContext(4L)
                              .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(statsService.getStatsForContext(any())).thenReturn(null);
    assertThat(statsController.getStatsForSpecificContext(4L)
                              .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    when(statsService.getStatsForContext(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> statsController.getStatsForSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);
  }
}
