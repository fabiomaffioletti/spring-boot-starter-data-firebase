# Firebase Realtime Database repositories for Spring Boot

Provides a generic way to interact with the Firebase Realtime Database in a Spring Boot application. It basically provides
a generic repository that mimics the main REST endpoints to be used most probably in backend admin applications. Here is the list
of methods that have been implemented:

- set
- update
- push
- remove
- removeAll
- get
- find
- findAll

## Getting started

### Preconditions
- you need to have a `RestTemplate` bean available
- you need to have `Jackson` on the classpath

But they usually come easily with web applications using:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Declare dependencies

To use this, just include these dependencies in your pom:

```xml
<dependency>
    <groupId>com.github.fabiomaffioletti</groupId>
    <artifactId>spring-boot-starter-data-firebase</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

...and you need also the `firebase-admin` dependency on your classpath:
```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>[version]</version>
</dependency>
```

### Configuration

To get it to work you need to:
1) go to your firebase console, generate a service account file and include it in your classpath
 or in your file system
2) specify two things in your application.properties (or yml) file

```properties
firebase.service-account-filename=[classpath|file]:[service-account-filename].json
firebase.realtime-database-url=[realtime database url] # for example https://your-application.firebaseio.com
```

### The code part

Then, on the Java part:
1) create a class representing the document you need to persist
2) annotate it with `@FirebaseDocument`, specifing the realtime database path
3) create an ID property and annotate it with `@FirebaseId`

```java
@FirebaseDocument("/albums")
public class Album {

    @FirebaseId
    private String id;

    private String title;

}
```

Then create a class that extends the `DefaultFirebaseRealtimeDatabaseRepository` class, and annotate it with `@Repository`
or mark it as a Spring `@Bean` in your application configuration.

```java
@Repository
public class AlbumRepository extends DefaultFirebaseRealtimeDatabaseRepository<Album, String> {
}
```

Finally, put `@EnableFirebaseRepositories` just next to `@SpringBootApplication` or in any `@Configuration` class in your
application.

## Notes
This is at its very early development stages. I did it because I found a lot of code and configuration duplication in 
every Firebase project I worked on. It is very far from what I would like it to be, but still I wanted to expose it to 
the community, hoping that it would be improved and used by people with the same needs as mines.

I could not commit the firebase service account file I am using for testing, so if you would like to contribute, you should
create the service account file and put it in the `src/test/resources/application.properties` file.

### Next steps
- Provide repositories for Cloud Firestore
- Provide repositories for Cloud Storage
- Automatically register repository beans interfaces rather than annotated classes
