package com.example.capstone.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {


    class ExcursionViewHolder extends RecyclerView.ViewHolder{
        private final TextView excursionItemView;
        private final TextView excursionItemView2;
        private ExcursionViewHolder(View itemView){
            super(itemView);
            excursionItemView=itemView.findViewById(R.id.textView2);
            excursionItemView2=itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    final Excursion current=mExcursion.get(position);
                    Intent intent=new Intent(context,ExcursionDetails.class);
                    intent.putExtra("excursionID", current.getExcursionID());
                    intent.putExtra("excursionName", current.getExcursionName());
                    intent.putExtra("excursionDate", current.getExcursionDate());
                    intent.putExtra("vacationID",current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
    private List<Excursion> mExcursion;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.excursion_list_item,parent,false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if(mExcursion!=null){
            Excursion current=mExcursion.get(position);
            String excursionName=current.getExcursionName();
            String excursionDate = current.getExcursionDate();
            holder.excursionItemView.setText(excursionName);
            holder.excursionItemView2.setText(excursionDate);
        }
        else{
            holder.excursionItemView.setText("No part name");
            holder.excursionItemView.setText("No product id");
        }
    }

    public void setExcursions(List<Excursion> excursions){
        mExcursion=excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExcursion.size();
    }
}

