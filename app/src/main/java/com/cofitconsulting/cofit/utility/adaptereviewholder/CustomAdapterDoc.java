package com.cofitconsulting.cofit.utility.adaptereviewholder;


import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.strutture.StrutturaUpload;

import java.util.List;

public class CustomAdapterDoc extends RecyclerView.Adapter<CustomAdapterDoc.ImageViewHolder> {
    private Context mContext;
    private List<StrutturaUpload> mStrutturaUploadList;
    private OnItemClickListener mListener;

    public CustomAdapterDoc(Context context, List<StrutturaUpload> uploads ){
        mContext = context;
        mStrutturaUploadList = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lv_item_documenti, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        StrutturaUpload uploadCurrent = mStrutturaUploadList.get(position);
        holder.textViewName.setText(uploadCurrent.getFileName());

    }

    @Override
    public int getItemCount() {
        return mStrutturaUploadList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public TextView textViewName;
        public ImageButton btnMenu;


        public ImageViewHolder(@NonNull final View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.nome);
            btnMenu = itemView.findViewById(R.id.btnMenu);


            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, btnMenu);
                    popupMenu.inflate(R.menu.menu_doc);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId())
                            {
                                case R.id.menu_download:
                                {
                                    if (mListener != null) {
                                        int position = getAdapterPosition();
                                        if (position != RecyclerView.NO_POSITION) {
                                            mListener.onDownloadClick(position);
                                            break;
                                        }
                                    }
                                }
                                case R.id.menu_delete:
                                {
                                    if (mListener != null) {
                                        int position = getAdapterPosition();
                                        if (position != RecyclerView.NO_POSITION) {
                                            mListener.onDeleteClick(position);
                                            break;
                                        }
                                    }
                                }
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem download = menu.add(Menu.NONE, 1, 1, "Download");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Elimina");
            download.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onDownloadClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onDownloadClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
