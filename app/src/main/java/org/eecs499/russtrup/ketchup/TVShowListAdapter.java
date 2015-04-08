package org.eecs499.russtrup.ketchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class TVShowListAdapter extends RecyclerView.Adapter<TVShowListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TVShow> mTVShows;
    private MyShowsListitemFragment mFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Episode details
        public TextView mEpisodeTitle;
        public TextView mAirdateTime;
        public TextView mNetwork;
        public ImageButton mOptionsButton;
        public ImageView mThumbnail;
        public ImageView mBadgeIcon;
        public RelativeLayout mLayout;
        public TVShow mTVShow;

        public ViewHolder(final View episodeView) {
            super(episodeView);
            mEpisodeTitle = (TextView) episodeView.findViewById(R.id.showTitle);
            mAirdateTime = (TextView) episodeView.findViewById(R.id.showTime);
            mNetwork = (TextView) episodeView.findViewById(R.id.showNetwork);
            mOptionsButton = (ImageButton) episodeView.findViewById(R.id.item_options);
            mThumbnail = (ImageView) episodeView.findViewById(R.id.showThumbnail);
            mBadgeIcon = (ImageView) episodeView.findViewById(R.id.myshow_unwatced_count_image);
            mLayout = (RelativeLayout) episodeView.findViewById(R.id.itemLayout);
        }
    }

    public TVShowListAdapter(MyShowsFragment myShowsFragment, TVShow[] tvshows, Context context) {
        mTVShows = new ArrayList<>(Arrays.asList(tvshows));
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mFragment = new MyShowsListitemFragment();
        return new ViewHolder(mFragment.get_view());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTVShow = mTVShows.get(position);

        holder.mEpisodeTitle.setText(holder.mTVShow.get_title());
        holder.mAirdateTime.setText(holder.mTVShow.get_airday() +  " @ " + holder.mTVShow.get_airtime());
        holder.mNetwork.setText(holder.mTVShow.get_network());

        if (holder.mTVShow.get_imageUrl() != null && !holder.mTVShow.get_imageUrl().equals("")) {
            Picasso.with(mContext).load(holder.mTVShow.get_imageUrl()).into(holder.mThumbnail);
        }

        mFragment.setUnwatchedBadge(holder.mBadgeIcon, holder.mTVShow.get_num_unwatched());

        holder.mEpisodeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.loadShowInfo();
            }
        });
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.loadShowInfo();
            }
        });
        holder.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.unsubClickListener();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTVShows.size();
    }
}
