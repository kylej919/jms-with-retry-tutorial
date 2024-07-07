package com.kylej.jmsretrytutorial.jms.sqs;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import jakarta.jms.Session;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

@Configuration
public class SqsConfiguration {

  /** Client to interact with localstack sqs */
  @Bean
  public SqsClient sqsClient() {
    SqsClientBuilder clientBuilder = SqsClient.builder().region(Region.US_WEST_2);
    clientBuilder.endpointOverride(URI.create("http://localhost:4566"));
    clientBuilder.credentialsProvider(
        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")));
    return clientBuilder.build();
  }

  @Bean
  public SQSConnectionFactory sqsConnectionFactory(SqsClient sqsClient) {
    return new SQSConnectionFactory(new ProviderConfiguration(), sqsClient);
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
      SQSConnectionFactory sqsConnectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(new CachingConnectionFactory(sqsConnectionFactory));
    factory.setDestinationResolver(new DynamicDestinationResolver());
    factory.setConcurrency("1-3");
    factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    return factory;
  }
}
