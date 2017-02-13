package com.example.android.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by toddskinner on 2/12/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ArrayList<String[]> mReviewItems;

    public ReviewsAdapter() {

    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        public final TextView reviewListItemNumberView;
        public final TextView reviewListItemAuthorView;
        public final TextView reviewListItemContentView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewListItemNumberView = (TextView) itemView.findViewById(R.id.tv_review_number);
            reviewListItemAuthorView = (TextView) itemView.findViewById(R.id.tv_review_author);
            reviewListItemContentView = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewsAdapter.ReviewViewHolder viewHolder = new ReviewsAdapter.ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String[] infoForThisReview = mReviewItems.get(position);
        String number = infoForThisReview[0];
        String author = infoForThisReview[1];
        String content = infoForThisReview[2];

        holder.reviewListItemNumberView.setText(number);
        holder.reviewListItemAuthorView.setText(author);
        holder.reviewListItemContentView.setText(content);

    }

    @Override
    public int getItemCount() {
        if(mReviewItems == null){
            return 0;
        } else {
            return mReviewItems.size();
        }
    }

    public void setReviewsData(ArrayList<String[]> reviewData){
        mReviewItems = reviewData;
        notifyDataSetChanged();
    }
}
