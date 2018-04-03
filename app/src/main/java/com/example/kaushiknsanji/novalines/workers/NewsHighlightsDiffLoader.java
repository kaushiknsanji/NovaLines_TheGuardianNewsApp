package com.example.kaushiknsanji.novalines.workers;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.util.DiffUtil;

import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;
import com.example.kaushiknsanji.novalines.utils.NewsSectionInfoDiffUtility;

import java.util.List;

/**
 * {@link AsyncTaskLoader} class that performs the difference computation
 * between two lists of {@link NewsSectionInfo} objects in a worker thread and
 * returns the result to the RecyclerView's Adapter to reload the data accordingly.
 *
 * @author Kaushik N Sanji
 */
public class NewsHighlightsDiffLoader extends AsyncTaskLoader<DiffUtil.DiffResult> {

    //Integer Constant of the Loader
    public static final int HIGHLIGHTS_DIFF_LOADER = 3;

    //Stores the Current list of NewsSectionInfo objects
    private List<NewsSectionInfo> mOldSectionInfoList;

    //Stores the New list of NewsSectionInfo objects
    private List<NewsSectionInfo> mNewSectionInfoList;

    //Stores the result of the difference computation between the
    //current and new List of NewsSectionInfo Objects
    private DiffUtil.DiffResult mDiffResult;

    /**
     * Constructor of the Loader {@link NewsHighlightsDiffLoader}
     *
     * @param context         is the reference to the Activity/Application Context
     * @param oldSectionInfos is the Current list of {@link NewsSectionInfo} objects to be compared
     * @param newSectionInfos is the New list of {@link NewsSectionInfo} objects to be compared
     */
    public NewsHighlightsDiffLoader(Context context, List<NewsSectionInfo> oldSectionInfos, List<NewsSectionInfo> newSectionInfos) {
        super(context);
        mOldSectionInfoList = oldSectionInfos;
        mNewSectionInfoList = newSectionInfos;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     * <p>
     * Method that performs the difference computation between
     * the current and the new List of {@link NewsSectionInfo} objects passed and returns its result
     *
     * @return The result of the load operation which is the {@link android.support.v7.util.DiffUtil.DiffResult}
     * obtained after the difference computation between two lists of {@link NewsSectionInfo} objects
     * @throws android.support.v4.os.OperationCanceledException if the load is canceled during execution.
     */
    @Override
    public DiffUtil.DiffResult loadInBackground() {
        //Instantiating the DiffUtil for difference computation
        NewsSectionInfoDiffUtility diffUtility = new NewsSectionInfoDiffUtility(mOldSectionInfoList, mNewSectionInfoList);
        //Computing the Difference and returning the result
        return DiffUtil.calculateDiff(diffUtility, false); //False, as RecyclerView items are stationary
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     *
     * @param diffResult the result of the load
     */
    @Override
    public void deliverResult(DiffUtil.DiffResult diffResult) {
        if (isReset()) {
            //Ignoring the result as the loader is already reset
            diffResult = null;
            //Returning when the loader is already reset
            return;
        }

        //Saving a reference to the old data as we are about to deliver the result
        DiffUtil.DiffResult oldDiffResult = mDiffResult;
        mDiffResult = diffResult;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(mDiffResult);
        }

        //Invalidating the old result as it is not required anymore
        if (oldDiffResult != null && oldDiffResult != mDiffResult) {
            oldDiffResult = null;
        }
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (mDiffResult != null) {
            //Deliver the result immediately if already present
            deliverResult(mDiffResult);
        }

        if (takeContentChanged() || mDiffResult == null) {
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
    public void onCanceled(DiffUtil.DiffResult data) {
        //Canceling any asynchronous load
        super.onCanceled(data);

        //Releasing the resources associated with the loader, as the loader is canceled
        releaseResources();
    }

    /**
     * Method to release the resources associated with the loader
     */
    private void releaseResources() {
        //Invalidating the Loader data
        if (mOldSectionInfoList != null) {
            mOldSectionInfoList = null;
        }

        if (mNewSectionInfoList != null) {
            mNewSectionInfoList = null;
        }

        if (mDiffResult != null) {
            mDiffResult = null;
        }
    }

    /**
     * Method that returns the new List of {@link NewsSectionInfo} objects passed to the Loader
     *
     * @return The new List of {@link NewsSectionInfo} objects passed to the Loader
     */
    public List<NewsSectionInfo> getNewSectionInfoList() {
        return mNewSectionInfoList;
    }

}