package com.svalero.sitescanner_android.adapter;

import static com.svalero.sitescanner_android.db.Constants.DATABASE_NAME;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.svalero.sitescanner_android.AddPlace;
import com.svalero.sitescanner_android.R;
import com.svalero.sitescanner_android.ShowMap;
import com.svalero.sitescanner_android.db.AppDatabase;
import com.svalero.sitescanner_android.domain.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.SuperheroHolder> {
    public Context context;

    public List<Place> places;


    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }


    public SuperheroHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new SuperheroHolder(view);
    }

    public void onBindViewHolder(SuperheroHolder holder, int position) {
        final AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();

        Place place = db.placeDao().getById(places.get(position).getId());
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
        holder.visited.setText(place.getAddress());
        holder.latitude.setText(String.valueOf(place.getLatitude()));
        holder.longitude.setText(String.valueOf(place.getLongitude()));
    }

    public int getItemCount() {
        return places.size();
    }

    public class SuperheroHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView address;
        public TextView latitude;
        public TextView longitude;
        public Button delete;
        public Button edit;
        public Button map;
        public View parentView;
        public CheckBox visited;

        public SuperheroHolder(View view) {
            super(view);
            parentView = view;
            name = view.findViewById(R.id.listName);
            visited = view.findViewById(R.id.chkVisited);
            address = view.findViewById(R.id.listAddress);
            latitude = view.findViewById(R.id.listLat);
            longitude = view.findViewById(R.id.listLong);
            edit = view.findViewById(R.id.btnEdit);
            map = view.findViewById(R.id.btnMap);
            delete = view.findViewById(R.id.btnDelete);
            edit.setOnClickListener(v -> editPlace(getAdapterPosition()));
            map.setOnClickListener(v -> showMap(getAdapterPosition()));
            delete.setOnClickListener(v -> deletePlace(getAdapterPosition()));


        }
    }


    public void showMap(int position) {

        Place place = places.get(position);
        Intent intent = new Intent(context, ShowMap.class);
        intent.putExtra("place", place);
        context.startActivity(intent);
    }

    public void deletePlace(int position) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setMessage(R.string.confirmation).setTitle(R.string.deleteMessage)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    final AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();

                    Place place = places.get(position);
                    db.placeDao().delete(place);
                    places.remove(place);
                    notifyItemRemoved(position);
                }).setNegativeButton(R.string.no, (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = deleteDialog.create();
        dialog.show();
    }

   public void editPlace(int position) {
        Place place = places.get(position);
        Intent intent = new Intent(context, AddPlace.class);
        intent.putExtra("place", place);
        context.startActivity(intent);
    }

}

