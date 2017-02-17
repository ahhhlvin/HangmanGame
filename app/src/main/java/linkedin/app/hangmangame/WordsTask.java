package linkedin.app.hangmangame;

/**
 * Created by alvin2 on 2/17/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * On a separate thread, will fetch the text containing all possible hangman words,
 * display a progress bar, and disable the submit & new word buttons and text field until network
 * call has completed.
 */
class WordsTask extends AsyncTask<String, Void, String> {

    WordsTaskListener mListener;

    @Override
    protected String doInBackground(String... strings) {
        return fetchWords(strings);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.equalsIgnoreCase("EXCEPTION CAUGHT")) {
            Log.e("NETWORK CALL STATUS", "Incorrect URL");
        } else {
            mListener.downloadTaskCompleted();
        }
    }

    /**
     * Makes the call to the API to retrieve the list of possible word choices and neatly returns
     * them all in a List of strings.
     *
     * @param url the string URL representing the word dictionary API that the network call will
     *            be made to
     * @return String represents the "SUCCESS" string that will be used to determine whether network
     * call was successful or not
     */
    private String fetchWords(String[] url) {
        try {
            String urlString = url[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();
            Response responses;

            responses = client.newCall(request).execute();
            String jsonData = responses.body().string();

            String[] words = jsonData.split("\\r?\\n");
            mListener.loadWords(Arrays.asList(words));

            responses.body().close();
            return ("SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            return ("EXCEPTION CAUGHT");
        }
    }

    void setWordsTaskListener(MainPresenter presenter) {
        mListener = presenter;
    }

    interface WordsTaskListener {
        void loadWords(List<String> gameWords);

        void downloadTaskCompleted();
    }
}