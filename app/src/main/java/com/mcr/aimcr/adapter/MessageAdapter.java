package com.mcr.aimcr.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcr.aimcr.R;
import com.mcr.aimcr.models.MessageModel;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    public ArrayList<MessageModel> messageData;

    public MessageAdapter(){
        messageData = new ArrayList<>();
    }

    public void UpdateData(MessageModel reqData){
        messageData.add(reqData);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageholdermodel,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageModel data = messageData.get(position);
        holder.MessageField.setText(data.getMessage());
        holder.TimeField.setText(data.getDate());
        if (data.getSender().equals("User")) {
            holder.SenderField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_person_pin_24,0,0,0);
        } else {
            holder.SenderField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_campaign_24,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return messageData.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{
        TextView MessageField,TimeField,SenderField;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            SenderField = itemView.findViewById(R.id.Sender);
            MessageField = itemView.findViewById(R.id.Message);
            TimeField = itemView.findViewById(R.id.Time);
        }
    }
}
