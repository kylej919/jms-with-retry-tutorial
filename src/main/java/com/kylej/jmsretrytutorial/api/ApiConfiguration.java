package com.kylej.jmsretrytutorial.api;

import com.kylej.jmsretrytutorial.props.HelloWorldProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
@RequiredArgsConstructor
public class ApiConfiguration {

  private final HelloWorldProps helloWorldProps;

  @Bean
  public HelloWorldAPI helloWorldAPI() {
    return new Retrofit.Builder()
        .baseUrl(helloWorldProps.getUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        .create(HelloWorldAPI.class);
  }
}
