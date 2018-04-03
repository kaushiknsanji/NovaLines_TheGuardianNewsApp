package com.example.kaushiknsanji.novalines.interfaces;

import android.view.View;

/**
 * A View Interface for the Popup Menu actions shown
 * on the News Article Card items 'R.layout.news_article_item'
 *
 * @author Kaushik N Sanji
 */
public interface IArticleActionView extends IGenericView {
    /**
     * Method that returns the Root {@link View}
     * of the Activity/Fragment implementing {@link IArticleActionView}
     *
     * @return The Root {@link View} of the implementing Activity/Fragment
     */
    View getRootView();
}
