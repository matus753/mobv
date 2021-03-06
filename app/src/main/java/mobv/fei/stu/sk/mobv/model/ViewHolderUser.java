package mobv.fei.stu.sk.mobv.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import mobv.fei.stu.sk.mobv.R;

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
public class ViewHolderUser extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolderUser";

    private final RecyclerView recyclerView;

    private Boolean creating = true;

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

    public Boolean getCreating() {
        return creating;
    }

    public void setCreating(Boolean creating) {
        this.creating = creating;
    }
}
