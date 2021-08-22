package org.sky.app.config;

import org.sky.app.common.redis.MessagePublisher;
import org.sky.app.common.redis.RedisMessagePublisher;
import org.sky.app.common.redis.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableRedisWebSession
public class RedisConfig {
  @Autowired private RedisProperties props;

  @Autowired private RedisMessageSubscriber redisMessageSubscriber;

  @Bean
  public static ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }

  // @Bean
  // public RedisConnectionFactory redisConnectionFactory() {
  //   final LettuceClientConfiguration configuration =
  //       props.isSsl()
  //           ? LettuceClientConfiguration.builder().useSsl().build()
  //           : LettuceClientConfiguration.defaultConfiguration();
  //   final RedisStandaloneConfiguration server =
  //       new RedisStandaloneConfiguration(props.getHost(), props.getPort());
  //   server.setPassword(props.getPassword());
  //   return new LettuceConnectionFactory(server, configuration);
  // }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    final var template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }

  @Bean
  public MessageListenerAdapter messageListener() {
    return new MessageListenerAdapter(redisMessageSubscriber);
  }

  @Bean
  public RedisMessageListenerContainer redisContainer() {
    final var container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory());
    container.addMessageListener(messageListener(), topic());
    return container;
  }

  @Bean
  public MessagePublisher redisPublisher() {
    return new RedisMessagePublisher(redisTemplate(), topic());
  }

  @Bean
  public ChannelTopic topic() {
    return new ChannelTopic("pubsub:queue");
  }
}
