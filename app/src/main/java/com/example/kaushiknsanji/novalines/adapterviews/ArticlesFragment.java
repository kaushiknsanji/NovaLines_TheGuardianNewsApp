package com.example.kaushiknsanji.novalines.adapterviews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.ArticlesAdapter;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.observers.BaseRecyclerViewScrollListener;
import com.example.kaushiknsanji.novalines.utils.NewsURLGenerator;
import com.example.kaushiknsanji.novalines.utils.PreferencesObserverUtility;
import com.example.kaushiknsanji.novalines.workers.NewsArticlesLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushik N Sanji on 08-Jan-18.
 */
public class ArticlesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<NewsArticleInfo>>,
        SwipeRefreshLayout.OnRefreshListener,
        ArticlesAdapter.OnAdapterItemDataSwapListener,
        ArticlesAdapter.OnAdapterItemClickListener,
        ArticlesAdapter.OnAdapterItemPopupMenuClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Constant used for logs
    private static final String LOG_TAG = ArticlesFragment.class.getSimpleName();

    //Constant that holds the count of unique loaders required by the Fragment instance
    private static final int LOADER_COUNT_PER_FRAG = 2;

    //Constant that sets the trigger point for when the vertical scroll reaches/leaves
    //the last y items in RecyclerView to show/hide the pagination panel for Paginated Results
    private static final int VSCROLL_PAGINATION_TRIGGER_POS = 3;

    //Bundle Key Constant to save/restore the value of the top visible Adapter Item position
    private static final String VISIBLE_ITEM_VIEW_POSITION_INT_KEY = "RecyclerView.TopItemPosition";
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

    //Saves the last page index of the News Query result
    private int mLastPageIndex = 1; //Defaulted to 1

    //Saves the top visible Adapter Item position
    private int mVisibleItemViewPosition;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.articles_layout'
        View rootView = inflater.inflate(R.layout.articles_layout, container, false);

        //Retrieving the Bundle arguments passed
        Bundle arguments = getArguments();
        mNewsTopicId = arguments.getString(NEWS_TOPIC_ID_STRING_KEY);
        int fragmentPosId = arguments.getInt(FRAGMENT_POS_INDEX_INT_KEY);
        int fragmentId = getId(); //Getting the current Fragment ID constant

        Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateView: Started");

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

        Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateView: mNewsTopicId " + mNewsTopicId);
        Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateView: mLoaderId  - 1 " + mLoaderIds[0]);
        Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateView: mLoaderId  - 2 " + mLoaderIds[1]);
        Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateView: getId " + getId());

        //Finding the SwipeRefreshLayout
        mSwipeContainer = rootView.findViewById(R.id.swipe_container_id);
        //Initializing the SwipeRefreshLayout
        setupSwipeRefresh();

        //Finding the RecyclerView
        mRecyclerView = mSwipeContainer.findViewById(R.id.news_recycler_view_id);
        //Initializing the RecyclerView
        setupRecyclerView();

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Reading the List of Preference Keys to exclude while triggering the loader to load data
        mKeysToExclude = PreferencesObserverUtility.getPreferenceKeysToExclude(getContext());

        //Initializing the URL Generator for use with the NewsArticlesLoader
        mUrlGenerator = new NewsURLGenerator(getContext());

        //Triggering the Data load
        triggerLoad(false);

        if (savedInstanceState != null) {
            //On subsequent launch of this Fragment

            if (mIsPaginatedView) {
                //For Paginated Results

                //Updating the state of Pagination Buttons on load
                ((HeadlinesFragment) getParentFragment()).updatePaginationButtonsState();
            }

            //Restoring the value of the position of the top Adapter item position previously visible
            mVisibleItemViewPosition = savedInstanceState.getInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY);
        }

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
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process if
     * restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving the current position of the top Adapter item partially/completely visible
        outState.putInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY, getFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
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
        mRecyclerAdapter = new ArticlesAdapter(getContext(), R.layout.news_article_item, newsArticleInfoList, mLoaderIds);

        //Registering the OnAdapterItemDataSwapListener
        mRecyclerAdapter.setOnAdapterItemDataSwapListener(this);

        //Registering the OnAdapterItemClickListener
        mRecyclerAdapter.setOnAdapterItemClickListener(this);

        //Registering the OnAdapterItemPopupMenuClickListener
        mRecyclerAdapter.setOnAdapterItemPopupMenuClickListener(this);

        if (mIsPaginatedView) {
            //For Paginated Results

            //Registering the pagination scroll listener on RecyclerView for paginated views
            mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(VSCROLL_PAGINATION_TRIGGER_POS));
            //Setting the Item Decor on RecyclerView for proper Card Item and Paginated Buttons spacing
            mRecyclerView.addItemDecoration(new CardItemDecoration(
                    getResources().getDimensionPixelOffset(R.dimen.card_item_spacing),
                    getResources().getDimensionPixelSize(R.dimen.pagination_button_size)
            ));
        } else {
            //For Single Page Results

            //Setting the Item Decor on RecyclerView for proper Card Item spacing
            mRecyclerView.addItemDecoration(new CardItemDecoration(
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
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
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
    private void triggerRefresh() {
        //Resetting the top visible item position to 0, prior to refresh
        mVisibleItemViewPosition = 0;

        if (mIsPaginatedView) {
            //For Paginated Results

            if (getStartPageIndex() > 1) {
                //When not on first page, reset the 'page' setting value to 1
                //to refresh the content and show the first page
                ((HeadlinesFragment) getParentFragment()).resetStartPageIndex();
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
            if (getFirstVisibleItemPosition() != position) {
                //Updating the item position reference
                mVisibleItemViewPosition = position;

                Log.d(LOG_TAG + "_" + mNewsTopicId, "scrollToItemPosition: Updating to position " + mVisibleItemViewPosition);

                if (scrollImmediate) {
                    //Scrolling to the item position immediately
                    layoutManager.scrollToPositionWithOffset(mVisibleItemViewPosition, 0);
                } else {
                    //Scrolling to the item position naturally with smooth scroll

                    //Configuring the Linear scroll
                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                        /**
                         * When scrolling towards a child view, this method defines whether we should align the top
                         * or the bottom edge of the child with the parent RecyclerView.
                         *
                         * @return SNAP_TO_START to align the top of the child with the parent RecyclerView.
                         */
                        @Override
                        protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
                    //Setting the item position to scroll to
                    smoothScroller.setTargetPosition(mVisibleItemViewPosition);
                    //Initiating the smooth scroll
                    layoutManager.startSmoothScroll(smoothScroller);
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
    private void checkAndEnablePaginationPanel() {
        if (mIsPaginatedView) {
            //For Paginated Results
            if (mVisibleItemViewPosition >= mRecyclerAdapter.getItemCount() - VSCROLL_PAGINATION_TRIGGER_POS) {
                //Displaying the Pagination panel when the Bottom Y items are reached
                ((HeadlinesFragment) getParentFragment()).showPaginationPanel(true);
            } else {
                //Hiding the Pagination panel when away from the Bottom Y items
                ((HeadlinesFragment) getParentFragment()).showPaginationPanel(false);
            }
        } else {
            //Hiding the Pagination panel by default for Single Page Results
            ((HeadlinesFragment) getParentFragment()).showPaginationPanel(false);
        }
    }

    /**
     * Method that retrieves the item position of the first completely visible
     * or the partially visible item in the screen.
     *
     * @return is the Integer value of the first item position that is currently visible in the screen
     */
    private int getFirstVisibleItemPosition() {
        //Retrieving the Linear Layout Manager of the RecyclerView
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        //First, retrieving the top completely visible item position
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        //Checking the validity of the above position
        if (position > RecyclerView.NO_POSITION) {
            return position; //Returning the same if valid
        } else {
            //Else, returning the top partially visible item position
            return linearLayoutManager.findFirstVisibleItemPosition();
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
            Log.d(LOG_TAG + "_" + mNewsTopicId, "onCreateLoader: NewsTopicId " + mNewsTopicId);
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
    public void onLoadFinished(Loader<List<NewsArticleInfo>> loader, List<NewsArticleInfo> newsArticleInfos) {
        if (loader.getId() == mLoaderIds[0]) {
            if (newsArticleInfos != null && newsArticleInfos.size() > 0) {
                //Loading the data to the adapter when present
                mRecyclerAdapter.swapItemData(newsArticleInfos);
                //Updating the last page index value to the Fragment member
                mLastPageIndex = ((NewsArticlesLoader) loader).getLastPageIndex();
            } else {
                //When the data returned is Empty or NULL

                //Hiding the Progress Indicator on failure
                mSwipeContainer.setRefreshing(false);

                NewsArticlesLoader newsArticlesLoader = (NewsArticlesLoader) loader;

                if (!newsArticlesLoader.getNetworkConnectivityStatus()) {
                    //Reporting Network Failure when False
                    Log.d(LOG_TAG + "_" + mNewsTopicId, "onLoadFinished: Network Failure");
                } else {
                    //When there is NO network issue and the current page has no data to be shown
                    Log.d(LOG_TAG + "_" + mNewsTopicId, "onLoadFinished: NO DATA RETURNED");

                    if (mIsPaginatedView && getStartPageIndex() > 1) {
                        //When not on first page, reset the 'page' setting value to 1,
                        //to refresh the content and show the first page if possible
                        ((HeadlinesFragment) getParentFragment()).resetStartPageIndex();
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
    public void onLoaderReset(Loader<List<NewsArticleInfo>> loader) {
        //Creating an Empty List of NewsArticleInfo objects to clear the content in the Adapter
        ArrayList<NewsArticleInfo> newsArticleInfoList = new ArrayList<>();
        //Calling the Adapter's swap method to clear the data
        mRecyclerAdapter.swapItemData(newsArticleInfoList);
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
     * Method that returns the current 'page' (Page to Display) setting value
     * from the SharedPreferences
     *
     * @return Integer value of the current 'page' (Page to Display) setting
     */
    private int getStartPageIndex() {
        //Returning the current value of 'page' (Page to Display) setting
        return mPreferences.getInt(
                getString(R.string.pref_page_index_key),
                getResources().getInteger(R.integer.pref_page_index_default_value)
        );
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
        //Hiding the Progress Indicator after the data load completion
        mSwipeContainer.setRefreshing(false);

        //Scrolling over to first item after data load
        scrollToItemPosition(mVisibleItemViewPosition, false);
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
        openLink(newsArticleInfo.getWebUrl());
    }

    /**
     * Method that opens a webpage for the URL passed
     *
     * @param webUrl is the String containing the URL of the News Article to be launched
     */
    private void openLink(String webUrl) {
        //Parsing the URL
        Uri webPageUri = Uri.parse(webUrl);
        //Creating an ACTION_VIEW Intent with the URI
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPageUri);
        //Checking if there is an Activity that accepts the Intent
        if (webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Launching the corresponding Activity and passing it the Intent
            startActivity(webIntent);
        }
    }

    /**
     * Method invoked when "Read Later" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onMarkForRead(NewsArticleInfo newsArticleInfo) {

    }

    /**
     * Method invoked when "Favorite this" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onMarkAsFav(NewsArticleInfo newsArticleInfo) {

    }

    /**
     * Method invoked when "Open News Section" option is clicked from the Popup Menu
     *
     * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
     *                        in the Adapter
     */
    @Override
    public void onOpenNewsSectionRequest(NewsArticleInfo newsArticleInfo) {

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

            //Triggering a new data load only if any parameters have changed
            //(This also prevents duplicate triggers)
            checkAndReloadData();
        }
    }

    /**
     * Method that compares the URL previously used by an existing loader
     * with the URL generated using the current parameters to trigger a new data load only if necessary
     */
    private void checkAndReloadData() {
        if (getActivity() != null) {
            //When attached to an Activity
            Log.d(LOG_TAG + "_" + mNewsTopicId, "checkAndReloadData: Started");
            //Retrieving the current loader of the Fragment
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
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
     * Subclass of {@link BaseRecyclerViewScrollListener} that listens to the scroll event
     * received when the scroll reaches/leaves the last y items in the {@link RecyclerView}
     */
    private class RecyclerViewScrollListener extends BaseRecyclerViewScrollListener {

        /**
         * Constructor of {@link BaseRecyclerViewScrollListener}
         *
         * @param bottomYEndItemPosForTrigger is the Integer value of the trigger point
         *                                    for when the vertical scroll reaches/leaves
         *                                    the last y items in RecyclerView
         */
        public RecyclerViewScrollListener(int bottomYEndItemPosForTrigger) {
            super(bottomYEndItemPosForTrigger);
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
            ((HeadlinesFragment) getParentFragment()).showPaginationPanel(verticalScrollAmount > 0);
        }

    }

    /**
     * Custom {@link android.support.v7.widget.RecyclerView.ItemDecoration} class
     * for proper Card Item spacing and spacing with the Paginated Buttons if any
     */
    private class CardItemDecoration extends RecyclerView.ItemDecoration {
        //Stores the Dimension size for the Card Item spacing
        private int mCardSpacing;
        //Stores the Dimension size of the Paginated Buttons
        private int mPaginationButtonSize;

        /**
         * Constructor of {@link CardItemDecoration}
         * (Used for Paginated Results)
         *
         * @param cardSpacing          is the Integer value of the Dimension size for Card Item spacing
         * @param paginationButtonSize is the Integer value of the Dimension size of Paginated Buttons
         */
        public CardItemDecoration(int cardSpacing, int paginationButtonSize) {
            mCardSpacing = cardSpacing;
            mPaginationButtonSize = paginationButtonSize;
        }

        /**
         * Constructor of {@link CardItemDecoration}
         * (Used for Single Page Results)
         *
         * @param cardSpacing is the Integer value of the Dimension size for Card Item spacing
         */
        public CardItemDecoration(int cardSpacing) {
            //Propagating the call to main constructor with the Pagination Button size as 0
            this(cardSpacing, 0);
        }

        /**
         * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
         * the number of pixels that the item view should be inset by, similar to padding or margin.
         * The default implementation sets the bounds of outRect to 0 and returns.
         *
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            //Retrieving the total item count
            int totalItems = parent.getAdapter().getItemCount();
            if (parent.getChildAdapterPosition(view) == totalItems - 1
                    && mPaginationButtonSize > 0) {
                //Setting the Bottom offset height of the last Child view for Paginated Results
                //(This correction is for the Pagination Buttons shown at the Bottom)
                outRect.bottom = mCardSpacing + mPaginationButtonSize;
            } else {
                //Setting the Bottom offset height of the Child views (including the last one for
                //Single Page Results)
                outRect.bottom = mCardSpacing;
            }
        }

    }


}