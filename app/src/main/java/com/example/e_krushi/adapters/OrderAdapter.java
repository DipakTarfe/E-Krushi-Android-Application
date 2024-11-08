package com.example.e_krushi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_krushi.R;
import com.example.e_krushi.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    private OnItemClickListener itemClickListener;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        Glide.with(context)
                .load(order.getOrderImage())
                .into(holder.imageView);
        holder.statusTextView.setText(order.getOrderStatus());
        holder.nameTextView.setText(order.getOrderName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Order order, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView statusTextView;
        TextView nameTextView;
        TextView orderPriceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.orderImage);
            statusTextView = itemView.findViewById(R.id.orderStatus);
            nameTextView = itemView.findViewById(R.id.orderName);
            orderPriceTextView = itemView.findViewById(R.id.orderPrice);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                Order order = orderList.get(position);
                itemClickListener.onItemClick(order, position);
            }
        }
    }
}
