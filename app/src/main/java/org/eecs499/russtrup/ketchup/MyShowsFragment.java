package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyShowsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyShowsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyShowsFragment extends ContentFragment
        implements MyShowsListitemFragment.OnFragmentInteractionListener{

    private OnFragmentInteractionListener mListener;

    private int numShows;
    private Collection<TVShow> myShows;
    private MyShowsFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyShowsFragment.
     */
    public static MyShowsFragment newInstance() {
        MyShowsFragment fragment = new MyShowsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyShowsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numShows = 0;
        myShows = new ArrayList<>();
        instance = this;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyShowsCallback implements KetchupAPI.HTTPCallback {

        View redirectView;

        public MyShowsCallback(View v) {
            redirectView = v;
        }

        @Override
        public void invokeCallback(JSONObject response) {
            FragmentManager fragmentManager =  getFragmentManager();
            JSONArray results;

            try {
                results = response.getJSONArray("shows");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            if (results.length() == 0) {
                redirectView.findViewById(R.id.noShowsPrompt).setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < results.length(); i++) {

                try {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    MyShowsListitemFragment result = new MyShowsListitemFragment();

                    JSONObject resultJson = (JSONObject)results.get(i);
                    String resultId = resultJson.getString("id");
                    String resultName = resultJson.getString("title");
                    String resultImage = resultJson.getString("imageUrl");
                    String resultHeader = resultJson.getString("headerUrl");
                    String resultTime = resultJson.getString("airtime");
                    String resultDay = resultJson.optString("airday", "???");
                    String resultNetwork = resultJson.optString("network", "???");

                    ArrayList<Episode> episodes = buildEpisodeArrayFromJson(resultJson.getJSONArray("episodes"));

                    TVShow show = new TVShow(resultId, resultName, resultNetwork, resultDay, resultTime, resultImage, resultHeader);
                    show.importEpisodes(episodes);
                    result.set_tvshow(show);
                    myShows.add(show);

                    fragmentTransaction.add(R.id.myShowsList, result);
                    fragmentTransaction.commit();

                    result.setParent(instance);

                    numShows++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            User.get_instance().updateSubscriptions(myShows);
        }

        @Override
        public void onFail() {
            Log.i("CALLBACK", "My Shows Failed");
        }
    }

    private ArrayList<Episode> buildEpisodeArrayFromJson(JSONArray episodes) {
        ArrayList<Episode> episodesArr = new ArrayList<>();
        int numEpisodes = episodes.length();

        for (int i = 0; i < numEpisodes; i++) {
            try {
                String epTitle = episodes.getJSONObject(i).getString("title");
                int epSeason = episodes.getJSONObject(i).getInt("season");
                int epEpNumber = episodes.getJSONObject(i).getInt("episodeNumber");
                Date epAirdate = buildDateFromYYYYMMDD(episodes.getJSONObject(i).getString("airdate"));
                Boolean epWatched = episodes.getJSONObject(i).getBoolean("watched");
                Episode episode = new Episode(epTitle, epSeason, epEpNumber, epAirdate, epWatched);
                episodesArr.add(episode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return episodesArr;
    }

    // Date in form yyyy-mm-dd
    private Date buildDateFromYYYYMMDD(String airdate) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(airdate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_my_shows, container, false);

        KetchupAPI.getMyShows(new MyShowsCallback(theView));

        FloatingActionButton fab = (FloatingActionButton) theView.findViewById(R.id.fab);
        ObservableScrollView scrollView = (ObservableScrollView) theView.findViewById(R.id.myShowsScrollView);
        fab.attachToScrollView(scrollView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.loadSearch(getActivity());
            }
        });

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

    public void removeShow(Fragment fragment, TVShow tvshow) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        numShows--;

        myShows.remove(tvshow);
        User.get_instance().updateSubscriptions(myShows);
    }

}

