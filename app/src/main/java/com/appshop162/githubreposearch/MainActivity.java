package com.appshop162.githubreposearch;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appshop162.githubreposearch.NetworkUtils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView tvOutput, tvError;
    private ProgressBar loadingIndicator;
    private Button buttonSearch;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit_text);
        tvOutput = (TextView) findViewById(R.id.text);
        tvError = (TextView) findViewById(R.id.tv_error_message_display);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        buttonSearch = (Button) findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) makeGithubSearchQuery();
            }
        });

//        String s = "";
//        for (int i = 0; i < 99; i++) {
//            s += "1234567890";
//        }
//        tvOutput.setText(s);
    }

    private void makeGithubSearchQuery() {
        String githubQuery = editText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        //System.out.println(githubSearchUrl);
        new GithubQueryTask().execute(githubSearchUrl);
    }

    private void showJsonDataView() {
        // First, make sure the error is invisible
        tvError.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        tvOutput.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // First, hide the currently visible data
        tvOutput.setVisibility(View.INVISIBLE);
        // Then, show the error
        tvError.setVisibility(View.VISIBLE);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchURL = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                showJsonDataView();
                tvOutput.setText(githubSearchResults);
            } else {
                showErrorMessage();
            }
        }
    }
}
