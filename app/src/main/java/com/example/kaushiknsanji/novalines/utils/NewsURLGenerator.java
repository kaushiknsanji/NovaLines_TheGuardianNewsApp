package com.example.kaushiknsanji.novalines.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.kaushiknsanji.novalines.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility Class that generates News Request URLs for -
 * <ul>
 * <li>Subscribed News Sections/Categories</li>
 * <li>Specific News with a search query (In Future Releases)</li>
 * </ul>
 *
 * @author Kaushik N Sanji
 */
public class NewsURLGenerator {

    //Constant used for logs
    private static final String LOG_TAG = NewsURLGenerator.class.getSimpleName();
    //Base URL Constant used for the News API Calls
    private static final String NEWS_BASE_URL = "https://content.guardianapis.com";
    //Constant for the International Path Segment of the Base URL
    private static final String INTERNATIONAL_PATH_SEGMENT = "international";
    //Constant for the Search Path Segment of the Base URL
    private static final String SEARCH_PATH_SEGMENT = "search";
    //Constants for the API KEY Query Parameter used for requesting data
    private static final String API_KEY_PARAM_NAME = "api-key";
    private static final String API_KEY_PARAM_VALUE = "test";
    //Stores reference to App Context
    private Context mAppContext;
    //Stores reference to the Preferences used by the App
    private SharedPreferences mSharedPreferences;
    //Stores whether the URL Generation is required for only Article Count purposes
    private boolean mCountMode;

    /**
     * Constructor that gives the instance of {@link NewsURLGenerator}
     * (This constructor is not meant for Article Count purpose)
     *
     * @param context is the Context of the Activity/Fragment or App
     */
    public NewsURLGenerator(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        mCountMode = false;
    }

