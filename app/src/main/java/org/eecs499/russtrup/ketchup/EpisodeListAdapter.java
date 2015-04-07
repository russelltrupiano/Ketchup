package org.eecs499.russtrup.ketchup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> {

    // Showid for API request
    private static String mShowId;
    private ArrayList<Episode> mEpisodes;
    private static Context mContext;
    private ShowInfoFragment mContainerFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Episode details
        public TextView mEpisodeTitle;
        public TextView mAirdateTime;
        public ImageButton mUpdateButton;
        public Episode mEpisode;

        public ViewHolder(final View episodeView) {
            super(episodeView);
            mEpisodeTitle = (TextView) episodeView.findViewById(R.id.show_info_episode_name);
            mAirdateTime = (TextView) episodeView.findViewById(R.id.show_info_episode_airdatetime);
            mUpdateButton = (ImageButton) episodeView.findViewById(R.id.show_info_update_button);
        }
    }

    public EpisodeListAdapter(String showId, Episode[] episodes, Context context, ShowInfoFragment containerFragment) {
        mShowId = showId;
        mEpisodes = new ArrayList<Episode>(Arrays.asList(episodes));
        mContext = context;
        mContainerFragment = containerFragment;
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
    public void onBindViewHolder(final EpisodeListAdapter.ViewHolder holder, final int position) {
        // - replace the contents of the view with that element
        holder.mEpisodeTitle.setText(mEpisodes.get(position).get_title() + " (" +
                mEpisodes.get(position).get_season() + "x" + mEpisodes.get(position).get_episodeNumber() + ")");
        holder.mAirdateTime.setText(mEpisodes.get(position).get_airdate().toString());
        holder.mEpisode = mEpisodes.get(position);
        if (!holder.mEpisode.get_watched()) {
            holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_accept));
        } else {
            holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_remove));
        }

        class UpdateEpisodeCallback implements KetchupAPI.HTTPCallback {

            View _itemView;

            public UpdateEpisodeCallback(View itemView) {
                _itemView = itemView;
            }

            public void setButtonSource() {
                if (!holder.mEpisode.get_watched()) {
                    holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_accept));
                } else {
                    holder.mUpdateButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_remove));
                }
                if (mContainerFragment.getClass().isAssignableFrom(ShowInfoUnwatchedFragment.class)) {
                    mEpisodes.remove(holder.mEpisode);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void invokeCallback(JSONObject response) throws JSONException {
                holder.mEpisode.set_watched(!holder.mEpisode.get_watched());
                setButtonSource();
            }

            @Override
            public void onFail() {
                setButtonSource();
            }
        }

        holder.mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SUB", holder.mEpisode.get_title() + " is " + holder.mEpisode.get_watched());
                KetchupAPI.updateEpisode(
                        mShowId, holder.mEpisode.get_season(), holder.mEpisode.get_episodeNumber(),
                        !holder.mEpisode.get_watched(), new UpdateEpisodeCallback(v));
            }
        });
    }

    public void batchUpdate(Boolean watched) {
        for (Episode mEpisode : mEpisodes) {
            mEpisode.set_watched(watched);
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }
}