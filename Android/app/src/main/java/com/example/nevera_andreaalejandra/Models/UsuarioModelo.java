package com.example.nevera_andreaalejandra.Models;
//El id será el correo
public class UsuarioModelo {
    //Los campos que deberá tener nuestro usuario
    private String id_usuario,nombre, apellidos, password, correo;

    //Creamos el constructor vacio
    public UsuarioModelo() {

    }

    //Creamos el constructor con los datos necesarios
    public UsuarioModelo(String id_usuario, String nombre, String apellidos, String password, String correo) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.password = password;
        this.correo = correo;
    }

    //Creamos los getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellidos;
    }

    public void setApellido(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
