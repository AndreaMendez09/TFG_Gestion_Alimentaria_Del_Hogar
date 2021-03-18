package com.example.nevera_andreaalejandra.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.List;

public class AdapterProducto extends RecyclerView.Adapter<AdapterProducto.ViewHolder> {
    private OnItemClickListener itemClickListener;
    private OnButtonClickListener buttonClickListener;
    private Context context;
    private int layout;
    private List<ProductoModelo> list;

    public AdapterProducto(Context context, int layout, List<ProductoModelo> list) {
        super();
        this.context = context;
        this.layout = layout;
        this.list = list;
    }
    public AdapterProducto(List<ProductoModelo> list, int layout,OnItemClickListener itemListener, OnButtonClickListener btnListener ) {
        super();
        this.layout = layout;
        this.list = list;
        this.itemClickListener = itemListener;
        this.buttonClickListener = btnListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position), itemClickListener, buttonClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public TextView precio;
        public TextView cantidad;
        public ImageView imagenItem;
        public ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nameProduct);
            precio = (TextView) itemView.findViewById(R.id.priceProduct);
            cantidad = (TextView) itemView.findViewById(R.id.cantidadProduct);
            imagenItem = (ImageView) itemView.findViewById(R.id.imageProduct);
            btnDelete = (ImageButton) itemView.findViewById(R.id.imageDelete);
        }

        public void bind(final ProductoModelo product, final OnItemClickListener itemListener, final OnButtonClickListener btnListener) {
            nombre.setText(product.getNombre());
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnListener.onButtonClick(product, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(product, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductoModelo productoModelo, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(ProductoModelo productoModelo, int position);
    }
}


