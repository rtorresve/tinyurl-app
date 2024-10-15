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
import com.tinyurl.infraestructure.config.ZooKeeperConfig;

import jakarta.annotation.PostConstruct;

public class CreateShortUrlUseCase {

    private final UrlRepository urlRepository;
    private final RedisUrlRepository redisUrlRepository;
    private ZooKeeperConfig zooKeeperConfig;

    @Value("${zookeeper.basepath}")
    private String zookeeperBasePath;

    public CreateShortUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository, ZooKeeperConfig zooKeeperConfig) {
        this.urlRepository = urlRepository;
        this.redisUrlRepository = redisUrlRepository;
        this.zooKeeperConfig = zooKeeperConfig;
    }

    public Url createShortUrl(String longUrl) throws ShortUrlCreationException {
        // Generate a 7-character alphanumeric hash
        String shortUrl = generateShortUrl();

        try {
            if (zooKeeperConfig.zooKeeper() != null) {
                shortUrl = handleZooKeeper(shortUrl, longUrl);
            }
        } catch (IOException e) {
            throw new ShortUrlCreationException("I/O error", e);
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
                if (zooKeeperConfig.zooKeeper().exists(zookeeperBasePath, false) == null) {
                    zooKeeperConfig.zooKeeper().create(zookeeperBasePath, zookeeperBasePath.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                if (zooKeeperConfig.zooKeeper().exists(path, false) == null) {
                    zooKeeperConfig.zooKeeper().create(path, longUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
            } catch (IOException e) {
                throw new ShortUrlCreationException("I/O error", e);
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