package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowInfoEpisodeListItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowInfoEpisodeListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowInfoEpisodeListItemFragment extends Fragment {

    private Episode mEpisode;
    private ShowInfoFragment mParent;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowInfoEpisodeListItemFragment.
     */
    public static ShowInfoEpisodeListItemFragment newInstance(Episode episode, ShowInfoFragment parent) {
        ShowInfoEpisodeListItemFragment fragment = new ShowInfoEpisodeListItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("EPISODE", episode);
        fragment.setArguments(args);
        fragment.set_parent(parent);
        return fragment;
    }

    public ShowInfoEpisodeListItemFragment() {
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
        View theView =  inflater.inflate(R.layout.fragment_show_info_episode_list_item, container, false);


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

    public void set_parent(ShowInfoFragment parent) {
        this.mParent = parent;
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
