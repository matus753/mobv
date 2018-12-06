package mobv.fei.stu.sk.mobv;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.model.User;
import mobv.fei.stu.sk.mobv.model.ViewHolderItem;
import mobv.fei.stu.sk.mobv.model.ViewHolderProfile;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PostsAdapter";

    private List<Object> data;
    private SimpleExoPlayer exoPlayer;
    private Context context;

    private static final String COUNT_OF_POSTS = "Počet príspevkov: ";
    private static final String TIME_OF_REGISTRATION = "Čas registrácie: ";

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? position : 1;
    }
    /**
     * Initialize the dataset of the Adapter.
     */
    public PostsAdapter(List<Object> data, SimpleExoPlayer exoPlayer, Context context) {
        this.data = data;
        this.exoPlayer = exoPlayer;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {

            case 0: return new ViewHolderProfile(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile, viewGroup, false));

            case 1: return new ViewHolderItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false));

            default: return new ViewHolderItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false)); //TODO: error page
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        SimpleDateFormat fomratter = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss");
        switch (viewHolder.getItemViewType()) {
            case 0: {
                User user = (User) data.get(position);
                ViewHolderProfile holderProfile = (ViewHolderProfile) viewHolder;

                if(user != null) {
                    holderProfile.getName().setText(user.getUsername());
                    holderProfile.getCount().setText(COUNT_OF_POSTS.concat(user.getNumberOfPosts().toString()));
                    holderProfile.getRegistrationDate().setText(TIME_OF_REGISTRATION.concat(fomratter.format(user.getDate())));
                }
                break;

            }
            case 1: {
                Post post = (Post) data.get(position);
                ViewHolderItem item = (ViewHolderItem) viewHolder;
                handlePost(post, item, fomratter);
                break;
                }
        }
    }

    private void handlePost(Post post, ViewHolderItem item,SimpleDateFormat fomratter) {
        item.getName().setText(post.getUsername());
        item.getDate().setText(fomratter.format(post.getDate()));

        if ("image".equals(post.getType())) {
            item.getImageView().setVisibility(View.VISIBLE);
            item.getPlayerView().setVisibility(View.GONE);
            Picasso.get()
                    .load(post.getUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_error_black_24dp)
                    // To fit image into imageView
                    .fit()
                    // To prevent fade animation
                    .noFade()
                    .into(item.getImageView());
        } else {
            item.getImageView().setVisibility(View.GONE);
            item.getPlayerView().setVisibility(View.VISIBLE);

            item.getPlayerView().setPlayer(exoPlayer);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "social-mobv"));
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(post.getUrl()));
            exoPlayer.prepare(videoSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            exoPlayer.setPlayWhenReady(true);
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Object> data){
        this.data = data;
    }
}
