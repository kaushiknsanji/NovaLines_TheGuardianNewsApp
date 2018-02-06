package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.TextView;

/**
 * Utility class for Text Appearance related modifications done using classes like {@link Spannable}
 *
 * @author Kaushik N Sanji
 */
public class TextAppearanceUtility {

    /**
     * Method that replaces the drawable identifier string embedded in the TextView's text
     * with its actual image/drawable
     *
     * @param context  is the {@link Context} of the application
     * @param textView is the TextView whose Text is already set
     */
    public static void replaceTextWithImage(Context context, TextView textView) {
        //Retrieving the Text from TextView as a Spannable Text
        Spannable spannable = (Spannable) textView.getText();

        //To keep track of the last character index scanned
        int previousEndIndex = 0;
        //Ensuring that we are using the App's Context
        context = context.getApplicationContext();
        //Read-Only text for iteration
        final String textContentStr = textView.getText().toString();

        do {
            //Retrieving the start and end Index of the identifiers embedded in the TextView's Text,
            //starting with the index of the last scanned character
            int startIndex = TextUtils.indexOf(textContentStr, '[', previousEndIndex);
            int endIndex = TextUtils.indexOf(textContentStr, ']', previousEndIndex);

            if (startIndex > -1 && endIndex > -1) {
                //When the drawable identifier string is present

                //Retrieving the drawable identifier string embedded in the TextView's Text
                String drawableString = TextUtils.substring(textContentStr, startIndex + 1, endIndex);

                //Retrieving the identifier from the current drawable package
                int drawableResId = context.getResources().getIdentifier(drawableString, "drawable", context.getPackageName());
                if (drawableResId == 0) {
                    //If the drawable resource was not found in the current package
                    //then look up the android source package
                    drawableResId = context.getResources().getIdentifier(drawableString, "drawable", "android");
                }

                //When the identifier of the drawable is retrieved
                if (drawableResId > 0) {
                    //Creating an ImageSpan for the drawable found
                    ImageSpan imageSpan = new ImageSpan(context, drawableResId, ImageSpan.ALIGN_BOTTOM);
                    //Updating the ImageSpan onto the spannable at the position of the identified part of the text
                    spannable.setSpan(
                            imageSpan, startIndex, endIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                //Updating the previous end index with the current end index for the next iteration
                previousEndIndex = endIndex + 1;

            } else {
                //Break out when the drawable identifier string is no longer present
                break;
            }

        }
        while (true); //Repeats till entire Text is scanned once for the drawable identifier strings
    }

    /**
     * Method that sets the Html Text content on the TextView passed
     *
     * @param textView      is the TextView on which the Html content needs to be set
     * @param htmlTextToSet is the String containing the Html markup that needs to be set on the TextView
     */
    public static void setHtmlText(TextView textView, String htmlTextToSet) {
        //Initializing a SpannableStringBuilder to build the text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //For Android N and above
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet, Html.FROM_HTML_MODE_COMPACT));
        } else {
            //For older versions
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet));
        }
        //Setting the Spannable Text on TextView with the SPANNABLE Buffer type,
        //for later modification on spannable if required
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

}
