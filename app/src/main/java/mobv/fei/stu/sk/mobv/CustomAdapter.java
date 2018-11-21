package mobv.fei.stu.sk.mobv;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.model.Profile;
import mobv.fei.stu.sk.mobv.model.ViewHolderItem;
import mobv.fei.stu.sk.mobv.model.ViewHolderProfile;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Object> mDataSet;

    private static final String COUNT_OF_POSTS = "Počet príspevkov: ";
    private static final String TIME_OF_REGISTRATION = "Čas registrácie: ";

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? position : 1;
    }
    /**
     * Initialize the dataset of the Adapter.
     */
    public CustomAdapter(List<Object> dataSet) {
        mDataSet = dataSet;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {

            case 0: return new ViewHolderProfile(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile, viewGroup, false));

            case 1: return new ViewHolderItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false));

            default: return new ViewHolderProfile(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile, viewGroup, false)); //TODO: error page
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        SimpleDateFormat fomratter = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss");

        switch (viewHolder.getItemViewType()) {
            case 0: {
                Profile profile = (Profile) mDataSet.get(position);
                ViewHolderProfile holderProfile = (ViewHolderProfile) viewHolder;

                holderProfile.getName().setText(profile.getUsername());
                holderProfile.getCount().setText(COUNT_OF_POSTS.concat(profile.getNumberOfPosts().toString()));
                holderProfile.getRegistrationDate().setText(TIME_OF_REGISTRATION.concat(fomratter.format(profile.getDate())));
                break;

            }
            case 1: {
                Post post = (Post) mDataSet.get(position);
                ViewHolderItem item = (ViewHolderItem) viewHolder;
                item.getName().setText(post.getUsername());
                item.getDate().setText(fomratter.format(post.getDate()));

                Picasso.get()
                       .load(post.getUrl())
                       .placeholder(R.drawable.ic_launcher_background)
                       .error(R.drawable.ic_error_black_24dp)
                       // To fit image into imageView
                       .fit()
                       // To prevent fade animation
                       .noFade()
                       .into(item.getImageView());
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}