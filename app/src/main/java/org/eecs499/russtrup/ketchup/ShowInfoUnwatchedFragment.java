package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.ObservableScrollView;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowInfoUnwatchedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowInfoUnwatchedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowInfoUnwatchedFragment extends ShowInfoFragment {

    private TVShow _tvshow;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowInfoUnwatchedFragment.
     */
    public static ShowInfoUnwatchedFragment newInstance(TVShow show) {
        ShowInfoUnwatchedFragment fragment = new ShowInfoUnwatchedFragment();
        fragment.set_tvshow(show);
        return fragment;
    }

    public ShowInfoUnwatchedFragment() {
        // Required empty public constructor
    }

    private void set_tvshow(TVShow tvshow) {
        _tvshow = tvshow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView =  inflater.inflate(R.layout.fragment_show_info_unwatched, container, false);

        final ImageView showInfoHeaderImage = (ImageView) theView.findViewById(R.id.showInfoHeaderImage);
        final LinearLayout headerImageWrapper = (LinearLayout) theView.findViewById(R.id.image_header_wrapper);
        TextView showInfoHeaderTitle = (TextView) theView.findViewById(R.id.showInfoShowName);
        TextView showInfoHeaderNetwork = (TextView) theView.findViewById(R.id.showInfoNetwork);
        TextView showInfoHeaderAirtime = (TextView) theView.findViewById(R.id.showInfoAirTime);

        if (_tvshow.get_headerUrl() != null && !_tvshow.get_headerUrl().equals("")) {
            Picasso.with(getActivity().getApplicationContext()).load(_tvshow.get_headerUrl()).into(showInfoHeaderImage);
        }
        showInfoHeaderTitle.setText(_tvshow.get_title());
        showInfoHeaderNetwork.setText(_tvshow.get_network());
        showInfoHeaderAirtime.setText(_tvshow.get_airday() + " @ " + _tvshow.get_airtime());

        mRecyclerView = (RecyclerView) theView.findViewById(R.id.episode_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EpisodeListAdapter(_tvshow.get_id(), _tvshow.get_unwatched_episodes(), getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        return theView;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateModel() {
        mAdapter = new EpisodeListAdapter(_tvshow.get_id(), _tvshow.get_unwatched_episodes(), getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}
