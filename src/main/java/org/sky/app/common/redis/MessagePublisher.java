package org.sky.app.common.redis;

public interface MessagePublisher {
  void publish(String type, String id, String message);
}
