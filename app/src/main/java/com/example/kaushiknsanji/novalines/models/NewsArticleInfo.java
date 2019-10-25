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

package com.example.kaushiknsanji.novalines.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Parcelable Model Class for storing the parsed data of the News Articles
 * retrieved in the News Query response, built by the Utility class
 * {@link com.example.kaushiknsanji.novalines.utils.NewsArticleInfoParserUtility}
 *
 * @author Kaushik N Sanji
 */
public class NewsArticleInfo implements Parcelable {

    /**
     * Implementation of {@link android.os.Parcelable.Creator} interface
     * to generate instances of this Parcelable class {@link NewsArticleInfo} from a {@link Parcel}
     */
    public static final Creator<NewsArticleInfo> CREATOR = new Creator<NewsArticleInfo>() {

        /**
         * Creates an instance of this Parcelable class {@link NewsArticleInfo} from
         * a given Parcel whose data had been previously written by #writeToParcel() method
         *
         * @param in The Parcel to read the object's data from.
         * @return Returns a new instance of this Parcelable class {@link NewsArticleInfo}
         */
        @Override
        public NewsArticleInfo createFromParcel(Parcel in) {
            return new NewsArticleInfo(in);
        }

        /**
         * Creates a new array of this Parcelable class {@link NewsArticleInfo}
         *
         * @param size Size of the array
         * @return Returns an array of this Parcelable class {@link NewsArticleInfo}, with every
         * entry initialized to null
         */
        @Override
        public NewsArticleInfo[] newArray(int size) {
            return new NewsArticleInfo[size];
        }
    };
    //Constant for the DateTime format used in the Published Date of the News article
    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    //Stores the Section ID of a particular News article
    private String mSectionId;
    //Stores the Section Name of the News article
    private String mSectionName;
    //Stores the total count of articles found for the News query
    private int mNewsArticleCount;
    //Stores the Published Date of the News article
    private String mPublishedDate;
    //Stores the Title of the News Article
    private String mNewsTitle;
    //Stores the link to the html content of the News Article
    private String mWebUrl;
    //Stores the link to the raw content of the News Article
    private String mApiUrl;
    //Stores the Trailing text of the News Headline
    private String mTrailText;
    //Stores the Author of the News Article
    private String mAuthor;
    //Stores the link to the Image of the News Article
    private String mThumbImageUrl;

    /**
     * Constructor to initialize the {@link NewsArticleInfo}
     */
    public NewsArticleInfo() {
    }

    /**
     * Parcelable constructor that de-serializes the data from a Parcel Class passed
     *
     * @param in is the Instance of the Parcel class containing the serialized data
     */
    protected NewsArticleInfo(Parcel in) {
        mSectionId = in.readString();
        mSectionName = in.readString();
        mNewsArticleCount = in.readInt();
        mPublishedDate = in.readString();
        mNewsTitle = in.readString();
        mWebUrl = in.readString();
        mApiUrl = in.readString();
        mTrailText = in.readString();
        mAuthor = in.readString();
        mThumbImageUrl = in.readString();
    }

    /**
     * Flattens/Serializes the object of {@link NewsArticleInfo} into a Parcel
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
        dest.writeString(mPublishedDate);
        dest.writeString(mNewsTitle);
        dest.writeString(mWebUrl);
        dest.writeString(mApiUrl);
        dest.writeString(mTrailText);
        dest.writeString(mAuthor);
        dest.writeString(mThumbImageUrl);
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
     * Method that returns the 'Section ID' of the News article
     *
     * @return String containing the 'Section ID' of the News article
     */
    public String getSectionId() {
        return mSectionId;
    }

    /**
     * Setter Method for the 'Section ID' of the News article
     *
     * @param sectionId is a String containing the 'Section ID' of the News article
     */
    public void setSectionId(String sectionId) {
        this.mSectionId = sectionId;
    }

    /**
     * Method that returns the Section Name of the News article
     *
     * @return String containing the Section Name of the News article
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Setter Method for the Section Name of the News article
     *
     * @param sectionName is a String containing the Section Name of the News article
     */
    public void setSectionName(String sectionName) {
        this.mSectionName = sectionName;
    }

    /**
     * Method that returns the total article count found for the News query
     *
     * @return Integer for the total article count, found for the News query
     */
    public int getNewsArticleCount() {
        return mNewsArticleCount;
    }

    /**
     * Setter Method for the Article count found for the News query
     *
     * @param newsArticleCount is an Integer for the total article count, found for the News query
     */
    public void setNewsArticleCount(int newsArticleCount) {
        this.mNewsArticleCount = newsArticleCount;
    }

