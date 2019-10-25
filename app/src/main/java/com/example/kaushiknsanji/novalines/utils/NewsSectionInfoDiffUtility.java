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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;

import java.util.List;

/**
 * Class that extends the {@link DiffUtil.Callback}
 * to analyse the difference between two list of
 * {@link NewsSectionInfo} objects, used for updating the RecyclerView's Adapter data
 *
 * @author Kaushik N Sanji
 */
public class NewsSectionInfoDiffUtility extends DiffUtil.Callback {

    //Bundle key constants used for the changes in payload content: START
    //For the Article Count
    public static final String PAYLOAD_ARTICLE_COUNT_INT_KEY = "Payload.ArticleCount";
    //For the Section Name
    public static final String PAYLOAD_SECTION_NAME_STR_KEY = "Payload.SectionName";
    //Bundle key constants used for the changes in payload content: END

    //Stores the Current list of NewsSectionInfo objects
    private List<NewsSectionInfo> mOldSectionInfoList;
    //Stores the New list of NewsSectionInfo objects
    private List<NewsSectionInfo> mNewSectionInfoList;

    /**
     * Constructor of {@link NewsSectionInfoDiffUtility}
     *
     * @param oldSectionInfos is the Current list of {@link NewsSectionInfo} objects to be compared
     * @param newSectionInfos is the New list of {@link NewsSectionInfo} objects to be compared
     */
    public NewsSectionInfoDiffUtility(List<NewsSectionInfo> oldSectionInfos, List<NewsSectionInfo> newSectionInfos) {
        mOldSectionInfoList = oldSectionInfos;
        mNewSectionInfoList = newSectionInfos;
    }

    /**
     * Returns the size of the old list.
     *
     * @return The size of the old list.
     */
    @Override
    public int getOldListSize() {
        return mOldSectionInfoList.size();
    }

    /**
     * Returns the size of the new list.
     *
     * @return The size of the new list.
     */
    @Override
    public int getNewListSize() {
        return mNewSectionInfoList.size();
    }

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //Returning the result of comparison of News Section ID
        return mOldSectionInfoList.get(oldItemPosition).getSectionId()
                .equals(mNewSectionInfoList.get(newItemPosition).getSectionId());
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * <p>
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * so that you can change its behavior depending on your UI.
     * For example, if you are using DiffUtil with a
     * {@link RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * <p>
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //Returning the result of comparison of the News Article Count
        return mOldSectionInfoList.get(oldItemPosition).getNewsArticleCount()
                == mNewSectionInfoList.get(newItemPosition).getNewsArticleCount();
    }

    /**
     * When {@link #areItemsTheSame(int, int)} returns {@code true} for two items and
     * {@link #areContentsTheSame(int, int)} returns false for them, DiffUtil
     * calls this method to get a payload about the change.
     * <p>
     * For example, if you are using DiffUtil with {@link RecyclerView}, you can return the
     * particular field that changed in the item and your
     * {@link RecyclerView.ItemAnimator ItemAnimator} can use that
     * information to run the correct animation.
     * <p>
     * Default implementation returns {@code null}.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return A payload object that represents the change between the two items.
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //Using a Bundle to pass the changes that are to be made
        Bundle bundle = new Bundle(2); //There are only two items, hence fixing the capacity

        //Getting the NewsSectionInfo objects at the position
        NewsSectionInfo oldSectionInfo = mOldSectionInfoList.get(oldItemPosition);
        NewsSectionInfo newSectionInfo = mNewSectionInfoList.get(newItemPosition);

        //Evaluating the differences and adding them to the Bundle: START
        if (oldSectionInfo.getNewsArticleCount() != newSectionInfo.getNewsArticleCount()) {
            bundle.putInt(PAYLOAD_ARTICLE_COUNT_INT_KEY, newSectionInfo.getNewsArticleCount());
        }

        if (!oldSectionInfo.getSectionName().equals(newSectionInfo.getSectionName())) {
            bundle.putString(PAYLOAD_SECTION_NAME_STR_KEY, newSectionInfo.getSectionName());
        }
        //Evaluating the differences and adding them to the Bundle: END

        //Returning null when no change is found
        if (bundle.size() == 0) return null;

        //Returning the Payload prepared
        return bundle;
    }
}
