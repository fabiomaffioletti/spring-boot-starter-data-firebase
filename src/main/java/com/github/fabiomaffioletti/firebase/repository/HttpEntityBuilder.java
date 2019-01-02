package com.github.fabiomaffioletti.firebase.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabiomaffioletti.firebase.exception.FirebaseRepositoryException;
import com.github.fabiomaffioletti.firebase.service.FirebaseApplicationService;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

public class HttpEntityBuilder<T> {

    private final ObjectMapper firebaseObjectMapper;

    private final FirebaseApplicationService firebaseApplicationService;

    private T document;

    private MultiValueMap<String, String> headers;

    private HttpEntityBuilder(ObjectMapper firebaseObjectMapper, FirebaseApplicationService firebaseApplicationService) {
        try {
            this.firebaseObjectMapper = firebaseObjectMapper;
            this.firebaseApplicationService = firebaseApplicationService;
            headers = this.firebaseApplicationService.headers();
        } catch (IOException e) {
            throw new FirebaseRepositoryException(e.getMessage());
        }
    }

    public static HttpEntityBuilder create(ObjectMapper firebaseObjectMapper, FirebaseApplicationService firebaseApplicationService) {
        return new HttpEntityBuilder(firebaseObjectMapper, firebaseApplicationService);
    }

    public HttpEntityBuilder document(T document) {
        this.document = document;
        return this;
    }

    public HttpEntityBuilder header(String headerName, String headerValue) {
        this.headers.add(headerName, headerValue);
        return this;
    }

    public HttpEntity<String> build() {
        try {
            if(document == null) {
                return new HttpEntity<>(headers);
            } else {
                return new HttpEntity<>(firebaseObjectMapper.writeValueAsString(document), headers);
            }
        } catch (IOException e) {
            throw new FirebaseRepositoryException(e.getMessage());
        }
    }

}
