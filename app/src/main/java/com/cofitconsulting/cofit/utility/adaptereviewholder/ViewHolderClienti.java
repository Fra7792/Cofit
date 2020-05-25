package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderClienti extends RecyclerView.ViewHolder {

    TextView mDenominazione, mEmail, mId;
    CircleImageView profileImage;
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

        mDenominazione = itemView.findViewById(R.id.txt_den);
        mEmail = itemView.findViewById(R.id.txt_email);
        profileImage = itemView.findViewById(R.id.profileImage);
    }

    public ViewHolderClienti.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderClienti.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
