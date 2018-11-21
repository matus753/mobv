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
public class ViewHolderProfile extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolderItem";

    private final TextView name;
    private final TextView registrationDate;
    private final TextView count;

    public ViewHolderProfile(View v) {
        super(v);
        // Define click listener for the ViewHolder's View.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
            }
        });
        count = v.findViewById(R.id.count);
        name = v.findViewById(R.id.name);
        registrationDate = v.findViewById(R.id.registration_date);
    }

    public TextView getName() {
        return name;
    }

    public TextView getRegistrationDate() {
        return registrationDate;
    }

    public TextView getCount() {
        return count;
    }
}
