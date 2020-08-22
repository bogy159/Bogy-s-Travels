package com.example.bogystravels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    //private List<String> mData;
    private Pair<ArrayList<String>, ArrayList<Object>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    //MyRecyclerViewAdapter(Context context, List<String> data) {
    MyRecyclerViewAdapter(Context context, ArrayList<Map> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = convertMapToList(data);
    }

    private Pair<ArrayList<String>, ArrayList<Object>> convertMapToList(ArrayList<Map> data){

        ArrayList<String> result = new ArrayList<>();
        ArrayList<Object> result2 = new ArrayList<>();
        try {
            for (Map m : data) {
                if (m.containsKey("location")){
                    if (m.containsKey("dateA")){
                        if (m.containsKey("dateD")){
                            MainActivity mainActivity = new MainActivity();
                            result.add(m.get("location").toString() + ": " + mainActivity.timestampToString((Timestamp) m.get("dateA")) + " - " + mainActivity.timestampToString((Timestamp) m.get("dateD")));
                        }
                        else{
                            MainActivity mainActivity = new MainActivity();
                            result.add(m.get("location").toString() + ": " + mainActivity.timestampToString((Timestamp) m.get("dateA")));
                        }
                    }
                    else {
                        result.add(m.get("location").toString());
                    }
                    result2.add(m.get("id"));
                }
            }
        }
        catch(Exception e){
            Log.d(TAG, "Error when formatting values: " + e);
        }
        return new Pair<>(result, result2);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //List<String> _temp_ = mData.first;
        String location = mData.first.get(position);
        holder.myTextView.setText(location);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.first.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        //String result = mData.second.get(id).toString();
        return mData.second.get(id).toString();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
