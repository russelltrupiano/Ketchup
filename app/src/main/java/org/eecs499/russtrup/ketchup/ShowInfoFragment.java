package org.eecs499.russtrup.ketchup;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Abstract base class to allow for dynamic fragment rending from the navigation bar
 */
public abstract class ShowInfoFragment extends Fragment {

    public abstract void updateModel();

}
