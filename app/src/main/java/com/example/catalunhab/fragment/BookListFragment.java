package com.example.catalunhab.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


/**
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan
 */
public class BookListFragment extends Fragment {

    private static final String TAG = "BookListFragment";

    public BookListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        getBooksFromDatabase();

        return root;
    }

    /**
     * Reference:
     * Retrieve objects from db - https://riptutorial.com/android/example/25872/retrieving-data-from-firebase
     */
    private void getBooksFromDatabase() {
        FirebaseDatabase.getInstance().getReference().child("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> books = dataSnapshot.getChildren().iterator();
                        ArrayList<Book> booksList = new ArrayList<>();

                        while (books.hasNext()) {
                            Book book = books.next().getValue(Book.class);
                            booksList.add(book);
                        }

                        Log.d(TAG, "Book arrayList: " + booksList.toString());

                        RecyclerView recyclerView = getActivity().findViewById(R.id.bookView_view);

                        BooksAdapter recyclerViewAdapter = new BooksAdapter(booksList);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

}
