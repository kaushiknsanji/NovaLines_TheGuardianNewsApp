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

package com.example.kaushiknsanji.novalines;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.TextAppearanceUtility;

/**
 * Activity that inflates the layout 'R.layout.activity_about' to display
 * the info related to the App and the developer on click of "About"
 * Navigation Menu in the {@link NewsActivity}
 *
 * @author Kaushik N Sanji
 */
public class AboutActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Constant used as a Request Code for activities launching this
    //with an Intent that waits for a result
    public static final int REQ_CODE = 20;
    //For the custom Toolbar used as ActionBar
    private Toolbar mToolbar;

    //Called when the activity is to be created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Finding the Toolbar
        mToolbar = findViewById(R.id.toolbar_id);
        //Initializing the Toolbar as ActionBar
        setupToolbar();

        //Finding the TextView for Title
        TextView titleTextView = findViewById(R.id.abt_title_text_id);
        //Setting the Font on the Title TextView
        titleTextView.setTypeface(ResourcesCompat.getFont(this, R.font.oldengl));

        //Retrieving the Font used for the text content
        Typeface contentTextTypeface = ResourcesCompat.getFont(this, R.font.quintessential);

        //Finding the TextView for the first line of content
        TextView firstLineTextView = findViewById(R.id.abt_text_1_id);
        //Setting the Html Text
        TextAppearanceUtility.setHtmlText(firstLineTextView, getString(R.string.abt_content_textline_1));
        //Setting the Font
        firstLineTextView.setTypeface(contentTextTypeface);

        //Finding the TextView for the second line of content
        TextView secondLineTextView = findViewById(R.id.abt_text_2_id);
        //Setting the Html Text
        TextAppearanceUtility.setHtmlText(secondLineTextView, getString(R.string.abt_content_textline_2));
        //Setting the Font
        secondLineTextView.setTypeface(contentTextTypeface);

        //Finding the TextView for "powered by" Text
        TextView pwdByTextView = findViewById(R.id.abt_pwd_by_text_id);
        //Setting the Font
        pwdByTextView.setTypeface(ResourcesCompat.getFont(this, R.font.source_code_pro_semibold));

        //Finding the Click views
        View guardianBrandingView = findViewById(R.id.abt_pwd_by_layout_id);
        ImageView udacityImageView = findViewById(R.id.abt_udacity_image_id);
        ImageView githubImageView = findViewById(R.id.abt_github_image_id);
        ImageView linkedinImageView = findViewById(R.id.abt_linkedin_image_id);

        //Registering Listener on Click Views
        guardianBrandingView.setOnClickListener(this);
        udacityImageView.setOnClickListener(this);
        githubImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for the activity to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Marking the result of the request as OK
        setResult(RESULT_OK);
    }

    /**
     * Method that initializes the Toolbar as ActionBar
     * and sets the Title
     */
    private void setupToolbar() {
        //Setting the Toolbar as the ActionBar
        setSupportActionBar(mToolbar);

        //Retrieving the Action Bar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            //Removing the default title text
            supportActionBar.setDisplayShowTitleEnabled(false);
            //Enabling home button to be used for Up navigation
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //Enabling home button
            supportActionBar.setHomeButtonEnabled(true);
        }

        //Finding the TextView to set the Title
        TextView titleTextView = mToolbar.findViewById(R.id.toolbar_title_text_id);
        titleTextView.setText(getString(R.string.about_title_str));
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handling the Menu Item selected based on their Id
        switch (item.getItemId()) {
            case android.R.id.home:
                //Handling the action bar's home/up button
                finish(); //Finishing the Activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Executing action based on View's id
        switch (view.getId()) {
            case R.id.abt_pwd_by_layout_id:
                //For the "powered by" branding layout

                //Opens the webpage for the "Guardian News API"
                IntentUtility.openLink(this, getString(R.string.news_api_link));
                break;
            case R.id.abt_udacity_image_id:
                //For the Udacity ImageView

                //Opens the webpage for the course provided by Udacity
                IntentUtility.openLink(this, getString(R.string.udacity_course_link));
                break;
            case R.id.abt_github_image_id:
                //For the GitHub ImageView

                //Opens my Github profile
                IntentUtility.openLink(this, getString(R.string.github_profile_link));
                break;
            case R.id.abt_linkedin_image_id:
                //For the LinkedIn ImageView

                //Opens my LinkedIn profile
                IntentUtility.openLink(this, getString(R.string.linkedin_profile_link));
                break;
        }
    }

}
