package org.eecs499.russtrup.ketchup;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> {

    // Showid for API request
    private static String mShowId;
    private Episode[] mEpisodes;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Episode details
        public TextView mEpisodeTitle;
        public TextView mAirdateTime;
        public ImageButton mUpdateButton;
        public Episode mEpisode;

        public ViewHolder(View episodeView) {
            super(episodeView);
            mEpisodeTitle = (TextView) episodeView.findViewById(R.id.show_info_episode_name);
            mAirdateTime = (TextView) episodeView.findViewById(R.id.show_info_episode_airdatetime);
            mUpdateButton = (ImageButton) episodeView.findViewById(R.id.show_info_update_button);

            mUpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KetchupAPI.updateEpisode(mShowId, mEpisode.get_season(), mEpisode.get_episodeNumber(), !mEpisode.get_watched());
                }
            });
        }
    }

    public EpisodeListAdapter(String showId, Episode[] episodes, Context context) {
        mShowId = showId;
        mEpisodes = episodes;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EpisodeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_show_info_episode_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EpisodeListAdapter.ViewHolder holder, int position) {
        // - replace the contents of the view with that element
        holder.mEpisodeTitle.setText(mEpisodes[position].get_title());
        holder.mAirdateTime.setText(mEpisodes[position].get_airdate().toString());
        holder.mEpisode = mEpisodes[position];
        if (!holder.mEpisode.get_watched()) {
            holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_accept));
        } else {
            holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_remove));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEpisodes.length;
    }
}