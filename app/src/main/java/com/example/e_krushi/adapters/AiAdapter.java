package com.example.e_krushi.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_krushi.R;
import com.example.e_krushi.activities.ModelAi;
import com.example.e_krushi.activities.DiseaseDetectionActivity;


import java.util.List;

public class AiAdapter extends RecyclerView.Adapter<AiAdapter.ViewHolder> {

    private List<ModelAi> aiList;

    public AiAdapter(List<ModelAi> aiList) {
        this.aiList = aiList;
    }

    @NonNull
    @Override
    public AiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AiAdapter.ViewHolder holder, int position) {

        int resource = aiList.get(position).getImageView();
        String name = aiList.get(position).getTextView();

        holder.setData(resource, name);

    }

    @Override
    public int getItemCount() {
        return aiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img);
            textView = itemView.findViewById(R.id.txt);
            cardView = itemView.findViewById(R.id.weather);

        }

        public void setData(int resource, String name) {
            imageView.setImageResource(resource);
            textView.setText(name);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the clicked item
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the corresponding ModelAi object
                        ModelAi clickedItem = aiList.get(position);

                        // Open a new intent or perform your desired action
                        if (clickedItem.getTextView().equals("Weather")) {
                            // Open the Weather activity
                            Intent intent = new Intent(v.getContext(), fr.qgdev.openweather.WeatherActivity.class);
                            v.getContext().startActivity(intent);
                        } else if (clickedItem.getTextView().equals("Scan")) {
                            // Open the Scan activity
                            Intent intent = new Intent(v.getContext(), DiseaseDetectionActivity.class);
                            v.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
