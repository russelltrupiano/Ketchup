package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MyShowsListitemFragment extends Fragment {

    private TVShow _tvshow;
    private MyShowsFragment _parent;
    private Context _context;
    private MyShowsListitemFragment _instance;
    private OnFragmentInteractionListener mListener;

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
        final View theView = inflater.inflate(R.layout.fragment_my_shows_listitem, container, false);

        if (_tvshow == null) {return theView;}

        _context = getActivity().getApplicationContext();

        ((TextView) theView.findViewById(R.id.showTitle)).setText(_tvshow.get_title());
        ((TextView) theView.findViewById(R.id.showTime)).setText(_tvshow.get_airday() + " @ " + _tvshow.get_airtime());
        ((TextView) theView.findViewById(R.id.showNetwork)).setText(_tvshow.get_network());

        ImageView thumbnail = (ImageView) theView.findViewById(R.id.showThumbnail);

        if (_tvshow.get_imageUrl() != null && !_tvshow.get_imageUrl().equals("")) {
            Picasso.with(_context).load(_tvshow.get_imageUrl()).into(thumbnail);
        }

        ImageView unwatchedBadge = (ImageView) theView.findViewById(R.id.myshow_unwatced_count_image);
        setUnwatchedBadge(unwatchedBadge, _tvshow.get_num_unwatched());

        RelativeLayout itemLayout = (RelativeLayout)theView.findViewById(R.id.itemLayout);
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.loadShowInfo(getActivity(), _tvshow);
            }
        });
        theView.findViewById(R.id.showTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.loadShowInfo(getActivity(), _tvshow);
            }
        });

        final ImageButton optionsExpander = (ImageButton) theView.findViewById(R.id.item_options);
        optionsExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(_context, (FrameLayout)theView.findViewById(R.id.unsubscribe_menu_wrapper));
                popupMenu.getMenuInflater().inflate(R.menu.menu_myshows_unsubscribe, popupMenu.getMenu());


                //registering popup with OnMenuItemClickListener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menu_unsubscribe) {
                            Log.i("UNSUB", "Unsubbing from id " + _tvshow.get_id() + " - " + _tvshow.get_title());
                            KetchupAPI.unsubscribeToShow(_tvshow.get_id(), new UnsubscribeCallback(theView));
                            return true;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        _instance = this;

        return theView;
    }

    private void setUnwatchedBadge(ImageView unwatchedBadge, int num_unwatched) {
        switch(num_unwatched) {
            case 0:
                break;
            case 1:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_1_box));
                break;
            case 2:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_2_box));
                break;
            case 3:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_3_box));
                break;
            case 4:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_4_box));
                break;
            case 5:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_5_box));
                break;
            case 6:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_6_box));
                break;
            case 7:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_7_box));
                break;
            case 8:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_8_box));
                break;
            case 9:
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_9_box));
                break;
            default:
                //9+
                unwatchedBadge.setImageDrawable(getActivity().getDrawable(R.drawable.numeric_9_plus_box));
                break;
        }
    }

    public void setParent(MyShowsFragment parent) {
        _parent = parent;
    }

    public class UnsubscribeCallback implements KetchupAPI.HTTPCallback {

        View redirectView;

        public UnsubscribeCallback(View v) {
            redirectView = v;
        }

        @Override
        public void invokeCallback(JSONObject response) {
            _parent.removeShow(_instance);
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

    public void set_tvshow(TVShow tvshow) {
        _tvshow = tvshow;
    }

}
