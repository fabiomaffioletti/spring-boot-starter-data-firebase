package com.github.fabiomaffioletti.firebase;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
@Import(FirebaseConfiguration.class)
public @interface EnableFirebaseRepositories {
}
