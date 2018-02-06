package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.util.Log;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Utility Class that fires a request to the News Section URL passed
 * and extracts information from the response to build a {@link NewsSectionInfo} object
 * for the News 'Section ID'.
 *
 * @author Kaushik N Sanji
 */
public class NewsSectionInfoParserUtility {

    //Constant used for logs
    private static final String LOG_TAG = NewsSectionInfoParserUtility.class.getSimpleName();

    /**
     * Method that makes a request to the News Section URL passed
     * and extracts information from the response
     * and builds the {@link NewsSectionInfo} object for the News 'Section ID'.
     *
     * @param sectionIdStr     is the Section ID of the News to be extracted for info
     * @param appContext       is the Application {@link Context}
     * @param requestURLObject is the URL object for the News section
     * @return {@link NewsSectionInfo} object updated the News Section related information
     */
    public static NewsSectionInfo getNewsSectionInfo(final String sectionIdStr, final Context appContext, final URL requestURLObject) {
        //Returning Null when there is no URL
        if (requestURLObject == null) {
            return null;
        }

        //Firing a Request to the URL and retrieving the JSON Response
        String jsonResponse = JsonUtility.getJsonResponse(requestURLObject);

        //Returning Null when there is no JSON Response
        if (jsonResponse == null) {
            return null;
        }

        //Creating the NewsSectionInfo object
        NewsSectionInfo newsSectionInfo = new NewsSectionInfo();

        try {
            //Retrieving the root JSON Object
            JSONObject rootJsonObject = new JSONObject(jsonResponse);

            //Retrieving the 'response' JSON Object
            JSONObject responseJsonObject = rootJsonObject.getJSONObject("response");

            //Retrieving the article count based on the Section: START
            if (sectionIdStr.equals(appContext.getString(R.string.top_stories_section_id))) {
                //For the "Top Stories" Section

                //Retrieving the JSON Array of results
                JSONArray resultsJsonArray = responseJsonObject.getJSONArray(sectionIdStr);
                //Getting the count from the array
                int articleCount = resultsJsonArray.length();

                //Updating the NewsSectionInfo Object with the details
                newsSectionInfo.setSectionId(sectionIdStr);
                newsSectionInfo.setSectionName(appContext.getString(R.string.top_stories_section_name));
                newsSectionInfo.setNewsArticleCount(articleCount);

            } else if (sectionIdStr.equals(appContext.getString(R.string.most_visited_section_id))) {
                //For the "Most Visited" section

                //Retrieving the JSON Array of results
                JSONArray resultsJsonArray = responseJsonObject.getJSONArray(sectionIdStr);
                //Getting the count from the array
                int articleCount = resultsJsonArray.length();

                //Updating the NewsSectionInfo Object with the details
                newsSectionInfo.setSectionId(sectionIdStr);
                newsSectionInfo.setSectionName(appContext.getString(R.string.most_visited_section_name));
                newsSectionInfo.setNewsArticleCount(articleCount);

            } else {
                //For other News sections

                //Retrieving the count from the Response JSON
                int articleCount = responseJsonObject.getInt("total");
                //Retrieving the 'section' JSON Object for the Section related details
                JSONObject sectionJsonObject = responseJsonObject.getJSONObject("section");

                //Updating the NewsSectionInfo Object with the details
                newsSectionInfo.setSectionId(sectionJsonObject.getString("id"));
                newsSectionInfo.setSectionName(sectionJsonObject.getString("webTitle"));
                newsSectionInfo.setNewsArticleCount(articleCount);
            }
            //Retrieving the article count based on the Section: END

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error occurred while parsing the JSON Response\n", e);
        }

        //Returning the NewsSectionInfo object for the News Section URL
        return newsSectionInfo;
    }

}