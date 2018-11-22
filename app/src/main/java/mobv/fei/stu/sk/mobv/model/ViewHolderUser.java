package mobv.fei.stu.sk.mobv.model;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import mobv.fei.stu.sk.mobv.R;

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
public class ViewHolderUser extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolderUser";

    private final RecyclerView recyclerView;

    public ViewHolderUser(View v) {
        super(v);
        // Define click listener for the ViewHolder's View.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
            }
        });
        recyclerView = v.findViewById(R.id.posts_recycler_view);

    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
