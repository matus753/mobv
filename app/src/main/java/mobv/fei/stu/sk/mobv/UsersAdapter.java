package mobv.fei.stu.sk.mobv;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.model.User;
import mobv.fei.stu.sk.mobv.model.ViewHolderUser;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class UsersAdapter extends RecyclerView.Adapter<ViewHolderUser> {
    private static final String TAG = "UsersAdapter";

    private List<Post> posts;

    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SimpleExoPlayer exoPlayer;

    private final FragmentActivity activity;
    private FirebaseFirestore db;
    private boolean done;

    /**
     * Initialize the dataset of the Adapter.
     */
    public UsersAdapter(List<Post> dataSet, FragmentActivity activity, SimpleExoPlayer exoPlayer) {
        posts = dataSet;
        this.activity = activity;
        this.exoPlayer = exoPlayer;

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }


    @Override
    public ViewHolderUser onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolderUser(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderUser viewHolder, final int position) {
        final Post post = posts.get(position);
        mRecyclerView = viewHolder.getRecyclerView();

        if(viewHolder.getCreating()) {
            try {
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(mRecyclerView);
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
        }else {
            viewHolder.setCreating(false);
        }


        db.collection("users").document(post.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(final DocumentSnapshot documentSnapshot) {
                 db.collection("posts").whereEqualTo("userid", post.getUserid()).orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Object> adapterData = new ArrayList<>();
                                adapterData.add(documentSnapshot.toObject(User.class).withId(documentSnapshot.getId()));


                                int scrollTo = 0, position = 0;
                                if (task.getResult() != null) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                            if(document.getId().equals(post.getId())){
                                                scrollTo = position;
                                            }
                                            adapterData.add(document.toObject(Post.class));
                                            position++;
                                        }
                                    }
                                }


                                mAdapter = new PostsAdapter(adapterData, exoPlayer, activity);
                                mRecyclerView.setAdapter(mAdapter);
                                mLayoutManager = new LinearLayoutManager(activity);
                                mRecyclerView.setLayoutManager(mLayoutManager);

                                mRecyclerView.scrollToPosition(scrollTo + 1);
                            }
                        }
                    });

                 }
             });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
