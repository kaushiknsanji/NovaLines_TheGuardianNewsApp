package com.example.kaushiknsanji.novalines.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.dialogs.PaginationNumberPickerDialogFragment;
import com.example.kaushiknsanji.novalines.interfaces.IGenericPresenter;
import com.example.kaushiknsanji.novalines.interfaces.IPaginationView;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;

/**
 * A Presenter Interface for the Pagination panel 'R.id.pagination_panel_id'
 * hosted by fragments that have paginated results.
 * <p>
 * Responsible for managing the buttons involved in pagination and the
 * page index values stored in {@link SharedPreferences}.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class PaginationPresenter implements IGenericPresenter<IPaginationView> {

    //Constant used for logs
    private static final String LOG_TAG = PaginationPresenter.class.getSimpleName();

    //For the view that displays the Pagination Panel
    private IPaginationView mPaginationView;
    //For the Context of the Activity/Fragment
    private Context mContext;

    /**
     * Default Constructor of {@link PaginationPresenter}
     */
    public PaginationPresenter() {
    }

    /**
     * Registers the Presenter {@link PaginationPresenter}
     * with the {@link IPaginationView}
     *
     * @param view Instance of the {@link IPaginationView}
     *             to be associated with this Presenter {@link PaginationPresenter}
     */
    @Override
    public void attachView(IPaginationView view) {
        //Associating this Presenter with the Pagination View passed
        mPaginationView = view;
        mContext = mPaginationView.getViewContext();
    }

    /**
     * Unregisters the previously registered {@link IPaginationView}
     * by the Presenter {@link PaginationPresenter}
     */
    @Override
    public void detachView() {
        //Removing the references held by this Presenter
        mPaginationView = null;
        mContext = null;
    }

    /**
     * Method that resets the 'page' (Page to Display) setting to 1, when not 1
     */
    public void resetStartPageIndex() {
        if (PreferencesUtility.getStartPageIndex(mContext) != 1) {
            //When the 'page' setting value is not equal to 1

            //Updating to its default value, which is 1
            PreferencesUtility.updateStartPageIndex(mContext, PreferencesUtility.getDefaultStartPageIndex(mContext));
        }
    }

    /**
     * Method that resets the 'page' (Page to Display) setting of a particular paginated view to 1, when not 1
     *
     * @param lastViewedPageIndex is the index of the last page viewed by the user
     *                            which is specific to the paginated view and not stored as a SharedPreference
     */
    public void resetLastViewedPageIndex(int lastViewedPageIndex) {
        if (lastViewedPageIndex > 1) {
            //When the index of the last page viewed is greater than 1

            //Updating the 'page' setting value to lastViewedPageIndex
            PreferencesUtility.updateStartPageIndex(mContext, lastViewedPageIndex);

            //Updating the 'page' setting value to its default value, which is 1
            //(This reapplication resets the start page to the first page for the current paginated view)
            PreferencesUtility.updateStartPageIndex(mContext, PreferencesUtility.getDefaultStartPageIndex(mContext));

        } else if (PreferencesUtility.getStartPageIndex(mContext) > 1) {
            //When the 'page' (Page to Display) setting is greater than 1 but not the last page viewed

            //Resetting the 'page' (Page to Display) setting value to 1
            resetStartPageIndex();
        }
    }

    /**
     * Method that resets/reapplies the value for the 'endIndex' preference setting
     *
     * @param endIndexValue Integer value of the last page index (specific to the paginated view)
     *                      to be applied to the 'endIndex' preference setting.
     *                      When the value is <=0, the default value of 'page' preference setting
     *                      will be applied to 'endIndex' preference setting
     */
    public void resetEndPageIndex(int endIndexValue) {
        //Flag to check whether the state of Pagination Buttons needs an update
        boolean updateButtonsStateReqd = false;

        if (endIndexValue <= 0) {
            //Defaulting the 'endIndex' setting to the default value of 'page' setting when
            //the value passed is 0 (or less)
            PreferencesUtility.updateLastPageIndex(mContext, PreferencesUtility.getDefaultStartPageIndex(mContext));
        } else {
            //When the endIndex value specific to the paginated view is more than 0

            if (PreferencesUtility.getLastPageIndex(mContext) == endIndexValue) {
                //When the endIndex value is same as that in preference, there will be no change in Preference value
                //Hence manual update of Pagination Buttons state is required
                updateButtonsStateReqd = true;
            }

            //honouring the value passed when greater than 0
            PreferencesUtility.updateLastPageIndex(mContext, endIndexValue);
        }

        if (updateButtonsStateReqd) {
            //When set to true, updating the state of Pagination Buttons
            updatePaginationButtonsState();
        }
    }

    /**
     * Method that updates the state of the Pagination Buttons
     * based on the current setting
     */
    public void updatePaginationButtonsState() {

        //Retrieving the 'page' (Page to Display) setting value
        int startIndex = PreferencesUtility.getStartPageIndex(mContext);

        //Retrieving the 'endIndex' preference value
        int endIndex = PreferencesUtility.getLastPageIndex(mContext, startIndex);

        if (startIndex == endIndex && startIndex != 1) {
            //When the last page is reached

            //Disabling the page-last and page-next buttons
            mPaginationView.getPageLastButton().setEnabled(false);
            mPaginationView.getPageNextButton().setEnabled(false);

            //Enabling the rest
            mPaginationView.getPageFirstButton().setEnabled(true);
            mPaginationView.getPagePreviousButton().setEnabled(true);
            mPaginationView.getPageMoreButton().setEnabled(true);

        }
        if (startIndex == endIndex && startIndex == 1) {
            //When the first and last page is same, and only one page is existing

            //Disabling all the buttons
            mPaginationView.getPageLastButton().setEnabled(false);
            mPaginationView.getPageNextButton().setEnabled(false);
            mPaginationView.getPageFirstButton().setEnabled(false);
            mPaginationView.getPagePreviousButton().setEnabled(false);
            mPaginationView.getPageMoreButton().setEnabled(false);

        } else if (startIndex != endIndex && startIndex == 1) {
            //When the first page is reached, and last page is not same as first page

            //Disabling the page-first and page-previous buttons
            mPaginationView.getPageFirstButton().setEnabled(false);
            mPaginationView.getPagePreviousButton().setEnabled(false);

            //Enabling the rest
            mPaginationView.getPageMoreButton().setEnabled(true);
            mPaginationView.getPageLastButton().setEnabled(true);
            mPaginationView.getPageNextButton().setEnabled(true);

        } else if (startIndex != endIndex) {
            //Enabling all the buttons when first and last page are different
            mPaginationView.getPageFirstButton().setEnabled(true);
            mPaginationView.getPagePreviousButton().setEnabled(true);
            mPaginationView.getPageMoreButton().setEnabled(true);
            mPaginationView.getPageLastButton().setEnabled(true);
            mPaginationView.getPageNextButton().setEnabled(true);

        }

    }

    /**
     * Method that displays the Pagination Panel
     */
    public void showPaginationPanel() {
        mPaginationView.getPaginationPanel().setVisibility(View.VISIBLE);
    }

    /**
     * Method that hides the Pagination Panel
     */
    public void hidePaginationPanel() {
        mPaginationView.getPaginationPanel().setVisibility(View.GONE);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     * @return <b>TRUE</b> when the view clicked was a Pagination Button, and was handled;
     * <b>FALSE</b> otherwise
     */
    public boolean onPaginationButtonClick(View view) {
        //Retrieving the current 'page' (Page to Display) setting value
        int startIndex = PreferencesUtility.getStartPageIndex(mContext);

        //Executing the click action based on the view's id
        switch (view.getId()) {
            case R.id.page_first_button_id:
                //On Page First action, updating the 'page' setting to 1
                PreferencesUtility.updateStartPageIndex(mContext, 1);
                //Displaying a Toast Message
                Toast.makeText(mContext, mContext.getString(R.string.navigate_page_first_msg), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.page_previous_button_id:
                //On Page Previous action, updating the 'page' setting
                //to a value less than itself by 1
                startIndex = startIndex - 1;
                PreferencesUtility.updateStartPageIndex(mContext, startIndex);
                //Displaying a Toast Message
                Toast.makeText(mContext, mContext.getString(R.string.navigate_page_x_msg, startIndex), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.page_more_button_id:
                //On Page More action, displaying a Number Picker Dialog
                //to allow the user to make the choice of viewing a random page

                //Retrieving the instance of the Dialog to be shown through the FragmentManager
                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                PaginationNumberPickerDialogFragment numberPickerDialogFragment
                        = (PaginationNumberPickerDialogFragment) fragmentManager.findFragmentByTag(PaginationNumberPickerDialogFragment.PAGN_DIALOG_FRAGMENT_TAG);

                if (numberPickerDialogFragment == null) {
                    //When there is no instance attached, that is the dialog is not active

                    //Creating the DialogFragment Instance
                    numberPickerDialogFragment = PaginationNumberPickerDialogFragment.newInstance(
                            //Using the default 'Page to Display' setting value as the minimum value
                            PreferencesUtility.getDefaultStartPageIndex(mContext),
                            //Using the 'endIndex' setting value as the maximum value
                            PreferencesUtility.getLastPageIndex(mContext)
                    );

                    //Displaying the DialogFragment
                    numberPickerDialogFragment.show(fragmentManager,
                            PaginationNumberPickerDialogFragment.PAGN_DIALOG_FRAGMENT_TAG);
                }
                return true;

            case R.id.page_next_button_id:
                //On Page Next action, updating the 'page' setting
                //to a value greater than itself by 1
                startIndex = startIndex + 1;
                PreferencesUtility.updateStartPageIndex(mContext, startIndex);
                //Displaying a Toast Message
                Toast.makeText(mContext, mContext.getString(R.string.navigate_page_x_msg, startIndex), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.page_last_button_id:
                //On Page Last action, updating the 'page' setting to
                //a value equal to that of the predetermined 'endIndex' preference value
                PreferencesUtility.updateStartPageIndex(mContext,
                        PreferencesUtility.getLastPageIndex(mContext, startIndex));
                //Displaying a Toast Message
                Toast.makeText(mContext, mContext.getString(R.string.navigate_page_last_msg), Toast.LENGTH_SHORT).show();
                return true;

            default:
                //Returning False when the view clicked is not a Pagination Button
                return false;
        }
    }

}