    /**
     * Method that returns the Published DateTime of the News Article
     * in the User's locale, in the sample format 'on Jan 14, 2018 at 1:50:00PM IST'
     *
     * @param fallback is the default fallback string that will be returned when No date is available
     *                 or the date is not parseable
     * @return String containing the locale formatted Published DateTime of the News Article
     */
    @SuppressLint("SimpleDateFormat")
    public String getPublishedDate(String fallback) {
        if (TextUtils.isEmpty(mPublishedDate)) {
            //Returning with the fallback string when the Published date is not available
            return fallback;
        }

        //Trimming the 'Z' in the DateTimeStamp if present: START
        String gmtDateTimeStr = mPublishedDate;
        if (mPublishedDate.endsWith("Z")) {
            gmtDateTimeStr = mPublishedDate.substring(0, mPublishedDate.indexOf("Z"));
        }
        //Trimming the 'Z' in the DateTimeStamp if present: END

        //Parsing the Source DateTime which is in GMT: START
        SimpleDateFormat sdfParser = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
        sdfParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsedDate = null;
        try {
            parsedDate = sdfParser.parse(gmtDateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Parsing the Source DateTime which is in GMT: END

        //Returning with the fallback string if the DateTime Parsing failed
        if (parsedDate == null) {
            return fallback;
        }

        //Formatting the Date and Time to User's locale
        String parsedDateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(parsedDate);
        String parsedTimeStr = DateFormat.getTimeInstance(DateFormat.LONG).format(parsedDate);

        //Returning the formatted DateTime
        //(appearing in the sample format 'on Jan 14, 2018 at 1:50:00PM IST')
        return MessageFormat.format("on {0} at {1}", parsedDateStr, parsedTimeStr);
    }

    /**
     * Setter Method for the Published Date of the News article
     *
     * @param publishedDate a String containing the Published Date of the News article
     */
    public void setPublishedDate(String publishedDate) {
        this.mPublishedDate = publishedDate;
    }

    /**
     * Method that returns the Title of the News article
     *
     * @return String containing the Title of the News article
     */
    public String getNewsTitle() {
        return mNewsTitle;
    }

    /**
     * Setter Method for the Title of the News article
     *
     * @param newsTitle a String containing the Title of the News article
     */
    public void setNewsTitle(String newsTitle) {
        this.mNewsTitle = newsTitle;
    }

    /**
     * Method that returns the link to the html content of the News article
     *
     * @return String containing the link to the html content of the News article
     */
    public String getWebUrl() {
        return mWebUrl;
    }

    /**
     * Setter Method for the link to the html content of the News article
     *
     * @param webUrl a String containing the link to the html content of the News article
     */
    public void setWebUrl(String webUrl) {
        this.mWebUrl = webUrl;
    }

    /**
     * Method that returns the link to the raw content of the News article
     *
     * @return String containing the link to the raw content of the News article
     */
    public String getApiUrl() {
        return mApiUrl;
    }

    /**
     * Setter Method for the link to the raw content of the News article
     *
     * @param apiUrl a String containing the link to the raw content of the News article
     */
    public void setApiUrl(String apiUrl) {
        this.mApiUrl = apiUrl;
    }

    /**
     * Method that return the Trailing Text of the Headline of the News article
     *
     * @return String containing the Trailing Text of the Headline of the News article
     */
    public String getTrailText() {
        return mTrailText;
    }

    /**
     * Setter Method for the Trailing Text of the Headline of the News article
     *
     * @param trailText a String containing the Trailing Text of the Headline of the News article
     */
    public void setTrailText(String trailText) {
        this.mTrailText = trailText;
    }

    /**
     * Method that returns the Author of the News article
     *
     * @param fallback String containing the default message to be returned when NO Author is present
     * @return String containing the Author of the News article
     */
    public String getAuthor(String fallback) {
        return TextUtils.isEmpty(mAuthor) ? fallback : mAuthor;
    }

    /**
     * Setter Method for the Author of the News article
     *
     * @param author a String containing the Author of the News article
     */
    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    /**
     * Method that returns the link to the Thumbnail Image of the News article
     *
     * @return String containing the link to the Thumbnail Image of the News article
     */
    public String getThumbImageUrl() {
        return mThumbImageUrl;
    }

    /**
     * Setter Method for the link to the Thumbnail Image of the News article
     *
     * @param thumbImageUrl a String containing the link to the Thumbnail Image of the News article
     */
    public void setThumbImageUrl(String thumbImageUrl) {
        this.mThumbImageUrl = thumbImageUrl;
    }
}