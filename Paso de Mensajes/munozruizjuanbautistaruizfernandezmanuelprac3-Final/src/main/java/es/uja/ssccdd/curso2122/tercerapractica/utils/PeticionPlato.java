/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.utils;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.Plato;

/**
 *
 * @author Pc
 */
public class PeticionPlato extends Peticion {
    Plato plato;
    public PeticionPlato(String tipoPeticion, int id, Plato plato) {
        super(tipoPeticion, id);
        this.plato=plato;
    }

    public Plato getPlato() {
        return plato;
    }

    @Override
    public String toString() {
        return "|" + "plato=" + plato + '|';
    }
}
