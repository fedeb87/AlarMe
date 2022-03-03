package com.federicoberon.newapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.databinding.ItemAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.ui.addalarm.AddAlarmFragment;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.DateUtils;
import com.federicoberon.newapp.utils.StringHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{

    private List<AlarmEntity> alarms;
    private final View parentView;
    private final EventListener listener;

    public interface EventListener {
        void onEvent(AlarmEntity alarmEntity);
    }

    public AlarmAdapter(View root, EventListener listener) {
        this.alarms = Collections.emptyList();
        this.listener = listener;
        parentView = root;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlarmBinding itemAlarmBinding = ItemAlarmBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AlarmViewHolder(itemAlarmBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        holder.onBind(position);
        AlarmEntity alarmEntity = alarms.get(position);

        holder.itemView.setOnClickListener(view -> {
            long alarmId = (long) view.getTag();
            Bundle args = new Bundle();

            args.putLong(AddAlarmFragment.KEY_ALARM_ID, alarmId);

            View itemDetailFragmentContainer = parentView!=null?parentView.findViewById(R.id.item_detail_nav_container):null;
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, args);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_addAlarmFragment, args);
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
    }

    private boolean containsTrue(boolean[] daysOfWeek) {
        for(boolean b : daysOfWeek) if(b) return true;
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<AlarmEntity> alarms) {
        this.alarms.clear();
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alarms.size();
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
            popup.setOnMenuItemClickListener(new CardItemClickListener(position));
            popup.show();
        }
    }
}
