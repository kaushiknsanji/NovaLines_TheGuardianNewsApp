package com.example.kaushiknsanji.novalines.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Utility class that provides convenience methods for use with {@link RecyclerView}
 *
 * @author Kaushik N Sanji
 */
public class RecyclerViewUtility {

    /**
     * Method that retrieves the item position of the first completely visible
     * or the partially visible item in the screen of the {@link RecyclerView}
     *
     * @param recyclerView is the instance of the {@link RecyclerView}
     * @return is the Integer value of the first item position that is currently visible in the screen
     */
    public static int getFirstVisibleItemPosition(RecyclerView recyclerView) {
        //Retrieving the Layout Manager of the RecyclerView
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //When the Layout Manager is an instance of LinearLayoutManager

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //Retrieving the top completely visible item position
            int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return linearLayoutManager.findFirstVisibleItemPosition();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //When the Layout Manager is an instance of StaggeredGridLayoutManager

            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            //Retrieving the top completely visible item position
            int position = gridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return gridLayoutManager.findFirstVisibleItemPositions(null)[0];
            }
        }

        //On all else, returning -1 (RecyclerView.NO_POSITION)
        return RecyclerView.NO_POSITION;
    }

    /**
     * Method that retrieves the item position of the last completely visible
     * or the partially visible item in the screen of the {@link RecyclerView}
     *
     * @param recyclerView is the instance of the {@link RecyclerView}
     * @return is the Integer value of the last item position that is currently visible in the screen
     */
    public static int getLastVisibleItemPosition(RecyclerView recyclerView) {
        //Retrieving the Layout Manager of the RecyclerView
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //When the Layout Manager is an instance of LinearLayoutManager

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //Retrieving the last completely visible item position
            int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the last partially visible item position
                return linearLayoutManager.findLastVisibleItemPosition();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //When the Layout Manager is an instance of StaggeredGridLayoutManager

            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            //Retrieving the number of Spans/Columns in the StaggeredGridLayout
            int noOfGridColumns = gridLayoutManager.getSpanCount();
            //Retrieving the last completely visible item position
            int position = gridLayoutManager.findLastCompletelyVisibleItemPositions(null)[noOfGridColumns - 1];
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the last partially visible item position
                return gridLayoutManager.findLastVisibleItemPositions(null)[noOfGridColumns - 1];
            }
        }

        //On all else, returning -1 (RecyclerView.NO_POSITION)
        return RecyclerView.NO_POSITION;
    }

    /**
     * Method that smoothly scrolls to the item position passed in a vertical {@link RecyclerView}
     * such that the item gets displayed at the top of the parent {@link RecyclerView}.
     *
     * @param recyclerView   is the instance of the {@link RecyclerView}
     * @param targetPosition Integer value representing the adapter position of
     *                       the item view in the {@link RecyclerView}
     *                       to which the screen needs to be scrolled to.
     */
    public static void smoothVScrollToPositionWithViewTop(RecyclerView recyclerView, int targetPosition) {
        //Configuring the LinearSmoothScroller
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
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
        linearSmoothScroller.setTargetPosition(targetPosition);
        //Initiating the smooth scroll with LinearSmoothScroller
        recyclerView.getLayoutManager().startSmoothScroll(linearSmoothScroller);
    }

}
