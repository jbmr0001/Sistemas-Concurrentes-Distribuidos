/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno1;

/**
 *
 * @author Pc
 */
public class Plato {
    int precio;
    int cliente;
    int id;

    public Plato(int id,int precio, int cliente) {
        this.precio = precio;
        this.cliente = cliente;
        this.id=id;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCliente() {
        return cliente;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return "| IdPlato="+id+", Precio=" + precio + ", idCliente=" + cliente+"|";
    }

}
