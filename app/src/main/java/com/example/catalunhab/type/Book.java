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

    public Book(String id, String title, String author, boolean available, String image) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.image = image;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available +
                '}';
    }
}
