package com.tinyurl.domain.usecase;

import java.util.Random;
import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.apache.zookeeper.KeeperException;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.ShortUrlCreationException;

import jakarta.annotation.PostConstruct;

public class CreateShortUrlUseCase {

    private final UrlRepository urlRepository;
    private final RedisUrlRepository redisUrlRepository;
    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${feature.zookeeper.enabled}")
    private boolean zookeeperEnabled;
    @Value("${zookeeper.sessionTimeout}")
    private Integer sessionTimeout;
    @Value("${zookeeper.basepath}")
    private String zookeeperBasePath;

    private ZooKeeper zooKeeper;

    @PostConstruct
    public void init() throws RuntimeException {
        try {
            System.setProperty("zookeeper.sasl.client", "false");
            zooKeeper = new ZooKeeper(zookeeperConnection, sessionTimeout, null);
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to ZooKeeper");
        }
    }

    public CreateShortUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository, ZooKeeper zooKeeper) {
        this.urlRepository = urlRepository;
        this.redisUrlRepository = redisUrlRepository;
        this.zooKeeper = zooKeeper;
    }

    public Url createShortUrl(String longUrl) throws ShortUrlCreationException {
        // Generate a 7-character alphanumeric hash
        String shortUrl = generateShortUrl();

        if (zookeeperEnabled) {
            shortUrl = handleZooKeeper(shortUrl, longUrl);
        }

        // Create Url object with the long and short URLs
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);

        // Save the Url in the repository
        urlRepository.save(url);
        redisUrlRepository.saveUrl(shortUrl, longUrl);

        return url;
    }

    private String handleZooKeeper(String shortUrl, String longUrl) throws ShortUrlCreationException {
        int retryCount = 0;
        final int maxRetries = 5;
        String path = zookeeperBasePath + "/" + shortUrl;
        while (retryCount < maxRetries) {
            try {
                if (zooKeeper.exists(zookeeperBasePath, false) == null) {
                    zooKeeper.create(zookeeperBasePath, zookeeperBasePath.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                if (zooKeeper.exists(path, false) == null) {
                    zooKeeper.create(path, longUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    break;
                } else {
                    shortUrl = generateShortUrl();
                    retryCount++;
                }
            } catch (KeeperException.NodeExistsException e) {
                shortUrl = generateShortUrl();
                retryCount++;
            } catch (KeeperException.ConnectionLossException e) {
                throw new ShortUrlCreationException("Connection to ZooKeeper lost", e);
            } catch (KeeperException.SessionExpiredException e) {
                throw new ShortUrlCreationException("ZooKeeper session expired", e);
            } catch (KeeperException e) {
                throw new ShortUrlCreationException("ZooKeeper error: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ShortUrlCreationException("Thread interrupted", e);
            }
        }
        if (retryCount == maxRetries) {
            throw new ShortUrlCreationException("Unable to assign a new short URL after multiple attempts");
        }
        return shortUrl;
    }

    private String generateShortUrl() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortUrl = new StringBuilder();
        Random rnd = new Random();
        while (shortUrl.length() < 7) {
            shortUrl.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return shortUrl.toString();
    }
}