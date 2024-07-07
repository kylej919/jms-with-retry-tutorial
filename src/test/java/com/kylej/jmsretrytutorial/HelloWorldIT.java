package com.kylej.jmsretrytutorial;

import static com.kylej.jmsretrytutorial.api.HelloWorldAPI.HELLO_WORLD_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.kylej.jmsretrytutorial.props.HelloWorldProps;
import com.kylej.jmsretrytutorial.util.SqsMessageProducer;
import com.kylej.jmsretrytutorial.util.SqsQueueUtil;
import java.net.URL;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloWorldIT {

  @Autowired private HelloWorldProps helloWorldProps;

  @Autowired private SqsQueueUtil sqsQueueUtil;

  @Autowired private SqsMessageProducer helloWorldQueueSender;

  @BeforeEach
  @SneakyThrows
  void init() {
    URL url = new URL(helloWorldProps.getUrl());
    WireMock.configureFor(url.getHost(), url.getPort());

    // when using dockerized wiremock, this is an important step to make sure all requests read in a
    // test case
    // are from that test case and not from previous test cases
    WireMock.resetAllRequests();

    // after resetting, there are no serve events
    assertThat(WireMock.getAllServeEvents()).isEmpty();

    // verifying the queue is empty between test runs
    sqsQueueUtil.purgeQueue(helloWorldProps.getQueueName());
  }

  /**
   * Sends a message the the queue and verifies that the api requests were made - the first is a
   * connection reset response, the 2nd is triggered by JMS retry and is successful
   */
  @Test
  @SneakyThrows
  void test() {
    helloWorldQueueSender.send("testMessage");

    await().until(() -> WireMock.getAllServeEvents().size() > 1);

    List<ServeEvent> serveEvents = WireMock.getAllServeEvents();
    assertThat(serveEvents).hasSize(2);

    // zero-indexed by latest request, event 0 will be the successful retry against the hello-world
    // endpoint
    ServeEvent successEvent = serveEvents.get(0);
    assertThat(successEvent.getRequest().getUrl()).isEqualTo(HELLO_WORLD_PATH);
    assertThat(successEvent.getResponse().getStatus()).isEqualTo(200);
    assertThat(successEvent.getResponse().getBodyAsString()).contains("success");

    // the first request was a connection reset response
    ServeEvent failingEvent = serveEvents.get(1);
    assertThat(failingEvent.getRequest().getUrl()).isEqualTo(HELLO_WORLD_PATH);
    assertThat(failingEvent.getResponse().getStatus()).isEqualTo(500);
    failingEvent.getResponse().getBodyAsString().contains("success");
    assertThat(failingEvent.getResponse().getFault()).isEqualTo(Fault.CONNECTION_RESET_BY_PEER);
  }

  @AfterEach
  void cleanUp() {
    WireMock.resetAllRequests();

    // verifying the queue is empty between test runs
    sqsQueueUtil.purgeQueue(helloWorldProps.getQueueName());
  }
}
