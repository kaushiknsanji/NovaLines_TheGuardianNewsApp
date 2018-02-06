package com.example.kaushiknsanji.novalines.utils;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;

import java.util.List;

/**
 * Class that extends the {@link DiffUtil.Callback}
 * to analyse the difference between two list of
 * {@link NewsArticleInfo} objects, used for updating the RecyclerView's Adapter data
 *
 * @author Kaushik N Sanji
 */
public class NewsArticleInfoDiffUtility extends DiffUtil.Callback {

    //Stores the Current list of NewsArticleInfo objects
    private List<NewsArticleInfo> mOldArticleInfoList;

    //Stores the New list of NewsArticleInfo objects
    private List<NewsArticleInfo> mNewArticleInfoList;

    /**
     * Constructor of {@link NewsArticleInfoDiffUtility}
     *
     * @param oldArticleInfos is the Current list of {@link NewsArticleInfo} objects to be compared
     * @param newArticleInfos is the New list of {@link NewsArticleInfo} objects to be compared
     */
    public NewsArticleInfoDiffUtility(List<NewsArticleInfo> oldArticleInfos, List<NewsArticleInfo> newArticleInfos) {
        mOldArticleInfoList = oldArticleInfos;
        mNewArticleInfoList = newArticleInfos;
    }

    /**
     * Returns the size of the old list.
     *
     * @return The size of the old list.
     */
    @Override
    public int getOldListSize() {
        return mOldArticleInfoList.size();
    }

    /**
     * Returns the size of the new list.
     *
     * @return The size of the new list.
     */
    @Override
    public int getNewListSize() {
        return mNewArticleInfoList.size();
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
        //Returning the result of the comparison of News Article Title
        return mOldArticleInfoList.get(oldItemPosition).getNewsTitle()
                .equals(mNewArticleInfoList.get(newItemPosition).getNewsTitle());
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
        //Getting the NewsArticleInfo objects at the position
        NewsArticleInfo oldArticleInfo = mOldArticleInfoList.get(oldItemPosition);
        NewsArticleInfo newArticleInfo = mNewArticleInfoList.get(newItemPosition);

        //Comparing the required contents of the NewsArticleInfo objects
        boolean isSectionIDSame = oldArticleInfo.getSectionId().equals(newArticleInfo.getSectionId());
        boolean isWebUrlSame = oldArticleInfo.getWebUrl().equals(newArticleInfo.getWebUrl());
        boolean isAuthorSame = oldArticleInfo.getAuthor("").equals(newArticleInfo.getAuthor(""));
        boolean isThumbImageUrlSame = oldArticleInfo.getThumbImageUrl().equals(newArticleInfo.getThumbImageUrl());
        boolean isTrailTextSame = oldArticleInfo.getTrailText().equals(newArticleInfo.getTrailText());

        //Returning the result of comparison
        return isSectionIDSame & isWebUrlSame & isAuthorSame & isThumbImageUrlSame & isTrailTextSame;
    }
}