package com.dayone.persist;

import com.dayone.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(
            String s, Pageable pageable);
    // ignoreCase 는 대소문자를 안가리게 해줌
    // StrartingWith은 쿼리문에서 like 와 같은 기능을 함

    Optional<CompanyEntity> findByTicker(String ticker);
}
