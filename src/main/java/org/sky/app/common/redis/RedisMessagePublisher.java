package org.sky.app.common.redis;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {
  @Autowired private RedisTemplate<String, Object> redisTemplate;
  @Autowired private ChannelTopic topic;

  @Override
  public void publish(final String type, final String id, final String message) {
    final var sendMsg = Map.of("type", type, "id", id, "message", message);
    redisTemplate.convertAndSend(topic.getTopic(), sendMsg);
    log.info("Message send: {}", sendMsg);
  }
}
