package com.example.redirector.repository;

import com.example.redirector.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByShortLink(String shortLink);

    boolean existsByShortLink(String shortLink);

}
