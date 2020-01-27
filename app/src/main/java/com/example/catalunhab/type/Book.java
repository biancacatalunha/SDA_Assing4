package com.example.catalunhab.type;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Object representing a book
 *
 * Properties that don't map to class fields are ignored when serializing to a class annotated with this annotation.
 */

@IgnoreExtraProperties
public class Book {

    private String id;
    private String image;
    private String title;
    private String author;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Book.class)
     */
    public Book()  {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @NonNull
    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
