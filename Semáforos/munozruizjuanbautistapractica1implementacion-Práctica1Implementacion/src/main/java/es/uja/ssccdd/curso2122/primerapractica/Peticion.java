/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;


import es.uja.ssccdd.curso2122.primerapractica.Constantes.Tipo;

/**
 *
 * @author Pc
 */
public class Peticion {
    private final int id;
    private final Tipo tipo;

    public Peticion(int id, Tipo tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }
    
}
