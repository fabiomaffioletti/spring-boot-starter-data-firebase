package com.github.fabiomaffioletti.firebase;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(classes = FirebaseConfiguration.class)
@TestPropertySource(locations = "classpath:application.properties")
@EnableConfigurationProperties(FirebaseConfigurationProperties.class)
public @interface FirebaseRealtimeDatabaseRepositoryTest {
}
