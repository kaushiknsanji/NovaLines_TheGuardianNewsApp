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
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.ArticlesAdapter;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.errorviews.NetworkErrorFragment;
import com.example.kaushiknsanji.novalines.errorviews.NoFeedResolutionFragment;
import com.example.kaushiknsanji.novalines.interfaces.IArticleActionView;
import com.example.kaushiknsanji.novalines.interfaces.IRefreshActionView;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.observers.BaseRecyclerViewScrollListener;
import com.example.kaushiknsanji.novalines.presenters.BookmarkActionPresenter;
import com.example.kaushiknsanji.novalines.presenters.FavoriteActionPresenter;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.NewsURLGenerator;
import com.example.kaushiknsanji.novalines.utils.PreferencesObserverUtility;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewItemDecorUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewUtility;
import com.example.kaushiknsanji.novalines.workers.NewsArticlesLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that inflates the layout 'R.layout.articles_layout'
 * containing the {@link RecyclerView} used in the {@link com.example.kaushiknsanji.novalines.adapters.HeadlinesPagerAdapter}
 * for the ViewPager shown in {@link HeadlinesFragment}.
 * <p>
 * Responsible for displaying the News Feeds from various News Categories/Sections
 * subscribed by the user.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class ArticlesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<NewsArticleInfo>>,
        SwipeRefreshLayout.OnRefreshListener,
        ArticlesAdapter.OnAdapterItemDataSwapListener,
        ArticlesAdapter.OnAdapterItemClickListener,
        ArticlesAdapter.OnAdapterItemPopupMenuClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, IArticleActionView, IRefreshActionView {

    //Constant used for logs
    private static final String LOG_TAG = ArticlesFragment.class.getSimpleName();

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
    //Bundle Key Constant to save/restore the index of the last page viewed by the user
    private static final String LAST_VIEWED_PAGE_INT_KEY = "PaginatedView.LastViewedIndex";

    //Constant used as a Bundle Key for the News Topic ID parameter
    private static final String NEWS_TOPIC_ID_STRING_KEY = "NewsTopicID";
    //Constant used as a Bundle Key to ID the Position of the Fragment in the ViewPager
    private static final String FRAGMENT_POS_INDEX_INT_KEY = "Fragment.PosIndex";
    //Stores the ID of the News Topic shown by the Fragment
    private String mNewsTopicId;
    //Stores the Unique IDs of the Loaders required by the Fragment instance
    private int[] mLoaderIds;
    //For the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeContainer;

    //For the RecyclerView
    private RecyclerView mRecyclerView;

    //For the Adapter of RecyclerView
    private ArticlesAdapter mRecyclerAdapter;

    //Stores reference to the URL Generator
    private NewsURLGenerator mUrlGenerator;

    //For the Settings SharedPreferences
    private SharedPreferences mPreferences;

    //List of Preference Keys to exclude while triggering the loader to load data
    private List<String> mKeysToExclude;

    //Saves whether this Fragment is showing a view with Paginated results or not
    private boolean mIsPaginatedView;

    //Saves whether the "No Feed Layout" should be visible/hidden
    private boolean mNoFeedViewVisible;

    //Saves whether the "Network Error Layout" should be visible/hidden
    private boolean mNetworkErrorViewVisible;

    //For the "Error Layout"
    private View mErrorView;

    //Saves the last page index of the News Query result
    private int mLastPageIndex = 1; //Defaulted to 1

    //Saves the last viewed page index of the News Query result
    private int mLastViewedPageIndex = 1; //Defaulted to 1

    //Saves the top visible Adapter Item position
    private int mVisibleItemViewPosition;

    //Presenters for handling the Bookmark and Favorite Actions
    private BookmarkActionPresenter mBookmarkActionPresenter;
    private FavoriteActionPresenter mFavoriteActionPresenter;

    /**
     * Static constructor of the Fragment {@link ArticlesFragment}
     *
     * @param newsTopicId String value representing the unique ID of the News Topic
     *                    that this Fragment is required to display
     * @param position    is the Integer value of the Position of the Fragment in the ViewPager
     * @return Instance of this Fragment {@link ArticlesFragment}
     */
    public static ArticlesFragment newInstance(String newsTopicId, int position) {
        ArticlesFragment articlesFragment = new ArticlesFragment();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(2);
        bundle.putString(NEWS_TOPIC_ID_STRING_KEY, newsTopicId);
        bundle.putInt(FRAGMENT_POS_INDEX_INT_KEY, position);
        articlesFragment.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return articlesFragment;
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
     * @return Return the View for the fragment's UI ('R.layout.articles_layout')
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.articles_layout'
        View rootView = inflater.inflate(R.layout.articles_layout, container, false);

        //Retrieving the Bundle arguments passed
        Bundle arguments = getArguments();
        mNewsTopicId = arguments != null ? arguments.getString(NEWS_TOPIC_ID_STRING_KEY) : "";
        int fragmentPosId = arguments != null ? arguments.getInt(FRAGMENT_POS_INDEX_INT_KEY) : 0;
        int fragmentId = getId(); //Getting the current Fragment ID constant

        if (!mNewsTopicId.equals(getString(R.string.top_stories_section_id))
                && !mNewsTopicId.equals(getString(R.string.most_visited_section_id))) {
            //Setting the pagination boolean to True for News Topics
            //other than "Top Stories" and "Most Visited" as they are single page views
            mIsPaginatedView = true;
        }

        //Initializing the Array of Loader IDs: START
        mLoaderIds = new int[LOADER_COUNT_PER_FRAG];
        int startLoaderId = fragmentId + fragmentPosId + (fragmentPosId - 1) * (LOADER_COUNT_PER_FRAG - 1);
        for (int index = 0; index < LOADER_COUNT_PER_FRAG; index++) {
            mLoaderIds[index] = startLoaderId + index;
        }
        //Initializing the Array of Loader IDs: END

        //Finding the SwipeRefreshLayout
        mSwipeContainer = rootView.findViewById(R.id.swipe_container_id);
        //Initializing the SwipeRefreshLayout
        setupSwipeRefresh();

        //Finding the RecyclerView
        mRecyclerView = mSwipeContainer.findViewById(R.id.news_recycler_view_id);
        //Initializing the RecyclerView
        setupRecyclerView();

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        //Reading the List of Preference Keys to exclude while triggering the loader to load data
        mKeysToExclude = PreferencesObserverUtility.getPreferenceKeysToExclude(getContext());

        //Initializing the URL Generator for use with the NewsArticlesLoader
        mUrlGenerator = new NewsURLGenerator(requireContext());

        //Finding the "Error View"
        mErrorView = rootView.findViewById(R.id.error_frame_id);

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Ensuring the "Error View" is hidden
            hideErrorView();

        } else {
            //On subsequent launch of this Fragment

            if (mIsPaginatedView && getUserVisibleHint()) {
                //For Paginated Results
                //(when this fragment is the one currently being viewed by the user)

                //Restoring the index of the last page viewed by the user
                mLastViewedPageIndex = savedInstanceState.getInt(LAST_VIEWED_PAGE_INT_KEY);

                //Updating the state of Pagination Buttons on load
                if (getParentFragment() != null) {
                    ((HeadlinesFragment) getParentFragment()).updatePaginationButtonsState();
                }
            }

            //Restoring the value of the position of the top Adapter item position previously visible
            mVisibleItemViewPosition = savedInstanceState.getInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY);

            //Restoring the visibility of "No Feed Layout"
            if (savedInstanceState.getBoolean(NO_FEED_VIEW_VISIBILITY_BOOL_KEY)) {
                showNoFeedLayout();
            }

            //Restoring the visibility of "Network Error Layout"
            if (savedInstanceState.getBoolean(NW_ERROR_VIEW_VISIBILITY_BOOL_KEY)) {
                showNetworkErrorLayout();
            }

        }

        //Triggering the Data load
        triggerLoad(false);

        //Returning the prepared layout
        return rootView;
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

        //UnRegistering the Preference Change Listener
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
        //Saving the index of the last page viewed
        outState.putInt(LAST_VIEWED_PAGE_INT_KEY, mLastViewedPageIndex);

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
        //Inflating the Menu options from section_news_menu.xml
        inflater.inflate(R.menu.section_news_menu, menu);
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
     * Method that returns whether the Fragment is displaying a view with Paginated results or not
     *
     * @return <b>TRUE</b> if the Fragment is displaying Paginated results;
     * <br/><b>FALSE</b> otherwise
     */
    public boolean isPaginatedView() {
        return mIsPaginatedView;
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
        mRecyclerAdapter = new ArticlesAdapter(requireContext(), R.layout.news_article_item, getLoaderManager(), newsArticleInfoList, mLoaderIds, mNewsTopicId);

        //Registering the OnAdapterItemDataSwapListener
        mRecyclerAdapter.setOnAdapterItemDataSwapListener(this);

        //Registering the OnAdapterItemClickListener
        mRecyclerAdapter.setOnAdapterItemClickListener(this);

        //Registering the OnAdapterItemPopupMenuClickListener
        mRecyclerAdapter.setOnAdapterItemPopupMenuClickListener(this);

        if (mIsPaginatedView) {
            //For Paginated Results

            //Registering the pagination scroll listener on RecyclerView for paginated views
            mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(this, VSCROLL_PAGINATION_TRIGGER_POS));
            //Setting the Item Decor on RecyclerView for proper Card Item and Paginated Buttons spacing
            mRecyclerView.addItemDecoration(new RecyclerViewItemDecorUtility(
                    getResources().getDimensionPixelOffset(R.dimen.card_item_spacing),
                    getResources().getDimensionPixelSize(R.dimen.pagination_button_size)
            ));
        } else {
            //For Single Page Results

            //Setting the Item Decor on RecyclerView for proper Card Item spacing
            mRecyclerView.addItemDecoration(new RecyclerViewItemDecorUtility(
                    getResources().getDimensionPixelOffset(R.dimen.card_item_spacing)
            ));
        }

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(mRecyclerAdapter);
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
     * based on whether the content is Paginated or Single Page
     */
    @Override
    public void triggerRefresh() {
        //Resetting the top visible item position to 0, prior to refresh
        mVisibleItemViewPosition = 0;

        if (mIsPaginatedView) {
            //For Paginated Results

            if (PreferencesUtility.getStartPageIndex(getContext(), mPreferences) > 1) {
                //When not on first page, reset the 'page' setting value to 1
                //to refresh the content and show the first page
                if (getParentFragment() != null) {
                    ((HeadlinesFragment) getParentFragment()).resetStartPageIndex();
                }
            } else {
                //Else, forcefully trigger a new data load
                triggerLoad(true);
            }

        } else {
            //For Single Page results, forcefully trigger a new data load when pulled/swiped for refresh
            triggerLoad(true);
        }
    }

    /**
     * Method that returns the section ID of the News Topic shown by the Fragment
     *
     * @return String denoting the section ID of the News Topic shown by the Fragment
     */
    public String getNewsTopicId() {
        return mNewsTopicId;
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

                Log.d(LOG_TAG + "_" + mNewsTopicId, "scrollToItemPosition: Updating to position " + mVisibleItemViewPosition);

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
        if (getParentFragment() != null) {
            if (mIsPaginatedView) {
                //For Paginated Results

                //Retrieving the current number of items in the RecyclerView
                int totalItems = mRecyclerAdapter.getItemCount();

                if (totalItems == 0) {
                    //Hiding the Pagination panel for no items
                    ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, false);
                } else if ((totalItems - VSCROLL_PAGINATION_TRIGGER_POS) <= 0) {
                    //Displaying the Pagination panel by default when the number of items are less
                    ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, true);
                } else {
                    //When there are considerable number of items
                    if (mVisibleItemViewPosition >= totalItems - VSCROLL_PAGINATION_TRIGGER_POS) {
                        //Displaying the Pagination panel when the Bottom Y items are reached
                        ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, true);
                    } else {
                        //Hiding the Pagination panel when away from the Bottom Y items
                        ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, false);
                    }

                }

            } else {
                //Hiding the Pagination panel by default for Single Page Results
                ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, false);
            }
        }
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
            URL sectionURL = mUrlGenerator.createSectionURL(mNewsTopicId);
            Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateLoader: SectionURL " + sectionURL);
            return new NewsArticlesLoader(getActivity(), sectionURL);
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
                mLastPageIndex = ((NewsArticlesLoader) loader).getLastPageIndex();
                if (mLastPageIndex > 0 && getUserVisibleHint()) {
                    //When the last page index is calculated
                    //and the current fragment is the one being viewed by the user

                    //Resetting the 'endIndex' preference setting value to the last page index determined
                    if (getParentFragment() != null) {
                        ((HeadlinesFragment) getParentFragment()).resetEndPageIndex(mLastPageIndex);
                    }
                }

            } else {
                //When the data returned is Empty or NULL

                //Hiding the Progress Indicator on failure
                mSwipeContainer.setRefreshing(false);

                NewsArticlesLoader newsArticlesLoader = (NewsArticlesLoader) loader;

                if (!newsArticlesLoader.getNetworkConnectivityStatus()) {
                    //Reporting Network Failure when False
                    Log.d(LOG_TAG + "_" + mNewsTopicId, "onLoadFinished: Network Failure");
                    //Displaying the "Network Error Layout"
                    showNetworkErrorLayout();
                } else {
                    //When there is NO network issue and the current page has no data to be shown
                    Log.d(LOG_TAG + "_" + mNewsTopicId, "onLoadFinished: NO DATA RETURNED");

                    //Retrying for Paginated Results if the current page is not the first page
                    if (mIsPaginatedView && PreferencesUtility.getStartPageIndex(getContext(), mPreferences) > 1) {
                        //When not on first page, reset the 'page' setting value to 1,
                        //to refresh the content and show the first page if possible
                        if (getParentFragment() != null) {
                            ((HeadlinesFragment) getParentFragment()).resetStartPageIndex();
                        }

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
     * Method that displays the "No Feed Layout".
     */
    private void showNoFeedLayout() {
        //Updating the visibility flag
        mNoFeedViewVisible = true;
        //When the layout needs to be shown
        mErrorView.setVisibility(View.VISIBLE);
        //Displaying the "No Feed Layout"
        replaceFragment(NoFeedResolutionFragment.newInstance(getString(R.string.headlines_title_str)), NoFeedResolutionFragment.FRAGMENT_TAG);
        //Ensuring the Progress is always not shown in this case
        mSwipeContainer.setRefreshing(false);
        //Hiding the Pagination Panel
        if (getParentFragment() != null) {
            ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, false);
        }
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
        if (getParentFragment() != null) {
            ((HeadlinesFragment) getParentFragment()).showPaginationPanel(this, false);
        }
        //Hiding other components
        enableDefaultComponents(false);
    }

    /**
     * Method that controls the visibility of the default view components in the layout.
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
        Log.d(LOG_TAG + "_" + mNewsTopicId, "hideErrorView: Started");
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
     * Method that returns the last page index of the News Query result
     *
     * @return Integer value of the last page index of the News Query result
     */
    public int getLastPageIndex() {
        return mLastPageIndex;
    }

    /**
     * Method that returns the index of the last page viewed by the user
     *
     * @return Integer value of the last page viewed by the user
     */
    public int getLastViewedPageIndex() {
        return mLastViewedPageIndex;
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
     * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
     */
    @Override
    public void onItemDataSwapped() {
        Log.d(LOG_TAG + "_" + mNewsTopicId, "onItemDataSwapped: News Articles data loaded successfully for " + mNewsTopicId);
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
        //Opening the News Category Tab for the News Section requested
        if (getParentFragment() != null) {
            ((HeadlinesFragment) getParentFragment()).openNewsCategoryTabByTitle(newsArticleInfo.getSectionName(), newsArticleInfo.getSectionId());
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or removed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!mKeysToExclude.contains(key)) {
            Log.d(LOG_TAG + "_" + mNewsTopicId, "onSharedPreferenceChanged: key " + key);
            mVisibleItemViewPosition = 0;

            if (getUserVisibleHint()) {
                //When the current fragment is the one viewed by the user
                if (key.equals(PreferencesUtility.getStartPageIndexKey(requireContext()))) {
                    //On the change in 'page' setting value

                    //Saving the 'page' setting value as the index of the last page viewed
                    mLastViewedPageIndex = PreferencesUtility.getStartPageIndex(getContext(), mPreferences);
                }
            }

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
        if (getActivity() != null && getUserVisibleHint()) {
            //When attached to an Activity and the current fragment is the one viewed by the user
            Log.d(LOG_TAG + "_" + mNewsTopicId, "checkAndReloadData: Started");
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
                String newRequestURLStr = mUrlGenerator.createSectionURL(mNewsTopicId).toExternalForm();
                if (!newRequestURLStr.equals(requestURLStr)) {
                    //When the URLs are different, reload the data
                    Log.d(LOG_TAG + "_" + mNewsTopicId, "checkAndReloadData: Reloading data");
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
        return getParentFragment() != null ? getParentFragment().getView() : null;
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
         * @param fragment The fragment that is registering this ScrollListener
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
            //Propagating the call to the Parent Fragment - HeadlinesFragment
            if (getParentFragment() != null) {
                ((HeadlinesFragment) getParentFragment()).showPaginationPanel(fragment, verticalScrollAmount > 0);
            }
        }

    }

}