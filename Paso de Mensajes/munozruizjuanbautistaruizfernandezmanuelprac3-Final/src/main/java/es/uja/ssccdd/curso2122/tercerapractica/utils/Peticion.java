/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.utils;

/**
 *
 * @author Pc
 */
public class Peticion {
    String tipo;
    int id;

    public Peticion(String tipoPeticion, int id) {
        this.tipo = tipoPeticion;
        this.id = id;
    }

    public String getTipoPeticion() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public void setTipoPeticion(String tipo) {
        this.tipo = tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "|" + "tipo=" + tipo + "|id=" + id + '|';
    }
    
}
