package com.example.nevera_andreaalejandra.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.List;

public class AdapterProducto extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ProductoModelo> list;

    public AdapterProducto(Context context, int layout, List<ProductoModelo> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ProductoModelo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new ViewHolder();
            holder.nombre = (TextView) convertView.findViewById(R.id.nameProduct);
            holder.precio = (TextView) convertView.findViewById(R.id.priceProduct);
            holder.cantidad = (TextView) convertView.findViewById(R.id.cantidadProduct);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ProductoModelo currentProduct = getItem(position);
        holder.nombre.setText(currentProduct.getNombre());
        holder.precio.setText((int) currentProduct.getPrecio());
        holder.cantidad.setText(currentProduct.getCantidad());


        return convertView;
    }

    static class ViewHolder {
        private TextView nombre;
        private TextView precio;
        private TextView cantidad;
        //private TextView fecha;
    }
}
