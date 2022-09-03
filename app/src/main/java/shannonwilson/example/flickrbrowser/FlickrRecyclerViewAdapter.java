package shannonwilson.example.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


//even in the class declaration we are technically outside the class, which is why it has to
//reference itself in the extension in order to access the image view holder
//having to refer the static nested class with the outer class as outerclass.innerclass from
//outside the outer class is the only difference from the inner class being a top level class
//this way, a reference to the inner class won't be as much of a security liability

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotosList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context,List<Photo> photoList) {
        mContext = context;
        mPhotosList = photoList;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view

        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    //called by recyclerview when it wants new data to be stored so it can be displayed
    //must get item from list and put its values into the viewholder widgets
    //problem here is that we're not storing an actual photo, but a url to get to the image
    //open source library picasso does what we need so we will add it as a dependency
    @Override

    //data is provided to the recyclerView when it calls onBindViewHolder method
    //So in case the search comes up empty, we will put a message in here as well
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if((mPhotosList == null) || (mPhotosList.size() == 0)){
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.empty_photo);
        } else {
            Photo photoItem = mPhotosList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " --> " + position);

            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.title.setText(photoItem.getTitle());
        }
    }
    //the recycler checks the number of photos a lot, so 1 is returned if the list is empty
    //so the recycler doesnt show an empty page instead of the empty search message.
    @Override
    public int getItemCount() {
        return ((mPhotosList != null) && (mPhotosList.size() != 0) ? mPhotosList.size() : 1);
    }

    //When query changes and new data is downloaded, must provide adapter with the new list
    void loadNewData(List<Photo> newPhotos){
        mPhotosList = newPhotos;
        //tells recycler view (or any registered observers) that data has changed
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return((mPhotosList != null && mPhotosList.size() != 0) ? mPhotosList.get(position) : null);
    }

    //the inner class being defined as static makes it the same as if it had its own file
    //the image viewholders are often defined inside the adapter class for packaging convenience.
    //so making it static is safer than having it be a public inner class
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder (View itemView){
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }


    }
}
