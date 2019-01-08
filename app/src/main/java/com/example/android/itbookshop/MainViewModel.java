package com.example.android.itbookshop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.itbookshop.database.AppDatabase;
import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.models.BookDB;

import java.util.List;
import java.util.StringTokenizer;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<BookDB>> books;
    //private String email;

    public MainViewModel(@NonNull Application application, String email) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        books = database.bookDao().loadAllBooks(email);
    }

    public LiveData<List<BookDB>> getBooks() {
        return books;
    }
    /*public void setEmail(String email) {
        this.email = email;
    }*/
}