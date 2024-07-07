# SQS, JMS, WireMock, and Localstack tutorial

The following tutorial will contain pieces of the following tools / concepts: 

- JMS, utilizing out of the box retry mechanisms
- Localstack (dockerized)
- WireMock (dockerized)
- RetroFit API client
- Aws SDK for Java

I'll provide a brief introduction for each:

### WireMock
[WireMock](https://wiremock.org/) is a flexible library for stubbing and mocking web services. Unlike general purpose mocking tools it works by creating an actual HTTP server that your code under test can connect to as it would a real web service.

In this demo, I create a wiremock stub for a simple GET /hello-world request, which returns a 500, connection reset response for the initial request, then returns a 200 Success response for the 2nd, retried request.

I transition between expected responses using [WireMock's stateful behavior](https://wiremock.org/docs/stateful-behaviour/) functionality

### Localstack
LocalStack is a fully functional local AWS cloud stack that enables you to develop and test your cloud apps offline. It spins up an easy-to-use testing environment on your local machine that provides the same functionality and APIs as the real AWS cloud environment.

In this demo, we are creating and using a single SQS queue. The entrypoint in the demo is the queue handler, which is triggered by a queue send

### JMS
Java Message Service (JMS) is an application program interface (API) used to send messages between two or more clients. JMS is a part of the Java Platform, Enterprise Edition, and is defined by a specification developed under the Java Community Process.

In this demo, we utilize an AWS library which adapts SQS to JMS protocol. We use the JMS retry mechanism to trigger a retry of the message send to the queue after the initial failure response, and the retry should succeed

## Tutorial Steps

To run the tutorial, start the docker containers in docker/docker-compose.yml. This will spin up localstack with the SQS queue, and wiremock with the /hello-world endpoint

Then, we can run HelloWorldIT.java, which sends a message to the newly-created SQS queue, which triggers the queue handler's HTTP requests. 

The test verifies the requests and responses against the WireMock docker container