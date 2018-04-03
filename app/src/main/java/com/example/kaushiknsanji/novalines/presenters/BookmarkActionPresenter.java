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
 * A Presenter Interface for the 'Read Later' Popup Menu action shown
 * on the News Article Card items 'R.layout.news_article_item'
 * <p>
 * Responsible for adding/removing the News Articles from the Bookmarks list (database)
 * and responding to the user's actions through a {@link Snackbar}.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class BookmarkActionPresenter implements IGenericPresenter<IArticleActionView> {
    //For the view that updates Bookmarks
    private IArticleActionView mBookmarkActionView;
    //For the Context of the Activity/Fragment
    private Context mContext;

    /**
     * Registers the Presenter {@link BookmarkActionPresenter}
     * with the {@link IArticleActionView}
     *
     * @param view Instance of the {@link IArticleActionView}
     *             to be associated with this Presenter {@link BookmarkActionPresenter}
     */
    @Override
    public void attachView(IArticleActionView view) {
        //Associating this Presenter with the view passed
        mBookmarkActionView = view;
        mContext = mBookmarkActionView.getViewContext();
    }

    /**
     * Unregisters the previously registered {@link IArticleActionView}
     * by the Presenter {@link BookmarkActionPresenter}
     */
    @Override
    public void detachView() {
        //Removing the references held by this Presenter
        mBookmarkActionView = null;
        mContext = null;
    }

    /**
     * Method that saves the selected News Article into Bookmarks
     *
     * @param newsArticleInfo is the {@link NewsArticleInfo} object of the News Article
     *                        to be saved in Bookmarks
     */
    public void addBookmark(NewsArticleInfo newsArticleInfo) {
        //(Adding entry to the Bookmarks table in future implementation)

        //Displaying the Snackbar on success: START
        //Initializing an empty Snackbar
        Snackbar snackbar = Snackbar.make(mBookmarkActionView.getRootView(), "", Snackbar.LENGTH_LONG);
        //Setting the Action
        snackbar.setAction(mContext.getString(R.string.snackbar_action_undo), new AddBookmarkUndoListener(newsArticleInfo));
        //Setting the Action Text Color
        snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.snackBarActionTextColorAmberA400));
        //Setting the Text along with replacing the placeholders for drawables with their corresponding resource: START
        TextView sbTextView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        sbTextView.setText(R.string.article_bookmarked_snack, TextView.BufferType.SPANNABLE);
        TextAppearanceUtility.replaceTextWithImage(mContext, sbTextView);
        //Setting the Text along with replacing the placeholders for drawables with their corresponding resource: END
        snackbar.show(); //Displaying the prepared Snackbar
        //Displaying the Snackbar on success: END
    }

    /**
     * Method that reverses the action done by {@link #addBookmark(NewsArticleInfo)}
     *
     * @param newsArticleInfo is the {@link NewsArticleInfo} object of the entry
     *                        that was added to the Bookmarks which needs to be undone
     */
    private void undoAddBookmark(NewsArticleInfo newsArticleInfo) {
        //(Removing the entry added to the Bookmarks table in future implementation)

        //Displaying the Snackbar on success of the removal of the entry
        Snackbar.make(mBookmarkActionView.getRootView(), mContext.getString(R.string.article_bookmarked_undo_snack), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Class that implements the {@link android.view.View.OnClickListener}
     * to provide the UNDO action for the Snackbar that is shown
     * when the user adds a News Article to the Bookmarks for Reading later
     */
    private class AddBookmarkUndoListener implements View.OnClickListener {

        //The NewsArticleInfo object of the entry that was added to the Bookmarks
        final NewsArticleInfo newsArticleInfo;

        /**
         * Constructor of {@link AddBookmarkUndoListener}
         *
         * @param newsArticleInfo is the {@link NewsArticleInfo} object of the entry that was added to the Bookmarks
         */
        private AddBookmarkUndoListener(NewsArticleInfo newsArticleInfo) {
            this.newsArticleInfo = newsArticleInfo;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            undoAddBookmark(newsArticleInfo);
        }
    }
}
