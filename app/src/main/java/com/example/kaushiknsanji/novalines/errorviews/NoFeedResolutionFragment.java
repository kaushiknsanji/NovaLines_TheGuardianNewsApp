package com.example.kaushiknsanji.novalines.errorviews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.NoFeedResolutionAdapter;
import com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment;
import com.example.kaushiknsanji.novalines.models.NoFeedInfo;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewItemDecorUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that inflates the layout 'R.layout.no_feed_layout'
 * to show the "No Feed Available" page when there is no news feed,
 * with information on how to resolve the issue.
 *
 * @author Kaushik N Sanji
 */
public class NoFeedResolutionFragment extends Fragment
        implements NoFeedResolutionAdapter.OnAdapterItemResolutionButtonClickListener {

    //Constant used for logs
    private static final String LOG_TAG = NoFeedResolutionFragment.class.getSimpleName();
    //Public Constant for use as Fragment's Tag
    public static final String FRAGMENT_TAG = LOG_TAG;

    //For the RecyclerView of No Feed Resolution Items
    private RecyclerView mRecyclerView;

    /**
     * Constructor of {@link NoFeedResolutionFragment}
     *
     * @return Instance of this Fragment {@link NoFeedResolutionFragment}
     */
    public static NoFeedResolutionFragment newInstance() {
        return new NoFeedResolutionFragment();
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
     * @return Return the View for the fragment's UI ('R.layout.no_feed_layout')
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.no_feed_layout'
        View rootView = inflater.inflate(R.layout.no_feed_layout, container, false);

        //Finding the Info Card TextView to set the short message "No Feed Available"
        TextView infoCardTextView = rootView.findViewById(R.id.info_card_text_id);
        setupInfoCardText(infoCardTextView);

        //Finding the No Feed Reason TextView to set the Font
        TextView noFeedReasonTextView = rootView.findViewById(R.id.no_feed_reason_text_id);
        noFeedReasonTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.gabriela), Typeface.BOLD);

        //Finding the RecyclerView to show the resolution items
        mRecyclerView = rootView.findViewById(R.id.no_feed_recycler_view_id);
        setupRecyclerView();

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Method that initializes the Layout Manager, Adapter, Listeners
     * and Decoration for the RecyclerView to show the resolution items
     */
    private void setupRecyclerView() {
        //Retrieving the data for the RecyclerView: START
        String[] resolutionTexts = getResources().getStringArray(R.array.nf_resolution_texts);
        String[] resolutionButtonTexts = getResources().getStringArray(R.array.nf_resolution_btn_texts);
        String[] resolutionButtonDrawablePaths = getResources().getStringArray(R.array.nf_resolution_btn_drawables);
        int noOfResolutions = resolutionTexts.length;
        //Retrieving the data for the RecyclerView: END

        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing a List to store the NoFeedInfo data
        List<NoFeedInfo> noFeedInfoList = new ArrayList<>();
        //Iterating and building the list of NoFeedInfo data
        for (int index = 0; index < noOfResolutions; index++) {
            //Creating the NoFeedInfo object to store the current data
            NoFeedInfo noFeedInfo = new NoFeedInfo();
            noFeedInfo.setResolutionItemNumber(index + 1);
            noFeedInfo.setResolutionText(resolutionTexts[index]);
            noFeedInfo.setResolutionButtonText(resolutionButtonTexts[index]);

            if (resolutionButtonDrawablePaths[index].startsWith("res")) {
                //When there is an image that needs to be shown with the button

                int startIndex = resolutionButtonDrawablePaths[index].lastIndexOf("/");
                int endIndex = resolutionButtonDrawablePaths[index].lastIndexOf(".");
                noFeedInfo.setResolutionButtonCompoundDrawableRes(
                        getResources().getIdentifier(
                                resolutionButtonDrawablePaths[index].substring(startIndex + 1, endIndex),
                                "drawable",
                                getActivity().getPackageName()
                        )
                );
            } else {
                //When there is no image to be shown, setting the DrawableRes value as 0
                noFeedInfo.setResolutionButtonCompoundDrawableRes(0);
            }

            //Finally, adding the current NoFeedInfo to the list
            noFeedInfoList.add(noFeedInfo);
        }

        //Initializing the Adapter with the data loaded
        NoFeedResolutionAdapter noFeedResolutionAdapter = new NoFeedResolutionAdapter(getContext(), R.layout.no_feed_help_item, noFeedInfoList);

        //Registering the OnAdapterItemResolutionButtonClickListener
        noFeedResolutionAdapter.setOnAdapterItemResolutionButtonClickListener(this);

        //Setting the Item Decor on RecyclerView for proper Card Item spacing
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorUtility(
                getResources().getDimensionPixelOffset(R.dimen.card_item_spacing)
        ));

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(noFeedResolutionAdapter);

        //Disabling to have smoother scrolling inside NestedScrollView
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * Method that Initializes the Info Card TextView 'R.id.info_card_text_id'
     * to display the short message "No Feed Available".
     */
    public void setupInfoCardText(TextView infoCardTextView) {
        //Setting the short message "No Feed Available"
        infoCardTextView.setText(getString(R.string.no_feed_title_text));
        //Setting the Left Compound Drawable
        infoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_alert_orange),
                null, null, null
        );
        //Setting the Compound Drawable Padding
        infoCardTextView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.info_card_text_drawable_padding));
    }

    /**
     * Method invoked when the Resolution button of the Adapter's Item
     * is clicked.
     *
     * @param noFeedInfo is the corresponding {@link NoFeedInfo} object of the item view
     *                   clicked in the Adapter.
     */
    @Override
    public void onResolutionButtonClick(NoFeedInfo noFeedInfo) {
        //Retrieving the Resolution Button text from the data
        String resolutionButtonText = noFeedInfo.getResolutionButtonText();
        //Executing action based on the Resolution Button text
        if (resolutionButtonText.equals(getString(R.string.nf_app_settings_btn_text))) {
            //Launching App Settings
            IntentUtility.openAppSettings(getContext());
        } else if (resolutionButtonText.equals(getString(R.string.nf_nw_settings_btn_text))) {
            //Launching Network Settings
            IntentUtility.openNetworkSettings(getContext());
        } else if (resolutionButtonText.equals(getString(R.string.nf_refresh_btn_text))) {
            //Triggering a refresh based on the Parent Fragment
            Fragment parentFragment = getParentFragment();
            if (parentFragment != null && parentFragment instanceof ArticlesFragment) {
                //When the Parent Fragment is ArticlesFragment
                ArticlesFragment articlesFragment = (ArticlesFragment) parentFragment;
                articlesFragment.triggerRefresh();
            }
        }
    }

}
