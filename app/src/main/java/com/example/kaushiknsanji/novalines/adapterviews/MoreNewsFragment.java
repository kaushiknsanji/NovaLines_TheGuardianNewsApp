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

package com.example.kaushiknsanji.novalines.adapterviews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * Fragment that inflates the layout 'R.layout.more_news_layout'
 * for displaying a page requesting a user to subscribe to additional News Feeds.
 * <p>
 * The Page also transforms the template, requesting a user to subscribe
 * to a particular News Category/Section that the user requested to launch
 * from the News Article Card Item Menu.
 *
 * @author Kaushik N Sanji
 */
public class MoreNewsFragment extends Fragment
        implements View.OnClickListener {

    //Constant used for logs
    private static final String LOG_TAG = MoreNewsFragment.class.getSimpleName();
    //Bundle Key constant to save/restore the Name of the News Topic Feed requested
    private static final String NEWS_TOPIC_NAME_REQ_STRING_KEY = "NewsTopicNameRequested";
    //Bundle Key constant to save/restore the ID of the News Topic Feed requested
    private static final String NEWS_TOPIC_ID_REQ_STRING_KEY = "NewsTopicIdRequested";
    //For displaying the short info on the Info Card as a Title
    private TextView mInfoCardTextView;
    //For displaying the message pertaining to News Feed subscription
    private TextView mMessageTextView;
    //For providing the Subscribe Button
    private Button mSubscribeButton;
    //For storing the Name of the News Topic Feed requested
    private String mNewsTopicNameRequested;
    //For storing the ID of the News Topic Feed requested
    private String mNewsTopicIdRequested;

    /**
     * Constructor of {@link MoreNewsFragment}
     *
     * @return Instance of this Fragment {@link MoreNewsFragment}
     */
    public static MoreNewsFragment newInstance() {
        return new MoreNewsFragment();
    }

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Indicating that this fragment has menu options to show
        setHasOptionsMenu(true);
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
     * @return Return the View for the fragment's UI ('R.layout.more_news_layout')
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView: Started with savedInstanceState as " + savedInstanceState);
        //Inflating the layout 'R.layout.more_news_layout'
        View rootView = inflater.inflate(R.layout.more_news_layout, container, false);

        //Finding the Info Card TextView to set the short message
        mInfoCardTextView = rootView.findViewById(R.id.info_card_text_id);

        //Finding the TextView to be set for displaying the message
        mMessageTextView = rootView.findViewById(R.id.add_more_news_text_id);

        //Finding the Button to set the Text and the listener
        mSubscribeButton = rootView.findViewById(R.id.add_more_news_btn_id);

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Displaying the default content
            showDefaultInfo();
        } else {
            //On subsequent launch of this Fragment

            if (TextUtils.isEmpty(savedInstanceState.getString(NEWS_TOPIC_NAME_REQ_STRING_KEY))) {
                //Displaying the default content
                showDefaultInfo();
            } else {
                //Else, displaying the content based on the News Feed requested
                showSpecificInfo(savedInstanceState.getString(NEWS_TOPIC_NAME_REQ_STRING_KEY),
                        savedInstanceState.getString(NEWS_TOPIC_ID_REQ_STRING_KEY));
            }

        }

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process if
     * restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Saving the info of the News Topic Feed requested
        outState.putString(NEWS_TOPIC_NAME_REQ_STRING_KEY, mNewsTopicNameRequested);
        outState.putString(NEWS_TOPIC_ID_REQ_STRING_KEY, mNewsTopicIdRequested);

        super.onSaveInstanceState(outState);
    }

    /**
     * Method that displays the content based on the News Topic Feed requested
     *
     * @param newsTopicNameRequested is the Name of the News Topic Feed requested
     * @param newsTopicIdRequested   is the ID of the News Topic Feed requested
     */
    public void showSpecificInfo(String newsTopicNameRequested, String newsTopicIdRequested) {
        //Saving the info of the News Topic Feed requested
        mNewsTopicNameRequested = newsTopicNameRequested;
        mNewsTopicIdRequested = newsTopicIdRequested;

        //Initializing the view components accordingly
        setupComponents();
    }

    /**
     * Method that displays the default content
     */
    public void showDefaultInfo() {
        //Resetting the info of the News Topic Feed requested
        mNewsTopicNameRequested = "";
        mNewsTopicIdRequested = "";

        //Initializing the view components with default info
        setupComponents();
    }

    /**
     * Method that sets up the content of the view components
     */
    private void setupComponents() {
        setupInfoCardText();
        setupMessageText();
        setupSubscribeButton();
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater The LayoutInflater object that can be used to inflate the Menu options
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflating the Menu options from more_news_menu.xml
        inflater.inflate(R.menu.more_news_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handling based on the Menu item selected
        switch (item.getItemId()) {
            case R.id.subscribe_action_id:
                //For the subscribe menu option

                //Launching an activity to allow the user to subscribe to the News Feeds of their choice
                if (getParentFragment() != null) {
                    ((HeadlinesFragment) getParentFragment()).subscribeMoreNews();
                }
                return true;
            case R.id.settings_action_id:
                //For the settings menu option

                //Loading App's Settings
                IntentUtility.openAppSettings(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Prepare the Fragment host's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @see #setHasOptionsMenu
     * @see #onCreateOptionsMenu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(mNewsTopicNameRequested)) {
            //When the Fragment was requested by selecting the menu option on a News Article

            //Hide the Subscribe Menu button as this is for subscribing to more than one News Category
            menu.removeItem(R.id.subscribe_action_id);
        }
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Method that initializes the Info Card TextView 'R.id.info_card_text_id'
     * for displaying a short message as a Title
     */
    private void setupInfoCardText() {
        if (TextUtils.isEmpty(mNewsTopicNameRequested)) {
            //When the Fragment was launched through the ViewPager Tab

            //Setting the short message "Subscribe to More Feeds"
            mInfoCardTextView.setText(getString(R.string.more_news_subscribe_title_text));

        } else {
            //When the Fragment was requested by selecting the menu option on a News Article

            //Setting the short message specific to the News Feed requested
            mInfoCardTextView.setText(getString(R.string.news_not_subscribed_title_text, mNewsTopicNameRequested));
        }

        //Setting the Left Compound Drawable
        mInfoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_info_outline_orange),
                null, null, null
        );
        //Setting the Compound Drawable Padding
        mInfoCardTextView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.info_card_text_drawable_padding));
    }

    /**
     * Method that initializes the TextView 'R.id.add_more_news_text_id'
     * for displaying a message related to News Feed subscription
     */
    private void setupMessageText() {
        if (TextUtils.isEmpty(mNewsTopicNameRequested)) {
            //When the Fragment was launched through the ViewPager Tab

            //Setting the default message text
            mMessageTextView.setText(getString(R.string.more_news_subscribe_msg), TextView.BufferType.SPANNABLE);
            TextAppearanceUtility.replaceTextWithImage(getContext(), mMessageTextView);
        } else {
            //When the Fragment was requested by selecting the menu option on a News Article

            //Setting the message text based on the News Feed requested
            TextAppearanceUtility.setHtmlText(mMessageTextView, getString(R.string.news_not_subscribed_msg, mNewsTopicNameRequested));
        }

        //Setting the Font
        mMessageTextView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gabriela));
    }

    /**
     * Method that initializes the Button 'R.id.add_more_news_btn_id'
     * for displaying a suitable text for News Feed subscription and registers a listener
     */
    private void setupSubscribeButton() {
        if (TextUtils.isEmpty(mNewsTopicNameRequested)) {
            //When the Fragment was launched through the ViewPager Tab

            //Setting the default button text
            mSubscribeButton.setText(getString(R.string.more_news_subscribe_btn_text));
        } else {
            //When the Fragment was requested by selecting the menu option on a News Article

            //Setting the button text based on the News Feed requested
            mSubscribeButton.setText(getString(R.string.news_not_subscribed_btn_text, mNewsTopicNameRequested));
        }

        //Registering the button click listener
        mSubscribeButton.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Executing based on the view clicked
        switch (view.getId()) {
            case R.id.add_more_news_btn_id:
                //For the Subscribe News Button
                if (TextUtils.isEmpty(mNewsTopicNameRequested)) {
                    //On the Default content, launching an activity
                    //to allow the user to subscribe to the News Feeds of their choice
                    if (getParentFragment() != null) {
                        ((HeadlinesFragment) getParentFragment()).subscribeMoreNews();
                    }
                } else {
                    //Else, subscribing the News Feed requested
                    if (getParentFragment() != null) {
                        ((HeadlinesFragment) getParentFragment()).subscribeAndLaunchNewsCategory(
                                mNewsTopicNameRequested, mNewsTopicIdRequested
                        );
                    }
                }
                break;
        }
    }


}