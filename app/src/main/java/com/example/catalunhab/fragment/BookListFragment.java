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
package com.example.catalunhab.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalunhab.adapter.BooksAdapter;
import com.example.catalunhab.type.Book;
import com.example.sdaassign4_2019.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * DCU - SDA - Assignment 4
 *
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 *
 * @author Chris Coughlan and Bianca Catalunha
 * @since January 2020
 */
public class BookListFragment extends Fragment {

    private static final String TAG = "BookListFragment";
    private static final String DATABASE_CHILD_NAME = "books";
    /**
     * Required empty constructor
     */
    public BookListFragment() {}

    /**
     * Inflates book list layout as a fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        getBooksFromDatabase();

        return root;
    }

    /**
     * Retrieves books from teh database
     * - Se†s an Listener for single value event
     * - Retrieves the books json as an iterator
     * - Transforms the JSON object to a Book object and adds it to a list
     * - Calls the recyclerViewAdapter passing the list of books
     *
     * Reference:
     * Retrieve objects from db - https://riptutorial.com/android/example/25872/retrieving-data-from-firebase
     */
    private void getBooksFromDatabase() {
        FirebaseDatabase.getInstance().getReference().child(DATABASE_CHILD_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> books = dataSnapshot.getChildren().iterator();
                        ArrayList<Book> booksList = new ArrayList<>();

                        while (books.hasNext()) {
                            booksList.add(books.next().getValue(Book.class));
                        }

                        Log.d(TAG, "Book arrayList: " + booksList.toString());

                        RecyclerView recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.bookView_view);

                        BooksAdapter recyclerViewAdapter = new BooksAdapter(booksList);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }
}