    /**
     * Constructor that gives the instance of {@link NewsURLGenerator}
     * (This constructor is meant for Article Count purpose, but can be used for the
     * Generic purpose by passing False for #countMode)
     *
     * @param context   is the Context of the Activity/Fragment or App
     * @param countMode Signifies whether the URL Generator will be used for Article Count Purpose
     *                  or the Generic Purpose
     *                  <br/><b>TRUE</b> for Count Purpose; <b>FALSE</b> for Generic Purpose
     */
    public NewsURLGenerator(Context context, boolean countMode) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        mCountMode = countMode;
    }

    /**
     * Method that prepares and returns a URL based on the News 'Section ID' being passed
     *
     * @param sectionIdStr is the Section ID of the News content required
     * @return URL object for the News Section
     */
    public URL createSectionURL(final String sectionIdStr) {
        //Returning NULL when the 'Section ID' string is empty
        if (TextUtils.isEmpty(sectionIdStr)) {
            return null;
        }

        //Forming the Base URI with the BASE URL constant
        Uri uriObject = Uri.parse(NEWS_BASE_URL);

        //Retrieving the URI Builder
        Uri.Builder uriBuilder = uriObject.buildUpon();

        //Appending Query Parameters based on the 'Section ID' string
        if (sectionIdStr.equals(mAppContext.getString(R.string.top_stories_section_id))
                || sectionIdStr.equals(mAppContext.getString(R.string.most_visited_section_id))) {
            appendInternationalParams(sectionIdStr, uriBuilder);
        } else {
            appendSectionGenericParams(sectionIdStr, uriBuilder);
        }

        //Appending the API KEY for the request
        appendApiKeyParam(uriBuilder);

        //Preparing and returning the URL Object formed
        return buildURL(uriBuilder);
    }

    /**
     * Method that prepares and returns a URL for the Search query being passed
     *
     * @param searchQueryStr is a String containing the Search query for the News required
     * @return URL object for the Search query passed
     */
    public URL createSearchURL(final String searchQueryStr) {
        //Returning NULL when the Search Query passed is empty
        if (TextUtils.isEmpty(searchQueryStr)) {
            return null;
        }

        //Forming the Base URI with the BASE URL constant
        Uri uriObject = Uri.parse(NEWS_BASE_URL);

        //Retrieving the URI Builder
        Uri.Builder uriBuilder = uriObject.buildUpon();

        //Appending Query Parameters based on the search query
        appendSearchGenericParams(searchQueryStr, uriBuilder);

        //Appending the API KEY for the request
        appendApiKeyParam(uriBuilder);

        //Preparing and returning the URL Object formed
        return buildURL(uriBuilder);
    }

    /**
     * Method that forms the URL from the URI built
     *
     * @param uriBuilder is the Builder of URI which has some prebuilt URI
     * @return URL object formed using the URI built
     */
    private URL buildURL(Uri.Builder uriBuilder) {
        //Forming the URL using the URI built
        URL urlObject = null;
        try {
            urlObject = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error occurred while forming the URL\n", e);
        }

        //Returning the URL Object formed
        return urlObject;
    }

    /**
     * Method that appends the Query Parameter 'api-key=test' to the URI
     *
     * @param uriBuilder is the Builder of URI which has some prebuilt URI
     */
    private void appendApiKeyParam(Uri.Builder uriBuilder) {
        uriBuilder.appendQueryParameter(API_KEY_PARAM_NAME, API_KEY_PARAM_VALUE);
    }

    /**
     * Method that appends the 'International' segment to the URI Path
     * and its related Query Parameters based on the 'Section ID' passed
     *
     * @param sectionIdStr is the Section ID of the News content required
     * @param uriBuilder   is the Builder of URI which has some prebuilt URI
     */
    @SuppressLint("SimpleDateFormat")
    private void appendInternationalParams(final String sectionIdStr, Uri.Builder uriBuilder) {
        //Appending the 'international' segment to the URI Path
        uriBuilder.appendPath(INTERNATIONAL_PATH_SEGMENT);

        //Appending the 'from-date' preference setting: START
        String fromDateKeyStr = mAppContext.getString(R.string.pref_start_period_manual_key);
        long fromDateInMillis = mSharedPreferences.getLong(fromDateKeyStr, Calendar.getInstance().getTimeInMillis());
        uriBuilder.appendQueryParameter(fromDateKeyStr,
                (new SimpleDateFormat("yyyy-MM-dd")).format(new Date(fromDateInMillis)));
        //Appending the 'from-date' preference setting: END

        //Appending the list of fields for filtering required content from the result: START
        String[] fieldsFilterArray = mAppContext.getResources().getStringArray(R.array.show_fields_filter);
        uriBuilder.appendQueryParameter(mAppContext.getString(R.string.show_fields_parameter),
                TextUtils.join(",", fieldsFilterArray));
        //Appending the list of fields for filtering required content from the result: END

        //Appending a special parameter based on the 'Section ID' passed
        if (sectionIdStr.equals(mAppContext.getString(R.string.top_stories_section_id))) {
            //Appending parameter to only list the Editors' Picks which will be the Top Stories
            uriBuilder.appendQueryParameter(mAppContext.getString(R.string.editor_picks_parameter),
                    "true");
        } else if (sectionIdStr.equals(mAppContext.getString(R.string.most_visited_section_id))) {
            //Appending parameter to only list the Most Viewed content which will be
            //the Most Visited News
            uriBuilder.appendQueryParameter(mAppContext.getString(R.string.most_viewed_parameter),
                    "true");
        }
    }

    /**
     * Method that appends the Section's segment to the URI Path
     * and its related Query Parameters for the 'Section ID' passed
     *
     * @param sectionIdStr is the Section ID of the News content required
     * @param uriBuilder   is the Builder of URI which has some prebuilt URI
     */
    @SuppressLint("SimpleDateFormat")
    private void appendSectionGenericParams(final String sectionIdStr, Uri.Builder uriBuilder) {
        //Appending the passed 'Section ID' as a segment to the URI Path
        uriBuilder.appendPath(sectionIdStr);
        //Appending all the Query Parameters
        appendGenericQueryParams(uriBuilder);
    }

    /**
     * Method that appends the Search segment to the URI Path
     * and its related Query Parameters for the Search query passed
     *
     * @param searchQueryStr is a String containing the Search query for the News required
     * @param uriBuilder     is the Builder of URI which has some prebuilt URI
     */
    @SuppressLint("SimpleDateFormat")
    private void appendSearchGenericParams(final String searchQueryStr, Uri.Builder uriBuilder) {
        //Appending the 'search' segment to the URI Path
        uriBuilder.appendPath(SEARCH_PATH_SEGMENT);
        //Appending the Search query parameter for the Search query passed
        uriBuilder.appendQueryParameter("q", searchQueryStr);
        //Appending all the Query Parameters
        appendGenericQueryParams(uriBuilder);
    }

    /**
     * Method that appends all the Generic Query Parameters to the URI
     *
     * @param uriBuilder is the Builder of URI which has some prebuilt URI
     */
    @SuppressLint("SimpleDateFormat")
    private void appendGenericQueryParams(Uri.Builder uriBuilder) {
        //Appending the 'from-date' preference setting: START
        String fromDateKeyStr = mAppContext.getString(R.string.pref_start_period_manual_key);
        long fromDateInMillis = mSharedPreferences.getLong(fromDateKeyStr, Calendar.getInstance().getTimeInMillis());
        uriBuilder.appendQueryParameter(
                fromDateKeyStr,
                (new SimpleDateFormat("yyyy-MM-dd")).format(new Date(fromDateInMillis))
        );
        //Appending the 'from-date' preference setting: END

        //Appending the list of fields for filtering required content from the result: START
        String[] fieldsFilterArray = mAppContext.getResources().getStringArray(R.array.show_fields_filter);
        uriBuilder.appendQueryParameter(
                mAppContext.getString(R.string.show_fields_parameter),
                TextUtils.join(",", fieldsFilterArray)
        );
        //Appending the list of fields for filtering required content from the result: END

        //Appending the 'order-by' preference setting: START
        String orderByKeyStr = mAppContext.getString(R.string.pref_sort_by_key);
        uriBuilder.appendQueryParameter(
                orderByKeyStr,
                mSharedPreferences.getString(orderByKeyStr,
                        mAppContext.getString(R.string.pref_sort_by_default))
        );
        //Appending the 'order-by' preference setting: END

        //Appending the 'order-date' preference setting: START
        String orderDateKeyStr = mAppContext.getString(R.string.pref_sort_on_key);
        uriBuilder.appendQueryParameter(
                orderDateKeyStr,
                mSharedPreferences.getString(orderDateKeyStr,
                        mAppContext.getString(R.string.pref_sort_on_default))
        );
        //Appending the 'order-date' preference setting: END

        //Appending the 'page-size' preference setting: START
        String pageSizeKeyStr = mAppContext.getString(R.string.pref_items_per_page_key);
        uriBuilder.appendQueryParameter(
                pageSizeKeyStr,
                String.valueOf(
                        mSharedPreferences.getInt(pageSizeKeyStr,
                                mAppContext.getResources().getInteger(R.integer.pref_items_per_page_default_value))
                )
        );
        //Appending the 'page-size' preference setting: END

        //Appending the 'page' preference setting: START
        String pageIndexKeyStr = mAppContext.getString(R.string.pref_page_index_key);
        if (mCountMode) {
            //Defaulting the 'page' setting value to 1, for Count purpose
            uriBuilder.appendQueryParameter(
                    pageIndexKeyStr,
                    String.valueOf(mAppContext.getResources().getInteger(R.integer.pref_page_index_default_value))
            );
        } else {
            //Using the current 'page' setting value for Generic purpose
            uriBuilder.appendQueryParameter(
                    pageIndexKeyStr,
                    String.valueOf(
                            mSharedPreferences.getInt(pageIndexKeyStr,
                                    mAppContext.getResources().getInteger(R.integer.pref_page_index_default_value))
                    )
            );
        }
        //Appending the 'page' preference setting: END
    }
}
