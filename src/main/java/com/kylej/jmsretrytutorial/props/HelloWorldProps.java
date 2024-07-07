package com.kylej.jmsretrytutorial.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hello-world-props")
public class HelloWorldProps {

    /**
     * The URL of the hello world API
     */
    private String url;

    /**
     * The name of the queue to listen for messages on
     */
    private String queueName;
}
