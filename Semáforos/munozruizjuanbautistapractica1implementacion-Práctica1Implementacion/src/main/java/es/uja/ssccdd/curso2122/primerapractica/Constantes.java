/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;

import java.util.Random;

/**
 *
 * @author Pc
 */
public interface Constantes {
    public static enum Tipo {INICIO, EJECUCION};
    public static Random random = new Random();
    public static final int NUM_RECURSOS = 20;
    public final int MINIMO_RECURSOS=2;
    public final int MAXIMO_RECURSOS=4;
    public final int MIN_TAREAS_PROCESO=8;
    public final int MAX_TAREAS_PROCESO=12;
    public final int NUM_PROCESOS_MAX=12;
    public final int MIN_TIEMPO_ESPERA=1;
    public final int MAX_TIEMPO_ESPERA=3;
    public final int MIN_NUM_TAREAS=4;
    public final int MAX_NUM_TAREAS=8;
    public final int TAM_BUFFER_RECURSOS=8;
    public final int MAX_TIEMPO_PRINCIPAl=120;//2 minutos
    public final int MAX_DURACION_OPERACION=2;
    public final int MIN_DURACION_OPERACION=1;
}
