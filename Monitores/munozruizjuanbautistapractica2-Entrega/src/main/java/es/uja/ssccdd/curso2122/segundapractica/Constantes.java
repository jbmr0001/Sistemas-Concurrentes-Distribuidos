/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

import java.util.Random;

/**
 *
 * @author Pc
 */
public interface Constantes {
     
    public enum Tipo { 
        ESTANDAR(0),PREMIUM(1);
        
        private int ordinal;

        private Tipo(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }
    
    public int MAX_TIEMPO_LLEGAR_CLIENTE=10;
    public int MIN_TIEMPO_LLEGAR_CLIENTE=0;
    public static Random random = new Random();
    public int MAX_CLIENTES_RESTAURANTE=10;
    public int NUM_CLIENTES=14;
    public int MAX_NUM_PLATOS=5;
    public int MIN_NUM_PLATOS=3;
    public int MAX_PRECIO_PREMIUM_PLATO=40;
    public int MIN_PRECIO_PREMIUM_PLATO=10;
    public int MAX_PRECIO_ESTANDAR_PLATO=20;
    public int MIN_PRECIO_ESTANDAR_PLATO=5;
    public int MAX_TIEMPO_CLIENTE_COMIENDO=5;
    public int MIN_TIEMPO_CLIENTE_COMIENDO=2;
    public int TIEMPO_COCINA=3;
    public int MAX_RACHA_PREMIUM=4;
    public int ESPERA_CLIENTES=130;
        
}
