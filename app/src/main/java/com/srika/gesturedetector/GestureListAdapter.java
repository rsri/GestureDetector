package com.srika.gesturedetector;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manan on 2/2/2015.
 */
public class GestureListAdapter extends ArrayAdapter<GestureHolder> {

    private List<GestureHolder> mGestureList;
    private Context mContext;

    public GestureListAdapter(ArrayList<GestureHolder> gestureList, Context context) {
        super(context, R.layout.gestures_list, gestureList);
        this.mGestureList = gestureList;
        this.mContext = context;

    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        GestureViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.gesture_list_item, parent, false);

            // fill the layout with the right values
            TextView idView = (TextView) v.findViewById(R.id.gesture_id);
            TextView nameView = (TextView) v.findViewById(R.id.gesture_name);
            ImageView gestureImageView = (ImageView) v.findViewById(R.id.gesture_image);
            TextView nameViewRef = (TextView) v.findViewById(R.id.gesture_name_ref);

            holder = new GestureViewHolder();
            holder.gestureId = idView;
            holder.gestureName = nameView;
            holder.gestureImage = gestureImageView;
            holder.gestureNameRef = nameViewRef;

            final ImageView mMenuItemButton =  (ImageView)v.findViewById(R.id.menu_item_options);
            mMenuItemButton.setClickable(true);

            v.setTag(holder);
        }
        else {
            holder = (GestureViewHolder) v.getTag();
        }

        GestureHolder gestureHolder = mGestureList.get(position);
        holder.gestureId.setText(String.valueOf(gestureHolder.getGesture().getID()));
        holder.gestureName.setText(gestureHolder.getNaam());
        holder.gestureNameRef.setText(gestureHolder.getNaam());

        try {
            holder.gestureImage.setImageBitmap(gestureHolder.getGesture().toBitmap(30, 30, 3,
                    ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //holder.gestureImage.setImageResource(R.drawable.ic_launcher);

        return v;
    }

    private class GestureViewHolder {
        TextView gestureId;
        TextView gestureName;
        ImageView gestureImage;
        TextView gestureNameRef;

    }
}
