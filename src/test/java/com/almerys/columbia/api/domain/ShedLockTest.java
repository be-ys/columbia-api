package com.almerys.columbia.api.domain;

import org.junit.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShedLockTest {

  @Test
  public void testShadLock() {
    ShedLock shedLock = new ShedLock();
    shedLock.setLock_until(new Timestamp(1L));
    shedLock.setLocked_at(new Timestamp(0L));
    shedLock.setLocked_by("test");
    shedLock.setName("essai");


    assertThat(shedLock.getLock_until()).isEqualTo(new Timestamp(1L));
    assertThat(shedLock.getLocked_at()).isEqualTo(new Timestamp(0L));
    assertThat(shedLock.getLocked_by()).isEqualTo("test");
    assertThat(shedLock.getName()).isEqualTo("essai");
  }

}
