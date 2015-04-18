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

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindShowsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindShowsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindShowsFragment extends ContentFragment
    implements PopularShowItemFragment.OnFragmentInteractionListener{

    FindShowsFragment instance;
    Collection<TVShowBase> _popularShows;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindShowsFragment.
     */
    public static FindShowsFragment newInstance(String param1, String param2) {
        FindShowsFragment fragment = new FindShowsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FindShowsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _popularShows = new ArrayList<>();
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View theView = inflater.inflate(R.layout.fragment_find_shows, container, false);

        KetchupAPI.SearchFindShowsHelper(new PopularShowsCallback(theView, R.id.todayShowsContainer), new PopularShowsCallback(theView, R.id.popularShowsContainer));

//        KetchupAPI.searchPopularShows(new PopularShowsCallback(theView, R.id.popularShowsContainer));
//        KetchupAPI.searchTodayShows(new PopularShowsCallback(theView, R.id.todayShowsContainer));

        return theView;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class PopularShowsCallback implements KetchupAPI.HTTPCallback {

        int mId;
        View mView;

        public PopularShowsCallback(View view, int containerId) {
            mView = view;
            mId = containerId;
        }

        @Override
        public void invokeCallback(JSONObject response) throws JSONException {
            FragmentManager fragmentManager = getFragmentManager();
            JSONArray shows;

            try {
                if (response.getInt("status") != 200) {
                    throw new HttpException(response.getInt("status") + ": REQUEST FAILED");
                }
                shows = response.getJSONArray("result");
                if (shows.length() == 0) {
                    throw new IllegalStateException("Result should never be empty");
                }
                Log.i("POPULAR_SHOWS", shows.toString());
            } catch (JSONException | HttpException e) {
                e.printStackTrace();
                return;
            }

            for (int i = 0; i < shows.length(); i++) {
                try {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    JSONObject resultJson = (JSONObject)shows.get(i);
                    String showSlug = resultJson.getString("id");
                    String showTitle = resultJson.getString("title");
                    int showYear = resultJson.getInt("year");
                    String showImage = resultJson.getString("background");

                    TVShowBase show = new TVShowBase(showSlug, showTitle, showYear, showImage);
                    _popularShows.add(show);

                    PopularShowItemFragment result = PopularShowItemFragment.newInstance(showSlug, showTitle, showYear, showImage);
                    result.set_tvshow(show);

                    fragmentTransaction.add(mId, result);
                    fragmentTransaction.commit();

                    result.setParent(instance);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFail() {

        }
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
}
