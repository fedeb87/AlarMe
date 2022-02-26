package com.federicoberon.newapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.databinding.ItemAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.ui.addalarm.AddAlarmFragment;
import com.federicoberon.newapp.utils.StringHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.AlarmViewHolder>{

    private List<AlarmEntity> alarms;
    private final View parentView;

    public MilestoneAdapter(View root) {
        this.alarms = Collections.emptyList();
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

        //todo listener para el switch

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<AlarmEntity> alarms) {
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
            //mBinding.textCardDays.setText(StringHelper.getFormatedAlarmDate(parentView.getContext(), alarm));

            mBinding.switchAlarm.setChecked(alarm.isStarted());

            mBinding.imageMenu.setOnClickListener(view -> showPopupMenu(position));
        }

        private void showPopupMenu(int position) {

            Context wrapper = new ContextThemeWrapper(itemView.getContext(), R.style.style);

            // inflate menu
            //PopupMenu popup = new PopupMenu(itemView.getContext(),mBinding.imageMenu, Gravity.NO_GRAVITY, 0, R.style.BasePopupMenu);
            PopupMenu popup = new PopupMenu(wrapper,mBinding.imageMenu);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.cardview_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new CardItemClickListener(position));
            popup.show();
        }
    }
}
