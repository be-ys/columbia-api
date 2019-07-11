package com.almerys.columbia.api.repository;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends SearchablePagingAndSortingRepository<ColumbiaUser, Long> {

  Iterable<ColumbiaUser> findByGrantedContextsContains(ColumbiaContext columbiaContext);

  Optional<ColumbiaUser> findByUsername(String username);

  Optional<ColumbiaUser> findById(String id);

  Optional<ColumbiaUser> findByEmail(String email);

  Optional<ColumbiaUser> findByUsernameAndDomain(String username, String domain);

  Iterable<ColumbiaUser> findAllByLastLoginIsNull();

  @Modifying
  @Query(value = "UPDATE columbia_revision SET username = 'Anonyme' WHERE username = :usn", nativeQuery = true)
  void destroyEnversHistory(String usn);

  void deleteById(String id);

  Iterable<ColumbiaUser> findAllByLastLoginBefore(Date date);

  Optional<ColumbiaUser> findByActivationKey(String key);
}