package com.example.android.itbookshop.models;

import java.util.List;

public class Book {
    private String title;
    private String isbn13;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Book(){

    }
    public Book(String title, String isbn13, String price, String coverUrl, String bookUrl, String authors, String publishedYear, int numberOfPages, String language, String description) {
        this.title = title;
        this.isbn13 = isbn13;
        setPrice(price);
        this.coverUrl = coverUrl;
        this.bookUrl = bookUrl;
        this.authors = authors;
        this.publishedYear = publishedYear;
        this.numberOfPages = numberOfPages;
        this.language = language;
        this.description = description;
    }

    public Book(String title, String isbn13, String price, String coverUrl, String bookUrl) {
        this.title = title;
        this.isbn13 = isbn13;
        setPrice(price);
        this.coverUrl = coverUrl;
        this.bookUrl = bookUrl;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if(Float.parseFloat(price.replace("$", "")) <= 0.0){
            price = "Free";
        }
        this.price = price;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private String price;
    private String coverUrl;
    private String bookUrl;
    private String authors;
    private String publishedYear;
    private int numberOfPages;
    private String language;
}
