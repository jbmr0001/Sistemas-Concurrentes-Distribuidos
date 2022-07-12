/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;

import static es.uja.ssccdd.curso2122.primerapractica.Constantes.*;
import static es.uja.ssccdd.curso2122.primerapractica.Constantes.random;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author pedroj
 */
public class PrimeraPractica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("HILO PRINCIPAL COMIENZA EJECUCION");

        ExecutorService ejecutor = Executors.newCachedThreadPool();
        LinkedList<Future<?>> lista = new LinkedList();
        Monitor monitor = new Monitor(Constantes.TAM_BUFFER_RECURSOS, NUM_RECURSOS);
        GestorRecursos gestor=new GestorRecursos(monitor);
        lista.add(ejecutor.submit(gestor));

        int tiempo = 0;
        int id = 0;
        Boolean fin=false;
        while(!fin){
            //tiempo de creación y ejecución por cada proceso
            int esperar=random.nextInt(Constantes.MAX_TIEMPO_ESPERA-Constantes.MIN_TIEMPO_ESPERA+1)+Constantes.MIN_TIEMPO_ESPERA;
            TimeUnit.SECONDS.sleep(esperar);
            tiempo+=esperar;
            if(tiempo>Constantes.MAX_TIEMPO_PRINCIPAl){
                fin=true;//salimos
            }
            
            Proceso proceso = new Proceso(id, monitor);
            monitor.incrementarTotalProcesos();//aumentamos los procesos creados
            lista.add(ejecutor.submit(proceso));   
            id++;
        }

        for(int i=0;i<lista.size();i++){
            lista.get(i).cancel(true);
        }
        ejecutor.shutdown();
        ejecutor.awaitTermination(1, TimeUnit.DAYS);
        
        monitor.informacion();
        System.out.println("HILO PRINCIPAL TERMINA EJECUCION");
    }
}
