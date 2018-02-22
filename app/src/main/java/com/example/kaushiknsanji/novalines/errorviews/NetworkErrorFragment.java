package com.example.kaushiknsanji.novalines.errorviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * Fragment that inflates the layout 'R.layout.network_error_layout'
 * to show the "Network Error" page when there is a Network issue,
 * with information on how to resolve the issue.
 *
 * @author Kaushik N Sanji
 */
public class NetworkErrorFragment extends Fragment
        implements View.OnClickListener {

    //Constant used for logs
    private static final String LOG_TAG = NetworkErrorFragment.class.getSimpleName();
    //Public Constant for use as Fragment's Tag
    public static final String FRAGMENT_TAG = LOG_TAG;

    /**
     * Constructor of {@link NetworkErrorFragment}
     *
     * @return Instance of this Fragment {@link NetworkErrorFragment}
     */
    public static NetworkErrorFragment newInstance() {
        return new NetworkErrorFragment();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI ('R.layout.network_error_layout')
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.network_error_layout'
        View rootView = inflater.inflate(R.layout.network_error_layout, container, false);

        //Finding the Info Card TextView to set the short message "Network Error"
        TextView infoCardTextView = rootView.findViewById(R.id.info_card_text_id);
        setupInfoCardText(infoCardTextView);

        //Finding the Network Error message TextView
        TextView networkErrorMsgTextView = rootView.findViewById(R.id.network_error_text_id);
        //Replacing the placeholder for drawables in Text with their corresponding image
        TextAppearanceUtility.replaceTextWithImage(getContext(), networkErrorMsgTextView);
        //Setting the Font
        networkErrorMsgTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.gabriela));

        //Finding the Network Settings button to set the listener
        Button networkSettingsButton = rootView.findViewById(R.id.network_settings_btn_id);
        //Registering the Click listener on the Button
        networkSettingsButton.setOnClickListener(this);

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Method that Initializes the Info Card TextView 'R.id.info_card_text_id'
     * to display the short message "Network Error".
     */
    public void setupInfoCardText(TextView infoCardTextView) {
        //Setting the short message "Network Error"
        infoCardTextView.setText(getString(R.string.nw_error_title_text));
        //Setting the Left Compound Drawable
        infoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_alert_orange),
                null, null, null
        );
        //Setting the Compound Drawable Padding
        infoCardTextView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.info_card_text_drawable_padding));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Executing action based on the view being clicked
        switch (view.getId()) {
            case R.id.network_settings_btn_id:
                //Launching Network Settings on click of this button
                IntentUtility.openNetworkSettings(getContext());
                break;
        }
    }

}
