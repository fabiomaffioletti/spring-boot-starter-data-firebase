package com.github.fabiomaffioletti.firebase.model.generatedkey;

import com.github.fabiomaffioletti.firebase.document.FirebaseDocument;
import com.github.fabiomaffioletti.firebase.document.FirebaseId;

import static com.github.fabiomaffioletti.firebase.model.generatedkey.Constants.BASE_PATH;

@FirebaseDocument(BASE_PATH + "/comments/{postId}")
public class Comment {

    @FirebaseId
    private String id;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
