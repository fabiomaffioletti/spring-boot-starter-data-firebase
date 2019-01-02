package com.github.fabiomaffioletti.firebase.document;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fabio on 02/12/2018.
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface FirebaseId {

}
