package com.example.kaushiknsanji.novalines.models;

/**
 * Created by Kaushik N Sanji on 08-Feb-18.
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
     */
    public NoFeedInfo() {
    }

    public int getResolutionItemNumber() {
        return mResolutionItemNumber;
    }

    public void setResolutionItemNumber(int resolutionItemNumber) {
        this.mResolutionItemNumber = resolutionItemNumber;
    }

    public String getResolutionText() {
        return mResolutionText;
    }

    public void setResolutionText(String resolutionText) {
        this.mResolutionText = resolutionText;
    }

    public String getResolutionButtonText() {
        return mResolutionButtonText;
    }

    public void setResolutionButtonText(String resolutionButtonText) {
        this.mResolutionButtonText = resolutionButtonText;
    }

    public int getResolutionButtonCompoundDrawableRes() {
        return mResolutionButtonCompoundDrawableRes;
    }

    public void setResolutionButtonCompoundDrawableRes(int resolutionButtonCompoundDrawableRes) {
        this.mResolutionButtonCompoundDrawableRes = resolutionButtonCompoundDrawableRes;
    }

}
