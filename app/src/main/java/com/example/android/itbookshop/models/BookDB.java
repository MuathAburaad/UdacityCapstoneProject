package com.example.android.itbookshop.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "bookdb")
public class BookDB {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BookDB(String isbn13, String email) {
        this.isbn13 = isbn13;
        this.email = email;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String isbn13;
    private String email;
}
