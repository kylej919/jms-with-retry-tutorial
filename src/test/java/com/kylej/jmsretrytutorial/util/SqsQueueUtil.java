package com.kylej.jmsretrytutorial.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import static org.awaitility.Awaitility.await;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsQueueUtil {

    private final SqsClient sqsClient;

    public int getInflightMessages(String queueName) {
        GetQueueAttributesResponse numMessages = sqsClient.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                        .queueUrl(sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                                .queueName(queueName)
                                .build()).queueUrl())
                        .attributeNames(APPROXIMATE_NUMBER_OF_MESSAGES,
                                APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)
                        .build());
        return Integer.parseInt(numMessages.attributes().get(APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE))
                + Integer.parseInt(numMessages.attributes().get(APPROXIMATE_NUMBER_OF_MESSAGES));
    }

    public void purgeQueue(String name) {
        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder().queueName(name).build();
        String queueUrl = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build());

        await().until(() -> getInflightMessages(name) == 0);
        log.info("Queue {} successfully purged", name);
    }
}
