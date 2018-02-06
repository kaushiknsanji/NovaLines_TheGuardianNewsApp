package com.example.kaushiknsanji.novalines.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;

/**
 * Activity that displays the Settings for the App
 *
 * @author Kaushik N Sanji
 */
public class SettingsActivity extends AppCompatActivity {

    //Constant used as a Request Code for activities launching this
    //with an Intent that waits for a result
    public static final int REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Add and setup the custom Toolbar
        setupToolbar();
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
     * Method that inflates the custom toolbar 'R.layout.toolbar_main' and
     * sets up as the ActionBar
     */
    private void setupToolbar() {
        //Retrieving the current root view
        ViewGroup rootView = findViewById(R.id.settings_fragment_id);

        if (rootView != null) {
            //Inflating the custom Toolbar layout
            View toolbarView = getLayoutInflater().inflate(R.layout.toolbar_main, rootView, false);
            rootView.addView(toolbarView, 0); //Adding toolbar to the top

            //Setting the Toolbar as the custom Action Bar
            Toolbar toolbar = toolbarView.findViewById(R.id.toolbar_root);
            setSupportActionBar(toolbar);

            //Finding the custom Toolbar Title Textview
            TextView toolbarTitleTextView = toolbar.findViewById(R.id.toolbar_title_text_id);
            //Setting the Toolbar Title Text to "Settings"
            toolbarTitleTextView.setText(getString(R.string.settings_title_str));
        }

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

}
