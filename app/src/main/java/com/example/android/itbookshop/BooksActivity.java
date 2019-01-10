package com.example.android.itbookshop;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.example.android.itbookshop.database.AppDatabase;
import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.models.BookDB;
import com.example.android.itbookshop.utilities.JsonUtils;
import com.example.android.itbookshop.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements BooksAdapter.BooksAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private BooksAdapter mBooksAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private SearchView searchView;

    private AdView mAdView;

    private List<BookDB> favoriteBooks;
    private int scrollX;
    private int scrollY;
    private String currentAction;
    private String searchQuery;

    private String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.books_toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, getString(R.string.mobile_ads_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_books);
        mBooksAdapter = new BooksAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBooksAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.books_loading_progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.books_loading_error_message);

        currentAction = getString(R.string.explore_new_books); // default explore
        // to remove this line when you return back the list and savedintance code
        //new FetchBooksTask().execute(currentAction);
        userEmail = "";
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.login_user_email))) {
            userEmail = intent.getStringExtra(getString(R.string.login_user_email));
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(getString(R.string.books_scroll_x)) && savedInstanceState.containsKey(getString(R.string.books_scroll_y))) {
                scrollX = savedInstanceState
                        .getInt(getString(R.string.books_scroll_x));
                scrollY = savedInstanceState
                        .getInt(getString(R.string.books_scroll_y));
            }
            if (savedInstanceState.containsKey(getString(R.string.books_action_type))) {
                currentAction = savedInstanceState
                        .getString(getString(R.string.books_action_type));
            }
        }
        if(currentAction.equals(getString(R.string.explore_favorite_books))) {
            loadFavoriteBooks();
        }
        else if(currentAction.equals(getString(R.string.search_for_books))) {
            searchQuery = "";
            if(savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.books_search_query))) {
                searchQuery = savedInstanceState.getString(getString(R.string.books_search_query));
            }
            loadSearchedBooks(searchQuery);
        }
        else {
            new FetchBooksTask().execute(currentAction);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainViewModel viewModel = new MainViewModel(this.getApplication(), userEmail);// ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookDB>>() {
            @Override
            public void onChanged(@Nullable List<BookDB> bookList) {
                favoriteBooks = bookList;
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(BooksActivity.this);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(BooksActivity.this, ITBookWidgetProvider.class));
                //Trigger data update to handle the GridView widgets and force a data refresh
                //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);
                //Now update all widgets
                ITBookWidgetProvider.updateAppWidgets(BooksActivity.this, appWidgetManager, appWidgetIds, getString(R.string.appwidget_text_favorite_books) + " " +favoriteBooks.size());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(scrollY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    //Toast.makeText(BooksActivity.this, s, Toast.LENGTH_SHORT).show();
                    /*currentAction = getString(R.string.search_for_books);
                    new FetchBooksTask().execute(currentAction, s);*/
                    loadSearchedBooks(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }
        if(currentAction.equals(getString(R.string.search_for_books)) && searchQuery != null) {
            searchView.setQuery(searchQuery, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            signOut();
        }
        else if (id == R.id.action_explore_new) {
            clearSearch();
            new FetchBooksTask().execute(getString(R.string.explore_new_books));
        }
        else if (id == R.id.action_explore_favorites) {
            currentAction = getString(R.string.explore_favorite_books);
            clearSearch();
            loadFavoriteBooks();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBooksList() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Book book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra(getString(R.string.book_details_isbn13), book.getIsbn13());
        intent.putExtra(getString(R.string.login_user_email), userEmail);

        Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        }else{
            startActivity(intent);
        }

    }

    public class FetchBooksTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Book> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL requestUrl = null;
            if(sortBy.equals(getString(R.string.explore_new_books))){
                requestUrl = NetworkUtils.buildNewBooksUrl();
                return getBooksList(requestUrl);
            }
            else if(sortBy.equals(getString(R.string.search_for_books))){
                String searchTopic = params[1];
                requestUrl = NetworkUtils.buildSearchBooksUrl(searchTopic);
                return getBooksList(requestUrl);
            }
            else if(sortBy.equals(getString(R.string.explore_book_details))){
                String isbn13 = params[1];
                requestUrl = NetworkUtils.buildBookDetailsUrl(isbn13);
                return getBooksList(requestUrl);
            }
            else if(sortBy.equals(getString(R.string.explore_favorite_books))) {
                List<Book> booksList = new ArrayList<>();
                if(favoriteBooks != null){
                    for(int i = 0; i < favoriteBooks.size(); i++){
                        requestUrl = NetworkUtils.buildBookDetailsUrl(favoriteBooks.get(i).getIsbn13());
                        if(requestUrl != null) {
                            try {
                                String jsonBookResponse = NetworkUtils
                                        .getResponseFromHttpUrl(requestUrl);
                                //System.out.print(jsonMoviesResponse);

                                Book book = JsonUtils.getBookDetailsFromJsonString(jsonBookResponse);
                                if(book != null){
                                    booksList.add(book);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }

                        }
                    }
                }
                return booksList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Book> booksList) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (booksList != null) {
                showBooksList();
                mBooksAdapter.setMoviesList(booksList);
                ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(scrollY);

            } else {
                showErrorMessage();
            }
        }
    }

    private List<Book> getBooksList(URL requestUrl){
        try {
            if(requestUrl != null){
                String jsonBooksResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);
                System.out.print(jsonBooksResponse);


                List<Book> booksList = JsonUtils.getBooksListFromJsonString(jsonBooksResponse);

                return booksList;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*@Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_out_button) {
            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            // Build a GoogleSignInClient with the specified options
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut().onSuccessTask(new SuccessContinuation<Void, Object>() {
                @NonNull
                @Override
                public Task<Object> then(@Nullable Void aVoid) throws Exception {
                    Toast.makeText(BooksActivity.this, "sign out", Toast.LENGTH_LONG).show();
                    return null;
                }
            });
        }
    }*/
    private void signOut() {
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the specified options
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().onSuccessTask(new SuccessContinuation<Void, Object>() {
            @NonNull
            @Override
            public Task<Object> then(@Nullable Void aVoid) throws Exception {
                Intent intent = new Intent(BooksActivity.this, LoginActivity.class);
                Bundle bundle = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation(BooksActivity.this).toBundle();
                    startActivity(intent, bundle);
                }else{
                    startActivity(intent);
                }
                finish();
                return null;
            }
        });
    }

    private void loadFavoriteBooks() {
        MainViewModel viewModel = new MainViewModel(this.getApplication(), userEmail);// ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookDB>>() {
            @Override
            public void onChanged(@Nullable List<BookDB> bookList) {
                favoriteBooks = bookList;
                String favoriteAction = getString(R.string.explore_favorite_books);
                if(currentAction.equals(favoriteAction)){
                    new FetchBooksTask().execute(favoriteAction);
                }
            }
        });
    }
    private void loadSearchedBooks(String query) {
        currentAction = getString(R.string.search_for_books);
        new FetchBooksTask().execute(currentAction, query);
    }
    private void clearSearch() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.books_action_type), currentAction);
        outState.putInt(getString(R.string.books_scroll_x), mRecyclerView.getScrollX());
        outState.putInt(getString(R.string.books_scroll_y), ((GridLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        outState.putString(getString(R.string.books_search_query), searchView.getQuery().toString());
    }
}
