package com.federicoberon.newapp.ui.home;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.R;

public class CardItemClickListener implements PopupMenu.OnMenuItemClickListener {
    private int position;
    public CardItemClickListener(int position) {
        this.position = position;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //TODO
        /*
        switch (menuItem.getItemId()) {

            case R.id.Not_interasted_catugury:
                String RemoveCategory=mDataSet.get(position).getCategory();
                // mDataSet.remove(position);
                //notifyItemRemoved(position);
                // notifyItemRangeChanged(position,mDataSet.size());

                mySharedPreferences.saveStringPrefs(Constants.REMOVE_CTAGURY,RemoveCategory, MainActivity.context);
                Toast.makeText(MainActivity.context, "Add to favourite", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.No_interasted:
                mDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mDataSet.size());
                Toast.makeText(MainActivity.context, "Done for now", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                mySharedPreferences.deletePrefs(Constants.REMOVE_CTAGURY,MainActivity.context);
            default:
        }
        return false;
    }

         */
        return false;
    }
}
