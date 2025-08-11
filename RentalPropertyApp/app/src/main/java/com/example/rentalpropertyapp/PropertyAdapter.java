package com.example.rentalpropertyapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private List<Property> propertyList;
    private Context context;

    public PropertyAdapter(List<Property> propertyList, Context context) {
        this.propertyList = propertyList;
        this.context = context;
    }

    public void updateList(List<Property> updatedList) {
        this.propertyList = updatedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.titleTextView.setText(property.getTitle());
        holder.locationTextView.setText(property.getLocation());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
        holder.priceTextView.setText(format.format(property.getPrice()));

        holder.descriptionTextView.setText(property.getDescription());
        holder.bedroomsTextView.setText(property.getBedroom() + " Beds");
        holder.bathroomsTextView.setText(property.getBathroom() + " Baths");
        holder.areaTextView.setText(String.format("%.0f sqft", property.getArea()));

        if (!TextUtils.isEmpty(property.getUsername())) {
            holder.userNameTextView.setVisibility(View.VISIBLE);
            holder.userNameTextView.setText("Listed by " + property.getUsername());
        } else {
            holder.userNameTextView.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(property.getImageUrl())
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_error_image)
                .into(holder.propertyImageView);

        Glide.with(context)
                .load(property.getProfileImage())
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_error_image)
                .into(holder.userProfileImage);

        final int currentPropertyId = property.getId();

        View.OnClickListener bottomSheetLauncher = v -> {
            TenantBottomSheet bottomSheet = TenantBottomSheet.newInstance(currentPropertyId);
            bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "TenantBottomSheet");
        };

        holder.interestedButton.setOnClickListener(bottomSheetLauncher);
        holder.itemView.setOnClickListener(bottomSheetLauncher);
    }

    @Override
    public int getItemCount() {
        return propertyList != null ? propertyList.size() : 0;
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImageView;
        TextView titleTextView, locationTextView, priceTextView, descriptionTextView, userNameTextView;
        TextView bedroomsTextView, bathroomsTextView, areaTextView;
        de.hdodenhof.circleimageview.CircleImageView userProfileImage;
        MaterialButton interestedButton;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.propertyImage);
            titleTextView = itemView.findViewById(R.id.propertyTitle);
            locationTextView = itemView.findViewById(R.id.propertyLocation);
            priceTextView = itemView.findViewById(R.id.propertyPrice);
            descriptionTextView = itemView.findViewById(R.id.propertyDescription);
            userNameTextView = itemView.findViewById(R.id.userName);
            bedroomsTextView = itemView.findViewById(R.id.propertyBedrooms);
            bathroomsTextView = itemView.findViewById(R.id.propertyBathrooms);
            areaTextView = itemView.findViewById(R.id.propertyArea);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            interestedButton = itemView.findViewById(R.id.btnInterested);
        }
    }
}
