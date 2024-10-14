package com.tinyurl.domain.repository;

import com.tinyurl.domain.model.Url;
import com.tinyurl.infraestructure.persistence.MysqlUrlRepository;

public interface UrlRepository extends MysqlUrlRepository{
    Url findByShortUrl(String shortUrl);
}
