package com.example.rentalpropertyapp;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalpropertyapp.databinding.ItemNotificationBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationItem> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification);
    }

    public NotificationAdapter(List<NotificationItem> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);

        holder.binding.notificationMessage.setText(notification.getMessage());
        holder.binding.notificationTime.setText(notification.getCreatedAt());

        if ("rent".equals(notification.getRequest())) {
            holder.binding.notificationIcon.setImageResource(R.drawable.ic_person);
        } else if ("visit".equals(notification.getRequest())) {
            holder.binding.notificationIcon.setImageResource(R.drawable.ic_calendar);
        }

        if (!notification.isReadStatus()) {
            holder.binding.notificationMessage.setTypeface(Typeface.DEFAULT_BOLD);
            holder.binding.notificationTime.setTypeface(Typeface.DEFAULT_BOLD);
            holder.binding.unreadIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.binding.notificationMessage.setTypeface(Typeface.DEFAULT);
            holder.binding.notificationTime.setTypeface(Typeface.DEFAULT);
            holder.binding.unreadIndicator.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
