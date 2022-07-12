/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.utils;

import es.uja.ssccdd.curso2122.tercerapractica.utils.Peticion;

/**
 *
 * @author Pc
 */
public class PeticionEntrada extends Peticion{
    String tipoCliente;
    public PeticionEntrada(String tipoPeticion, int id,String tipoCliente) {
        super(tipoPeticion, id);
        this.tipoCliente=tipoCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    @Override
    public String toString() {
        return "|" + "tipoCliente=" + tipoCliente + '|';
    }
    
}
