package com.tinyurl.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.ShortUrlCreationException;
import com.tinyurl.infraestructure.config.ZooKeeperConfig;


@TestPropertySource(properties = {
    "feature.zookeeper.enabled=true",
    "tinyurl.domain=http://test.com"
})
public class CreateShortUrlUseCaseTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisUrlRepository redisUrlRepository;

    @Mock
    private ZooKeeperConfig zooKeeperConfig;

    @Mock
    private ZooKeeper zooKeeper;

    @InjectMocks
    private CreateShortUrlUseCase createShortUrlUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        createShortUrlUseCase = new CreateShortUrlUseCase(urlRepository, redisUrlRepository, zooKeeperConfig);
    }


    @Test
    public void testCreateShortUrlSuccess() throws KeeperException, InterruptedException, ShortUrlCreationException, IOException {
        String longUrl = "http://example.com";
        String shortUrl = "abc1234";
        String path = "/urls/" + shortUrl;

        when(zooKeeper.exists(path, false)).thenReturn(null);
        when(zooKeeper.create(path, shortUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)).thenReturn(path);

        Url url = createShortUrlUseCase.createShortUrl(longUrl);
        String shortUrlPart = url.getShortUrl().substring(url.getShortUrl().lastIndexOf('/') + 1);

        assertNotNull(url);
        assertEquals(longUrl, url.getLongUrl());
        assertEquals(7, shortUrlPart.length());
    }

    @Test
    public void testCreateShortUrlCollision() throws KeeperException, InterruptedException, ShortUrlCreationException, IOException {
        String longUrl = "http://example.com";
        String shortUrl1 = "abc1234";
        String shortUrl2 = "def5678";
        String path1 = "/urls/" + shortUrl1;
        String path2 = "/urls/" + shortUrl2;

        when(zooKeeper.exists(path1, false)).thenReturn(new Stat());
        when(zooKeeper.exists(path2, false)).thenReturn(null);
        when(zooKeeper.create(path2, longUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)).thenReturn(path2);

        Url url = createShortUrlUseCase.createShortUrl(longUrl);
        String shortUrlPart = url.getShortUrl().substring(url.getShortUrl().lastIndexOf('/') + 1);

        assertNotNull(url);
        assertEquals(longUrl, url.getLongUrl());
        assertEquals(7, shortUrlPart.length());
        verify(urlRepository).save(url);
        verify(redisUrlRepository).saveUrl(shortUrlPart, longUrl);
    }

}