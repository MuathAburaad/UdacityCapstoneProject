package com.example.android.itbookshop.utilities;

import com.example.android.itbookshop.models.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private final static String MOVIES_LIST = "books";
    private final static String TITLE = "title";
    private final static String AUTHORS = "authors";
    private final static String ISBN13 = "isbn13";
    private final static String NUMBER_OF_PAGES = "pages";
    private final static String PUBLISHED_YEAR = "year";
    private final static String DESCRIPTION = "desc";
    private final static String PRICE = "price";
    private final static String COVER_IMAGE_URL = "image";
    private final static String BOOK_URL = "url";
    private final static String LANGUAGE = "language";

    public static List<Book> getBooksListFromJsonString(String jsonString){

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray booksJsonArray = jsonObject.getJSONArray(MOVIES_LIST);
            List<Book> booksList = new ArrayList<Book>();
            if (booksJsonArray != null) {
                for (int i=0;i<booksJsonArray.length();i++){
                    String title = "";
                    String isbn13 = "";
                    String price = "";
                    String coverUrl = "";
                    String bookUrl = "";
                    List<String> authors = new ArrayList<>();
                    String publishedYear = "";
                    int numberOfPages;
                    String language = "";
                    JSONObject bookJson = booksJsonArray.getJSONObject(i);
                    if(bookJson.has(TITLE)){
                        title = bookJson.getString(TITLE);
                    }
                    if(bookJson.has(ISBN13)){
                        isbn13 = bookJson.getString(ISBN13);
                    }
                    if(bookJson.has(PRICE)){
                        price = bookJson.getString(PRICE);
                    }
                    if(bookJson.has(COVER_IMAGE_URL)){
                        coverUrl = bookJson.getString(COVER_IMAGE_URL);
                    }
                    if(bookJson.has(BOOK_URL)){
                        bookUrl = bookJson.getString(BOOK_URL);
                    }
                    Book book = new Book(title, isbn13, price, coverUrl, bookUrl);
                    booksList.add(book);
                }
            }
            return booksList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<Book>();
        }
    }

    public static Book getBookDetailsFromJsonString(String jsonString){

        try {
            JSONObject bookJson = new JSONObject(jsonString);
            String title = "";
            String isbn13 = "";
            String price = "";
            String coverUrl = "";
            String bookUrl = "";
            String authors = "";
            String publishedYear = "";
            int numberOfPages = 0;
            String language = "";
            String description = "";
            if(bookJson.has(TITLE)){
                title = bookJson.getString(TITLE);
            }
            if(bookJson.has(ISBN13)){
                isbn13 = bookJson.getString(ISBN13);
            }
            if(bookJson.has(PRICE)){
                price = bookJson.getString(PRICE);
            }
            if(bookJson.has(COVER_IMAGE_URL)){
                coverUrl = bookJson.getString(COVER_IMAGE_URL);
            }
            if(bookJson.has(BOOK_URL)){
                bookUrl = bookJson.getString(BOOK_URL);
            }if(bookJson.has(AUTHORS)){
                authors = bookJson.getString(AUTHORS);
            }if(bookJson.has(PUBLISHED_YEAR)){
                publishedYear = bookJson.getString(PUBLISHED_YEAR);
            }if(bookJson.has(NUMBER_OF_PAGES)){
                numberOfPages = bookJson.getInt(NUMBER_OF_PAGES);
            }if(bookJson.has(LANGUAGE)){
                language = bookJson.getString(LANGUAGE);
            }if(bookJson.has(DESCRIPTION)){
                description = bookJson.getString(DESCRIPTION);
            }
            Book book = new Book(title, isbn13, price, coverUrl, bookUrl, authors, publishedYear, numberOfPages, language, description);
            return book;
        } catch (JSONException e) {
            e.printStackTrace();
            return new Book();
        }
    }

    /*public static Movie getMovieFromJsonString(String jsonString){

        try {
            JSONObject movieJson = new JSONObject(jsonString);
            if (movieJson != null) {
                int id;
                String originalTitle;
                String posterImg;
                String overview;
                Double userRating;
                String releaseDate;
                id = movieJson.getInt(ID);
                originalTitle = movieJson.getString(ORIGINAL_TITLE);
                posterImg = movieJson.getString(POSTER_IMG);
                overview = movieJson.getString(OVERVIEW);
                userRating = movieJson.getDouble(USER_RATING);
                releaseDate = movieJson.getString(RELEASE_DATE);
                Movie movie = new Movie(id, originalTitle, posterImg, overview, userRating, releaseDate);
                return movie;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /*public static List<Trailer> getMovieTrailersFromJsonString(String jsonString){

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray trailersJsonArray = jsonObject.getJSONArray(TRAILERS_LIST);
            List<Trailer> trailersList = new ArrayList<Trailer>();
            if (trailersJsonArray != null) {
                for (int i=0;i<trailersJsonArray.length();i++){
                    String key;
                    String site;
                    String type;
                    JSONObject trailerJson = trailersJsonArray.getJSONObject(i);
                    key = trailerJson.getString(TRAILER_KEY);
                    site = trailerJson.getString(TRAILER_SITE);
                    type = trailerJson.getString(TRAILER_TYPE);
                    Trailer trailer = new Trailer(key, site, type);
                    trailersList.add(trailer);
                }
            }
            return trailersList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<Trailer>();
        }
    }*/

    /*public static List<Review> getMovieReviewsFromJsonString(String jsonString){

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray reviewsJsonArray = jsonObject.getJSONArray(REVIEWS_LIST);
            List<Review> reviewsList = new ArrayList<Review>();
            if (reviewsJsonArray != null) {
                for (int i=0;i<reviewsJsonArray.length();i++){
                    String author;
                    String content;
                    JSONObject reviewJson = reviewsJsonArray.getJSONObject(i);
                    author = reviewJson.getString(REVIEW_AUTHOR);
                    content = reviewJson.getString(REVIEW_CONTENT);
                    Review review = new Review(author, content);
                    reviewsList.add(review);
                }
            }
            return reviewsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<Review>();
        }
    }*/
}
