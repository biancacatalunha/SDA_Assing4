package com.example.catalunhab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catalunhab.type.Book;
import com.example.sdaassign4_2019.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Class to upload book objects to firebase database
 */
public class UploadDataActivity extends AppCompatActivity {

    private static final String TAG = "UploadDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uploadBooks();
    }

    private void uploadBooks() {
        ArrayList<Book> books = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.book_titles);
        String[] authors = getResources().getStringArray(R.array.book_authors);

        if(titles.length == authors.length) {
            for(int x = 0; x < titles.length; x++) {
                Book book = new Book(String.valueOf(x+1), String.valueOf(titles[x]), String.valueOf(authors[x]), true);
                books.add(book);
                Log.d(TAG, "Book added to arrayList: " + book.toString());
            }

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            for(Book book: books) {
                mDatabase.child("books").child(book.getId()).setValue(book);
                Log.d(TAG, "Book added to database: " + book.toString());
            }

            Intent intent = new Intent(getApplicationContext(), com.example.catalunhab.MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Number of titles do not match number of authors");
        }
    }
}
