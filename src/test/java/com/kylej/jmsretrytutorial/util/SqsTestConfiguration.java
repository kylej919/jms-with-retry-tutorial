package com.kylej.jmsretrytutorial.util;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.kylej.jmsretrytutorial.props.HelloWorldProps;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Beans related to sending SQS messages, used in tests only */
@Configuration
@RequiredArgsConstructor
public class SqsTestConfiguration {

  @Bean
  public SQSConnection sqsConnection(SQSConnectionFactory connectionFactory) throws JMSException {
    return connectionFactory.createConnection();
  }

  @Bean
  public SQSSession sqsSession(SQSConnection sqsConnection) throws JMSException {
    return (SQSSession) sqsConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
  }

    /**
     * Creates a {@link MessageProducer} for the HelloWorld queue
     */
  @Bean
  public SqsMessageProducer helloWorldQueueSender(
      Session sqsSession, HelloWorldProps helloWorldProps) throws JMSException {
    MessageProducer messageProducer =
        sqsSession.createProducer(sqsSession.createQueue(helloWorldProps.getQueueName()));
    return new SqsMessageProducer(messageProducer, sqsSession, helloWorldProps.getQueueName());
  }
}
