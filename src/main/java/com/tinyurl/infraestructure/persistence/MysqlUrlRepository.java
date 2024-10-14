package com.tinyurl.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinyurl.domain.model.Url;

public interface MysqlUrlRepository extends JpaRepository<Url, String> {
}