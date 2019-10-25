/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kaushiknsanji.novalines.errorviews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.kaushiknsanji.novalines.interfaces.IRefreshActionView;
import com.example.kaushiknsanji.novalines.models.NoFeedInfo;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewItemDecorUtility;

import java.util.ArrayList;
import java.util.Arrays;
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

    //Constant used as the Bundle Key for the Parameter that identifies the fragment which displays this
    private static final String PARENT_FRAG_TITLE_STR_KEY = "ParentFragment.Title";

    //For the RecyclerView of No Feed Resolution Items
    private RecyclerView mRecyclerView;

    /**
     * Constructor of {@link NoFeedResolutionFragment}
     *
     * @return Instance of this Fragment {@link NoFeedResolutionFragment}
     */
    public static NoFeedResolutionFragment newInstance(String parentFragmentTitleStr) {
        NoFeedResolutionFragment noFeedResolutionFragment = new NoFeedResolutionFragment();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(1);
        bundle.putString(PARENT_FRAG_TITLE_STR_KEY, parentFragmentTitleStr);
        noFeedResolutionFragment.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return noFeedResolutionFragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.no_feed_layout'
        View rootView = inflater.inflate(R.layout.no_feed_layout, container, false);

        //Finding the Info Card TextView to set the short message "No Feed Available"
        TextView infoCardTextView = rootView.findViewById(R.id.info_card_text_id);
        setupInfoCardText(infoCardTextView);

        //Finding the No Feed Reason TextView to set the Font
        TextView noFeedReasonTextView = rootView.findViewById(R.id.no_feed_reason_text_id);
        noFeedReasonTextView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gabriela), Typeface.BOLD);

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
        //Retrieving the resolution data for the RecyclerView: START
        List<String> resolutionTextList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.nf_resolution_texts)));
        List<String> resolutionButtonTextList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.nf_resolution_btn_texts)));
        List<String> resolutionButtonDrawablePathList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.nf_resolution_btn_drawables)));

        //Adding resolution data specific to the requesting Parent Fragment: START
        String parentFragmentTitleStr = getArguments() != null ? getArguments().getString(PARENT_FRAG_TITLE_STR_KEY) : null;
        if (parentFragmentTitleStr != null && parentFragmentTitleStr.equals(getString(R.string.random_news_title_str))) {
            //For RandomNewsFragment
            resolutionTextList.add(getString(R.string.nf_search_resolution_text));
            resolutionButtonTextList.add("");
            resolutionButtonDrawablePathList.add("");
        }
        //Adding resolution data specific to the requesting Parent Fragment: END
        int noOfResolutions = resolutionTextList.size();
        //Retrieving the resolution data for the RecyclerView: END

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
            noFeedInfo.setResolutionText(resolutionTextList.get(index));
            noFeedInfo.setResolutionButtonText(resolutionButtonTextList.get(index));

            if (resolutionButtonDrawablePathList.get(index).startsWith("res")) {
                //When there is an image that needs to be shown with the button

                int startIndex = resolutionButtonDrawablePathList.get(index).lastIndexOf("/");
                int endIndex = resolutionButtonDrawablePathList.get(index).lastIndexOf(".");
                noFeedInfo.setResolutionButtonCompoundDrawableRes(
                        getResources().getIdentifier(
                                resolutionButtonDrawablePathList.get(index).substring(startIndex + 1, endIndex),
                                "drawable",
                                requireActivity().getPackageName()
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
        NoFeedResolutionAdapter noFeedResolutionAdapter = new NoFeedResolutionAdapter(requireContext(), R.layout.no_feed_help_item, noFeedInfoList);

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
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_alert_orange),
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
            IntentUtility.openNetworkSettings(requireContext());
        } else if (resolutionButtonText.equals(getString(R.string.nf_refresh_btn_text))) {
            //Triggering a refresh based on the Parent Fragment type
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof IRefreshActionView) {
                ((IRefreshActionView) parentFragment).triggerRefresh();
            }
        }
    }

}
