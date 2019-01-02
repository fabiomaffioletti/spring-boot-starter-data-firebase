package com.github.fabiomaffioletti.firebase;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "firebase")
public class FirebaseConfigurationProperties {

    private String serviceAccountFilename;

    private String realtimeDatabaseUrl;

    public String getServiceAccountFilename() {
        return serviceAccountFilename;
    }

    public void setServiceAccountFilename(String serviceAccountFilename) {
        this.serviceAccountFilename = serviceAccountFilename;
    }

    public String getRealtimeDatabaseUrl() {
        return realtimeDatabaseUrl;
    }

    public void setRealtimeDatabaseUrl(String realtimeDatabaseUrl) {
        this.realtimeDatabaseUrl = realtimeDatabaseUrl;
    }

}
