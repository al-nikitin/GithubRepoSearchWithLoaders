package com.appshop162.githubreposearch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appshop162.githubreposearch.NetworkUtils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private EditText editText;
    private TextView tvOutput, tvError;
    private ProgressBar loadingIndicator;
    private Button buttonSearch;
    private String query;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int GITHUB_SEARCH_LOADER = 22;

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

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
        }

        //LoaderManager.getInstance(this).initLoader(GITHUB_SEARCH_LOADER, null, null);
    }

    private void makeGithubSearchQuery() {
        query = editText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(query);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchUrl.toString());
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);

        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);

    }

    private void showJsonDataView() {
        tvOutput.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        tvError.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (bundle == null) return;
                tvError.setVisibility(View.INVISIBLE);
                tvOutput.setVisibility(View.INVISIBLE);
                loadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                try {
                    Thread.sleep(9);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String searchQueryUrlString = bundle.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || searchQueryUrlString.equals("")) return null;
                try {
                    URL githubURL = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(githubURL);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        if (s != null && !s.equals("")) {
            showJsonDataView();
            tvOutput.setText(s);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        System.out.println("RESET");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY_URL_EXTRA, query);
    }
}
