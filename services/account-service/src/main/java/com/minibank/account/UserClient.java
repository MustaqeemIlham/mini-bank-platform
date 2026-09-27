package com.minibank.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

// Our "phone line" to user-service. Microservices talk over HTTP, never by sharing tables.
@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${user-service.url}") String userServiceUrl) {
        this.restClient = RestClient.create(userServiceUrl);
    }

    public boolean userExists(Long userId) {
        try {
            restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity(); // we only care that it answered 200, not the body
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        } catch (ResourceAccessException e) {
            // User-service is down -> let it bubble up as 500 to the API caller
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "user-service is down");
        }
        // If user-service is down, RestClient throws ResourceAccessException -> 500 for our caller
    }
}
