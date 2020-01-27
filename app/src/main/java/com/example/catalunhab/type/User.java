package com.example.catalunhab.type;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Object representing a User
 *
 * Properties that don't map to class fields are ignored when serializing to a class annotated with this annotation.
 */
@IgnoreExtraProperties
public class User {

    private String id;
    private String name;
    private String email;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(User.class)
     */
    public User() {}

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}