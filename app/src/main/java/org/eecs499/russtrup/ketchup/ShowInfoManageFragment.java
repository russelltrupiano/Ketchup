package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowInfoManageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowInfoManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowInfoManageFragment extends Fragment {

    private TVShow _tvshow;
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

        // TODO: Get landscape image for this view
//        showInfoHeaderImage = ????
        showInfoHeaderTitle.setText(_tvshow.get_title());
        showInfoHeaderNetwork.setText(_tvshow.get_network());
        showInfoHeaderAirtime.setText(_tvshow.get_airday() + " @ " + _tvshow.get_airtime());

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

}
