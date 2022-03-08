package com.federicoberon.newapp.ui.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.newapp.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback{

    private final ColorDrawable background;
    private final Drawable icon;
    private final AlarmAdapter mAlarmAdapter;

    public SwipeToDeleteCallback(AlarmAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAlarmAdapter = adapter;
        this.icon = ContextCompat.getDrawable(mAlarmAdapter.getContext(),
                R.drawable.ic_delete_white);
        this.background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAlarmAdapter.deleteItem(position);
    }

    @Override
    public float getSwipeThreshold( RecyclerView.ViewHolder viewHolder){
        return .2f;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView
        double multiplier = 1.5;

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()*2) / 2;
        int iconTop = (int) (itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()*multiplier) / 2);
        int iconBottom = (int) (iconTop + icon.getIntrinsicHeight()*multiplier);

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = (int) (itemView.getLeft() + iconMargin + icon.getIntrinsicWidth()*multiplier);

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = (int) (itemView.getRight() - iconMargin - icon.getIntrinsicWidth()*multiplier);
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            icon.setBounds(0, 0,0, 0);
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
