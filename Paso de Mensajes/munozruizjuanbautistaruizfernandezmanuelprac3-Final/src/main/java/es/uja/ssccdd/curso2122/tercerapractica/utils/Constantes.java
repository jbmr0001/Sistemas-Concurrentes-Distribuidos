/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.utils;

import java.util.Random;

/**
 *
 * @author pedroj
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
        
        @Override
        public String toString(){
            if(ordinal==0){
                return "ESTANDAR";
            }else{
                return "PREMIUM";
            }
        }
    }
    
    public int NUM_CLIENTES=20;
    public int CAPACIDAD_RESTAURANTE=10;
    public int MIN_TIEMPO_LLEGAR_CLIENTE=0;
    public int MAX_TIEMPO_LLEGAR_CLIENTE=10;
    public static Random random = new Random();
    public static String CONNECTION = "tcp://suleiman.ujaen.es:8018";
    public static String GRUPO = "jbmr0001mrf00020";
    public static String BUZON_RESTAURANTE = "ssccdd.curso2021." + GRUPO+".restaurante";
    public static String BUZON_COCINA = "ssccdd.curso2021." + GRUPO+".cocina";
    public static String BUZON_CLIENTE = "ssccdd.curso2021." + GRUPO+".cliente.";
    public static int MAX_PREMIUM_SEGUIDOS=4;
    public static int MAX_NUM_PLATOS=5;
    public static int MIN_NUM_PLATOS=3;
    public static int MAX_PRECIO_PLATO_ESTANDAR=20;
    public static int MIN_PRECIO_PLATO_ESTANDAR=5;
    public static int MAX_PRECIO_PLATO_PREMIUM=40;
    public static int MIN_PRECIO_PLATO_PREMIUM=10;
    public static int MIN_TIEMPO_COMIENDO=2;
    public static int MAX_TIEMPO_COMIENDO=5;
    public static int TIEMPO_ESPERA_RESTAURANTE=150;
    public static int TIEMPO_COCINA_PLATO=1;
}
