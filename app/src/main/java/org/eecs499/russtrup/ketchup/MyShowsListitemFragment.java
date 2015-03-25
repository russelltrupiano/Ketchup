package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyShowsListitemFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private String _id;
    private String _name;
    private String _imageUrl;
    private String _time;
    private String _airday;
    private String _network;

    public MyShowsListitemFragment() {
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
        View theView = inflater.inflate(R.layout.fragment_my_shows_listitem, container, false);

        ((TextView) theView.findViewById(R.id.showTitle)).setText(_name);
        ((TextView) theView.findViewById(R.id.showTime)).setText(_airday + " @ " + _time);
        ((TextView) theView.findViewById(R.id.showNetwork)).setText(_network);

        ImageView thumbnail = (ImageView) theView.findViewById(R.id.showThumbnail);

        if (_imageUrl != null && !_imageUrl.equals("")) {
            Picasso.with(getActivity().getApplicationContext()).load(_imageUrl).into(thumbnail);
        }

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.loadShowInfo(getActivity());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // nada
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

    public void fillData(String id, String name, String imageUrl, String time, String day, String network) {
        _id = id;
        _name = name;
        _time = formatTime(time);
        _airday = day;
        _network = network;
        _imageUrl = imageUrl;
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
