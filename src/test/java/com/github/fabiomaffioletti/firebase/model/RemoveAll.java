package com.github.fabiomaffioletti.firebase.model;

import com.github.fabiomaffioletti.firebase.document.FirebaseDocument;
import com.github.fabiomaffioletti.firebase.document.FirebaseId;

import static com.github.fabiomaffioletti.firebase.model.generatedkey.Constants.BASE_PATH;

@FirebaseDocument(BASE_PATH)
public class RemoveAll {

    @FirebaseId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
