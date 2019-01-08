package com.example.android.itbookshop;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.itbookshop.models.Book;
import com.example.android.itbookshop.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksAdapterViewHolder> {
    private List<Book> mBooksList;
    Context context;
    private final BooksAdapterOnClickHandler mClickHandler;

    public BooksAdapter(BooksAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public BooksAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.book_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new BooksAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapterViewHolder booksAdapterViewHolder, int i) {
        String bookCoverUrl = mBooksList.get(i).getCoverUrl();
        try{
            if(bookCoverUrl != null && !bookCoverUrl.isEmpty()){
                Picasso.get().load(bookCoverUrl).into(booksAdapterViewHolder.mImageView);
            }else{
                Picasso.get().load(R.drawable.no_image).into(booksAdapterViewHolder.mImageView);
            }
        }catch (Exception ex){
            Picasso.get().load(R.drawable.no_image).into(booksAdapterViewHolder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mBooksList == null){
            return 0;
        }
        return mBooksList.size();
    }

    public interface BooksAdapterOnClickHandler {
        void onClick(Book book);
    }
    public class BooksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;
        public BooksAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.book_cover);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if(mBooksList != null && mClickHandler != null){
                Book book = mBooksList.get(adapterPosition);
                mClickHandler.onClick(book);
            }
        }
    }
    public void setMoviesList(List<Book> booksList) {
        mBooksList = booksList;
        notifyDataSetChanged();
    }
}
