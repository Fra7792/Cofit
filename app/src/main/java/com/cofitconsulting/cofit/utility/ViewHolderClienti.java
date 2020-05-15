package com.cofitconsulting.cofit.utility;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;

public class ViewHolderClienti extends RecyclerView.ViewHolder {

    TextView mDenominazione, mEmail, mId;
    View mView;

    public ViewHolderClienti(@NonNull View itemView){
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        mDenominazione = itemView.findViewById(R.id.txt_den);
        mEmail = itemView.findViewById(R.id.txt_email);
        mId = itemView.findViewById(R.id.txt_id);
    }

    public ViewHolderClienti.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolderClienti.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
