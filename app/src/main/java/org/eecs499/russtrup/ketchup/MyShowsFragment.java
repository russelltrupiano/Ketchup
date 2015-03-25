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

import java.util.ArrayList;

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
    private ArrayList<Fragment> myShows;
    private MyShowsFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyShowsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyShowsFragment newInstance(String param1, String param2) {
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

            Log.i("MYSHOWS", "Showing " + results.length() + " shows.");

            for (int i = 0; i < results.length(); i++) {

                try {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    MyShowsListitemFragment result = new MyShowsListitemFragment();

                    myShows.add(result);

                    JSONObject resultJson = (JSONObject)results.get(i);
                    String resultId = resultJson.getString("id");
                    String resultName = resultJson.getString("title");
                    String resultImage = resultJson.getString("imageUrl");
                    String resultTime = resultJson.getString("airtime");
                    String resultDay = resultJson.optString("airday", "???");

                    Log.i("MY SHOW", resultName + " - " + resultId);
                    String resultNetwork = resultJson.getString("network");


                    fragmentTransaction.add(R.id.myShowsList, result);
                    fragmentTransaction.commit();
                    result.fillData(resultId, resultName, resultImage, resultTime, resultDay, resultNetwork);
                    result.setParent(instance);

                    numShows++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFail() {
            Log.i("CALLBACK", "My Shows Failed");
        }
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

    public void removeShow(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        numShows--;
    }

}

