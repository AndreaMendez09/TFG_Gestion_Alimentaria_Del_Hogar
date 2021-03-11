package com.example.nevera_andreaalejandra.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nevera_andreaalejandra.Models.ListaModelo;
import com.example.nevera_andreaalejandra.R;

import java.util.List;

public class AdapterLista extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ListaModelo> listas;

    public AdapterLista(Context context, int layout, List<ListaModelo> listas) {
        this.context = context;
        this.layout = layout;
        this.listas = listas;
    }

    @Override
    public int getCount() {
        return this.listas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listas.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        // View Holder Pattern
        ViewHolder holder;

        if (convertView == null) {
            // Inflamos la vista que nos ha llegado con nuestro layout personalizado
            convertView = LayoutInflater.from(context).inflate(layout, null);

            //El holder es uno de los componentes de la lista
            holder = new ViewHolder();

            // Referenciamos cada elemento del layout a modificar y se rellena
            holder.NombreLista = (TextView) convertView.findViewById(R.id.textViewName);
            holder.CantidadProductos = (TextView) convertView.findViewById(R.id.products);

            convertView.setTag(holder);
        } else {
            //Anular lo que se está haciendo
            holder = (ViewHolder) convertView.getTag();
        }

        //  Se trae el valor actual dependiente de la posición
        ListaModelo listaModelo = (ListaModelo) getItem(position);

        // Referenciamos el elemento a modificar y lo rellenamos
        holder.NombreLista.setText(listaModelo.getNombre_lista());
        holder.CantidadProductos.setText(listaModelo.getCantidad_lista());

        // Se devuelve la vista inflada y modificada con nuestros datos
        return convertView;
    }


    static class ViewHolder {
        private TextView NombreLista;
        private TextView CantidadProductos;
    }
}
