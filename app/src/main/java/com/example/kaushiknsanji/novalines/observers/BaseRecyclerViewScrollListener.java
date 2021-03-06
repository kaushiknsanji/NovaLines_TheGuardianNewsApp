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

package com.example.kaushiknsanji.novalines.observers;

import android.support.v7.widget.RecyclerView;

import com.example.kaushiknsanji.novalines.utils.RecyclerViewUtility;

/**
 * Abstract Class that extends the RecyclerView.OnScrollListener to inform
 * the subclass when the scroll has reached the last item view or scrolled away
 * from the last item view
 *
 * @author Kaushik N Sanji
 */
public abstract class BaseRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //Flag to keep a tab on the scroll, whether it has reached the bottom or not
    //to avoid invoking callbacks multiple times
    private boolean mIsScrolledToBottomEnd = false;

    //Stores the trigger point for when the vertical scroll
    // reaches/leaves the last y items in RecyclerView
    private int mBottomYEndItemPosForTrigger;

    /**
     * Constructor of {@link BaseRecyclerViewScrollListener}
     *
     * @param bottomYEndItemPosForTrigger is the Integer value of the trigger point
     *                                    for when the vertical scroll reaches/leaves
     *                                    the last y items in RecyclerView
     */
    public BaseRecyclerViewScrollListener(int bottomYEndItemPosForTrigger) {
        mBottomYEndItemPosForTrigger = bottomYEndItemPosForTrigger + 1;
    }

    /**
     * Callback method invoked when RecyclerView's scroll state changes.
     *
     * @param recyclerView The RecyclerView whose scroll state has changed.
     * @param newState     The updated scroll state.
     */
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (mIsScrolledToBottomEnd && newState == RecyclerView.SCROLL_STATE_IDLE) {
            //When the scroll to bottom state is true and the RecyclerView scroll is idle

            //Retrieving the current number of items in the RecyclerView
            int totalItems = recyclerView.getAdapter().getItemCount();

            if ((totalItems - mBottomYEndItemPosForTrigger) <= mBottomYEndItemPosForTrigger) {
                //When there is less number of items for scroll

                //Updating the scrolled to bottom state as False
                mIsScrolledToBottomEnd = false;
                //Fake Signalling that the last item view has been reached
                onBottomReached(1);
            }
        }
    }

    /**
     * Callback method invoked when the RecyclerView has been scrolled. This will be
     * called after the scroll has completed.
     *
     * @param recyclerView The RecyclerView which scrolled.
     * @param dx           The amount of horizontal scroll.
     * @param dy           The amount of vertical scroll.
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (dy != 0) {
            //Scanning for vertical scrolls only

            //Retrieving the current number of items in the RecyclerView
            int totalItems = recyclerView.getAdapter().getItemCount();
            //Retrieving the current last seen item position
            int lastItemPosition = RecyclerViewUtility.getLastVisibleItemPosition(recyclerView);

            if ((totalItems - mBottomYEndItemPosForTrigger) <= mBottomYEndItemPosForTrigger) {
                //When there is less number of items for scroll

                //Updating the scrolled to bottom state as False
                mIsScrolledToBottomEnd = false;
                //Fake Signalling that the last item view has been reached
                onBottomReached(1);

            } else if (dy > 0) {
                //Scanning when scrolling to the bottom

                //Checking if the last seen item was one of the last y items
                if (lastItemPosition >= (totalItems - mBottomYEndItemPosForTrigger) && !mIsScrolledToBottomEnd) {
                    //If not scrolled to bottom before and the screen has reached the last y items

                    //Updating the scrolled to bottom state as True
                    mIsScrolledToBottomEnd = true;
                    //Signalling that the last item view has been reached
                    onBottomReached(dy);

                } else if (lastItemPosition < (totalItems - mBottomYEndItemPosForTrigger) && mIsScrolledToBottomEnd) {
                    //If scrolled to bottom before, but had been reset to the 0th item
                    //then the scrolled to bottom state should be reset to False
                    mIsScrolledToBottomEnd = false;
                }

            } else {
                //Scanning when scrolling to the top

                if (lastItemPosition < (totalItems - mBottomYEndItemPosForTrigger) && mIsScrolledToBottomEnd) {
                    //If scrolled to bottom before and now scrolling away from the bottom
                    //and away from the last y items

                    //Updating the scrolled to bottom state as False
                    mIsScrolledToBottomEnd = false;
                    //Signalling that the scroll has moved away from the last item view
                    onBottomReached(dy);
                }
            }

        }
    }

    /**
     * Callback Method to be implemented to receive events when the
     * scroll has reached/left the last y items in the {@link RecyclerView}
     *
     * @param verticalScrollAmount is the amount of vertical scroll.
     *                             <br/>If >0 then scroll is moving towards the bottom;
     *                             <br/>If <0 then scroll is moving towards the top
     */
    public abstract void onBottomReached(int verticalScrollAmount);

}
