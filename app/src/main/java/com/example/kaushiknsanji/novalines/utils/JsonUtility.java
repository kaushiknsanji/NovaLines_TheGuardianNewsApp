package com.example.kaushiknsanji.novalines.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility Class that deals with firing a request to the URL passed and
 * retrieving the response received.
 *
 * @author Kaushik N Sanji
 */
public class JsonUtility {

    //Constant used for logs
    private final static String LOG_TAG = JsonUtility.class.getSimpleName();

    /**
     * Method that makes a request to the URL passed and returns the response received.
     *
     * @param requestURLObject is a {@link URL} object to which the HTTP GET request call is to be made
     * @return String containing the response received for the call made to the request URL
     */
    public static String getJsonResponse(final URL requestURLObject) {
        String jsonResponse = "";
        try {
            //Making the HTTP Request to retrieve the JSON Response
            jsonResponse = makeHttpGetRequest(requestURLObject);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while closing the URL Stream\n", e);
        }

        //If the response received is null or empty, then return as NULL
        if (jsonResponse == null
                || jsonResponse.trim().length() == 0) {
            return null;
        }

        //Returning the response received
        return jsonResponse;
    }

    /**
     * Method that makes a HTTP GET Request to the URL passed and returns the response received
     *
     * @param urlObject is the {@link URL} to which the HTTP GET Request is to be established
     * @return String containing the response received after the GET Request call was made to the URL
     * @throws IOException while closing connection stream to URL
     */
    private static String makeHttpGetRequest(final URL urlObject) throws IOException {
        //Declaring the JSON Response String and defaulting to empty string
        String jsonResponse = "";

        //Declaring the URLConnection and InputStream objects
        HttpURLConnection urlConnection = null;
        InputStream urlConnectionInputStream = null;

        try {
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setReadTimeout(10000); //10 Seconds Read Timeout
            urlConnection.setRequestMethod("GET"); //Request Method set to GET
            urlConnection.connect(); //Establishing connection

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //When the response is OK(200), then read the response
                urlConnectionInputStream = urlConnection.getInputStream();
                jsonResponse = readStream(urlConnectionInputStream);
            } else {
                //When the response is not OK(200), then log the error code
                Log.e(LOG_TAG, "HTTP GET Request failed with the code " + urlConnection.getResponseCode() + " for URL " + urlObject);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while opening connection to URL\n", e);
        } finally {

            if (urlConnection != null) {
                //Disconnecting in the end if the connection was established
                urlConnection.disconnect();
            }

            if (urlConnectionInputStream != null) {
                //Closing the URL Stream if opened
                urlConnectionInputStream.close();
            }

        }

        //Returning the JSON Response received
        return jsonResponse;
    }

    /**
     * Method that reads and builds the response from the URL Stream
     *
     * @param urlConnectionInputStream is the InputStream object of a URL Connection
     * @return String containing the response read from the URL Stream
     */
    private static String readStream(InputStream urlConnectionInputStream) {
        //StringBuilder instance to build and store the response read
        StringBuilder responseBuilder = new StringBuilder();

        //Reading the response through BufferedReader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnectionInputStream));
        try {
            String readStr = "";
            while ((readStr = bufferedReader.readLine()) != null) {
                //When Not Null, appending the line read to the Builder
                responseBuilder.append(readStr);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while reading the response stream\n", e);
            //resetting the StringBuilder to empty string
            responseBuilder.delete(0, responseBuilder.length());
        }

        //Returning the response read
        return responseBuilder.toString();
    }

}