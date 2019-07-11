package com.almerys.columbia.api.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SearchablePagingAndSortingRepository<E, ID> extends PagingAndSortingRepository<E, ID>, JpaSpecificationExecutor<E> {

}
