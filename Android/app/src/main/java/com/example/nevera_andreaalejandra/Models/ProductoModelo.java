package com.example.nevera_andreaalejandra.Models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

//Implementamos serializable para poder pasar el objeto de un frament a otro
public class ProductoModelo implements Serializable {
    //Creamos las variables que deberá tener nuestro producto
    private String id;
    private int cantidad;
    private String tipo;
    private String nombre;
    private String fecha;
    private double precio;
    private String ubicacion;
    private String usuario;

    //Creamos el constructor vacio
    public ProductoModelo() {

    }

    //Para añadir
    public ProductoModelo(String id, String nombre, int cantidad, double precio,String ubicacion, String tipo, String fecha, String UID_usuario) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
        this.usuario = UID_usuario;
        this.ubicacion = ubicacion;
    }


    //Para addEdit
    public ProductoModelo( String nombre, int cantidad,String ubicacion, String tipo, String UID_usuario) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.usuario = UID_usuario;
        this.ubicacion = ubicacion;
    }

    //Para cuando sea editar, porque no debe tener ni id, ni ubicacion, ni usuario
    public ProductoModelo(String nombre, int cantidad, double precio, String tipo, String fecha) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    //***Métodos para ordenar los productos***
    public static Comparator<ProductoModelo> ProductoAZ = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return p1.getNombre().compareToIgnoreCase(p2.getNombre());
        }
    };

    public static Comparator<ProductoModelo> ProductoZA = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return p2.getNombre().compareToIgnoreCase(p1.getNombre());
        }
    };

    public static Comparator<ProductoModelo> ProductoCantidadA = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return p2.getCantidad() - p1.getCantidad();
        }
    };

    public static Comparator<ProductoModelo> ProductoCantidadD = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return p1.getCantidad() - p2.getCantidad();
        }
    };

    public static Comparator<ProductoModelo> ProductoPrecioD = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return (int)(p1.getPrecio() - p2.getPrecio());
        }
    };

    public static Comparator<ProductoModelo> ProductoPrecioA = new Comparator<ProductoModelo>() {
        @Override
        public int compare(ProductoModelo p1, ProductoModelo p2) {
            return (int)(p2.getPrecio() - p1.getPrecio());
        }
    };





    //Creamos los getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUID_usuario() {
        return usuario;
    }

    public void setUID_usuario(String UID_usuario) {
        this.usuario = UID_usuario;
    }
}
