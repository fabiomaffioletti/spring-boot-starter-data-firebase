package com.github.fabiomaffioletti.firebase.repository;

import java.util.List;

/**
 * Created by fabio on 02/12/2018.
 */
public interface FirebaseRealtimeDatabaseRepository<T, ID> {

    T set(T document, Object... uriVariables);

    T update(T document, Object... uriVariables);

    T push(T document, Object... uriVariables);

    void remove(ID id, Object... uriVariables);

    void removeAll(Object... uriVariables);

    T get(ID id, Object... uriVariables);

    List<T> findAll(Object... uriVariables);

    List<T> find(Filter filter, Object... uriVariables);
}
