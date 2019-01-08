package com.example.android.itbookshop.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String NEW_BOOKS_URL = "https://api.itbook.store/1.0/new";
    private static final String SEARCH_BOOKS_URL = "https://api.itbook.store/1.0/search/";
    private static final String SPECIFIC_BOOK_URL = "https://api.itbook.store/1.0/books/";


    public static URL buildNewBooksUrl() {
        URL url = null;
        try {
            url = new URL(NEW_BOOKS_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "URI " + url);

        return url;
    }

    public static URL buildSearchBooksUrl(String subject) {
        URL url = null;
        try {
            url = new URL(SEARCH_BOOKS_URL + subject);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "URI " + url);

        return url;
    }

    public static URL buildBookDetailsUrl(String isbn13) {
        URL url = null;
        try {
            url = new URL(SPECIFIC_BOOK_URL + isbn13);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "URI " + url);

        return url;
    }

    /*public static URL buildMovieTrailersUrl(int movieId) {
        Uri builtUri = Uri.parse(SPECIFIC_MOVIES_URL + movieId + TRAILERS_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "URI " + url);

        return url;
    }

    public static URL buildMovieReviewsUrl(int movieId) {
        Uri builtUri = Uri.parse(SPECIFIC_MOVIES_URL + movieId + REVIEWS_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "URI " + url);

        return url;
    }

    public static String getYoutubeUrl(String key) {
        Log.d("Youtube link: ", YOUTUBE_URL + "key" );
        return YOUTUBE_URL + key;
    }*/

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
