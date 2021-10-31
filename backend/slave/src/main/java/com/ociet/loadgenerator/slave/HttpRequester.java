package com.ociet.loadgenerator.slave;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequester {
    static HttpClient client = HttpClient.newHttpClient();

    static void makeRequest(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(
                        URI.create(url)
                )
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
