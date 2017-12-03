package edu.sjsu.kyle.todobucketlist;

import android.content.Context;

import java.util.List;

/**
 * Created by Kiyeon on 12/3/2017.
 */

public class ArraySwipeAdapter<T> extends com.daimajia.swipe.adapters.ArraySwipeAdapter {
    public ArraySwipeAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ArraySwipeAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArraySwipeAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public ArraySwipeAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArraySwipeAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public ArraySwipeAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

}
