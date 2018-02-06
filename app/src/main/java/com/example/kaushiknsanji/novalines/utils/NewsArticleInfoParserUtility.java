package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.util.Log;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class that fires a request to the News Query URL passed
 * and extracts the News Articles information from the response
 * to build a list of {@link NewsArticleInfo} objects.
 *
 * @author Kaushik N Sanji
 */
public class NewsArticleInfoParserUtility {

    //Constant used for logs
    private static final String LOG_TAG = NewsArticleInfoParserUtility.class.getSimpleName();

    //Stores reference to App Context
    private Context mAppContext;

    //Stores the Number of Pages of data available for the News Query
    private int mPagesCount;

    /**
     * Constructor of {@link NewsArticleInfoParserUtility}
     *
     * @param context is the Context of the Activity/Fragment or App
     */
    public NewsArticleInfoParserUtility(Context context) {
        mAppContext = context.getApplicationContext();
    }

    /**
     * Method that makes a request to the News query URL passed and
     * extracts the News Articles information from the response
     * and builds a list of {@link NewsArticleInfo} objects.
     *
     * @param requestURLObject is the URL object for a particular News query
     * @return List of {@link NewsArticleInfo} objects containing the parsed information of all
     * the News articles retrieved for the News query URL passed
     */
    public List<NewsArticleInfo> getNewsArticleFeed(final URL requestURLObject) {
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

        //Initializing an ArrayList of NewsArticleInfo objects to store the data parsed
        ArrayList<NewsArticleInfo> newsArticleInfoList = new ArrayList<>();

        try {
            //Retrieving the root JSON Object
            JSONObject rootJsonObject = new JSONObject(jsonResponse);

            //Retrieving the 'response' JSON Object
            JSONObject responseJsonObject = rootJsonObject.getJSONObject("response");

            if (responseJsonObject.
                    optJSONArray(mAppContext.getString(R.string.top_stories_section_id)) != null) {
                //For the "Top Stories" News Category

                //Extracting News Articles' data and building the List of NewsArticleInfo objects
                buildNewsArticles(newsArticleInfoList, responseJsonObject,
                        mAppContext.getString(R.string.top_stories_section_id));

            } else if (responseJsonObject.
                    optJSONArray(mAppContext.getString(R.string.most_visited_section_id)) != null) {
                //For the "Most Visited" News Category

                //Extracting News Articles' data and building the List of NewsArticleInfo objects
                buildNewsArticles(newsArticleInfoList, responseJsonObject,
                        mAppContext.getString(R.string.most_visited_section_id));

            } else {
                //For other News categories

                //Extracting News Articles' data and building the List of NewsArticleInfo objects
                buildNewsArticles(newsArticleInfoList, responseJsonObject, "results");
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error occurred while parsing the JSON Response\n", e);
        }

        //Returning the list of NewsArticleInfo objects parsed
        return newsArticleInfoList;
    }

    /**
     * Method that parses the list of News articles found and stores them in a list
     * of {@link NewsArticleInfo} objects
     *
     * @param newsArticleInfoList is an ArrayList of {@link NewsArticleInfo} objects
     *                            for storing the data parsed
     * @param responseJsonObject  is a "response" {@link JSONObject} attribute
     *                            parsed from the JSON response
     * @param resultsJsonAttr     is a {@link String} for the {@link JSONArray} attribute
     *                            that is found to be containing the list of News Articles required
     * @throws JSONException while parsing the JSON Response
     */
    private void buildNewsArticles(ArrayList<NewsArticleInfo> newsArticleInfoList,
                                   JSONObject responseJsonObject,
                                   String resultsJsonAttr) throws JSONException {

        //Retrieving the JSON Array of results
        JSONArray resultsJsonArray = responseJsonObject.getJSONArray(resultsJsonAttr);
        //Retrieving the number of pages
        mPagesCount = responseJsonObject.optInt("pages", 0);
        //Retrieving the number of News Articles found in the active page
        int activePageArticleCount = resultsJsonArray.length();
        //Determining the total number of News Articles
        int totalArticleCount = mPagesCount > 0 ? responseJsonObject.getInt("total") : activePageArticleCount;

        //Iterating over the News articles found to extract the data
        for (int index = 0; index < activePageArticleCount; index++) {
            //Retrieving the current item JSON Object from the result array
            JSONObject itemResultJsonObject = resultsJsonArray.getJSONObject(index);

            //Creating an instance of NewsArticleInfo to store the parsed information
            NewsArticleInfo newsArticleInfo = new NewsArticleInfo();

            //Updating the NewsArticleInfo Object with the details: START
            newsArticleInfo.setSectionId(itemResultJsonObject.getString("sectionId"));
            newsArticleInfo.setSectionName(itemResultJsonObject.getString("sectionName"));
            //Storing the total article count found. This will be used for pagination if required
            newsArticleInfo.setNewsArticleCount(totalArticleCount);
            newsArticleInfo.setPublishedDate(itemResultJsonObject.optString("webPublicationDate", ""));
            newsArticleInfo.setNewsTitle(itemResultJsonObject.getString("webTitle"));
            newsArticleInfo.setWebUrl(itemResultJsonObject.getString("webUrl"));
            newsArticleInfo.setApiUrl(itemResultJsonObject.getString("apiUrl"));
            //Retrieving extended article information from the "fields" JSON Object
            JSONObject newsFieldsJsonObject = itemResultJsonObject.getJSONObject("fields");
            //Extracting the Text following the Headline of the article if present
            newsArticleInfo.setTrailText(newsFieldsJsonObject.optString("trailText", ""));
            //Extracting the Author of the article if present
            newsArticleInfo.setAuthor(newsFieldsJsonObject.optString("byline", ""));
            //Extracting the link to the News Thumbnail if present
            newsArticleInfo.setThumbImageUrl(newsFieldsJsonObject.optString("thumbnail", ""));
            //Updating the NewsArticleInfo Object with the details: END

            //Appending the NewsArticleInfo Object to the list
            newsArticleInfoList.add(newsArticleInfo);
        }

    }

    /**
     * Method that returns the Number of Pages of data available for the News Query
     *
     * @return Number of Pages of data available for the News Query
     */
    public int getPagesCount() {
        return mPagesCount;
    }

}