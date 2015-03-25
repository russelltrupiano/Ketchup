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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String _id;
    private String _name;
    private String _time;
    private String _day;
    private String _network;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1, String param2) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View theView = inflater.inflate(R.layout.fragment_search_result, container, false);

        ((TextView) theView.findViewById(R.id.showTitle)).setText(_name);
        ((TextView) theView.findViewById(R.id.showTime)).setText(_day + " @ " + _time);
        ((TextView) theView.findViewById(R.id.showNetwork)).setText(_network);

        theView.findViewById(R.id.addShowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            KetchupAPI.subscribeToShow(_id, new SubscribeCallback(v));
            theView.findViewById(R.id.addShowBtn).setVisibility(View.INVISIBLE);
            theView.findViewById(R.id.removeShowBtn).setVisibility(View.VISIBLE);
            }
        });

        return theView;
    }

    public class SubscribeCallback implements KetchupAPI.HTTPCallback {

        View redirectView;

        public SubscribeCallback(View v) {
            redirectView = v;
        }

        @Override
        public void invokeCallback(JSONObject response) {
            Log.i("CALLBACK", "Subscribe Succeeded: " + response.toString());
        }

        @Override
        public void onFail() {
            Log.i("CALLBACK", "Subscribe Failed");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void fillData(String id, String name, String imageUrl, String time, String day, String network) {
        _id = id;
        _name = name;
        _time = formatTime(time);
        _day = day;
        _network = network;
    }

    private String formatTime(String time) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat outputDf = new SimpleDateFormat("hh:mm aa");

        Date date;
        String output = null;

        try {
            date = df.parse(time);
            output = outputDf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

}
