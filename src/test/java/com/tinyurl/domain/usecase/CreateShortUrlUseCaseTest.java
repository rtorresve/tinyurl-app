package com.tinyurl.domain.usecase;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.ShortUrlCreationException;


@TestPropertySource(properties = {
    "feature.zookeeper.enabled=true"
})
public class CreateShortUrlUseCaseTest {

    private CreateShortUrlUseCase createShortUrlUseCase;
    private ZooKeeper zooKeeper;
    private UrlRepository urlRepository;
    private RedisUrlRepository redisUrlRepository;

    @BeforeEach
    public void setUp() {
        zooKeeper = mock(ZooKeeper.class);
        urlRepository = mock(UrlRepository.class);
        redisUrlRepository = mock(RedisUrlRepository.class);
        createShortUrlUseCase = new CreateShortUrlUseCase(urlRepository, redisUrlRepository, zooKeeper);
    }

    @Test
    public void testCreateShortUrlSuccess() throws KeeperException, InterruptedException, ShortUrlCreationException {
        String longUrl = "http://example.com";
        String shortUrl = "abc1234";
        String path = "/urls/" + shortUrl;

        when(zooKeeper.exists(path, false)).thenReturn(null);
        when(zooKeeper.create(path, longUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)).thenReturn(path);

        Url url = createShortUrlUseCase.createShortUrl(longUrl);

        assertNotNull(url);
        assertEquals(longUrl, url.getLongUrl());
        assertEquals(url.getShortUrl().length(), 7);
        verify(urlRepository).save(url);
        verify(redisUrlRepository).saveUrl(url.getShortUrl(), longUrl);
    }

    @Test
    public void testCreateShortUrlCollision() throws KeeperException, InterruptedException, ShortUrlCreationException {
        String longUrl = "http://example.com";
        String shortUrl1 = "abc1234";
        String shortUrl2 = "def5678";
        String path1 = "/urls/" + shortUrl1;
        String path2 = "/urls/" + shortUrl2;

        when(zooKeeper.exists(path1, false)).thenReturn(new Stat());
        when(zooKeeper.exists(path2, false)).thenReturn(null);
        when(zooKeeper.create(path2, longUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)).thenReturn(path2);

        Url url = createShortUrlUseCase.createShortUrl(longUrl);

        assertNotNull(url);
        assertEquals(longUrl, url.getLongUrl());
        assertEquals(url.getShortUrl().length(), 7);
        verify(urlRepository).save(url);
        verify(redisUrlRepository).saveUrl(url.getShortUrl(), longUrl);
    }
}