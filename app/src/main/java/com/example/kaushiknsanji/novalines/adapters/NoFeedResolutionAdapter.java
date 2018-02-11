package com.example.kaushiknsanji.novalines.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NoFeedInfo;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

import java.util.List;

/**
 * Created by Kaushik N Sanji on 08-Feb-18.
 */
public class NoFeedResolutionAdapter extends RecyclerView.Adapter<NoFeedResolutionAdapter.ViewHolder> {

    //Constant used for logs
    private static final String LOG_TAG = NoFeedResolutionAdapter.class.getSimpleName();

    //Stores the layout resource of the list item that needs to be inflated manually
    private int mLayoutRes;

    //Stores the reference to the Context
    private Context mContext;

    //Stores a list of NoFeedInfo objects which is the Dataset of the Adapter
    private List<NoFeedInfo> mNoFeedInfoList;

    //Stores the reference to the Listener OnAdapterItemResolutionButtonClickListener
    private OnAdapterItemResolutionButtonClickListener mResolutionButtonClickListener;

    //Stores the Typeface for the Resolution Number Text
    private Typeface mResolutionNumberTextTypeface;
    //Stores the Typeface for the Resolution Text
    private Typeface mResolutionTextTypeface;

    /**
     * Constructor of the Adapter {@link NoFeedResolutionAdapter}
     *
     * @param context     is the Context of the Fragment
     * @param resource    is the layout resource ID of the item view ('R.layout.no_feed_help_item')
     * @param noFeedInfos is the list of {@link NoFeedInfo} objects which is the Dataset of the Adapter
     */
    public NoFeedResolutionAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NoFeedInfo> noFeedInfos) {
        mContext = context;
        mLayoutRes = resource;
        mNoFeedInfoList = noFeedInfos;

        //Loading the Fonts to be used
        mResolutionNumberTextTypeface = ResourcesCompat.getFont(mContext, R.font.croissant_one);
        mResolutionTextTypeface = ResourcesCompat.getFont(mContext, R.font.lobster_two_regular);
    }

    /**
     * Method that registers the {@link OnAdapterItemResolutionButtonClickListener}
     * for the {@link com.example.kaushiknsanji.novalines.errorviews.NoFeedResolutionFragment}
     * to receive event callbacks
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemResolutionButtonClickListener}
     */
    public void setOnAdapterItemResolutionButtonClickListener(OnAdapterItemResolutionButtonClickListener listener) {
        mResolutionButtonClickListener = listener;
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
        //Retrieving the NoFeedInfo object at the current item position
        NoFeedInfo noFeedInfo = mNoFeedInfoList.get(position);

        //Populating the data onto the Template View using the NoFeedInfo object: START

        //Updating the Resolution Item Number
        holder.resolutionNumberTextView.setText(String.valueOf(noFeedInfo.getResolutionItemNumber()));
        //Setting the Font
        holder.resolutionNumberTextView.setTypeface(mResolutionNumberTextTypeface);

        //Updating the Resolution Text
        holder.resolutionTextView.setText(noFeedInfo.getResolutionText());
        //Replacing the placeholder for drawables in Text with their corresponding image
        TextAppearanceUtility.replaceTextWithImage(mContext, holder.resolutionTextView);
        //Setting the Font
        holder.resolutionTextView.setTypeface(mResolutionTextTypeface);

        //Updating the Resolution Button Text
        holder.resolutionButton.setText(noFeedInfo.getResolutionButtonText());

        //Checking if any Compound Drawable is set, that needs to be displayed on the Resolution Button
        int resolutionBtnCompoundDrawableRes = noFeedInfo.getResolutionButtonCompoundDrawableRes();
        if (resolutionBtnCompoundDrawableRes > 0) {
            //When the Drawable is present

            //Setting the Left Compound Drawable
            holder.resolutionButton.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, resolutionBtnCompoundDrawableRes),
                    null,
                    null,
                    null
            );
            //Setting the Compound Drawable Padding
            holder.resolutionButton.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.nfhi_resolution_btn_drawable_padding));
        } else {
            //When the Drawable is absent

            //Resetting the Left Compound Drawable
            holder.resolutionButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
            );

            //Resetting the Compound Drawable Padding
            holder.resolutionButton.setCompoundDrawablePadding(0);
        }

        //Populating the data onto the Template View using the NoFeedInfo object: END
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter which is the
     * total number of {@link NoFeedInfo} objects in the adapter
     */
    @Override
    public int getItemCount() {
        return mNoFeedInfoList.size();
    }

    /**
     * Interface that declares methods to be implemented by
     * the Fragment {@link com.example.kaushiknsanji.novalines.errorviews.NoFeedResolutionFragment}
     * to receive event callbacks related to the click action
     * on the Resolution button of the item views displayed by the RecyclerView's Adapter
     */
    public interface OnAdapterItemResolutionButtonClickListener {
        /**
         * Method invoked when the Resolution button of the Adapter's Item
         * is clicked.
         *
         * @param noFeedInfo is the corresponding {@link NoFeedInfo} object of the item view
         *                   clicked in the Adapter.
         */
        void onResolutionButtonClick(NoFeedInfo noFeedInfo);
    }

    /**
     * ViewHolder class for caching View components of the template item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //Declaring the View components of the template item view
        private TextView resolutionNumberTextView;
        private TextView resolutionTextView;
        private Button resolutionButton;

        /**
         * Constructor of the ViewHolder
         *
         * @param itemView is the inflated item layout View passed
         *                 for caching its View components
         */
        public ViewHolder(View itemView) {
            super(itemView);

            //Doing the view lookup for each of the item layout view's components
            resolutionNumberTextView = itemView.findViewById(R.id.nfhi_resolution_number_text_id);
            resolutionTextView = itemView.findViewById(R.id.nfhi_resolution_text_id);
            resolutionButton = itemView.findViewById(R.id.nfhi_resolution_btn_id);

            //Setting the Click Listener on the item Button
            resolutionButton.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            //Retrieving the position of the item view clicked
            int adapterPosition = getAdapterPosition();
            if (adapterPosition > RecyclerView.NO_POSITION) {
                //Verifying the validity of the position before proceeding

                //Retrieving the data at the position
                NoFeedInfo noFeedInfo = mNoFeedInfoList.get(adapterPosition);

                //Executing action based on the view being clicked
                switch (view.getId()) {
                    case R.id.nfhi_resolution_btn_id:
                        //For the Resolution Button

                        //Propagating the call to the listener with the selected item's data
                        if (mResolutionButtonClickListener != null) {
                            mResolutionButtonClickListener.onResolutionButtonClick(noFeedInfo);
                        }
                        break;
                }

            }
        }

    }

}