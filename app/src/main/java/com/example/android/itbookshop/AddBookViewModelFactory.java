package com.example.android.itbookshop;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.itbookshop.database.AppDatabase;

public class AddBookViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mMovieIsbn;
    private final String mUserEmail;

    public AddBookViewModelFactory(AppDatabase database, String movieIsbn, String email) {
        mDb = database;
        mMovieIsbn = movieIsbn;
        mUserEmail = email;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddBookViewModel(mDb, mMovieIsbn, mUserEmail);
    }
}
