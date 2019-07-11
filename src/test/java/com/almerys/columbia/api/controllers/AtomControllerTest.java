package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.AtomService;
import com.rometools.rome.feed.atom.Feed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AtomControllerTest {
  @Mock
  AtomService atomService;

  @InjectMocks
  AtomController atomController;

  @Test
  public void testGetAtom() {
    when(atomService.getLastModificationsAsAtomFeed()).thenReturn(new Feed());
    assertThat(atomController.getAtomFeed()
                             .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(atomService.getLastModificationsAsAtomFeed()).thenReturn(null);
    assertThat(atomController.getAtomFeed()
                             .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    when(atomService.getLastModificationsAsAtomFeed()).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> atomController.getAtomFeed()).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testGetAtomForSpecificContext() {
    when(atomService.getLastModificationsAsAtomFeedForSpecificContext(any())).thenReturn(new Feed());
    assertThat(atomController.getAtomFeedForSpecificContext(4L)
                             .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(atomService.getLastModificationsAsAtomFeedForSpecificContext(any())).thenReturn(null);
    assertThat(atomController.getAtomFeedForSpecificContext(4L)
                             .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    when(atomService.getLastModificationsAsAtomFeedForSpecificContext(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> atomController.getAtomFeedForSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);
  }
}
