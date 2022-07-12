/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

/**
 *
 * @author Pc
 */
public class Plato {
    private int id;
    private int precio;
    private int cliente;

    public Plato(int id, int precio,int cliente) {
        this.id = id;
        this.precio = precio;
        this.cliente=cliente;
    }

    public int getId() {
        return id;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCliente() {
        return cliente;
    }
    
    void setPrecio(int precio){
        this.precio=precio;
    }

    @Override
    public String toString() {
        return  "|ID="+this.id+" Precio="+this.precio+" Cliente="+cliente+"|";
    }
      
}
