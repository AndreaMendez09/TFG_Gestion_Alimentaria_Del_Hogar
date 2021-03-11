package com.example.nevera_andreaalejandra.Models;

public class ListaModelo {
    private String nombre_lista;
    private int cantidad_lista;

    //Creamos el constructor vacio
    public ListaModelo() {

    }

    //Creamos el constructor completo
    public ListaModelo(String nombre_lista, int cantidad_lista) {
        this.nombre_lista = nombre_lista;
        this.cantidad_lista = cantidad_lista;
    }

    //Creamos getters y setters
    public String getNombre_lista() {
        return nombre_lista;
    }

    public void setNombre_lista(String nombre_lista) {
        this.nombre_lista = nombre_lista;
    }

    public int getCantidad_lista() {
        return cantidad_lista;
    }

    public void setCantidad_lista(int cantidad_lista) {
        this.cantidad_lista = cantidad_lista;
    }
}
