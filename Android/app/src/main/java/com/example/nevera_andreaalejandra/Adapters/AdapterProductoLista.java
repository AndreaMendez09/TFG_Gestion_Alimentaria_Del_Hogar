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

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.List;

public class AdapterProductoLista extends RecyclerView.Adapter<AdapterProductoLista.ViewHolder> {
    private AdapterProductoLista.OnItemClickListener itemClickListener;
    private AdapterProductoLista.OnButtonClickListener buttonClickListener;
    private Context context;
    private int layout;
    private String tipoproducto;
    private List<ProductoModelo> list;

    public AdapterProductoLista(Context context, int layout, List<ProductoModelo> list) {
        super();
        this.context = context;
        this.layout = layout;
        this.list = list;
    }
    public AdapterProductoLista(List<ProductoModelo> list, int layout, AdapterProductoLista.OnItemClickListener itemListener, AdapterProductoLista.OnButtonClickListener btnListener ) {
        super();
        this.layout = layout;
        this.list = list;
        this.itemClickListener = itemListener;
        this.buttonClickListener = btnListener;
    }

    @NonNull
    @Override
    public AdapterProductoLista.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        AdapterProductoLista.ViewHolder vh = new AdapterProductoLista.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductoLista.ViewHolder holder, int position) {
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

        public void bind(final ProductoModelo product, final AdapterProductoLista.OnItemClickListener itemListener, final AdapterProductoLista.OnButtonClickListener btnListener) {
            nombre.setText(product.getNombre());
            tipoproducto = product.getTipo();
            //precio.setText("Precio: " + product.getPrecio());
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

    public interface OnItemClickListener {
        void onItemClick(ProductoModelo productoModelo, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(ProductoModelo productoModelo, int position);
    }
}
