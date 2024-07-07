package com.kylej.jmsretrytutorial.api;

import com.kylej.jmsretrytutorial.entity.HelloWorldResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface HelloWorldAPI {

    String HELLO_WORLD_PATH = "/hello-world";

    @GET(HELLO_WORLD_PATH)
    @Headers("accept: application/json")
    Call<HelloWorldResponse> getHelloWorld();
}
