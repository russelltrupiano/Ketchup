package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    private String TAG = SearchResultFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private TVShowBase _tvshowbase;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchResultFragment.
     */
    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View theView = inflater.inflate(R.layout.fragment_search_result, container, false);

        ((TextView) theView.findViewById(R.id.showTitle)).setText(_tvshowbase.get_title());
        ((TextView) theView.findViewById(R.id.showTime)).setText(_tvshowbase.get_airday() + " @ " + _tvshowbase.get_airtime());
        ((TextView) theView.findViewById(R.id.showNetwork)).setText(_tvshowbase.get_network());

        if (User.get_instance().subscribedTo(_tvshowbase)) {
            Log.i(TAG, "Subscribed to " + _tvshowbase.get_title());
            theView.findViewById(R.id.removeShowBtn).setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "Not subscribed to " + _tvshowbase.get_title());
            theView.findViewById(R.id.addShowBtn).setVisibility(View.VISIBLE);
        }

        theView.findViewById(R.id.addShowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KetchupAPI.subscribeToShow(_tvshowbase.get_id(), new SubscribeCallback(v));
                theView.findViewById(R.id.addShowBtn).setVisibility(View.INVISIBLE);
                theView.findViewById(R.id.removeShowBtn).setVisibility(View.VISIBLE);
            }
        });

        theView.findViewById(R.id.removeShowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KetchupAPI.unsubscribeToShow(_tvshowbase.get_id(), new UnsubscribeCallback(v));
                theView.findViewById(R.id.removeShowBtn).setVisibility(View.INVISIBLE);
                theView.findViewById(R.id.addShowBtn).setVisibility(View.VISIBLE);
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
            try {
                String title = response.getString("title");
                Log.i("SUBTITLE", "Adding show " + title);
                if (title.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "You're already subscribed to " + _tvshowbase.get_title() + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Successfully subscribed to " + title + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("SUB_ERR", e.getMessage());
            }
            Log.i("CALLBACK", "Subscribe Succeeded: " + response.toString());
        }

        @Override
        public void onFail() {

            Log.i("CALLBACK", "Subscribe Failed");
        }
    }

    public class UnsubscribeCallback implements KetchupAPI.HTTPCallback {

        View redirectView;

        public UnsubscribeCallback(View v) {
            redirectView = v;
        }

        @Override
        public void invokeCallback(JSONObject response) {
            try {
                String title = response.getJSONArray("title").getString(0);
                Toast.makeText(getActivity().getApplicationContext(), "Unsubscribed from " + title, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("UNSUB_ERR", e.getMessage());
            }
            Log.i("CALLBACK", "Unsubscribe Succeeded: " + response.toString());
        }

        @Override
        public void onFail() {
            Log.i("CALLBACK", "Unsubscribe Failed");
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

    public void set_tvshowbase(TVShowBase tvshowbase) {
        _tvshowbase = tvshowbase;
    }

}
