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

package com.example.kaushiknsanji.novalines.presenters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.interfaces.IArticleActionView;
import com.example.kaushiknsanji.novalines.interfaces.IGenericPresenter;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * A Presenter Interface for the 'Favorite This' Popup Menu action shown
 * on the News Article Card items 'R.layout.news_article_item'
 * <p>
 * Responsible for adding/removing the News Articles from the Favorites list (database)
 * and responding to the user's actions through a {@link Snackbar}.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class FavoriteActionPresenter implements IGenericPresenter<IArticleActionView> {
    //For the view that updates Favorites
    private IArticleActionView mFavoriteActionView;
    //For the Context of the Activity/Fragment
    private Context mContext;

    /**
     * Registers the Presenter {@link FavoriteActionPresenter}
     * with the {@link IArticleActionView}
     *
     * @param view Instance of the {@link IArticleActionView}
     *             to be associated with this Presenter {@link FavoriteActionPresenter}
     */
    @Override
    public void attachView(IArticleActionView view) {
        //Associating this Presenter with the view passed
        mFavoriteActionView = view;
        mContext = mFavoriteActionView.getViewContext();
    }

    /**
     * Unregisters the previously registered {@link IArticleActionView}
     * by the Presenter {@link FavoriteActionPresenter}
     */
    @Override
    public void detachView() {
        //Removing the references held by this Presenter
        mFavoriteActionView = null;
        mContext = null;
    }

    /**
     * Method that saves the selected News Article into Favorites
     *
     * @param newsArticleInfo is the {@link NewsArticleInfo} object of the News Article
     *                        to be saved in Favorites
     */
    public void addFavorite(NewsArticleInfo newsArticleInfo) {
        //(Adding entry to the Favorites table in future implementation)

        //Displaying the Snackbar on success: START
        //Initializing an empty Snackbar
        Snackbar snackbar = Snackbar.make(mFavoriteActionView.getRootView(), "", Snackbar.LENGTH_LONG);
        //Setting the Action
        snackbar.setAction(mContext.getString(R.string.snackbar_action_undo), new AddFavoriteUndoListener(newsArticleInfo));
        //Setting the Action Text Color
        snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.snackBarActionTextColorAmberA400));
        //Setting the Text along with replacing the placeholders for drawables with their corresponding resource: START
        TextView sbTextView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        sbTextView.setText(R.string.article_favorited_snack, TextView.BufferType.SPANNABLE);
        TextAppearanceUtility.replaceTextWithImage(mContext, sbTextView);
        //Setting the Text along with replacing the placeholders for drawables with their corresponding resource: END
        snackbar.show(); //Displaying the prepared Snackbar
        //Displaying the Snackbar on success: END
    }

    /**
     * Method that reverses the action done by {@link #addFavorite(NewsArticleInfo)}
     *
     * @param newsArticleInfo is the {@link NewsArticleInfo} object of the entry
     *                        that was added to the Favorites which needs to be undone
     */
    private void undoAddFavorite(NewsArticleInfo newsArticleInfo) {
        //(Removing the entry added to the Favorites table in future implementation)

        //Displaying the Snackbar on success of the removal of the entry
        Snackbar.make(mFavoriteActionView.getRootView(), mContext.getString(R.string.article_favorited_undo_snack), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Class that implements the {@link android.view.View.OnClickListener}
     * to provide the UNDO action for the Snackbar that is shown
     * when the user adds a News Article to the Favorites
     */
    private class AddFavoriteUndoListener implements View.OnClickListener {

        //The NewsArticleInfo object of the entry that was added to the Favorites
        final NewsArticleInfo newsArticleInfo;

        /**
         * Constructor of {@link AddFavoriteUndoListener}
         *
         * @param newsArticleInfo is the {@link NewsArticleInfo} object of the entry that was added to the Favorites
         */
        private AddFavoriteUndoListener(NewsArticleInfo newsArticleInfo) {
            this.newsArticleInfo = newsArticleInfo;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            undoAddFavorite(newsArticleInfo);
        }
    }

}
