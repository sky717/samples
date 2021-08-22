package org.sky.app.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.util.retry.Retry;

@Configuration
public class RSocketConfig {

  @Bean
  public RSocketRequester rSocketRequester() {
    return RSocketRequester.builder()
        .rsocketConnector(
            connector ->
                connector
                    .reconnect(Retry.backoff(Integer.MAX_VALUE, Duration.ofSeconds(1)))
                    .keepAlive(Duration.ofMillis(500L), Duration.ofSeconds(120)))
        .tcp("localhost", 7000);
  }
}
