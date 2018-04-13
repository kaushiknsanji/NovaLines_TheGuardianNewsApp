package com.example.kaushiknsanji.novalines.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NavDrawerItem;

import java.util.List;

/**
 * Adapter Class of the Heterogeneous RecyclerView used as a Navigation Drawer
 * for the entire app by the Activity {@link com.example.kaushiknsanji.novalines.NewsActivity}
 * to bind and display the Navigation Drawer Items.
 *
 * @author Kaushik N Sanji
 */
public class NavRecyclerAdapter extends RecyclerView.Adapter<NavRecyclerAdapter.ViewHolder> {

    //Constant used for logs
    private static final String LOG_TAG = NavRecyclerAdapter.class.getSimpleName();

    //Stores the reference to the Context
    private Context mContext;

    //Stores the List of Drawer Items which is the Dataset of the Adapter
    private List<NavDrawerItem> mNavDrawerItemsList;

    //Keeps track of the Item View selected. Defaulted to Item View of Position - 1
    private int selectedPosition = 1;

    //Stores reference to the Listener OnNavAdapterItemClickListener
    private OnNavAdapterItemClickListener mAdapterItemClickListener;

    /**
     * Constructor of the Adapter {@link NavRecyclerAdapter}
     *
     * @param context        is the context of the Activity
     * @param navDrawerItems is the List of {@link NavDrawerItem} objects which is the Dataset
     *                       of the Adapter
     */
    public NavRecyclerAdapter(@NonNull Context context, @NonNull List<NavDrawerItem> navDrawerItems) {
        mContext = context;
        mNavDrawerItemsList = navDrawerItems;
    }

    /**
     * Method that returns the reference to the Context used
     *
     * @return the reference to the Context used
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Method that registers the {@link OnNavAdapterItemClickListener} for the Activity
     * to receive item click events
     *
     * @param listener is the instance of the Activity implementing {@link OnNavAdapterItemClickListener}
     */
    public void setOnNavAdapterItemClickListener(OnNavAdapterItemClickListener listener) {
        mAdapterItemClickListener = listener;
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>.
     */
    @Override
    public int getItemViewType(int position) {
        //Returning the Resource Id of the Layout to be used for the Item View
        NavDrawerItem navDrawerItem = mNavDrawerItemsList.get(position);
        return navDrawerItem.getDrawerLayout();
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
    @NonNull
    @Override
    public NavRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflating the item Layout view
        //Passing False as we are attaching the View ourselves
        View itemView = LayoutInflater.from(getContext()).inflate(viewType, parent, false);

        //Instantiating the ViewHolder to initialize the reference to the view components in the item layout
        //and returning the same
        return new ViewHolder(itemView, viewType);
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
    public void onBindViewHolder(@NonNull NavRecyclerAdapter.ViewHolder holder, int position) {
        if (holder.getItemViewType() == R.layout.nav_item) {
            //Binding of data is required only for the Navigation Drawer Item

            //Marking the Item View as selected when the current position is the selected one
            holder.itemView.setSelected(selectedPosition == position);

            //Retrieving the NavDrawerItem object at the current item position
            NavDrawerItem navDrawerItem = mNavDrawerItemsList.get(position);

            //Setting the Drawer Item Image if present: START
            if (navDrawerItem.getDrawerIcon() <= 0) {
                //When there is no Drawer Item Icon

                //Hiding the Drawer Item Icon
                holder.iconImageView.setVisibility(View.GONE);

            } else {
                //When there is a Drawer Item Icon

                //Setting the Image for the Drawer Item
                holder.iconImageView.setImageResource(navDrawerItem.getDrawerIcon());
                holder.iconImageView.setVisibility(View.VISIBLE);
            }
            //Setting the Drawer Item Image if present: END

            //Setting the Text of the Drawer Item
            holder.menuItemTextView.setText(navDrawerItem.getItemTitle());
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter,
     * which is the total number of {@link NavDrawerItem} objects in the adapter
     */
    @Override
    public int getItemCount() {
        return mNavDrawerItemsList.size();
    }

    /**
     * Method that selects and highlights the {@link NavDrawerItem}
     * based on the position of the item.
     *
     * @param itemPosition Integer value of the adapter position which needs
     *                     to be shown as selected.
     */
    private void setSelection(int itemPosition) {
        //Redraw the old selection and the current selection
        notifyItemChanged(selectedPosition); //Rebinding the old selection
        selectedPosition = itemPosition; //Updating the selection
        notifyItemChanged(selectedPosition); //Rebinding the new selection
    }

    /**
     * Method that selects and highlights the {@link NavDrawerItem}
     * based on the Title String value of the Item.
     *
     * @param itemTitle String value of the @link NavDrawerItem}'s Title
     *                  which determines the item to be shown as selected.
     */
    public void setSelectedItemByTitle(String itemTitle) {
        //Defaulting the item position to invalid value
        int itemPosition = RecyclerView.NO_POSITION;

        //Retrieving the total item count
        int totalItemCount = getItemCount();

        //Iterating over the items to check for the item with the given Title passed
        for (int index = 0; index < totalItemCount; index++) {
            NavDrawerItem navDrawerItem = mNavDrawerItemsList.get(index);
            if (navDrawerItem.getItemTitle().equals(itemTitle)) {
                //Saving the position of the item when found
                itemPosition = index;
                break; //exit loop
            }
        }

        if (itemPosition > RecyclerView.NO_POSITION) {
            //Set the item as selected when a valid position is determined
            setSelection(itemPosition);
        }

    }

    /**
     * Interface that declares methods to be implemented by the {@link com.example.kaushiknsanji.novalines.NewsActivity}
     * to receive event callbacks related to the click action on the item views displayed
     * by the RecyclerView's Adapter
     */
    public interface OnNavAdapterItemClickListener {

        /**
         * Method invoked when an Item on the Adapter is clicked
         *
         * @param navDrawerItem is the corresponding {@link NavDrawerItem} object of the item view
         *                      clicked in the Adapter
         */
        void onNavItemClick(NavDrawerItem navDrawerItem);
    }

    /**
     * ViewHolder class for caching View components of the template item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //Declaring the View components of the template item view
        private ImageView iconImageView;
        private TextView menuItemTextView;

        /**
         * Constructor of the ViewHolder
         *
         * @param itemView is the inflated item layout View passed
         *                 for caching its View components
         * @param viewType is the integer value of the layout resource id
         *                 that identifies the type of the view
         */
        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == R.layout.nav_item) {
                //Finding the view components for the Drawer item layout
                iconImageView = itemView.findViewById(R.id.nav_item_icon_id);
                menuItemTextView = itemView.findViewById(R.id.nav_item_text_id);

                //Registering the click listener on this Item View
                itemView.setOnClickListener(this);
            }

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

                //Redraw the old selection and the current selection
                setSelection(adapterPosition);

                //Propagating the call to the listener with the selected item's data
                mAdapterItemClickListener.onNavItemClick(mNavDrawerItemsList.get(adapterPosition));
            }
        }
    }

}
