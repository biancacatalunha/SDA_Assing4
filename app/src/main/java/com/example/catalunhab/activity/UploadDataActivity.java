/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.catalunhab.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.catalunhab.type.Book;
import com.example.sdaassign4_2019.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * DCU - SDA - Assignment 4
 *
 * Helper class to upload book objects to Firebase database
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 *
 *
 */
//todo get reference
public class UploadDataActivity extends AppCompatActivity {

    private static final String TAG = "UploadDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uploadBooks();
    }

    /**
     * Gets a list of authors and titles from the string resources,
     * Iterates through the list creating Book objects and uploading
     * them to firebase
     */
    private void uploadBooks() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String[] titles = getResources().getStringArray(R.array.book_titles);
        String[] authors = getResources().getStringArray(R.array.book_authors);

        if(titles.length == authors.length) {
            for(int x = 0; x < titles.length; x++) {
                final Book book = new Book();

                book.setId(String.valueOf(x+1));
                book.setTitle(String.valueOf(titles[x]));
                book.setAuthor(String.valueOf(authors[x]));
                book.setAvailable(true);

                storageReference.child("book_covers/sku1000" + (x + 1) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        book.setImage(uri.toString());
                        mDatabase.child("books").child(book.getId()).setValue(book);
                        Log.d(TAG, "Book added to database: " + book.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error getting URI ", e);
                    }
                });
            }

            Intent intent = new Intent(getApplicationContext(), com.example.catalunhab.MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Number of titles do not match number of authors");
        }
    }
}
