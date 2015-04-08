package org.eecs499.russtrup.ketchup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class HorizontalPosterAdapter extends RecyclerView.Adapter<HorizontalPosterAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Search result details
        public TextView mShowTitle;
        public ImageButton mUpdateButton;
        public TVShowBase mTVShow;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public HorizontalPosterAdapter() {
    }

    @Override
    public HorizontalPosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(HorizontalPosterAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
