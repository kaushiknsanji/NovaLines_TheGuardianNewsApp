package com.example.kaushiknsanji.novalines.utils;

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
}
