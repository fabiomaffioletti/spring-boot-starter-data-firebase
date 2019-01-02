package com.github.fabiomaffioletti.firebase.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabiomaffioletti.firebase.document.*;
import com.github.fabiomaffioletti.firebase.exception.FirebaseRepositoryException;
import com.github.fabiomaffioletti.firebase.service.FirebaseApplicationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.LongStream;

/**
 * Maps methods here:
 * - https://firebase.google.com/docs/database/rest/save-data?authuser=0
 * - https://firebase.google.com/docs/database/rest/retrieve-data?authuser=0
 */
@NoRepositoryBean
@SuppressWarnings("unchecked")
public class DefaultFirebaseRealtimeDatabaseRepository<T, ID> implements FirebaseRealtimeDatabaseRepository<T, ID> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper firebaseObjectMapper;

    @Autowired
    private FirebaseApplicationService firebaseApplicationService;

    private Class<T> documentClass;

    private Class<ID> documentIdClass;

    private Field documentId;

    private String documentPath;

    private final static List<Class> allowedIdTypes = Lists.newArrayList(Integer.class, Long.class, String.class);

    public DefaultFirebaseRealtimeDatabaseRepository() {
        documentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        documentIdClass = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        if(!allowedIdTypes.contains(documentIdClass)) {
            throw new RuntimeException(String.format("FirebaseId type %s is not managed for class %s", documentIdClass.getSimpleName(), documentClass.getSimpleName()));
        }

        FirebaseDocument annotation = AnnotationUtils.findAnnotation(documentClass, FirebaseDocument.class);
        assert annotation != null : String.format("No FirebaseDocument annotation found on class %s", documentClass.getSimpleName());
        documentId = Arrays.stream(documentClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(FirebaseId.class))
                .findFirst().orElseThrow(() -> new FirebaseRepositoryException(String.format("FirebaseId not found for class %s", documentClass.getSimpleName())));
        documentPath = annotation.value();
    }

    @Override
    public T set(T document, Object... uriVariables) {
        ReflectionUtils.makeAccessible(documentId);
        ID id = (ID) ReflectionUtils.getField(documentId, document);
        assert id != null : "When using set an id value must be specified";

        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).document(document).build();
        T response = restTemplate.exchange(getDocumentPath(id), HttpMethod.PUT, httpEntity, documentClass, uriVariables).getBody();
        ReflectionUtils.setField(documentId, response, id);

        return response;
    }

    @Override
    public T update(T document, Object... uriVariables) {
        ReflectionUtils.makeAccessible(documentId);
        ID id = (ID) ReflectionUtils.getField(documentId, document);
        assert id != null : "When using update an id value must be specified";

        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService)
                .header("X-HTTP-Method-Override", HttpMethod.PATCH.name())
                .document(document).build();
        T response = restTemplate.exchange(getDocumentPath(id), HttpMethod.PUT, httpEntity, documentClass, uriVariables).getBody();
        ReflectionUtils.setField(documentId, response, id);

        return response;
    }

    @Override
    public T push(T document, Object... uriVariables) {
        ReflectionUtils.makeAccessible(documentId);

        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).document(document).build();
        FirebasePushResponse response = restTemplate.exchange(getDocumentPath(), HttpMethod.POST, httpEntity, FirebasePushResponse.class, uriVariables).getBody();
        assert response != null  : String.format("Response is null for push document of class %s", documentClass.getSimpleName());
        ReflectionUtils.setField(documentId, document, response.getName());

        return document;
    }

    @Override
    public void remove(ID id, Object... uriVariables) {
        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).build();
        restTemplate.exchange(getDocumentPath(id), HttpMethod.DELETE, httpEntity, Void.class, uriVariables).getBody();
    }

    @Override
    public void removeAll(Object... uriVariables) {
        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).build();
        restTemplate.exchange(getDocumentPath(), HttpMethod.DELETE, httpEntity, Void.class, uriVariables).getBody();
    }

    @Override
    public T get(ID id, Object... uriVariables) throws FirebaseRepositoryException {
        ReflectionUtils.makeAccessible(documentId);

        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).build();
        T response = restTemplate.exchange(getDocumentPath(id), HttpMethod.GET, httpEntity, documentClass, uriVariables).getBody();
        if (response == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        } else {
            ReflectionUtils.setField(documentId, response, id);
            return response;
        }
    }

    @Override
    public List<T> findAll(Object... uriVariables) {
        return find(Filter.FilterBuilder.builder().build(), uriVariables);
    }

    @Override
    public List<T> find(Filter filter, Object... uriVariables) {
        HttpEntity httpEntity = HttpEntityBuilder.create(firebaseObjectMapper, firebaseApplicationService).build();
        String responseString = restTemplate.exchange(getDocumentPath() + filter.toQueryParameters(), HttpMethod.GET, httpEntity, String.class, uriVariables).getBody();

        try {
            return readAsList(responseString);
        } catch (IOException objectListException) {
            try {
                return readAsArray(responseString);
            } catch (IOException objectArrayException) {
                return Lists.newArrayList();
            }
        }
    }

    private List<T> readAsArray(String responseString) throws IOException {
        List<T> collect = firebaseObjectMapper.readValue(responseString, new FirebaseObjectListTypeReference(firebaseArrayFindResponseParameterizedTypeReference));
        LongStream.range(0, collect.size()).forEach(index -> {
            if (collect.get((int) index) != null) {
                ReflectionUtils.makeAccessible(documentId);

                // for now only Integer and Long are supported
                if(documentIdClass.equals(Integer.class)) {
                    ReflectionUtils.setField(documentId, collect.get((int) index), (int) index);
                } else {
                    ReflectionUtils.setField(documentId, collect.get((int) index), index);
                }

            }
        });

        return collect;
    }

    private List<T> readAsList(String responseString) throws IOException {
        List<T> result = Lists.newArrayList();
        FirebaseMapFindResponse<ID, T> response = firebaseObjectMapper.readValue(responseString, new FirebaseObjectListTypeReference(firebaseMapFindResponseParameterizedTypeReference));
        if (response != null) {
            response.forEach((key, value) -> {
                ReflectionUtils.makeAccessible(documentId);
                ReflectionUtils.setField(documentId, value, key);
                result.add(value);
            });
        }
        return result;
    }

    private String getDocumentPath(ID id) {
        return new StringJoiner("/", "", ".json")
                .add(firebaseApplicationService.getDatabaseUrl())
                .add(documentPath)
                .add(String.valueOf(id))
                .toString();
    }

    private String getDocumentPath() {
        return new StringJoiner("/", "", ".json")
                .add(firebaseApplicationService.getDatabaseUrl())
                .add(documentPath)
                .toString();
    }

    // https://stackoverflow.com/questions/21987295/using-spring-resttemplate-in-generic-method-with-generic-parameter/29547365#29547365
    private ParameterizedTypeReference<FirebaseMapFindResponse<ID, T>> firebaseMapFindResponseParameterizedTypeReference = new ParameterizedTypeReference<FirebaseMapFindResponse<ID, T>>() {
        @Override
        public Type getType() {
            ParameterizedType delegate = (ParameterizedType) super.getType();
            Type[] actualTypeArguments = new Type[]{documentId.getType(), documentClass};
            return getParameterizedType(actualTypeArguments, delegate);
        }
    };

    private ParameterizedTypeReference<FirebaseArrayFindResponse<T>> firebaseArrayFindResponseParameterizedTypeReference = new ParameterizedTypeReference<FirebaseArrayFindResponse<T>>() {
        @Override
        public Type getType() {
            ParameterizedType delegate = (ParameterizedType) super.getType();
            Type[] actualTypeArguments = new Type[]{documentClass};
            return getParameterizedType(actualTypeArguments, delegate);
        }
    };

    private ParameterizedType getParameterizedType(Type[] actualTypeArguments, ParameterizedType delegate) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            @Override
            public Type getRawType() {
                return delegate.getRawType();
            }

            @Override
            public Type getOwnerType() {
                return delegate.getOwnerType();
            }
        };
    }

    public static class FirebaseObjectListTypeReference extends TypeReference<Object> {
        private final Type type;

        public FirebaseObjectListTypeReference(ParameterizedTypeReference parameterizedTypeReference) {
            this.type = parameterizedTypeReference.getType();
        }

        @Override
        public Type getType() {
            return type;
        }
    }

}
