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

package com.example.kaushiknsanji.novalines.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Custom {@link android.support.v7.widget.RecyclerView.ItemDecoration} class
 * for proper Item spacing with RecyclerView Bottom padding if required
 *
 * @author Kaushik N Sanji
 */
public class RecyclerViewItemDecorUtility extends RecyclerView.ItemDecoration {
    //Stores the Dimension size for the RecyclerView Item spacing
    private int mItemSpacing;
    //Stores the Dimension size for the RecyclerView Bottom padding
    private int mBottomPadding;

    /**
     * Constructor of {@link RecyclerViewItemDecorUtility}
     *
     * @param itemSpacing   is the Integer value of the Dimension size for RecyclerView Item spacing
     * @param bottomPadding is the Integer value of the Dimension size for RecyclerView Bottom padding
     */
    public RecyclerViewItemDecorUtility(int itemSpacing, int bottomPadding) {
        mItemSpacing = itemSpacing;
        mBottomPadding = bottomPadding;
    }

    /**
     * Constructor of {@link RecyclerViewItemDecorUtility}
     *
     * @param itemSpacing is the Integer value of the Dimension size for RecyclerView Item spacing
     */
    public RecyclerViewItemDecorUtility(int itemSpacing) {
        //Propagating the call to main constructor with the Bottom padding size as 0
        this(itemSpacing, 0);
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
                && mBottomPadding > 0) {
            //Setting the Bottom offset height of the last Child view with the Bottom Padding when present
            outRect.bottom = mItemSpacing + mBottomPadding;
        } else {
            //Setting the Bottom offset height of all the Child views (including the last one)
            //with the RecyclerView Item spacing mentioned
            outRect.bottom = mItemSpacing;
        }
    }

}
