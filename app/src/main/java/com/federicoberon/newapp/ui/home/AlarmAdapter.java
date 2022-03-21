package com.federicoberon.newapp.ui.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.databinding.ItemAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.DateUtils;
import com.federicoberon.newapp.utils.StringHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{

    private List<AlarmEntity> alarms;
    private final View parentView;
    private final EventListener listener;
    private AlarmEntity mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private ArrayList<AlarmEntity> selectedAlarmsList;

    public Context getContext() {
        return parentView.getContext();
    }

    public interface EventListener {
        void onEvent(AlarmEntity alarmEntity);
        void onItemClicked(View view, int position, boolean duplicate);
        void onItemLongClicked(View view, int position);
        void onDeleteMenuClicked(int position);
        void onDeleteSwiped(AlarmEntity alarmEntity);
        void onPreviewMenuClicked(AlarmEntity alarmEntity);
    }

    public AlarmAdapter(View root, EventListener listener) {
        this.alarms = Collections.emptyList();
        this.listener = listener;
        parentView = root;
        this.selectedAlarmsList = new ArrayList<>();
    }

    public void setSelectedAlarmsList(ArrayList<AlarmEntity> selectedAlarmsList) {
        this.selectedAlarmsList = new ArrayList<>();
        this.selectedAlarmsList = selectedAlarmsList;
    }

    public void addSelectedAlarmsList(AlarmEntity selected) {
        this.selectedAlarmsList.add(selected);
    }

    public void removeSelectedAlarmsList(AlarmEntity selected) {
        this.selectedAlarmsList.remove(selected);
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlarmBinding itemAlarmBinding = ItemAlarmBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AlarmViewHolder(itemAlarmBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, @SuppressLint("RecyclerView") int position) {

        AlarmEntity alarmEntity = alarms.get(position);

        // set correct day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmEntity.getAlarmDate());
        calendar.set(Calendar.DAY_OF_MONTH, DateUtils.isTomorrow(alarmEntity.getHour(),
                alarmEntity.getMinute()));
        alarmEntity.setAlarmDate(calendar.getTime());


        holder.onBind(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(v, position, false);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClicked(v, position);
                return true;
            }
        });

        holder.mBinding.switchAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = holder.mBinding.switchAlarm.isChecked();
                // change state
                alarmEntity.setStarted(isChecked);
                if(isChecked){
                    // change day
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(alarmEntity.getAlarmDate());
                    calendar.set(Calendar.DAY_OF_MONTH, DateUtils.isTomorrow(alarmEntity.getHour(),
                            alarmEntity.getMinute()));

                    alarmEntity.setAlarmDate(calendar.getTime());

                    // programarla
                    AlarmManager.schedule(view.getContext(), alarmEntity);

                    // change visible text for the card
                    String textToSet = StringHelper.getFormatedAlarmDate(
                            view.getContext(), alarmEntity);
                    holder.mBinding.textCardDays.setText(textToSet);

                }else
                    // cancelarla
                    AlarmManager.dismissAlarm(view.getContext(), alarmEntity);

                // update database
                listener.onEvent(alarmEntity);
            }
        });

        // lo contiene y no tiene el fondo correcto
        if(selectedAlarmsList.contains(alarmEntity)) {
            // is selected
            // disable context menu for the item
            holder.mBinding.imageMenu.setEnabled(false);
            holder.mBinding.imageMenu.setColorFilter((ContextCompat.getColor(getContext(), R.color.colorGray)));

            holder.mBinding.itemCardView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.colorCardChecked)));
            holder.mBinding.checkedIcon.setVisibility(View.VISIBLE);

            //move hour
            ObjectAnimator animation_right_hour = ObjectAnimator.ofFloat(holder.mBinding.textCardHour, "translationX", 100f);
            animation_right_hour.setDuration(400);

            //move title
            ObjectAnimator animation_right_title = ObjectAnimator.ofFloat(holder.mBinding.textCardTitle, "translationX", 100f);
            animation_right_title.setDuration(400);

            //move icon
            ObjectAnimator animation_right_icon = ObjectAnimator.ofFloat(holder.mBinding.checkedIcon, "translationX", 120f);
            animation_right_icon.setDuration(400);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animation_right_hour).with(animation_right_icon).with(animation_right_title);
            animatorSet.start();

        }else{
            // disable context menu for the item
            holder.mBinding.imageMenu.setEnabled(true);
            holder.mBinding.imageMenu.setColorFilter((ContextCompat.getColor(getContext(), R.color.colorGrayLight)));
            holder.mBinding.itemCardView.setCardBackgroundColor((ContextCompat.getColor(getContext(), R.color.transparent)));

            //move hour
            ObjectAnimator animation_left_hour = ObjectAnimator.ofFloat(holder.mBinding.textCardHour, "translationX", 0f);
            animation_left_hour.setDuration(400);

            //move title
            ObjectAnimator animation_left_title = ObjectAnimator.ofFloat(holder.mBinding.textCardTitle, "translationX", 0f);
            animation_left_hour.setDuration(400);

            //move title
            ObjectAnimator animation_left_icon = ObjectAnimator.ofFloat(holder.mBinding.checkedIcon, "translationX", 0f);
            animation_left_icon.setDuration(400);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animation_left_hour).with(animation_left_title).with(animation_left_icon);
            animatorSet.start();

            holder.mBinding.checkedIcon.setVisibility(View.GONE);//transparent

        }
    }

    public void deleteItem(int position) {
        listener.onDeleteSwiped(alarms.get(position));
        mRecentlyDeletedItem = alarms.get(position);
        mRecentlyDeletedItemPosition = position;
        alarms.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        //Snackbar snackbar = Snackbar.make(parentView, R.string.snack_bar_text,
        Snackbar snackbar = Snackbar.make(((MainActivity)getContext()).getBinding().appBarMain.getRoot(), R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAnchorView(parentView.findViewById(R.id.fab));
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        alarms.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

    private boolean containsTrue(boolean[] daysOfWeek) {
        for(boolean b : daysOfWeek) if(b) return true;
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = new ArrayList<>();
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (alarms == null) {
            return 0;
        }
        return alarms.size();
    }

    public void removeAlarmFromList(int position){
        alarms.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,alarms.size());
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final ItemAlarmBinding mBinding;

        public AlarmViewHolder(ItemAlarmBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void onBind(int position) {
            AlarmEntity alarm = alarms.get(position);

            mBinding.textCardTitle.setText(alarm.getTitle());

            itemView.setTag(alarm.getId());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarm.getAlarmDate());
            mBinding.textCardHour.setText(String.format("%s:%s",
                    DateFormat.format("HH", calendar),
                    DateFormat.format("mm", calendar)));

            String textToSet = StringHelper.getFormatedAlarmDate(
                    itemView.getContext(), alarm);
            mBinding.textCardDays.setText(textToSet);
            mBinding.switchAlarm.setChecked(alarm.isStarted());
            mBinding.imageMenu.setOnClickListener(view -> showPopupMenu(position));
        }

        private void showPopupMenu(int position) {
            Context wrapper = new ContextThemeWrapper(itemView.getContext(), R.style.style);

            // inflate menu
            PopupMenu popup = new PopupMenu(wrapper,mBinding.imageMenu);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.cardview_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new CardItemClickListener(itemView, position));
            popup.show();
        }
    }

    public class CardItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private final View itemView;
        private int position;
        public CardItemClickListener(View itemView, int position) {
            this.itemView = itemView;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {

                case R.id.previewCard:
                    listener.onPreviewMenuClicked(alarms.get(position));
                    return true;
                case R.id.duplicateCard:
                    listener.onItemClicked(itemView, position, true);
                    return true;
                case R.id.deleteCard:
                    listener.onDeleteMenuClicked(position);
                default:
                    return false;
            }
        }
    }
}
