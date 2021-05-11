package com.example.nevera_andreaalejandra.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterProducto extends RecyclerView.Adapter<AdapterProducto.ViewHolder> {
    private OnItemClickListener itemClickListener;
    private OnButtonClickListener buttonClickListener;
    private Context context;
    private int layout;
    private String tipoproducto;
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

    public void setList(List<ProductoModelo> list) {
        this.list = list;
    }

    public void filterList(ArrayList<ProductoModelo> filteredList) {
        setList(filteredList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public TextView fecha;
        public TextView cantidad;
        public ImageView imagenItem;
        public ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nameProduct);
            fecha = (TextView) itemView.findViewById(R.id.dateProduct);
            cantidad = (TextView) itemView.findViewById(R.id.cantidadProduct);
            imagenItem = (ImageView) itemView.findViewById(R.id.imageProduct);
            btnDelete = (ImageButton) itemView.findViewById(R.id.imageDelete);

        }

        public void bind(final ProductoModelo product, final OnItemClickListener itemListener, final OnButtonClickListener btnListener) {
            nombre.setText(product.getNombre());
            tipoproducto = product.getTipo();
            fecha.setText("Caducidad: " + product.getFecha());
            cantidad.setText("Cantidad : "+ product.getCantidad() + "");
            switch (tipoproducto){
                case "Vegetales":
                    imagenItem.setImageResource(R.drawable.ic_carrot_solid);
                    break;
                case "Pescado":
                    imagenItem.setImageResource(R.drawable.ic_fish_solid);
                    break;
                case "Lacteos":
                    imagenItem.setImageResource(R.drawable.ic_milk);
                    break;
                case "Dulces":
                    imagenItem.setImageResource(R.drawable.ic_candy);
                    break;
                case "Carne":
                    imagenItem.setImageResource(R.drawable.ic_drumstick_bite_solid);
                    break;
                case "Fruta":
                    imagenItem.setImageResource(R.drawable.ic_apple_alt_solid);
                    break;
                case "Legumbres":
                    imagenItem.setImageResource(R.drawable.ic_beans);
                    break;
                case "Carbohidratos":
                    imagenItem.setImageResource(R.drawable.ic_bread_slice_solid);
                    break;
                case "Conservas":
                    imagenItem.setImageResource(R.drawable.ic_conservas);
                    break;
                case "Bebidas":
                    imagenItem.setImageResource(R.drawable.ic_coffee_solid);
                    break;
                case "Otros":
                    imagenItem.setImageResource(R.drawable.ic_groceries);
                    break;
                default:
                    System.out.println("No se encuentra");
            }


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

}


