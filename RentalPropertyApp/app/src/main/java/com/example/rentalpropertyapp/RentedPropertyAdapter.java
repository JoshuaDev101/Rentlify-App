    package com.example.rentalpropertyapp;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.List;

    public class RentedPropertyAdapter extends RecyclerView.Adapter<RentedPropertyAdapter.ViewHolder> {

        private List<RentedProperty> rentedProperties;
        private Context context;
        private static final String TAG = "RentedPropertyAdapter";

        public RentedPropertyAdapter(List<RentedProperty> rentedProperties, Context context) {
            this.rentedProperties = rentedProperties;
            this.context = context;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView propertyTitle, address, rentalPrice, tenantName, tenantEmail, tenantPhone, rentedDate;

            public ViewHolder(View itemView) {
                super(itemView);
                propertyTitle = itemView.findViewById(R.id.propertyTitleTextView);
                address = itemView.findViewById(R.id.propertyAddressTextView);
                rentalPrice = itemView.findViewById(R.id.rentalPriceTextView);
                tenantName = itemView.findViewById(R.id.tenantNameTextView);
                tenantEmail = itemView.findViewById(R.id.tenantEmailTextView);
                tenantPhone = itemView.findViewById(R.id.tenantPhoneTextView);
                rentedDate = itemView.findViewById(R.id.rentedAtTextView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rented_property, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RentedProperty property = rentedProperties.get(position);

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

            if (holder.tenantName != null) {
                holder.tenantName.setText("Tenant: " + property.getTenantName());
            }

            if (holder.tenantEmail != null) {
                holder.tenantEmail.setText("Email: " + property.getTenantEmail());
            }

            if (holder.tenantPhone != null) {
                String phone = property.getTenantPhone();
                if (phone == null || phone.trim().isEmpty() || phone.equalsIgnoreCase("null")) {
                    holder.tenantPhone.setText("Phone: No phone provided");
                } else {
                    holder.tenantPhone.setText("Phone: " + phone);
                }
            }

            if (holder.rentedDate != null) {
                String[] dateParts = property.getRentedAt().split(" ");
                String dateOnly = dateParts.length > 0 ? dateParts[0] : property.getRentedAt();
                holder.rentedDate.setText("Rented on: " + dateOnly);
            }
        }

        @Override
        public int getItemCount() {
            return rentedProperties.size();
        }
    }