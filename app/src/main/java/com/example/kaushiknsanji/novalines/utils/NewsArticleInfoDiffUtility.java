package com.example.kaushiknsanji.novalines.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

    //Bundle key constants used for the changes in payload content: START
    //For the Section Name of the News article
    public static final String PAYLOAD_ARTICLE_SECTION_NAME_STR_KEY = "Payload.ArticleSectionName";
    //For the Published Date of the News article
    public static final String PAYLOAD_ARTICLE_DATE_STR_KEY = "Payload.ArticleDate";
    //For the Title of the News Article
    public static final String PAYLOAD_ARTICLE_TITLE_STR_KEY = "Payload.ArticleTitle";
    //For the Author of the News Article
    public static final String PAYLOAD_ARTICLE_AUTHOR_STR_KEY = "Payload.ArticleAuthor";
    //For the link to the Image of the News Article
    public static final String PAYLOAD_ARTICLE_IMAGE_LINK_STR_KEY = "Payload.ArticleImageLink";
    //For the Trailing text of the News Headline
    public static final String PAYLOAD_ARTICLE_TRAIL_TEXT_STR_KEY = "Payload.ArticleTrailText";
    //Bundle key constants used for the changes in payload content: END

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
        //Returning the result of the comparison of News Article Section ID
        return mOldArticleInfoList.get(oldItemPosition).getSectionId()
                .equals(mNewArticleInfoList.get(newItemPosition).getSectionId());
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

        //Comparing the required contents of the NewsArticleInfo objects: START
        //Returning FALSE when one of them are not same

        //Comparing the Section Name of the News article
        if (!oldArticleInfo.getSectionName().equals(newArticleInfo.getSectionName())) {
            return false;
        }

        //Comparing the Published Date of the News article
        if (!oldArticleInfo.getPublishedDate("").equals(newArticleInfo.getPublishedDate(""))) {
            return false;
        }

        //Comparing the Title of the News Article
        if (!oldArticleInfo.getNewsTitle().equals(newArticleInfo.getNewsTitle())) {
            return false;
        }

        //Comparing the Author of the News Article
        if (!oldArticleInfo.getAuthor("").equals(newArticleInfo.getAuthor(""))) {
            return false;
        }

        //Comparing the link to the Image of the News Article
        if (!oldArticleInfo.getThumbImageUrl().equals(newArticleInfo.getThumbImageUrl())) {
            return false;
        }

        //Comparing the Trailing text of the News Headline
        if (!oldArticleInfo.getTrailText().equals(newArticleInfo.getTrailText())) {
            return false;
        }

        //Comparing the required contents of the NewsArticleInfo objects: END

        //Returning TRUE when all the contents compared are same
        return true;
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
        Bundle bundle = new Bundle(6); //There are only six items, hence fixing the capacity

        //Getting the NewsArticleInfo objects at the position
        NewsArticleInfo oldArticleInfo = mOldArticleInfoList.get(oldItemPosition);
        NewsArticleInfo newArticleInfo = mNewArticleInfoList.get(newItemPosition);

        //Evaluating the differences and adding them to the Bundle: START
        //Comparing the Section Name of the News article
        if (!oldArticleInfo.getSectionName().equals(newArticleInfo.getSectionName())) {
            bundle.putString(PAYLOAD_ARTICLE_SECTION_NAME_STR_KEY, newArticleInfo.getSectionName());
        }

        //Comparing the Published Date of the News article
        if (!oldArticleInfo.getPublishedDate("").equals(newArticleInfo.getPublishedDate(""))) {
            bundle.putString(PAYLOAD_ARTICLE_DATE_STR_KEY, newArticleInfo.getPublishedDate(""));
        }

        //Comparing the Title of the News Article
        if (!oldArticleInfo.getNewsTitle().equals(newArticleInfo.getNewsTitle())) {
            bundle.putString(PAYLOAD_ARTICLE_TITLE_STR_KEY, newArticleInfo.getNewsTitle());
        }

        //Comparing the Author of the News Article
        if (!oldArticleInfo.getAuthor("").equals(newArticleInfo.getAuthor(""))) {
            bundle.putString(PAYLOAD_ARTICLE_AUTHOR_STR_KEY, newArticleInfo.getAuthor(""));
        }

        //Comparing the link to the Image of the News Article
        if (!oldArticleInfo.getThumbImageUrl().equals(newArticleInfo.getThumbImageUrl())) {
            bundle.putString(PAYLOAD_ARTICLE_IMAGE_LINK_STR_KEY, newArticleInfo.getThumbImageUrl());
        }

        //Comparing the Trailing text of the News Headline
        if (!oldArticleInfo.getTrailText().equals(newArticleInfo.getTrailText())) {
            bundle.putString(PAYLOAD_ARTICLE_TRAIL_TEXT_STR_KEY, newArticleInfo.getTrailText());
        }
        //Evaluating the differences and adding them to the Bundle: END

        //Returning null when no change is found
        if (bundle.size() == 0) return null;

        //Returning the Payload prepared
        return bundle;
    }
}