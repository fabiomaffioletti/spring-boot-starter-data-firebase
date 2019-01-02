package com.github.fabiomaffioletti.firebase.repository;

import java.util.List;

/**
 * Created by fabio on 02/12/2018.
 */
public interface FirebaseRealtimeDatabaseRepository<T, ID> {

    /**
     * Saves a new document. The document must have a user defined id.
     * @param document The document to be saved
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The saved document along with its id
     */
    T set(T document, Object... uriVariables);

    /**
     * Updates an existing document. If the document does not exist, it will be created. The document must have an id.
     * @param document The document (or part of the document) to be updated
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The saved document along with its id
     */
    T update(T document, Object... uriVariables);

    /**
     * Saves a new document. Firebase will generate an id and return it with the document.
     * @param document The document to be saved
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The saved document along with its id
     */
    T push(T document, Object... uriVariables);

    /**
     * Removes a document.
     * @param id The id of the document to be removed
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     */
    void remove(ID id, Object... uriVariables);

    /**
     * Removes all documents.
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     */
    void removeAll(Object... uriVariables);

    /**
     * Gets a document with the given id.
     * @param id The id of the document to be retrieved
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The document along with its id
     */
    T get(ID id, Object... uriVariables);

    /**
     * Retrieves all documents.
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The list of matching documents
     */
    List<T> findAll(Object... uriVariables);

    /**
     * Retrieves all documents matching the given filter.
     * @param filter The filter, or query, to be applied when filtering
     * @param uriVariables The optional uri variables corresponding to placeholders in the FirebaseDocument annotation
     * @return The list of matching documents
     */
    List<T> find(Filter filter, Object... uriVariables);

}
