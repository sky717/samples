package org.sky.app.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {
  // Stomp 用
  // @Autowired private SimpMessagingTemplate template;

  @Override
  public void onMessage(final Message message, final byte[] pattern) {
    try {
      final var recvMsg =
          new ObjectMapper()
              .readValue(message.getBody(), new TypeReference<Map<String, String>>() {});
      log.info("Message recieved: {}", recvMsg);
      // Stomp 用
      // template.convertAndSend(
      //     "/send/" + recvMsg.get("id") + "/chatmsgs" + recvMsg.get("type"),
      //     new RedisMessage(recvMsg.get("message")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
