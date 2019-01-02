package com.github.fabiomaffioletti.firebase.model.definedkey;

import com.github.fabiomaffioletti.firebase.document.FirebaseDocument;
import com.github.fabiomaffioletti.firebase.document.FirebaseId;

import static com.github.fabiomaffioletti.firebase.model.generatedkey.Constants.BASE_PATH;

@FirebaseDocument(BASE_PATH + "/authors")
public class Author {

    @FirebaseId
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
