package shannonwilson.example.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

//enum holds the downloading status so we can tell what is happening with data
//is not actually part of the class
enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

//downloading data is an Async Task, so must extend it
class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";

    //m stands for member variable
    private DownloadStatus mDownloadStatus;
    //private final MainActivity mCallBack; - changed to variable below
    private final OnDownloadComplete mCallBack;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callBack){
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallBack = callBack;
    }

    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");

//        onPostExecute(doInBackground(s));
        if (mCallBack != null){
//            String result = doInBackground(s);
//            mCallBack.onDownloadComplete(result, mDownloadStatus);
            mCallBack.onDownloadComplete(doInBackground(s), mDownloadStatus);
        }

        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
       // Log.d(TAG, "onPostExecute: parameter = " + s);
        if (mCallBack != null){
            mCallBack.onDownloadComplete(s, mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");

    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null){
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection)  url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: Response code was " + response);

            StringBuilder result = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Can use while loop or for loop. either works
//            String line;
//            while(null != (line = reader.readLine())){
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                result.append(line).append("\n");
            }

            mDownloadStatus = DownloadStatus.OK;
            return result.toString();


        } catch(MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage());
        } catch(IOException e){
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage());
        } catch(SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?" + e.getMessage());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                } catch(IOException e){
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage() );
                }
            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
