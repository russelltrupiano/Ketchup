package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowInfoManageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowInfoManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowInfoManageFragment extends ShowInfoFragment {

    private int _selectedSeason;
    private TVShow _tvshow;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowInfoManageFragment.
     */
    public static ShowInfoManageFragment newInstance(TVShow show) {
        ShowInfoManageFragment fragment = new ShowInfoManageFragment();
        fragment.set_tvshow(show);
        return fragment;
    }

    public ShowInfoManageFragment() {
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
        View theView = inflater.inflate(R.layout.fragment_show_info_manage, container, false);

        ImageView showInfoHeaderImage = (ImageView) theView.findViewById(R.id.showInfoHeaderImage);
        TextView showInfoHeaderTitle = (TextView) theView.findViewById(R.id.showInfoShowName);
        TextView showInfoHeaderNetwork = (TextView) theView.findViewById(R.id.showInfoNetwork);
        TextView showInfoHeaderAirtime = (TextView) theView.findViewById(R.id.showInfoAirTime);

        Spinner seasonSpinner = (Spinner) theView.findViewById(R.id.season_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, _tvshow.get_seasons_array());
        seasonSpinner.setAdapter(adapter);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // position is 0-indexed, and seasons never skip
                _selectedSeason = position + 1;

                updateModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        // Default Setting
        _selectedSeason = 1;
        mAdapter = new EpisodeListAdapter(_tvshow.get_id(),
                _tvshow.get_episodes_for_season(_selectedSeason), getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        return theView;
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
        mAdapter = new EpisodeListAdapter(_tvshow.get_id(),
                _tvshow.get_episodes_for_season(_selectedSeason), getActivity().getApplicationContext(), this);
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
