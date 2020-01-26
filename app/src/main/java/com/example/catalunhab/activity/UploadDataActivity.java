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
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catalunhab.type.Book;
import com.example.sdaassign4_2019.R;
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
public class UploadDataActivity extends AppCompatActivity {

    private static final String TAG = "UploadDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uploadBooks();
    }

    /**
     * Gets a list of authors and titles from the string resources,
     * Iterates through the list creating setting a new book object for each
     * The image URI is obtained by calling getDownloadUrl on the storage reference
     * with the name of the child and file
     * Once the URI is retrieved, the object is uploaded to the firebase database
     */
    private void uploadBooks() {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final String[] titles = getResources().getStringArray(R.array.book_titles);
        final String[] authors = getResources().getStringArray(R.array.book_authors);
        final String bookStorageFolderName = getResources().getString(R.string.book_storage_folder_name);
        final String bookNamePattern = getResources().getString(R.string.book_name_pattern);
        final String jpgExtension = getResources().getString(R.string.jpg_extension);
        final String booksLabel = getResources().getString(R.string.books);

        if(titles.length == authors.length) {
            for(int x = 0; x < titles.length; x++) {
                final Book book = new Book();

                book.setId(String.valueOf(x+1));
                book.setTitle(String.valueOf(titles[x]));
                book.setAuthor(String.valueOf(authors[x]));
                book.setAvailable(true);

                storageReference.child(bookStorageFolderName + "/" + bookNamePattern + (x + 1) + jpgExtension)
                        .getDownloadUrl().addOnSuccessListener(uri -> {
                            book.setImage(uri.toString());
                            mDatabase.child(booksLabel).child(book.getId()).setValue(book);

                            Log.d(TAG, "Book added to database: " + book.toString());
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.images_uploaded_successfully, Toast.LENGTH_SHORT);
                            toast.show();
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "Error getting URI ", e);
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.images_upload_error, Toast.LENGTH_SHORT);
                            toast.show();
                        });
            }

            Intent intent = new Intent(getApplicationContext(), com.example.catalunhab.MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Number of titles do not match number of authors");
        }
    }
}
