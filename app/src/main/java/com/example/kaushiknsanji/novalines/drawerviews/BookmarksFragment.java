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

package com.example.kaushiknsanji.novalines.drawerviews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * Drawer Fragment that inflates the layout 'R.layout.bookmarks_layout'
 * which displays a list of News Article items that have been bookmarked by the user
 * for Reading later.
 *
 * @author Kaushik N Sanji
 */
public class BookmarksFragment extends Fragment {

    //Constant used for logs
    private static final String LOG_TAG = BookmarksFragment.class.getSimpleName();
    //Public Constant for use as Fragment's Tag
    public static final String NAV_FRAGMENT_TAG = LOG_TAG;

    //For the custom Toolbar used as ActionBar
    private Toolbar mToolbar;

    //For displaying the No results message
    private TextView mNoResultsTextView;

    //For displaying the short info on the Info Card
    private TextView mInfoCardTextView;

    /**
     * Constructor of {@link BookmarksFragment}
     *
     * @return Instance of this Fragment {@link BookmarksFragment}
     */
    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
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
     * @return Return the View for the fragment's UI ('R.layout.bookmarks_layout')
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.bookmarks_layout'
        View rootView = inflater.inflate(R.layout.bookmarks_layout, container, false);

        //Finding the Toolbar
        mToolbar = rootView.findViewById(R.id.toolbar_id);
        //Initializing the Toolbar as ActionBar
        setupToolBar();

        //Finding the Info Card TextView to set the short message
        mInfoCardTextView = rootView.findViewById(R.id.info_card_text_id);
        //Initializing the Info Card TextView
        setupInfoCardText();

        //Finding the TextView to be set for displaying the message for no results
        mNoResultsTextView = rootView.findViewById(R.id.message_text_id);
        //Initializing the No Results TextView
        setupNoResultsText();

        //Returning the prepared layout
        return rootView;
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
        //Inflating the Menu options from bookmarks_menu.xml
        inflater.inflate(R.menu.bookmarks_menu, menu);
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
            case R.id.refresh_action_id:
                //For the refresh menu option

                //As this Fragment has no data to show, this is not implemented
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
     * Method that Initializes the Info Card TextView 'R.id.info_card_text_id'
     */
    private void setupInfoCardText() {
        //Setting the Text on TextView
        mInfoCardTextView.setText(getString(R.string.not_implemented_text_info));
        //Setting the Left Compound Drawable
        mInfoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_info_outline_orange),
                null, null, null
        );
        //Setting the Compound Drawable Padding
        mInfoCardTextView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.info_card_text_drawable_padding));
    }

    /**
     * Method that Initializes the No Results TextView 'R.id.message_text_id'
     */
    private void setupNoResultsText() {
        //Setting the Html Text to be displayed for No Results
        TextAppearanceUtility.setHtmlText(mNoResultsTextView, getString(R.string.bookmark_no_results_text));
        //Setting the Font for the Text
        mNoResultsTextView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gabriela));
        //Replacing the placeholder for drawables in Text with their corresponding image
        TextAppearanceUtility.replaceTextWithImage(getContext(), mNoResultsTextView);
    }

    /**
     * Method that initializes the Toolbar as ActionBar
     * and sets the Title
     */
    private void setupToolBar() {
        //Setting the Toolbar as the ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        //Retrieving the Action Bar
        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            //Removing the default title text
            supportActionBar.setDisplayShowTitleEnabled(false);
            //Enabling home button to be used for Up navigation
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //Enabling home button
            supportActionBar.setHomeButtonEnabled(true);
        }

        //Finding the TextView to set the Title
        TextView titleTextView = mToolbar.findViewById(R.id.toolbar_title_text_id);
        titleTextView.setText(getString(R.string.bookmarked_news_title_str));
    }
}
