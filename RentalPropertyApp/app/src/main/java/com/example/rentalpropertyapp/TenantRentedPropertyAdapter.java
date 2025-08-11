package com.example.rentalpropertyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TenantRentedPropertyAdapter extends RecyclerView.Adapter<TenantRentedPropertyAdapter.ViewHolder> {

    private List<TenantRentedProperty> rentedProperties;
    private Context context;
    private static final String TAG = "TenantRentedPropertyAdapter";

    public TenantRentedPropertyAdapter(List<TenantRentedProperty> rentedProperties, Context context) {
        this.rentedProperties = rentedProperties;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView propertyTitle, address, rentalPrice, landlordName, landlordContact, rentedDate;

        public ViewHolder(View itemView) {
            super(itemView);
            propertyTitle = itemView.findViewById(R.id.textViewPropertyTitle);
            address = itemView.findViewById(R.id.textViewAddress);
            rentalPrice = itemView.findViewById(R.id.textViewRentPrice);
            landlordName = itemView.findViewById(R.id.textViewLandlordName);
            landlordContact = itemView.findViewById(R.id.textViewLandlordContact);
            rentedDate = itemView.findViewById(R.id.textViewRentedDate);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tenant_rented_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenantRentedProperty property = rentedProperties.get(position);

        // Set text values with null checks
        if (holder.propertyTitle != null) {
            holder.propertyTitle.setText(property.getPropertyTitle());
        }

        if (holder.address != null) {
            holder.address.setText(property.getAddress());
        }

        if (holder.rentalPrice != null) {
            holder.rentalPrice.setText("₱" + property.getRentalPrice() + " / month");
        }

        if (holder.landlordName != null) {
            holder.landlordName.setText(property.getLandlordName());
        }

        if (holder.landlordContact != null) {
            String email = property.getLandlordEmail();
            String phone = property.getLandlordPhone();

            if (phone == null || phone.trim().isEmpty() || phone.equalsIgnoreCase("null")) {
                phone = "No phone provided";
            }

            holder.landlordContact.setText("Email: " + email + " | Phone: " + phone);
        }

        if (holder.rentedDate != null) {
            String[] dateParts = property.getRentedAt().split(" ");
            String dateOnly = dateParts.length > 0 ? dateParts[0] : property.getRentedAt();
            holder.rentedDate.setText("Rented since: " + dateOnly);
        }
    }

    @Override
    public int getItemCount() {
        return rentedProperties.size();
    }
}