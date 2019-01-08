package com.example.android.itbookshop;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.itbookshop.database.AppDatabase;
import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.models.BookDB;
import com.example.android.itbookshop.utilities.JsonUtils;
import com.example.android.itbookshop.utilities.NetworkUtils;
import com.google.android.gms.common.data.DataBufferUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {

    private LinearLayout mBookDetailsLayout;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private String isbn13;
    private ImageView mBookCover;
    private TextView mBookAuthors;
    private TextView mBookPrice;
    private TextView mBookPublishedYear;
    private TextView mBookDescription;
    private TextView mBookLanguage;
    private TextView mBookNumberOfPages;
    private Button mBookOpenInWebsiteButton;
    private ImageButton mBookFavorite;

    private AppDatabase mDb;
    private static BookDB mBook;
    private String userEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        mBookDetailsLayout = (LinearLayout) findViewById(R.id.book_details);
        mBookCover = (ImageView) findViewById(R.id.book_detail_cover);
        mBookAuthors = findViewById(R.id.detail_book_authors_value);
        mBookPrice = findViewById(R.id.detail_book_price_value);
        mBookPublishedYear = findViewById(R.id.detail_book_published_year_value);
        mBookDescription = findViewById(R.id.detail_book_description_value);
        mBookLanguage = findViewById(R.id.detail_book_language_value);
        mBookNumberOfPages = findViewById(R.id.detail_book_pages_value);
        mBookOpenInWebsiteButton = findViewById(R.id.detail_book_open_in_website_button);
        mBookFavorite = (ImageButton) findViewById(R.id.book_favorite_button);

        mProgressBar = (ProgressBar) findViewById(R.id.book_details_loading_progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.book_details_loading_error_message);

        mDb = AppDatabase.getsInstance(getApplicationContext());
        mBookFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite();
            }
        });

        final Intent intent = getIntent();
        String bookIsbn13 = "";
        if(intent.hasExtra(getString(R.string.book_details_isbn13))){
            bookIsbn13 = intent.getStringExtra(getString(R.string.book_details_isbn13));
            new FetchBookDetailsTask().execute(bookIsbn13);
        }
        if(intent.hasExtra(getString(R.string.login_user_email))) {
            userEmail = intent.getStringExtra(getString(R.string.login_user_email));
        }
        mBook = new BookDB(bookIsbn13, userEmail);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BookDB book = mDb.bookDao().loadBookByIsbn(mBook.getIsbn13(), mBook.getEmail());
                if(book != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBookFavorite.setImageResource(R.drawable.ic_favourite);
                        }
                    });
                }
            }
        });
    }

    public class FetchBookDetailsTask extends AsyncTask<String, Void, Book> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Book doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            URL requestUrl = null;
            String isbn13 = params[0];
            requestUrl = NetworkUtils.buildBookDetailsUrl(isbn13);
            return getBookDetails(requestUrl);
        }

        @Override
        protected void onPostExecute(Book book) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (book != null) {
                showBookDetails();
                fillBookDetails(book);

            } else {
                showErrorMessage();
            }
        }
    }

    private Book getBookDetails(URL requestUrl){
        try {
            if(requestUrl != null){
                String jsonBookDetailsResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);
                System.out.print(jsonBookDetailsResponse);


                Book detailedBook = JsonUtils.getBookDetailsFromJsonString(jsonBookDetailsResponse);

                return detailedBook;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void fillBookDetails(final Book book) {
        String coverUrl = book.getCoverUrl();
        if(coverUrl != null && !coverUrl.isEmpty()) {
            Picasso.get().load(book.getCoverUrl()).into(mBookCover);
        }
        else {
            Picasso.get().load(R.drawable.no_image).into(mBookCover);
        }

        mBookOpenInWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookUrl = book.getBookUrl();
                Uri bookUri = Uri.parse(bookUrl);
                Intent bookIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                if (bookIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(bookIntent);
                }
            }
        });
        mBookAuthors.setText(book.getAuthors());
        mBookPrice.setText(book.getPrice());
        mBookNumberOfPages.setText("" + book.getNumberOfPages());
        mBookPublishedYear.setText(book.getPublishedYear());
        mBookLanguage.setText(book.getLanguage());
        mBookDescription.setText(book.getDescription());
    }

    public void toggleFavorite() {
        if(mBook != null) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //Movie movie = mDb.movieDao().loadMovieById(mMovie.getId());
                    AddBookViewModelFactory factory = new AddBookViewModelFactory(mDb, mBook.getIsbn13(), mBook.getEmail());
                    AddBookViewModel viewModel = ViewModelProviders.of(BookDetailsActivity.this, factory).get(AddBookViewModel.class);
                    BookDB book = viewModel.getBook();
                    if(book != null){
                        mDb.bookDao().deleteBook(mBook.getIsbn13(), mBook.getEmail());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBookFavorite.setImageResource(R.drawable.ic_favourite_empty);
                            }
                        });
                    }
                    else {
                        mDb.bookDao().insertBook(mBook);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBookFavorite.setImageResource(R.drawable.ic_favourite);
                            }
                        });
                    }
                }
            });
        }
    }

    private void showBookDetails() {
        mBookDetailsLayout.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }
    private void showErrorMessage() {
        mBookDetailsLayout.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}
