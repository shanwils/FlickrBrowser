package shannonwilson.example.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable,
                            RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false); //don't want home button on main activity

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Create instance of RecyclerItemClickListner class and add it as a touch listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);

//        GetRawData getRawData = new GetRawData(this);
//        getRawData.execute("https://www.flickr.com/services/feeds/photos_public.gne?tags=android,nougat,sdk&tagmode=any&format=json&nojsoncallback=1");

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY,"");

        if(queryResult.length() > 0){
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
            getFlickrJsonData.execute(queryResult);
        }

        //data retrieval code from earlier in the app. Did not want to delete it.
//        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
//        //getFlickrJsonData.executeOnSameThread("android, nougat");
//        getFlickrJsonData.execute("android,nougat");
        Log.d(TAG, "onResume: ends");
    }

//    @Override
//    protected void onPostResume() {
//        Log.d(TAG, "onPostResume: starts");
//        super.onPostResume();
//        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
//        //getFlickrJsonData.executeOnSameThread("android, nougat");
//        getFlickrJsonData.execute("android,nougat");
//        Log.d(TAG, "onPostResume: ends");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_search){
//            Intent intent = new Intent(this, SearchActivity.class);
//            startActivity(intent);
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected() returned: returned");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status){
        Log.d(TAG, "onDataAvailable: start");
        if (status == DownloadStatus.OK){
            mFlickrRecyclerViewAdapter.loadNewData(data);
        } else {
            //download or processing failed
            Log.e(TAG, "onDataAvailable: failed with status " + status);
        }

        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this, "Normal tap at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
//        Toast.makeText(MainActivity.this, "Long tap at position " + position, Toast.LENGTH_SHORT).show();
        //the .class below creates a class literal that can be used to pass the class as a parameter
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        //for putExtra to work, the object must be serializable. that is, it must be able to be stored and retrieved
        //serializing means converting the object to a byte stream that can be reverted back into a copy of the object
        //to accomplish this here, the photo class must implement serializable
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }

    //    //this method is created for demonstration purposes then deleted, so it has been commented out
//    public void onDownloadComplete(String data, DownloadStatus status){
//        if(status == DownloadStatus.OK){
//            Log.d(TAG, "onDownloadComplete: data is " + data);
//        } else{
//            //download or processing failed
//            Log.e(TAG, "onDownloadComplete: failed with status: " + status);
//        }
//    }

}
