package com.kylej.jmsretrytutorial.util;

import com.amazon.sqs.javamessaging.SQSMessagingClientConstants;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Wrapper for sending text-only messages to SQS
 */
@RequiredArgsConstructor
public class SqsMessageProducer {

    private final MessageProducer messageProducer;
    private final Session sqsSession;
    private final String queueName;


    public void send(String textToSend) throws JMSException {
        TextMessage message = sqsSession.createTextMessage(textToSend);

        if (queueName.endsWith(".fifo")) {
            // setting additional required message properties for FIFO queues
            message.setStringProperty(SQSMessagingClientConstants.JMSX_GROUP_ID, "groupId");
            message.setStringProperty(SQSMessagingClientConstants.JMS_SQS_DEDUPLICATION_ID,
                    UUID.randomUUID().toString());
        }

        messageProducer.send(message);
    }
}
