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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.ArticlesAdapter;
import com.example.kaushiknsanji.novalines.errorviews.NetworkErrorFragment;
import com.example.kaushiknsanji.novalines.errorviews.NoFeedResolutionFragment;
import com.example.kaushiknsanji.novalines.interfaces.IArticleActionView;
import com.example.kaushiknsanji.novalines.interfaces.IPaginationView;
import com.example.kaushiknsanji.novalines.interfaces.IRefreshActionView;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.observers.BaseRecyclerViewScrollListener;
import com.example.kaushiknsanji.novalines.presenters.BookmarkActionPresenter;
import com.example.kaushiknsanji.novalines.presenters.FavoriteActionPresenter;
import com.example.kaushiknsanji.novalines.presenters.PaginationPresenter;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.Logger;
import com.example.kaushiknsanji.novalines.utils.NewsURLGenerator;
import com.example.kaushiknsanji.novalines.utils.PreferencesObserverUtility;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewItemDecorUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewUtility;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;
import com.example.kaushiknsanji.novalines.workers.NewsArticlesLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Drawer Fragment that inflates the layout 'R.layout.random_news_layout'
 * containing a {@link SearchView} that enables the users to search
 * the News by using any keywords with wildcards.
 * <p>
 * Responsible for displaying the News Feeds from various News Categories/Sections
 * pertaining to the Search Query entered by the user.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class RandomNewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<NewsArticleInfo>>,
        SwipeRefreshLayout.OnRefreshListener,
        ArticlesAdapter.OnAdapterItemDataSwapListener,
        ArticlesAdapter.OnAdapterItemClickListener,
        ArticlesAdapter.OnAdapterItemPopupMenuClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener, SearchView.OnQueryTextListener,
        IArticleActionView, IPaginationView, IRefreshActionView {

    //Constant used for logs
    private static final String LOG_TAG = RandomNewsFragment.class.getSimpleName();
    //Public Constant for use as Fragment's Tag
    public static final String NAV_FRAGMENT_TAG = LOG_TAG;

    //Constant that holds the count of unique loaders required by the Fragment instance
    private static final int LOADER_COUNT_PER_FRAG = 2;

    //Constant that sets the trigger point for when the vertical scroll reaches/leaves
    //the last y items in RecyclerView to show/hide the pagination panel for Paginated Results
    private static final int VSCROLL_PAGINATION_TRIGGER_POS = 3;

    //Bundle Key Constant to save/restore the value of the top visible Adapter Item position
    private static final String VISIBLE_ITEM_VIEW_POSITION_INT_KEY = "RecyclerView.TopItemPosition";
    //Bundle Key Constant to save/restore the value of the visibility of "No Feed Layout"
    private static final String NO_FEED_VIEW_VISIBILITY_BOOL_KEY = "Visibility.NoFeedView";
    //Bundle Key Constant to save/restore the value of the visibility of "Network Error Layout"
    private static final String NW_ERROR_VIEW_VISIBILITY_BOOL_KEY = "Visibility.NetworkErrorView";
    //Bundle Key Constant to save/restore the Search Query last executed
    private static final String SEARCH_VIEW_QUERY_STR_KEY = "SearchView.Query";
    //Bundle Key Constant to save/restore the Search Query that was being entered by the user in SearchView
    private static final String SEARCH_VIEW_QUERY_IN_PROGRESS_STR_KEY = "SearchView.QueryInProgress";
    //Bundle Key Constant to save/restore the value of the visibility of "Intro View"
    private static final String INTRO_VIEW_VISIBILITY_BOOL_KEY = "Visibility.IntroView";

    //For the custom Toolbar used as ActionBar
    private Toolbar mToolbar;

    //For the SearchView
    private SearchView mSearchView;

    //For the Settings SharedPreferences
    private SharedPreferences mPreferences;

    //List of Preference Keys to exclude while triggering the loader to load data
    private List<String> mKeysToExclude;

    //For the Pagination of content
    private View mPaginationPanel;

    //For the Intro View
    private View mIntroView;

    //For displaying the short info on the Info Card
    private TextView mInfoCardTextView;

    //For the Search Results View
    private View mSearchResultsView;

    //Stores the Unique IDs of the Loaders required by the Fragment instance
    private int[] mLoaderIds;

    //For the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeContainer;

    //For the RecyclerView
    private RecyclerView mRecyclerView;

    //For the Adapter of RecyclerView
    private ArticlesAdapter mRecyclerAdapter;

    //Saves the Search Query executed by the user in SearchView
    private String mSearchQueryStr;

    //Saves the Search Query being entered by the user in SearchView, but not completed the search
    private String mSearchQueryInProgressStr;

    //Stores reference to the URL Generator
    private NewsURLGenerator mUrlGenerator;

    //Saves whether the "No Feed Layout" should be visible/hidden
    private boolean mNoFeedViewVisible;

    //Saves whether the "Network Error Layout" should be visible/hidden
    private boolean mNetworkErrorViewVisible;

    //For the "Error Layout"
    private View mErrorView;

    //Saves the top visible Adapter Item position
    private int mVisibleItemViewPosition;

    //For the Pagination buttons displayed at the bottom (when visible)
    private ImageButton mPageFirstButton;
    private ImageButton mPageLastButton;
    private ImageButton mPageNextButton;
    private ImageButton mPagePreviousButton;
    private ImageButton mPageMoreButton;

    //For the Pagination Presenter
    private PaginationPresenter mPaginationPresenter;

    //Presenters for handling the Bookmark and Favorite Actions
    private BookmarkActionPresenter mBookmarkActionPresenter;
    private FavoriteActionPresenter mFavoriteActionPresenter;

    /**
     * Constructor of {@link RandomNewsFragment}
     *
     * @return Instance of this Fragment {@link RandomNewsFragment}
     */
    public static RandomNewsFragment newInstance() {
        return new RandomNewsFragment();
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
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI ('R.layout.random_news_layout')
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.random_news_layout'
        View rootView = inflater.inflate(R.layout.random_news_layout, container, false);

        //Finding the Toolbar
        mToolbar = rootView.findViewById(R.id.toolbar_id);
        //Initializing the Toolbar as ActionBar
        setupToolBar();

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        //Reading the List of Preference Keys to exclude while triggering the loader to load data
        mKeysToExclude = PreferencesObserverUtility.getPreferenceKeysToExclude(requireContext());

        //Initializing the URL Generator for use with the NewsArticlesLoader
        mUrlGenerator = new NewsURLGenerator(requireContext());

        //Initializing the Array of Loader IDs: START
        mLoaderIds = new int[LOADER_COUNT_PER_FRAG];
        int startLoaderId = getId();
        for (int index = 0; index < LOADER_COUNT_PER_FRAG; index++) {
            mLoaderIds[index] = startLoaderId + index;
        }
        //Initializing the Array of Loader IDs: END

        //Finding the SearchView
        mSearchView = rootView.findViewById(R.id.search_view_id);
        //Registering the QueryTextListener
        mSearchView.setOnQueryTextListener(this);

        //Finding the Info Card TextView to set the short message
        mInfoCardTextView = rootView.findViewById(R.id.info_card_text_id);
        //Initializing the Info Card TextView
        setupInfoCardText();

        //Finding the view that displays the info related to the fragment
        mIntroView = rootView.findViewById(R.id.random_news_intro_id);
        //Initializing the layout that displays the information on how to use this fragment
        setupIntroView();

        //Finding the view that displays the Search Results
        mSearchResultsView = rootView.findViewById(R.id.search_results_content_id);

        //Finding the SwipeRefreshLayout
        mSwipeContainer = mSearchResultsView.findViewById(R.id.swipe_container_id);
        //Initializing the SwipeRefreshLayout
        setupSwipeRefresh();

        //Finding the RecyclerView
        mRecyclerView = mSwipeContainer.findViewById(R.id.news_recycler_view_id);
        //Initializing the RecyclerView
        setupRecyclerView();

        //Finding the Pagination Panel
        mPaginationPanel = rootView.findViewById(R.id.pagination_panel_id);

        //Finding the Pagination Buttons
        mPageFirstButton = rootView.findViewById(R.id.page_first_button_id);
        mPageLastButton = rootView.findViewById(R.id.page_last_button_id);
        mPageNextButton = rootView.findViewById(R.id.page_next_button_id);
        mPagePreviousButton = rootView.findViewById(R.id.page_previous_button_id);
        mPageMoreButton = rootView.findViewById(R.id.page_more_button_id);

        //Registering click listener on the Pagination buttons
        mPageFirstButton.setOnClickListener(this);
        mPageLastButton.setOnClickListener(this);
        mPageNextButton.setOnClickListener(this);
        mPagePreviousButton.setOnClickListener(this);
        mPageMoreButton.setOnClickListener(this);

        //Registering the Pagination Presenter
        mPaginationPresenter = new PaginationPresenter();
        mPaginationPresenter.attachView(this);

        //Finding the "Error View"
        mErrorView = mSearchResultsView.findViewById(R.id.error_frame_id);

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Ensuring that the Search Results View is hidden
            showIntroView();

            //Ensuring the "Error View" is hidden
            hideErrorView();

            //Setting focus on SearchView
            mSearchView.requestFocus();

            //Resetting the 'page' (Page to Display) setting to 1 on initial launch
            mPaginationPresenter.resetStartPageIndex();

            //Updating the state of Pagination Buttons on load
            mPaginationPresenter.updatePaginationButtonsState();

        } else {
            //On subsequent launch of this Fragment

            //Restoring the Search Query last executed
            mSearchQueryStr = savedInstanceState.getString(SEARCH_VIEW_QUERY_STR_KEY);
            //Restoring the Search Query last entered
            mSearchQueryInProgressStr = savedInstanceState.getString(SEARCH_VIEW_QUERY_IN_PROGRESS_STR_KEY);

            if (TextUtils.isEmpty(mSearchQueryStr)) {
                //When there was no search done previously, load the intro view again
                showIntroView();

                //Updating the search view content if the user had entered any text
                if (!TextUtils.isEmpty(mSearchQueryInProgressStr)) {
                    mSearchView.setQuery(mSearchQueryInProgressStr, false);
                    mSearchView.requestFocus();
                }

            } else {
                //When a Search was previously done, show the search results view
                hideIntroView();

                //Initializing other components accordingly: START

                //Restoring the visibility of "No Feed Layout"
                if (savedInstanceState.getBoolean(NO_FEED_VIEW_VISIBILITY_BOOL_KEY)) {
                    showNoFeedLayout();
                }

                //Restoring the visibility of "Network Error Layout"
                if (savedInstanceState.getBoolean(NW_ERROR_VIEW_VISIBILITY_BOOL_KEY)) {
                    showNetworkErrorLayout();
                }

                //Restoring the value of the position of the top Adapter item position previously visible
                mVisibleItemViewPosition = savedInstanceState.getInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY);

                //Updating the state of Pagination Buttons on load
                mPaginationPresenter.updatePaginationButtonsState();

                if (!TextUtils.isEmpty(mSearchQueryInProgressStr)) {
                    //Updating the search view content with the partial text if entered previously
                    mSearchView.setQuery(mSearchQueryInProgressStr, false);
                    mSearchView.requestFocus();
                } else {
                    //Updating the search view content with the last search query text executed
                    mSearchView.setQuery(mSearchQueryStr, false);
                    mSearchView.clearFocus();
                }

                if (!mNoFeedViewVisible && !mNetworkErrorViewVisible) {
                    //Triggering the Data load when there was no error previously
                    triggerLoad(false);
                }

                //Initializing other components accordingly: END
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
        //Saving the current position of the top Adapter item partially/completely visible
        outState.putInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY, RecyclerViewUtility.getFirstVisibleItemPosition(mRecyclerView));
        //Saving the visibility state of the "No Feed Layout"
        outState.putBoolean(NO_FEED_VIEW_VISIBILITY_BOOL_KEY, mNoFeedViewVisible);
        //Saving the visibility state of the "Network Error Layout"
        outState.putBoolean(NW_ERROR_VIEW_VISIBILITY_BOOL_KEY, mNetworkErrorViewVisible);
        //Saving the visibility state of the "Intro View"
        outState.putBoolean(INTRO_VIEW_VISIBILITY_BOOL_KEY, (mIntroView.getVisibility() == View.VISIBLE));
        //Saving the Search Query last executed
        outState.putString(SEARCH_VIEW_QUERY_STR_KEY, mSearchQueryStr);
        //Saving the Search Query being entered by the user
        outState.putString(SEARCH_VIEW_QUERY_IN_PROGRESS_STR_KEY, mSearchQueryInProgressStr);

        super.onSaveInstanceState(outState);
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
        //Inflating the Menu options from random_news_menu.xml
        inflater.inflate(R.menu.random_news_menu, menu);
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
                triggerRefresh();
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
     * Method that displays the Intro View ('R.id.random_news_intro_id')
     * and hides the Search Results View ('R.id.search_results_content_id')
     */
    private void showIntroView() {
        //Hiding the Search Results View
        mSearchResultsView.setVisibility(View.GONE);
        //Displaying the Intro View
        mIntroView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that displays the Search Results View ('R.id.search_results_content_id')
     * and hides the Intro View ('R.id.random_news_intro_id')
     */
    private void hideIntroView() {
        //Hiding the Intro View
        mIntroView.setVisibility(View.GONE);
        //Displaying the Search Results View
        mSearchResultsView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that displays the "No Feed Layout".
     */
    private void showNoFeedLayout() {
        //Updating the visibility flag
        mNoFeedViewVisible = true;
        //When the layout needs to be shown
        mErrorView.setVisibility(View.VISIBLE);
        //Displaying the "No Feed Layout"
        replaceFragment(NoFeedResolutionFragment.newInstance(getString(R.string.random_news_title_str)), NoFeedResolutionFragment.FRAGMENT_TAG);
        //Ensuring the Progress is always not shown in this case
        mSwipeContainer.setRefreshing(false);
        //Hiding the Pagination Panel
        showPaginationPanel(false);
        //Hiding other components
        enableDefaultComponents(false);
    }

    /**
     * Method that displays the "Network Error Layout".
     */
    private void showNetworkErrorLayout() {
        //Updating the visibility flag
        mNetworkErrorViewVisible = true;
        //When the layout needs to be shown
        mErrorView.setVisibility(View.VISIBLE);
        //Displaying the "Network Error Layout"
        replaceFragment(NetworkErrorFragment.newInstance(), NetworkErrorFragment.FRAGMENT_TAG);
        //Ensuring the Progress is always not shown in this case
        mSwipeContainer.setRefreshing(false);
        //Hiding the Pagination Panel
        showPaginationPanel(false);
        //Hiding other components
        enableDefaultComponents(false);
    }

    /**
     * Method that replaces the Fragment at 'R.id.error_frame_id' with the Fragment and its Tag passed.
     * Prior to replacing, it checks whether the given Fragment is already present at 'R.id.error_frame_id'
     * or not.
     *
     * @param fragment is the instance of the Fragment that needs to be displayed at 'R.id.error_frame_id'
     * @param tag      is the String identifier used by the FragmentManager for checking the presence of the Fragment
     *                 prior to replacing the content with the Fragment
     */
    private void replaceFragment(Fragment fragment, String tag) {
        //Getting the Instance of the FragmentManager
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            //Getting the FragmentTransaction
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            //Replacing the Fragment at 'R.id.error_frame_id' with the given Fragment and its Tag
            fragmentTransaction.replace(R.id.error_frame_id, fragment, tag).commit();
        }
    }

    /**
     * Method that controls the visibility of the default view components in the search results layout.
     *
     * @param visibility <b>TRUE</b> to display the view components; <b>FALSE</b> to hide them.
     */
    private void enableDefaultComponents(boolean visibility) {
        if (visibility) {
            //Displaying the View components
            mSwipeContainer.setVisibility(View.VISIBLE);
        } else {
            //Hiding the View components
            mSwipeContainer.setVisibility(View.GONE);
            //Clearing the RecyclerView Pool if present : START
            RecyclerView.RecycledViewPool recycledViewPool = mRecyclerView.getRecycledViewPool();
            if (recycledViewPool != null) {
                recycledViewPool.clear();
            }
            //Clearing the RecyclerView Pool if present : END
        }
    }

    /**
     * Method that hides the "Error View"
     * and takes care of setting the dependent view visibility flags to false
     */
    private void hideErrorView() {
        //Hiding the "Error View"
        mErrorView.setVisibility(View.GONE);
        //Setting the "No Feed Layout" visibility flag to false
        mNoFeedViewVisible = false;
        //Setting the "Network Error Layout" visibility flag to false
        mNetworkErrorViewVisible = false;
        //Displaying other components
        enableDefaultComponents(true);
    }

    /**
     * Method that Initializes the Info Card TextView 'R.id.info_card_text_id'
     */
    private void setupInfoCardText() {
        //Setting the Text on TextView
        mInfoCardTextView.setText(getString(R.string.random_news_page_info_text));
        //Setting the Left Compound Drawable
        mInfoCardTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_info_outline_orange),
                null, null, null
        );
        //Setting the Compound Drawable Padding
        mInfoCardTextView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.info_card_text_drawable_padding));
    }

    /**
     * Method that initializes the layout 'R.layout.random_news_intro' for displaying the
     * information on how to use this fragment
     */
    private void setupIntroView() {
        //Finding the TextView to initialize with the information
        TextView infoTextView = mIntroView.findViewById(R.id.message_text_id);
        //Setting the Html Text to be displayed
        TextAppearanceUtility.setHtmlText(infoTextView, getString(R.string.random_news_intro_text));
        //Setting the Font for the Text
        infoTextView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gabriela));
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
        titleTextView.setText(getString(R.string.random_news_title_str));
    }

    /**
     * Method that initializes the SwipeRefreshLayout
     * and its Listener
     */
    private void setupSwipeRefresh() {
        //Registering the refresh listener which triggers the new data loading
        mSwipeContainer.setOnRefreshListener(this);
        //Configuring the progress circle indicator colors
        mSwipeContainer.setColorSchemeResources(
                R.color.colorPink500,
                R.color.colorPurple500,
                R.color.colorIndigo500,
                R.color.colorGreen500
        );
    }

    /**
     * Method that initializes the Layout Manager, Adapter, Listeners
     * and Decoration for the RecyclerView
     */
    private void setupRecyclerView() {
        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing an empty ArrayList of NewsArticleInfo Objects as the dataset for the Adapter
        ArrayList<NewsArticleInfo> newsArticleInfoList = new ArrayList<>();

        //Initializing the Adapter for the List view
        mRecyclerAdapter = new ArticlesAdapter(requireContext(), R.layout.news_article_item, getLoaderManager(), newsArticleInfoList, mLoaderIds);

        //Registering the OnAdapterItemDataSwapListener
        mRecyclerAdapter.setOnAdapterItemDataSwapListener(this);

        //Registering the OnAdapterItemClickListener
        mRecyclerAdapter.setOnAdapterItemClickListener(this);

        //Registering the OnAdapterItemPopupMenuClickListener
        mRecyclerAdapter.setOnAdapterItemPopupMenuClickListener(this);

        //Registering the pagination scroll listener on RecyclerView for paginated views
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(this, VSCROLL_PAGINATION_TRIGGER_POS));
        //Setting the Item Decor on RecyclerView for proper Card Item and Paginated Buttons spacing
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorUtility(
                getResources().getDimensionPixelOffset(R.dimen.card_item_spacing),
                getResources().getDimensionPixelSize(R.dimen.pagination_button_size)
        ));

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        //Triggering content refresh
        triggerRefresh();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onResume of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        //Registering the Preference Change Listener
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        //Triggering a new data load only if any parameters have changed
        checkAndReloadData();
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to Activity.onPause of the containing Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();

        //Unregistering the Preference Change Listener
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);

        //Unregistering the Bookmark Action Presenter
        if (mBookmarkActionPresenter != null) {
            mBookmarkActionPresenter.detachView();
        }

        //Unregistering the Favorite Action Presenter
        if (mFavoriteActionPresenter != null) {
            mFavoriteActionPresenter.detachView();
        }
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        //Unregistering the Pagination Presenter
        mPaginationPresenter.detachView();
        super.onDestroy();
    }

    /**
     * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
     */
    @Override
    public void onItemDataSwapped() {
        Logger.d(LOG_TAG, "onItemDataSwapped: News Articles data loaded successfully");
        //Ensuring the "Error View" is hidden
        hideErrorView();

        //Hiding the Progress Indicator after the data load completion
        mSwipeContainer.setRefreshing(false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Scrolling over to first item after data load with a delay of 10ms
                scrollToItemPosition(mVisibleItemViewPosition, false);
            }
        }, 10); //This delay is for the animations to complete


        //Clearing the focus on SearchView
        mSearchView.clearFocus();

        //Updating the state of Pagination Buttons after data load
        mPaginationPresenter.updatePaginationButtonsState();
    }

    /**
     * Method that sets the Layout Manager's currently viewing position to the item position specified
     *
     * @param position        is the item position to which the Layout Manager needs to be set
     * @param scrollImmediate is a boolean which denotes the way in which the scroll to position
     *                        needs to be handled
     *                        <br/><b>TRUE</b> if the scroll to position needs to be set immediately
     *                        without any animation
     *                        <br/><b>FALSE</b> if the scroll to position needs to be done naturally
     *                        with the default animation
     */
    public void scrollToItemPosition(int position, boolean scrollImmediate) {
        //Retrieving the layout manager of RecyclerView
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (position > RecyclerView.NO_POSITION) {
            //Scrolling to the item position passed when valid

            //Validating the position passed is different from the top one to update if required
            if (RecyclerViewUtility.getFirstVisibleItemPosition(mRecyclerView) != position) {
                //Updating the item position reference
                mVisibleItemViewPosition = position;

                Logger.d(LOG_TAG, "scrollToItemPosition: Updating to position " + mVisibleItemViewPosition);

                if (scrollImmediate) {
                    //Scrolling to the item position immediately
                    layoutManager.scrollToPositionWithOffset(mVisibleItemViewPosition, 0);
                } else {
                    //Scrolling to the item position naturally with smooth scroll
                    RecyclerViewUtility.smoothVScrollToPositionWithViewTop(mRecyclerView, mVisibleItemViewPosition);
                }
            }

            //Show/Hide Pagination panel based on the position of the currently visible item at the top
            checkAndEnablePaginationPanel();
        }
    }

    /**
     * Method that shows/hides the pagination panel for Paginated results
     * based on the position of the currently visible item at the top
     */
    public void checkAndEnablePaginationPanel() {
        //Retrieving the current number of items in the RecyclerView
        int totalItems = mRecyclerAdapter.getItemCount();

        if (totalItems == 0) {
            //Hiding the Pagination panel for no items
            showPaginationPanel(false);
        } else if ((totalItems - VSCROLL_PAGINATION_TRIGGER_POS) <= 0) {
            //Displaying the Pagination panel by default when the number of items are less
            showPaginationPanel(true);
        } else {
            //When there are considerable number of items
            if (mVisibleItemViewPosition >= totalItems - VSCROLL_PAGINATION_TRIGGER_POS) {
                //Displaying the Pagination panel when the Bottom Y items are reached
                showPaginationPanel(true);
            } else {
                //Hiding the Pagination panel when away from the Bottom Y items
                showPaginationPanel(false);
            }
        }
    }

    /**
     * Method invoked when an Item in the Adapter is clicked
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        clicked in the Adapter
     */
    @Override
    public void onItemClick(NewsArticleInfo newsArticleInfo) {
        //Launching the News Article in a Web Browser
        IntentUtility.openLink(requireContext(), newsArticleInfo.getWebUrl());
    }

    /**
     * Method invoked when "Share News" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onShareNewsArticle(NewsArticleInfo newsArticleInfo) {
        //Building and launching the share intent, to share the Webpage URL
        IntentUtility.shareText(getActivity(), newsArticleInfo.getWebUrl(),
                getString(R.string.article_share_chooser_title));
    }

    /**
     * Method invoked when "Read Later" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onMarkForRead(NewsArticleInfo newsArticleInfo) {
        //(Adding entry to the Bookmarks table in future implementation)

        //Delegating to the Presenter to the add the selected News Article to Bookmarks: START
        mBookmarkActionPresenter = new BookmarkActionPresenter();
        mBookmarkActionPresenter.attachView(this);
        mBookmarkActionPresenter.addBookmark(newsArticleInfo);
        //Delegating to the Presenter to the add the selected News Article to Bookmarks: END
    }

    /**
     * Method invoked when "Favorite this" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onMarkAsFav(NewsArticleInfo newsArticleInfo) {
        //(Adding entry to the Favorites table in future implementation)

        //Delegating to the Presenter to the add the selected News Article to Favorites: START
        mFavoriteActionPresenter = new FavoriteActionPresenter();
        mFavoriteActionPresenter.attachView(this);
        mFavoriteActionPresenter.addFavorite(newsArticleInfo);
        //Delegating to the Presenter to the add the selected News Article to Favorites: END
    }

    /**
     * Method invoked when "Open News Section" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onOpenNewsSectionRequest(NewsArticleInfo newsArticleInfo) {
        //No-op, as this is not applicable for this fragment
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!mKeysToExclude.contains(key)) {
            Logger.d(LOG_TAG, "onSharedPreferenceChanged: key " + key);
            //Resetting to the position of top item
            mVisibleItemViewPosition = 0;

            //Triggering a new data load only if any parameters have changed
            //(This also prevents duplicate triggers)
            checkAndReloadData();
        }
    }

    /**
     * Method that compares the URL previously used by an existing loader
     * with the URL generated using the current parameters to trigger a new data load only if necessary
     */
    public void checkAndReloadData() {
        if (getActivity() != null && !TextUtils.isEmpty(mSearchQueryStr)) {
            //When attached to an Activity and there has been a search previously executed
            Logger.d(LOG_TAG, "checkAndReloadData: Started");
            //Retrieving the current loader of the Fragment
            LoaderManager loaderManager = getLoaderManager();
            Loader<List<NewsArticleInfo>> loader = loaderManager.getLoader(mLoaderIds[0]);
            if (loader != null) {
                //When loader was previously registered with the loader id used

                //Casting the loader to NewsArticlesLoader
                NewsArticlesLoader articlesLoader = (NewsArticlesLoader) loader;
                //Retrieving the URL used by the Loader
                String requestURLStr = articlesLoader.getRequestURLStr();
                //Generating a new URL using the current parameters for comparison
                String newRequestURLStr = mUrlGenerator.createSearchURL(mSearchQueryStr).toExternalForm();
                if (!newRequestURLStr.equals(requestURLStr)) {
                    //When the URLs are different, reload the data
                    Logger.d(LOG_TAG, "checkAndReloadData: Reloading data");
                    triggerLoad(true);
                }
            }
        }
    }

    /**
     * This method returns the {@link Context}
     * of the Activity/Fragment implementing {@link IArticleActionView}
     *
     * @return {@link Context} of the Activity/Fragment
     */
    @Override
    public Context getViewContext() {
        return getContext();
    }

    /**
     * Method that returns the Root {@link View}
     * of the Activity/Fragment implementing {@link IArticleActionView}
     *
     * @return The Root {@link View} of the implementing Activity/Fragment
     */
    @Override
    public View getRootView() {
        return getView();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<NewsArticleInfo>> onCreateLoader(int id, Bundle args) {
        if (id == mLoaderIds[0]) {
            //Returning the Instance of NewsArticlesLoader
            URL searchURL = mUrlGenerator.createSearchURL(mSearchQueryStr);
            Logger.d(LOG_TAG, "onCreateLoader: SearchURL " + searchURL);
            return new NewsArticlesLoader(getActivity(), searchURL);
        }
        return null;
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader           The Loader that has finished.
     * @param newsArticleInfos The List of {@link NewsArticleInfo} objects extracted by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsArticleInfo>> loader, List<NewsArticleInfo> newsArticleInfos) {
        if (loader.getId() == mLoaderIds[0]) {
            if (newsArticleInfos != null && newsArticleInfos.size() > 0) {
                //Loading the data to the adapter when present
                mRecyclerAdapter.swapItemData(newsArticleInfos);
                //Updating the last page index value to the Fragment member
                int lastPageIndex = ((NewsArticlesLoader) loader).getLastPageIndex();
                if (lastPageIndex > 0) {
                    //When the last page index is calculated

                    //Resetting the 'endIndex' preference setting value to the last page index determined
                    mPaginationPresenter.resetEndPageIndex(lastPageIndex);
                }

            } else {
                //When the data returned is Empty or NULL

                //Hiding the Progress Indicator on failure
                mSwipeContainer.setRefreshing(false);

                NewsArticlesLoader newsArticlesLoader = (NewsArticlesLoader) loader;

                if (!newsArticlesLoader.getNetworkConnectivityStatus()) {
                    //Reporting Network Failure when False
                    Logger.d(LOG_TAG, "onLoadFinished: Network Failure");
                    //Displaying the "Network Error Layout"
                    showNetworkErrorLayout();
                } else {
                    //When there is NO network issue and the current page has no data to be shown
                    Logger.d(LOG_TAG, "onLoadFinished: NO DATA RETURNED");

                    //Retrying if the current page is not the first page
                    if (PreferencesUtility.getStartPageIndex(getContext(), mPreferences) > 1) {
                        //When not on first page, reset the 'page' setting value to 1,
                        //to refresh the content and show the first page if possible
                        mPaginationPresenter.resetStartPageIndex();

                    } else {
                        //Otherwise, displaying the "No Feed Layout" when there is no data
                        showNoFeedLayout();
                    }

                }

            }
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsArticleInfo>> loader) {
        //Creating an Empty List of NewsArticleInfo objects to clear the content in the Adapter
        ArrayList<NewsArticleInfo> newsArticleInfoList = new ArrayList<>();
        //Calling the Adapter's swap method to clear the data
        mRecyclerAdapter.swapItemData(newsArticleInfoList);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Delegating to the Pagination Presenter to handle
        mPaginationPresenter.onPaginationButtonClick(view);
    }

    /**
     * Method that returns the {@link View}
     * of the Pagination Panel
     *
     * @return {@link View} of the Pagination Panel
     */
    @Override
    public View getPaginationPanel() {
        return mPaginationPanel;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the very First Page
     *
     * @return The Pagination {@link ImageButton} for the First Page
     */
    @Override
    public ImageButton getPageFirstButton() {
        return mPageFirstButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Last page
     *
     * @return The Pagination {@link ImageButton} for the Last Page
     */
    @Override
    public ImageButton getPageLastButton() {
        return mPageLastButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Next page
     *
     * @return The Pagination {@link ImageButton} for the Next Page
     */
    @Override
    public ImageButton getPageNextButton() {
        return mPageNextButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Previous Page
     *
     * @return The Pagination {@link ImageButton} for the Previous Page
     */
    @Override
    public ImageButton getPagePreviousButton() {
        return mPagePreviousButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Page selected by the user in the corresponding dialog
     *
     * @return The Pagination {@link ImageButton} for the User Selected Page
     */
    @Override
    public ImageButton getPageMoreButton() {
        return mPageMoreButton;
    }

    /**
     * Method that displays/hides the Pagination Panel
     * based on the value of visibility passed.
     *
     * @param visibility <b>TRUE</b> to display the Pagination Panel; <b>FALSE</b> otherwise
     */
    private void showPaginationPanel(boolean visibility) {
        if (visibility) {
            //Displaying the Pagination Panel when True
            mPaginationPresenter.showPaginationPanel();
        } else {
            //Hiding the Pagination Panel when False
            mPaginationPresenter.hidePaginationPanel();
        }
    }

    /**
     * Method that triggers a data load
     *
     * @param forceLoad Boolean value that controls the nature of the trigger
     *                  <br/><b>TRUE</b> to start a new/existing load process
     *                  <br/><b>FALSE</b> to forcefully start a new load process
     */
    private void triggerLoad(boolean forceLoad) {
        if (getActivity() != null) {
            //Triggering only when attached to an Activity
            LoaderManager loaderManager = getLoaderManager();
            if (forceLoad) {
                //When forcefully triggered, restart the loader
                loaderManager.restartLoader(mLoaderIds[0], null, this);
            } else {
                //When triggered, start a new loader or load the existing loader
                loaderManager.initLoader(mLoaderIds[0], null, this);
            }
            //Displaying the data load progress indicator
            mSwipeContainer.setRefreshing(true);
        }
    }

    /**
     * Method that triggers a content refresh
     */
    @Override
    public void triggerRefresh() {
        //Resetting the top visible item position to 0, prior to refresh
        mVisibleItemViewPosition = 0;

        if (PreferencesUtility.getStartPageIndex(getContext(), mPreferences) > 1) {
            //When not on first page, reset the 'page' setting value to 1
            //to refresh the content and show the first page
            mPaginationPresenter.resetStartPageIndex();
        } else {
            //Else, forcefully trigger a new data load
            triggerLoad(true);
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        //Saving the Query entered
        mSearchQueryStr = query;
        //Clearing the member that stores the partially entered search query
        mSearchQueryInProgressStr = "";
        //Ensuring that the Intro view is hidden
        hideIntroView();
        //Triggering content refresh (instead of trigger load since the 'page' setting value might be > 1)
        triggerRefresh();
        //Clearing the focus on SearchView
        mSearchView.clearFocus();
        //Returning true as we are handling the action manually
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        //Saving the partially entered Search query
        mSearchQueryInProgressStr = newText;
        //Returning true as we do not care about suggestions
        return true;
    }

    /**
     * Subclass of {@link BaseRecyclerViewScrollListener} that listens to the scroll event
     * received when the scroll reaches/leaves the last y items in the {@link RecyclerView}
     */
    private class RecyclerViewScrollListener extends BaseRecyclerViewScrollListener {

        //Stores the fragment that is registered to this ScrollListener
        private Fragment fragment;

        /**
         * Constructor of {@link BaseRecyclerViewScrollListener}
         *
         * @param fragment                    The fragment that is registering this ScrollListener
         * @param bottomYEndItemPosForTrigger is the Integer value of the trigger point
         *                                    for when the vertical scroll reaches/leaves
         *                                    the last y items in RecyclerView
         */
        RecyclerViewScrollListener(Fragment fragment, int bottomYEndItemPosForTrigger) {
            super(bottomYEndItemPosForTrigger);
            this.fragment = fragment;
        }

        /**
         * Callback Method to be implemented to receive events when the
         * scroll has reached/left the last y items in the {@link RecyclerView}
         *
         * @param verticalScrollAmount is the amount of vertical scroll.
         *                             <br/>If >0 then scroll is moving towards the bottom;
         *                             <br/>If <0 then scroll is moving towards the top
         */
        @Override
        public void onBottomReached(int verticalScrollAmount) {
            //Show/Hide the Pagination Panel based on the scroll amount
            showPaginationPanel(verticalScrollAmount > 0);
        }

    }
}
