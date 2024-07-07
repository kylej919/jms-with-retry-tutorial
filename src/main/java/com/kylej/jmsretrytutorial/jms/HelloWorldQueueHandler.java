package com.kylej.jmsretrytutorial.jms;

import com.kylej.jmsretrytutorial.api.HelloWorldAPI;
import com.kylej.jmsretrytutorial.entity.HelloWorldResponse;
import com.kylej.jmsretrytutorial.props.HelloWorldProps;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import retrofit2.Response;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(HelloWorldProps.class)
public class HelloWorldQueueHandler {

  private final HelloWorldAPI helloWorldAPI;

  /**
   * Simple queue handler that calls the hello world API and logs the response
   *
   * @throws IOException - if there's a network-related error calling GET /hello-world
   */
  @JmsListener(destination = "${hello-world-props.queue-name}")
  public void handleMessage() throws IOException {
    log.info("Processing message from hello world queue");

    Response<HelloWorldResponse> apiResponse = helloWorldAPI.getHelloWorld().execute();

    if (apiResponse.isSuccessful()) {
      log.info("Hello world response: {}", apiResponse.body());
    } else {
      log.error("Failed to get hello world response: {}", apiResponse.errorBody().string());
    }
  }
}
