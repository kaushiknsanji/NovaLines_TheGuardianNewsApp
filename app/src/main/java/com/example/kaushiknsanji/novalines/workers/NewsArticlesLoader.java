package com.example.kaushiknsanji.novalines.workers;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.utils.NetworkUtility;
import com.example.kaushiknsanji.novalines.utils.NewsArticleInfoParserUtility;

import java.net.URL;
import java.util.List;

/**
 * {@link AsyncTaskLoader} Class for extracting a list of News Articles information
 * for the News Query, in a worker thread.
 *
 * @author Kaushik N Sanji
 */
public class NewsArticlesLoader extends AsyncTaskLoader<List<NewsArticleInfo>> {

    //Saves the query result which is a List of NewsArticleInfo objects
    private List<NewsArticleInfo> mNewsArticleInfoList;

    //Saves the URL to which the request is to be made to get the News Articles
    private URL mRequestURLObject;

    //Boolean that stores the Network Connectivity state
    private boolean mIsNetworkConnected = false;

    //Saves the last page index of the News Query result
    private int mLastPageIndex = 1; //Defaulted to 1

    /**
     * Constructor of the Loader {@link NewsArticlesLoader}
     *
     * @param context          is the reference to Activity Context
     * @param requestURLObject is the URL to which the request is to be made to get the News Articles
     */
    public NewsArticlesLoader(Context context, URL requestURLObject) {
        super(context);
        mRequestURLObject = requestURLObject;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     *
     * @return The result of the load operation which is a List of {@link NewsArticleInfo} objects
     * retrieved for the News Query request.
     * @throws android.support.v4.os.OperationCanceledException if the load is canceled during execution.
     */
    @Override
    public List<NewsArticleInfo> loadInBackground() {
        //Retrieving the reference to Context
        Context context = getContext();

        //Proceeding to extract data when the Internet Connectivity is established
        if (NetworkUtility.isNetworkConnected(context)) {
            //Updating the Connectivity status to True
            mIsNetworkConnected = true;

            //Initializing the Parser for News Articles
            NewsArticleInfoParserUtility articleInfoParserUtility = new NewsArticleInfoParserUtility(context);
            //Firing the request to the URL to retrieve a list of NewsArticleInfo Objects
            List<NewsArticleInfo> newsArticleInfoList = articleInfoParserUtility.getNewsArticleFeed(mRequestURLObject);

            if (newsArticleInfoList != null && newsArticleInfoList.size() > 0) {
                //Retrieving the Number of Pages of available data when there is News feed
                mLastPageIndex = articleInfoParserUtility.getPagesCount();
            }

            //Returning the extracted list of NewsArticleInfo Objects
            return newsArticleInfoList;
        }

        //Updating the Connectivity status to False as it is not active
        mIsNetworkConnected = false;

        //For all else, returning null
        return null;
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     *
     * @param newData the result of the load
     */
    @Override
    public void deliverResult(List<NewsArticleInfo> newData) {
        if (isReset()) {
            //Ignoring the result if the loader is already reset
            newData = null;
            //Returning when the loader is already reset
            return;
        }

        //Storing reference to the old list of NewsArticleInfo objects as we are about to deliver the result
        List<NewsArticleInfo> oldNewsArticleInfoList = mNewsArticleInfoList;
        mNewsArticleInfoList = newData;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(mNewsArticleInfoList);
        }

        //Invalidating the old list of NewsArticleInfo objects as it is not required anymore
        if (oldNewsArticleInfoList != null && oldNewsArticleInfoList != mNewsArticleInfoList) {
            oldNewsArticleInfoList = null;
        }
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (mNewsArticleInfoList != null) {
            //Deliver the result immediately if already retrieved
            deliverResult(mNewsArticleInfoList);
        }

        if (mNewsArticleInfoList == null || takeContentChanged()) {
            //Force a new load when the data is not yet retrieved
            //or the content has changed
            forceLoad();
        }
    }

    /**
     * Subclasses must implement this to take care of stopping their loader,
     * as per {@link #stopLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #stopLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStopLoading() {
        //Canceling the load if any as the loader has entered Stopped state
        cancelLoad();
    }

    /**
     * Subclasses must implement this to take care of resetting their loader,
     * as per {@link #reset()}.  This is not called by clients directly,
     * but as a result of a call to {@link #reset()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onReset() {
        //Ensuring the loader has stopped
        onStopLoading();

        //Releasing the resources associated with the loader
        releaseResources();
    }

    /**
     * Called if the task was canceled before it was completed.  Gives the class a chance
     * to clean up post-cancellation and to properly dispose of the result.
     *
     * @param data The value that was returned by {@link #loadInBackground}, or null
     *             if the task threw {@link android.support.v4.os.OperationCanceledException}.
     */
    @Override
    public void onCanceled(List<NewsArticleInfo> data) {
        //Canceling any asynchronous load
        super.onCanceled(data);

        //Releasing the resources associated with the loader, as the loader is canceled
        releaseResources();
    }

    /**
     * Method to release the resources associated with the loader
     */
    private void releaseResources() {
        //Invalidating the loader data
        if (mNewsArticleInfoList != null) {
            mNewsArticleInfoList = null;
        }
    }

    /**
     * Method that returns the evaluated Network Connectivity status
     *
     * @return a Boolean representing the state of Internet Connectivity
     * <br/><b>TRUE</b> if the Internet Connectivity is established
     * <br/><b>FALSE</b> otherwise
     */
    public boolean getNetworkConnectivityStatus() {
        return mIsNetworkConnected;
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
     * Method that returns the URL used by the loader to download the feed
     *
     * @return String representation of the URL used by the loader
     */
    public String getRequestURLStr() {
        return mRequestURLObject.toExternalForm();
    }
}
