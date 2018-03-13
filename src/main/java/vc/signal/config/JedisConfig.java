package vc.signal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis configuration.
 */
@Configuration
public class JedisConfig {

  @Value("${REDIS_HOST:localhost}")
  private String jedisHost;

  @Value("${REDIS_PORT:6379}")
  private int jedisPort;

  @Bean
  JedisPool jedisPool() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestWhileIdle(true);
    return new JedisPool(poolConfig, jedisHost, jedisPort, 30);
  }
}
