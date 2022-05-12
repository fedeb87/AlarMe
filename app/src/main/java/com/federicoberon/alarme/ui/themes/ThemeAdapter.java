package com.federicoberon.alarme.ui.themes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.databinding.ItemAlarmBinding;
import com.federicoberon.alarme.databinding.ItemThemeBinding;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.Theme;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemesViewHolder> {

    private List<Theme> themeList;
    private RecyclerViewClickListener mRecyclerViewClickListener;
 
    public class ThemesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemThemeBinding mBinding;
        private RecyclerViewClickListener mListener;
 
        public ThemesViewHolder(ItemThemeBinding binding, RecyclerViewClickListener listener) {

            super(binding.getRoot());
            this.mBinding = binding;
            mListener = listener;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
            // here change theme
            MainActivity.selectedTheme = getAdapterPosition();
            MainActivity.mTheme = MainActivity.mThemeList.get(getAdapterPosition()).getId();
            ThemeAdapter.this.notifyDataSetChanged();
        }

        public void onBind(int position) {
            Theme theme = themeList.get(position);
            mBinding.textCardTitle.setText(String.format("id: %d", theme.getId()));
            mBinding.backgroundCardView.setBackground(theme.getBackground());

        }
    }
 
 
    public ThemeAdapter(List<Theme> themeList, RecyclerViewClickListener recyclerViewClickListener) {
        this.themeList = themeList;
        mRecyclerViewClickListener = recyclerViewClickListener;
    }
 
    @Override
    public ThemesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemThemeBinding itemAlarmBinding = ItemThemeBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ThemesViewHolder(itemAlarmBinding, mRecyclerViewClickListener);
        /*
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theme, parent, false);

        return new ThemesViewHolder(itemView, mRecyclerViewClickListener);*/
    }
 
    @Override
    public void onBindViewHolder(final ThemesViewHolder holder, final int position) {
        Theme theme = themeList.get(position);

        holder.onBind(position);
        // todo change background and colors for each theme, saco la info de theme

    }
 
    @Override
    public int getItemCount() {
        return themeList.size();
    }
}