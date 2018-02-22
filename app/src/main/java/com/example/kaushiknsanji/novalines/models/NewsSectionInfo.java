package com.example.kaushiknsanji.novalines.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable Model Class for storing the parsed metadata of the Subscribed News Sections
 * retrieved in the News Section response, built by the Utility class
 * {@link com.example.kaushiknsanji.novalines.utils.NewsSectionInfoParserUtility}
 *
 * @author Kaushik N Sanji
 */
public class NewsSectionInfo implements Parcelable {

    /**
     * Implementation of {@link android.os.Parcelable.Creator} interface
     * to generate instances of this Parcelable class {@link NewsSectionInfo} from a {@link Parcel}
     */
    public static final Creator<NewsSectionInfo> CREATOR = new Creator<NewsSectionInfo>() {

        /**
         * Creates an instance of this Parcelable class {@link NewsSectionInfo} from
         * a given Parcel whose data had been previously written by #writeToParcel() method
         *
         * @param in The Parcel to read the object's data from.
         * @return Returns a new instance of this Parcelable class {@link NewsSectionInfo}
         */
        @Override
        public NewsSectionInfo createFromParcel(Parcel in) {
            return new NewsSectionInfo(in);
        }

        /**
         * Creates a new array of this Parcelable class {@link NewsSectionInfo}
         *
         * @param size Size of the array
         * @return Returns an array of this Parcelable class {@link NewsSectionInfo}, with every
         * entry initialized to null
         */
        @Override
        public NewsSectionInfo[] newArray(int size) {
            return new NewsSectionInfo[size];
        }
    };
    //Stores the Section ID of the a particular News section
    private String mSectionId;
    //Stores the Section Name of the News section
    private String mSectionName;
    //Stores the total count of articles found under the section
    private int mNewsArticleCount;

    /**
     * Constructor to initialize the {@link NewsSectionInfo}
     */
    public NewsSectionInfo() {
    }

    /**
     * Parcelable constructor that de-serializes the data from a Parcel Class passed
     *
     * @param in is the Instance of the Parcel class containing the serialized data
     */
    protected NewsSectionInfo(Parcel in) {
        mSectionId = in.readString();
        mSectionName = in.readString();
        mNewsArticleCount = in.readInt();
    }

    /**
     * Flattens/Serializes the object of {@link NewsSectionInfo} into a Parcel
     *
     * @param dest  The Parcel in which the object should be written
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSectionId);
        dest.writeString(mSectionName);
        dest.writeInt(mNewsArticleCount);
    }

    /**
     * Describes the kinds of special objects contained in this Parcelable
     * instance's marshaled representation.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0; //Indicating with no mask
    }

    /**
     * Method that returns the 'Section ID' of the News Section
     *
     * @return String containing the 'Section ID' of the News Section
     */
    public String getSectionId() {
        return mSectionId;
    }

    /**
     * Setter Method for the 'Section ID' of the News section
     *
     * @param sectionId is a String containing the 'Section ID' of the News Section
     */
    public void setSectionId(String sectionId) {
        this.mSectionId = sectionId;
    }

    /**
     * Method that returns the Section Name of the News section
     *
     * @return String containing the Section Name of the News section
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Setter Method for the Section Name of the News section
     *
     * @param sectionNameStr is a String containing the Section Name of the News section
     */
    public void setSectionName(String sectionNameStr) {
        this.mSectionName = sectionNameStr;
    }

    /**
     * Method that returns the total article count under the News section
     *
     * @return Integer for the total article count under the News section
     */
    public int getNewsArticleCount() {
        return mNewsArticleCount;
    }

    /**
     * Setter Method for the Article count under the News section
     *
     * @param newsArticleCount is an Integer for the total article count under the News section
     */
    public void setNewsArticleCount(int newsArticleCount) {
        this.mNewsArticleCount = newsArticleCount;
    }

}