package mobv.fei.stu.sk.mobv;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.model.User;
import mobv.fei.stu.sk.mobv.model.ViewHolderUser;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class UsersAdapter extends RecyclerView.Adapter<ViewHolderUser> {
    private static final String TAG = "UsersAdapter";

    private List<User> users;


    protected RecyclerView mRecyclerView;
    protected PostsAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private FirebaseFirestore db;

    private final FragmentActivity activity;

    private boolean creating = true;

    /**
     * Initialize the dataset of the Adapter.
     */
    public UsersAdapter(List<User> dataSet, FragmentActivity activity) {
        users = dataSet;
        this.activity = activity;

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
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
        if(viewHolder.getCreating()) {
            Log.d(TAG, "Element " + position + " set.");

            final User user = users.get(position);
            user.setPosts(new ArrayList<>());
            db.collection("posts").whereEqualTo("userid", user.getId()).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            user.getPosts().add(user);

                            if (task.getResult() != null) {
                                if (task.getResult().getDocuments().size() > 0) {
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        user.getPosts().add(document.toObject(Post.class));
                                    }
                                }
                            }

                            mRecyclerView = viewHolder.getRecyclerView();

                            mAdapter = new PostsAdapter(user.getPosts());
                            // Set PostsAdapter as the adapter for RecyclerView.
                            mRecyclerView.setAdapter(mAdapter);

                            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
                            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
                            // elements are laid out.
                            mLayoutManager = new LinearLayoutManager(activity);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            PagerSnapHelper snapHelper = new PagerSnapHelper();
                            snapHelper.attachToRecyclerView(mRecyclerView);

                            mLayoutManager.scrollToPosition(1);
                        }
                    }
                });
            viewHolder.setCreating(false);
        } else {
            mAdapter.notifyDataSetChanged();
            mLayoutManager.scrollToPosition(1);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return users.size();
    }
}