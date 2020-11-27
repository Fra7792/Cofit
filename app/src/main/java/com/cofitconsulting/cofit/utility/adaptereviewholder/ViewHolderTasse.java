package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;

public class ViewHolderTasse extends RecyclerView.ViewHolder {

    TextView mDescrizione, mImporto, mScadenza, tvScaduto;
    View mView;

    public ViewHolderTasse(@NonNull View itemView){
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

        mDescrizione = itemView.findViewById(R.id.txt_tassa);
        mImporto = itemView.findViewById(R.id.txt_importo);
        mScadenza = itemView.findViewById(R.id.txt_scadenza);
        tvScaduto = itemView.findViewById(R.id.tvScaduto);
    }

    private ViewHolderTasse.ClickListener mClickListener;

    public interface ClickListener{
        void onItemCLick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolderTasse.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
