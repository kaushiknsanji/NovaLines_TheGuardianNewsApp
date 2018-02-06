package com.example.kaushiknsanji.novalines.drawerviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * Created by Kaushik N Sanji on 28-Jan-18.
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
     * Method that Initializes the Info Card TextView 'R.id.info_card_text_id'
     */
    private void setupInfoCardText() {
        //Setting the Text on TextView
        mInfoCardTextView.setText(getString(R.string.not_implemented_text_info));
        //Setting the Left Compound Drawable
        mInfoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_info_outline_orange),
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
        mNoResultsTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.elsie));
        //Replacing the placeholder for drawables in Text with their corresponding image
        TextAppearanceUtility.replaceTextWithImage(getContext(), mNoResultsTextView);
    }

    /**
     * Method that initializes the Toolbar as ActionBar
     * and sets the Title
     */
    private void setupToolBar() {
        //Setting the Toolbar as the ActionBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        //Retrieving the Action Bar
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
