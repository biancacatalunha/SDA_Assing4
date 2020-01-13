package com.example.catalunhab.type;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {

    private String id;
    private String image;
    private String title;
    private String author;
    private boolean available;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(User.class)
     */
    public Book()  {}

    public Book(String id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available +
                '}';
    }
}
