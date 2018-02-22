package com.example.kaushiknsanji.novalines.models;

/**
 * Model class for storing the details of the Resolution items
 * that is shown when there is No Feed for the News Category/Section
 * due to some issue, for the Resolution of it.
 *
 * @author Kaushik N Sanji
 */
public class NoFeedInfo {

    //Stores the Item Number provided for the Resolution
    private int mResolutionItemNumber;

    //Stores the Resolution Text
    private String mResolutionText;

    //Stores the corresponding Button Text
    private String mResolutionButtonText;

    //Stores the Resource ID of the compound drawable to be used
    private int mResolutionButtonCompoundDrawableRes;

    /**
     * Constructor to initialize {@link NoFeedInfo}
     * for the Resolution Item
     */
    public NoFeedInfo() {
    }

    /**
     * Method that returns the Item Number to be set for the Resolution Item
     *
     * @return Integer value of the Item Number to be set for the Resolution Item
     */
    public int getResolutionItemNumber() {
        return mResolutionItemNumber;
    }

    /**
     * Methods that sets the Item Number for the Resolution Item
     * @param resolutionItemNumber is the Item Number for the Resolution Item
     */
    public void setResolutionItemNumber(int resolutionItemNumber) {
        this.mResolutionItemNumber = resolutionItemNumber;
    }

    /**
     * Method that returns the Resolution text to be set for the Resolution Item
     * @return Resolution text to be set for the Resolution Item
     */
    public String getResolutionText() {
        return mResolutionText;
    }

    /**
     * Method that sets the Resolution text of the Resolution Item
     * @param resolutionText is the Resolution text for the Resolution Item
     */
    public void setResolutionText(String resolutionText) {
        this.mResolutionText = resolutionText;
    }

    /**
     * Method that returns the Text to be set on the Button of the Resolution Item
     * @return String value representing the Text to be set on the Button of the Resolution Item
     */
    public String getResolutionButtonText() {
        return mResolutionButtonText;
    }

    /**
     * Method that sets the Button Text on the Button of the Resolution Item
     * @param resolutionButtonText is the Text for the Button of the Resolution Item
     */
    public void setResolutionButtonText(String resolutionButtonText) {
        this.mResolutionButtonText = resolutionButtonText;
    }

    /**
     * Method that returns the Compound Drawable Resource ID to be set on the
     * Button of the Resolution Item
     * @return Integer value for the Resource ID of the Drawable to be set on the
     * Button of the Resolution Item
     */
    public int getResolutionButtonCompoundDrawableRes() {
        return mResolutionButtonCompoundDrawableRes;
    }

    /**
     * Method that sets the Compound Drawable Resource ID for the
     * Button of the Resolution Item
     * @param resolutionButtonCompoundDrawableRes is the Integer value of the Compound Drawable
     *                                            Resource ID for the Button of the Resolution Item
     */
    public void setResolutionButtonCompoundDrawableRes(int resolutionButtonCompoundDrawableRes) {
        this.mResolutionButtonCompoundDrawableRes = resolutionButtonCompoundDrawableRes;
    }

}
