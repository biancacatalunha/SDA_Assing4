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
package com.example.catalunhab.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.catalunhab.type.Book;
import com.example.sdaassign4_2019.R;

import java.util.ArrayList;


/**
 * DCU - SDA - Assignment 4
 *
 * Adapter to link each book item to the recycler view list
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private ArrayList<Book> dataSet;

    private static final String TAG = "ProductsAdapter";

    /**
     * OnCreateViewHolder this method is called creating a view for each object
     */
    class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;
        TextView authorText;
        TextView titleText;
        Button checkOut;
        RelativeLayout itemParentLayout;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.bookImage);
            authorText = itemView.findViewById(R.id.authorText);
            titleText = itemView.findViewById(R.id.bookTitle);
            checkOut = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);
        }
    }

    /**
     * Initializes the ArrayList of products
     *
     * @param dataSet list of products
     */
    public BooksAdapter(ArrayList<Book> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Attaches the view group to the fragment item
     * Sets an onClick listener to each book image
     * When the user clicks on it, a toast message is displayed showing the title of the book
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        ImageView bookImage = v.findViewById(R.id.bookImage);
        final TextView bookTitle = v.findViewById(R.id.bookTitle);

        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(v.getContext(), bookTitle.getText().toString(), Toast.LENGTH_SHORT);
                toast.show();
                Log.d(TAG, "Book clicked: " + bookTitle.getText().toString());
            }
        });
        Log.d(TAG, "Book View holder created");

        return new BookViewHolder(v);
    }

    /**
     * Sets the holder object with the data entered in BookListFragment
     *
     * @param holder the object that stores a product
     * @param position gets the array position
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        String url = "https://firebasestorage.googleapis.com/v0/b/sda-assign4.appspot.com/o" + dataSet.get(position).getImage();

        Log.d(TAG, "URL: " + url);

        holder.titleText.setText(dataSet.get(position).getTitle());
        holder.authorText.setText(dataSet.get(position).getAuthor());
        Glide
                .with(holder.imageItem.getContext())
                .load(url)
        .into(holder.imageItem);
    }

    /**
     * @return the amount of books in the array
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
