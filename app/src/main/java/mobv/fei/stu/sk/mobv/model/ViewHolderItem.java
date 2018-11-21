package mobv.fei.stu.sk.mobv.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobv.fei.stu.sk.mobv.R;

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
public class ViewHolderItem extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolderItem";

    private final TextView name;
    private final TextView date;
    private final ImageView imageView;

    public ViewHolderItem(View v) {
        super(v);
        // Define click listener for the ViewHolder's View.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
            }
        });
        imageView = v.findViewById(R.id.imageView);
        name = v.findViewById(R.id.name);
        date = v.findViewById(R.id.date);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getName() {
        return name;
    }

    public TextView getDate() {
        return date;
    }
}
