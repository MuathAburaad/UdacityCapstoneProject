package com.example.android.itbookshop.databaseDaos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.models.BookDB;

import java.util.List;

@Dao
public interface BookDao {

    @Query("SELECT * FROM bookdb WHERE email = :email ORDER BY id")
    LiveData<List<BookDB>> loadAllBooks(String email);

    @Insert
    void insertBook(BookDB book);

    @Query("DELETE FROM bookdb WHERE isbn13 = :isbn13 AND email = :email")
    void deleteBook(String isbn13, String email);

    @Query("SELECT * FROM bookdb WHERE isbn13 = :isbn13 AND email = :email")
    BookDB loadBookByIsbn(String isbn13, String email);
}
