/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno1;

import es.uja.ssccdd.curso2122.tercerapractica.utils.Peticion;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 *
 * @author Pc
 */
//Peticion de tipo salida que envia el cliente al restaurante para notificar su salida y sus datos
//Hereda de Peticion, implementada por el compañero
public class PeticionSalida extends Peticion {
    LocalDateTime llegada; //tiempo de llegada del cliente
    LocalDateTime entrada; //tiempo de entrada del cliente
    LocalDateTime salida; //tiempo de salida del cliente
    LinkedList<Plato> platosRecibidos;  //vector de platos del cliente 

    public PeticionSalida(String tipoPeticion,int id,LocalDateTime llegada, LocalDateTime entrada, LocalDateTime salida, LinkedList<Plato> platosRecibidos) {
        super(tipoPeticion, id);
        this.llegada = llegada;
        this.entrada = entrada;
        this.salida = salida;
        this.platosRecibidos = platosRecibidos;
    }

    public LocalDateTime getLlegada() {
        return llegada;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSalida() {
        return salida;
    }

    public LinkedList<Plato> getPlatosRecibidos() {
        return platosRecibidos;
    }
    
    

}
