package com.example.kaushiknsanji.novalines.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;
import com.example.kaushiknsanji.novalines.utils.NetworkUtility;
import com.example.kaushiknsanji.novalines.utils.NewsSectionInfoParserUtility;
import com.example.kaushiknsanji.novalines.utils.NewsURLGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * {@link AsyncTaskLoader} Class for extracting the News Section metadata information
 * for the Subscribed News Sections, in a worker thread.
 *
 * @author Kaushik N Sanji
 */
public class NewsHighlightsLoader extends AsyncTaskLoader<List<NewsSectionInfo>> {

    //Integer Constant of the Loader
    public final static int HIGHLIGHTS_LOADER = 1;

    //Constant used for Logs
    private static final String LOG_TAG = NewsHighlightsLoader.class.getSimpleName();

    //Saves the query result which is a List of NewsSectionInfo objects
    private List<NewsSectionInfo> mNewsSectionInfoList;

    //Saves the list of Ids of the Subscribed News Categories
    private List<String> mSubscribedNewsSectionIdsList;

    //Boolean that stores the Network Connectivity state
    private boolean mIsNetworkConnected = false;

    //Saves the value of the 'from-date' Preference setting used by the loader
    private long mFromDateInMillis;

    /**
     * Constructor of the Loader {@link NewsHighlightsLoader}
     *
     * @param context is the reference to Activity Context
     * @param subscribedNewsSectionIdsList is a List of Ids of the Subscribed News Categories
     */
    public NewsHighlightsLoader(Context context, ArrayList<String> subscribedNewsSectionIdsList) {
        super(context);
        mSubscribedNewsSectionIdsList = subscribedNewsSectionIdsList;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     *
     * @return The result of the load operation which is a List of {@link NewsSectionInfo} objects
     * retrieved for the Subscribed News Sections.
     * @throws android.support.v4.os.OperationCanceledException if the load is canceled during execution.
     */
    @Override
    public List<NewsSectionInfo> loadInBackground() {
        //Retrieving the reference to Context
        Context context = getContext();

        //Proceeding to extract data when the Internet Connectivity is established
        if (NetworkUtility.isNetworkConnected(context)) {
            //Updating the Connectivity status to True
            mIsNetworkConnected = true;

            //Initializing the List of NewsSectionInfo objects
            ArrayList<NewsSectionInfo> newsSectionInfoList = new ArrayList<>();

            //Initializing the NewsURLGenerator
            NewsURLGenerator urlGenerator = new NewsURLGenerator(context, true);

            //Iterating over the Subscribed News Sections to retrieve their data: START
            for (String sectionIdStr : mSubscribedNewsSectionIdsList) {
                //Firing the request and extracting the News Section Info
                NewsSectionInfo newsSectionInfo = NewsSectionInfoParserUtility
                        .getNewsSectionInfo(
                                sectionIdStr,
                                context.getApplicationContext(),
                                //Creating the URL inline, for the Section passed
                                urlGenerator.createSectionURL(sectionIdStr)
                        );

                //Appending the NewsSectionInfo Object to the list
                newsSectionInfoList.add(newsSectionInfo);
            }
            //Iterating over the Subscribed News Sections to retrieve their data: END

            //Retrieving the start date value of the News from the preference
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            mFromDateInMillis = preferences.getLong(context.getString(R.string.pref_start_period_manual_key),
                    Calendar.getInstance().getTimeInMillis());

            //Returning the result prepared
            return newsSectionInfoList;
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
    public void deliverResult(List<NewsSectionInfo> newData) {
        if (isReset()) {
            //Ignoring the result if the loader is already reset
            newData = null;
            //Returning when the loader is already reset
            return;
        }

        //Storing reference to the old list of NewsSectionInfo objects as we are about to deliver the result
        List<NewsSectionInfo> oldNewsSectionInfoList = mNewsSectionInfoList;
        mNewsSectionInfoList = newData;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(mNewsSectionInfoList);
        }

        //Invalidating the old list of NewsSectionInfo objects as it is not required anymore
        if (oldNewsSectionInfoList != null && oldNewsSectionInfoList != mNewsSectionInfoList) {
            oldNewsSectionInfoList = null;
        }

    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (mNewsSectionInfoList != null) {
            //Deliver the result immediately if already retrieved
            deliverResult(mNewsSectionInfoList);
        }

        if (mNewsSectionInfoList == null || takeContentChanged()) {
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
        //Canceling a load if any as the loader has entered Stopped state
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
    public void onCanceled(List<NewsSectionInfo> data) {
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
        if (mNewsSectionInfoList != null) {
            mNewsSectionInfoList = null;
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
     * Method that returns the value of the Start date used by the loader
     *
     * @return Long value containing the Start date of the News used by the loader in Millis
     */
    public long getFromDateInMillis() {
        return mFromDateInMillis;
    }

    /**
     * Method that returns a list of {@link NewsSectionInfo} objects parsed
     * from the response
     *
     * @return List of {@link NewsSectionInfo} objects parsed.
     */
    public List<NewsSectionInfo> getNewsSectionInfoList() {
        return mNewsSectionInfoList;
    }

}