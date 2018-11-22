package mobv.fei.stu.sk.mobv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mobv.fei.stu.sk.mobv.model.Post;
import mobv.fei.stu.sk.mobv.model.User;

public class UsersRecyclerViewFragment extends Fragment {

    private static final String TAG = "UsersRecyclerView";
    private static final int DATASET_COUNT = 60;

    protected RecyclerView mRecyclerView;
    protected UsersAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<User> users = new ArrayList<>();

    private FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.users_recycler_view_frag, container, false);
        rootView.setTag(TAG);

        initDataset(rootView);

        return rootView;
    }


    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset(final View rootView) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                    } else {
                        int position = 0;
                        int currentUserPosition = 0;
                        for (DocumentSnapshot document: documentSnapshots.getDocuments()){
                            User user = document.toObject(User.class);
                            if(user != null) {
                                if(currentUser.getUid().equals(document.getId())){
                                    currentUserPosition = position;
                                }
                                user.setId(document.getId());
                                users.add(user);
                            }
                            position++;
                        }

                        mRecyclerView = rootView.findViewById(R.id.users_recycler_view);

                        mAdapter = new UsersAdapter(users, getActivity());
                        // Set PostsAdapter as the adapter for RecyclerView.
                        mRecyclerView.setAdapter(mAdapter);

                        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
                        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
                        // elements are laid out.
                        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        PagerSnapHelper snapHelper = new PagerSnapHelper();
                        snapHelper.attachToRecyclerView(mRecyclerView);

                        mLayoutManager.scrollToPosition(currentUserPosition);

                        }
                    }
                });

        }
    }
}
