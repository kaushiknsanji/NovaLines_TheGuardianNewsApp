package com.example.kaushiknsanji.novalines.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;
import com.example.kaushiknsanji.novalines.workers.NewsHighlightsDiffLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushik N Sanji on 05-Jan-18.
 */
public class HighlightsAdapter extends RecyclerView.Adapter<HighlightsAdapter.ViewHolder>
        implements LoaderManager.LoaderCallbacks<DiffUtil.DiffResult> {

    //Constant used for Logs
    private static final String LOG_TAG = HighlightsAdapter.class.getSimpleName();

    //Constants used for the Diff Loader's Bundle arguments
    private static final String OLD_LIST_STR_KEY = "NewsSectionInfo.Old";
    private static final String NEW_LIST_STR_KEY = "NewsSectionInfo.New";

    //Stores the layout resource of the list item that needs to be inflated manually
    private int mLayoutRes;

    //Stores the reference to the Context
    private Context mContext;

    //Stores a list of NewsSectionInfo objects which is the Dataset of the Adapter
    private List<NewsSectionInfo> mNewsSectionInfoList;

    //Stores the reference to the Listener OnAdapterItemClickListener
    private OnAdapterItemClickListener mItemClickListener;

    //Stores the reference to the Listener OnAdapterItemDataSwapListener
    private OnAdapterItemDataSwapListener mItemDataSwapListener;

    /**
     * Constructor of the Adapter {@link HighlightsAdapter}
     *
     * @param context          is the Context of the Fragment {@link com.example.kaushiknsanji.novalines.adapterviews.HighlightsFragment}
     * @param resource         is the layout resource ID of the item view ('R.layout.highlights_item')
     * @param newsSectionInfos is the list of {@link NewsSectionInfo} objects which is the Dataset of the Adapter
     */
    public HighlightsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NewsSectionInfo> newsSectionInfos) {
        mContext = context;
        mLayoutRes = resource;
        mNewsSectionInfoList = newsSectionInfos;
    }

    /**
     * Method that registers the {@link OnAdapterItemClickListener}
     * for the Activity/Fragment
     * to receive item click events
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemClickListener}
     */
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Method that registers the {@link OnAdapterItemDataSwapListener}
     * for the {@link com.example.kaushiknsanji.novalines.adapterviews.HighlightsFragment}
     * to receive event callbacks
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemDataSwapListener}
     */
    public void setOnAdapterItemDataSwapListener(OnAdapterItemDataSwapListener listener) {
        mItemDataSwapListener = listener;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating the item Layout view
        //Passing False as we are attaching the View ourselves
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutRes, parent, false);

        //Instantiating the ViewHolder to initialize the reference to the view components in the item layout
        //and returning the same
        return new ViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Retrieving the NewsSectionInfo Object at the current item position
        NewsSectionInfo newsSectionInfo = mNewsSectionInfoList.get(position);

        //Populating the data onto the Template View using the NewsSectionInfo object: START

        //Updating the total article count under the News Section
        int newsArticleCount = newsSectionInfo.getNewsArticleCount();
        if (newsArticleCount <= 1000) {
            //Displaying the count AS-IS when it less than or equal to 1000
            holder.newsArticleCountTextView.setText(String.valueOf(newsArticleCount));
        } else {
            //Displaying the count as "1000+" when the count is more than 1000
            holder.newsArticleCountTextView.setText(TextUtils.concat(String.valueOf(newsArticleCount), "+"));
        }

        //Updating the News Section Name
        holder.newsSectionTextView.setText(newsSectionInfo.getSectionName());

        //Populating the data onto the Template View using the NewsSectionInfo object: END
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter which is the
     * total number of {@link NewsSectionInfo} objects in the adapter
     */
    @Override
    public int getItemCount() {
        return mNewsSectionInfoList.size();
    }

    /**
     * Method that computes the difference between the current and the new list of
     * {@link NewsSectionInfo} objects and sends the result to the adapter to notify the changes
     * on the item data and reload accordingly
     *
     * @param newSectionInfos is the new list of {@link NewsSectionInfo} objects which is the Dataset of the Adapter
     */
    public void swapItemData(@NonNull List<NewsSectionInfo> newSectionInfos) {
        //Loading the List of NewsSectionInfo objects as Bundle arguments to be passed to a Loader
        final Bundle args = new Bundle(2);
        args.putParcelableArrayList(OLD_LIST_STR_KEY, (ArrayList<? extends Parcelable>) mNewsSectionInfoList);
        args.putParcelableArrayList(NEW_LIST_STR_KEY, (ArrayList<? extends Parcelable>) newSectionInfos);
        //Initiating a loader to execute the difference computation in a background thread
        ((FragmentActivity) mContext).getSupportLoaderManager().restartLoader(NewsHighlightsDiffLoader.HIGHLIGHTS_DIFF_LOADER, args, this);
    }

    /**
     * Internal Method called by the Loader {@link NewsHighlightsDiffLoader}
     * after the difference computation between the current and the new list of
     * {@link NewsSectionInfo} objects to notify the adapter of the changes required
     * with respect to the data
     *
     * @param diffResult         the result obtained after the difference computation between
     *                           two lists of {@link NewsSectionInfo} objects
     * @param newSectionInfoList is the new list of {@link NewsSectionInfo} objects which is the Dataset of the Adapter
     */
    private void doSwapItemData(DiffUtil.DiffResult diffResult, @NonNull List<NewsSectionInfo> newSectionInfoList) {
        Log.d(LOG_TAG, "doSwapItemData: Started");
        //Clearing the Adapter's data to load the new list of NewsSectionInfo objects
        mNewsSectionInfoList.clear();
        mNewsSectionInfoList.addAll(newSectionInfoList);

        //Informing the adapter about the changes required, so that it triggers the notify accordingly
        diffResult.dispatchUpdatesTo(this);

        Log.d(LOG_TAG, "doSwapItemData: mItemDataSwapListener " + mItemDataSwapListener);
        //Dispatching the Item Data Swap event to the listener
        if (mItemDataSwapListener != null) {
            mItemDataSwapListener.onItemDataSwapped();
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
    public Loader<DiffUtil.DiffResult> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NewsHighlightsDiffLoader.HIGHLIGHTS_DIFF_LOADER:
                //Preparing the Diff Loader and returning the instance
                List<NewsSectionInfo> oldSectionInfoList = args.getParcelableArrayList(OLD_LIST_STR_KEY);
                List<NewsSectionInfo> newSectionInfoList = args.getParcelableArrayList(NEW_LIST_STR_KEY);
                return new NewsHighlightsDiffLoader(mContext, oldSectionInfoList, newSectionInfoList);
            default:
                return null;
        }
    }

    /**
     * Called when a previously created loader has finished its load.
     * This is where we notify the Adapter with the result of the difference computation.
     *
     * @param loader     The Loader that has finished.
     * @param diffResult The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<DiffUtil.DiffResult> loader, DiffUtil.DiffResult diffResult) {
        if (diffResult != null) {
            //When there is a result of the difference computation
            switch (loader.getId()) {
                case NewsHighlightsDiffLoader.HIGHLIGHTS_DIFF_LOADER:
                    //Update the New Data to the Adapter and notify the changes in the data
                    doSwapItemData(diffResult, ((NewsHighlightsDiffLoader) loader).getNewSectionInfoList());
                    break;
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
    public void onLoaderReset(Loader<DiffUtil.DiffResult> loader) {
        //No-op, just invalidating the loader
        loader = null;
    }

    /**
     * Interface that declares methods to be implemented by
     * the Fragment {@link com.example.kaushiknsanji.novalines.adapterviews.HighlightsFragment}
     * to receive event callbacks related to RecyclerView's Adapter data change
     */
    public interface OnAdapterItemDataSwapListener {
        /**
         * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
         */
        void onItemDataSwapped();
    }

    /**
     * Interface that declares methods to be implemented by
     * the Activity/Fragment
     * to receive event callbacks related to the click action
     * on the item views displayed by the RecyclerView's Adapter
     */
    public interface OnAdapterItemClickListener {
        /**
         * Method invoked when an Item in the Adapter is clicked
         *
         * @param newsSectionInfo is the corresponding {@link NewsSectionInfo} object of the item view
         *                        clicked in the Adapter
         */
        void onItemClick(NewsSectionInfo newsSectionInfo);
    }

    /**
     * ViewHolder class for caching View components of the template item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //Declaring the View components of the template item view
        private TextView newsArticleCountTextView;
        private TextView newsSectionTextView;

        /**
         * Constructor of the ViewHolder
         *
         * @param itemView is the inflated item layout View passed
         *                 for caching its View components
         */
        public ViewHolder(View itemView) {
            super(itemView);

            //Doing the view lookup for each of the item layout view's components
            newsArticleCountTextView = itemView.findViewById(R.id.news_count_text_id);
            newsSectionTextView = itemView.findViewById(R.id.hl_article_section_text_id);

            //Setting the Click Listener on the Item View
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            //Retrieving the position of the item view clicked
            int adapterPosition = getAdapterPosition();
            if (adapterPosition > RecyclerView.NO_POSITION) {
                //Verifying the validity of the position before proceeding

                Log.d(LOG_TAG, "onClick: mItemClickListener " + mItemClickListener);
                //Propagating the call to the listener with the selected item's data
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mNewsSectionInfoList.get(adapterPosition));
                }
            }
        }
    }
}