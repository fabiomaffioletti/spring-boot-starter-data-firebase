package com.github.fabiomaffioletti.firebase.model.definedkey;

import com.github.fabiomaffioletti.firebase.document.FirebaseDocument;
import com.github.fabiomaffioletti.firebase.document.FirebaseId;

import static com.github.fabiomaffioletti.firebase.model.generatedkey.Constants.BASE_PATH;

@FirebaseDocument(BASE_PATH + "/books")
public class Book {

    @FirebaseId
    private Integer id;

    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
