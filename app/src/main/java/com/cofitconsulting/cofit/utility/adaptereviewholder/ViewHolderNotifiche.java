package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;

public class ViewHolderNotifiche extends RecyclerView.ViewHolder {

    TextView mEmail, mData;
    View mView;

    public ViewHolderNotifiche(@NonNull View itemView){
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemCLick(v, getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        mEmail = itemView.findViewById(R.id.txt_email);
        mData = itemView.findViewById(R.id.txt_data);
    }

    private ViewHolderNotifiche.ClickListener mClickListener;

    public interface ClickListener{
        void onItemCLick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolderNotifiche.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
