package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PopularShowItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PopularShowItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularShowItemFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SLUG = "slug";
    private static final String TITLE = "title";
    private static final String YEAR = "year";
    private static final String IMAGE = "image";
    private static String TAG = PopularShowItemFragment.class.getSimpleName();

    private String slug;
    private String title;
    private int year;
    private String image;

    private FindShowsFragment _parent;
    private Context _context;
    private TVShowBase _tvshow;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param slug Trakt slug id.
     * @param title Show title.
     * @param year Release year.
     * @param image Image url.
     * @return A new instance of fragment PopularShowItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularShowItemFragment newInstance(String slug, String title, int year, String image) {
        PopularShowItemFragment fragment = new PopularShowItemFragment();
        Bundle args = new Bundle();
        args.putString(SLUG, slug);
        args.putString(TITLE, title);
        args.putInt(YEAR, year);
        args.putString(IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    public PopularShowItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            slug = getArguments().getString(SLUG);
            title = getArguments().getString(TITLE);
            year = getArguments().getInt(YEAR);
            image = getArguments().getString(IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View theView = inflater.inflate(R.layout.fragment_popular_show_item, container, false);

        if (_tvshow == null) {return theView;}

        _context = getActivity().getApplicationContext();

        ((TextView) theView.findViewById(R.id.showTitle)).setText(_tvshow.get_title());

        ImageView thumbnail = (ImageView) theView.findViewById(R.id.show_thumbnail);

        if (_tvshow.get_background_url() != null && !_tvshow.get_background_url().equals("")) {
            Picasso.with(_context).load(_tvshow.get_background_url()).into(thumbnail);
        }

        if (User.get_instance().subscribedTo(_tvshow)) {
            Log.i(TAG, "Subscribed to " + _tvshow.get_title());
            theView.findViewById(R.id.findShowsUnsub).setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "Not subscribed to " + _tvshow.get_title());
            theView.findViewById(R.id.findShowsSub).setVisibility(View.VISIBLE);
        }

        theView.findViewById(R.id.findShowsSub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KetchupAPI.subscribeToShow(_tvshow.get_id(), new SubscribeCallback(v));
                theView.findViewById(R.id.findShowsSub).setVisibility(View.INVISIBLE);
                theView.findViewById(R.id.findShowsUnsub).setVisibility(View.VISIBLE);
            }
        });

        theView.findViewById(R.id.findShowsUnsub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KetchupAPI.unsubscribeToShow(_tvshow.get_id(), new UnsubscribeCallback(v));
                theView.findViewById(R.id.findShowsUnsub).setVisibility(View.INVISIBLE);
                theView.findViewById(R.id.findShowsSub).setVisibility(View.VISIBLE);
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
                    Toast.makeText(getActivity().getApplicationContext(), "You're already subscribed to " + _tvshow.get_title() + "!", Toast.LENGTH_SHORT).show();
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

    public void setParent(FindShowsFragment parent) {
        _parent = parent;
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

    public void set_tvshow(TVShowBase show) {
        _tvshow = show;
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
