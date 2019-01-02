package com.github.fabiomaffioletti.firebase.service;

import com.github.fabiomaffioletti.firebase.FirebaseConfigurationProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;

@EnableConfigurationProperties(FirebaseConfigurationProperties.class)
public class FirebaseApplicationService {

    private GoogleCredential scoped;

    private final ResourceLoader resourceLoader;

    private final FirebaseConfigurationProperties firebaseConfigurationProperties;

    public FirebaseApplicationService(ResourceLoader resourceLoader, FirebaseConfigurationProperties firebaseConfigurationProperties) throws IOException {
        this.firebaseConfigurationProperties = firebaseConfigurationProperties;
        this.resourceLoader = resourceLoader;
        assert firebaseConfigurationProperties.getServiceAccountFilename() != null;
        assert firebaseConfigurationProperties.getRealtimeDatabaseUrl() != null;

        // file: or classpath:
        Resource serviceAccount = resourceLoader.getResource(firebaseConfigurationProperties.getServiceAccountFilename());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .setDatabaseUrl(firebaseConfigurationProperties.getRealtimeDatabaseUrl())
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        GoogleCredential googleCred = GoogleCredential.fromStream(serviceAccount.getInputStream());
        scoped = googleCred.createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.database", "https://www.googleapis.com/auth/userinfo.email"));
        scoped.refreshToken();
    }

    private String token() throws IOException {
        String token = scoped.getAccessToken();
        if (token == null || scoped.getExpiresInSeconds() < 100) {
            scoped.refreshToken();
            token = scoped.getAccessToken();
        }
        return token;
    }

    public MultiValueMap<String, String> headers() throws IOException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token());
        return headers;
    }

    public String getDatabaseUrl() {
        return firebaseConfigurationProperties.getRealtimeDatabaseUrl();
    }

}
