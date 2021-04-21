package com.example.nevera_andreaalejandra.Adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnCheckedChangeListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterProductoLista extends RecyclerView.Adapter<AdapterProductoLista.ViewHolder> {
    private OnItemClickListener itemClickListener;
    private OnButtonClickListener buttonClickListener;
    private OnCheckedChangeListener checkedChangeListener;
    private Context context;
    private int layout;
    private String tipoproducto;
    public List<ProductoModelo> list;
    SparseBooleanArray mSparseBooleanArray;

    public AdapterProductoLista() {

    }

    public AdapterProductoLista(Context context, List<ProductoModelo> list, int layout, OnItemClickListener itemListener, OnButtonClickListener btnListener, OnCheckedChangeListener checkedChangeListener) {
        super();
        this.layout = layout;
        this.list = list;
        this.itemClickListener = itemListener;
        this.buttonClickListener = btnListener;
        this.checkedChangeListener = checkedChangeListener;
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
        holder.bind(list.get(position), itemClickListener, buttonClickListener, checkedChangeListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<ProductoModelo> getCheckedItems() {
        List<ProductoModelo> mTempArry = new ArrayList<ProductoModelo>(); //Instanciamos nuestra lista temporal
        for(int i=0;i<list.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(list.get(i));
            }
        }
        return mTempArry;
    }

    /*public void checkAll(){
        for(int x = 0; x < list.size(); x++){
            list.get(x).setChecked(!alumno.get(x).isChecked());
        }
    }*/


    public void DondeEstoy(String estoyEn) {
        Toast.makeText(context, estoyEn , Toast.LENGTH_LONG).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public TextView precio;
        public TextView cantidad;
        public ImageView imagenItem;
        public ImageButton btnDelete;
        public CheckBox check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nameProduct);
            precio = (TextView) itemView.findViewById(R.id.priceProduct);
            cantidad = (TextView) itemView.findViewById(R.id.cantidadProduct);
            imagenItem = (ImageView) itemView.findViewById(R.id.imageProduct);
            btnDelete = (ImageButton) itemView.findViewById(R.id.imageDelete);
            check = (CheckBox) itemView.findViewById(R.id.checkbox_product);

        }

        public void bind(final ProductoModelo product, final OnItemClickListener itemListener, final OnButtonClickListener btnListener, final OnCheckedChangeListener checkListener) { //
            nombre.setText(product.getNombre());
            tipoproducto = product.getTipo();
            precio.setText("Precio: " + product.getPrecio() + "€");
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

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkListener.onButtonClick(product, getAdapterPosition(), isChecked);
                }
            });

        }

    }


}
/*
    public void seleccionarTodo(final ProductoModelo product,final AdapterProductoLista.OnCheckedChangeListener checkListener){

        for (int i=0; list.size()==i; i++){

            if(list.get(i).getUbicacion().equals("nevera")){
                list.setItemChecked(i,true);
            }if(list.get(i).getUbicacion().equals("congelador")){

            }
        }
                /*
        Query queryCheckAllNevera = FirebaseDatabase.getInstance().getReference("Producto")
                .orderByChild("ubicacion")
                .equalTo("nevera");
        Query queryCheckAllCongelador = FirebaseDatabase.getInstance().getReference("Producto")
                .orderByChild("ubicacion")
                .equalTo("congelador");

    }*/