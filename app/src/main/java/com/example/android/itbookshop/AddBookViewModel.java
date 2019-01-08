package com.example.android.itbookshop;

import android.arch.lifecycle.ViewModel;

import com.example.android.itbookshop.database.AppDatabase;
import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.models.BookDB;


public class AddBookViewModel extends ViewModel {
    private BookDB book;

    // Note: The constructor should receive the database and the movieId
    public AddBookViewModel(AppDatabase database, String bookIsbn, String email) {
        book = database.bookDao().loadBookByIsbn(bookIsbn, email);
    }

    public BookDB getBook() {
        return book;
    }
}
