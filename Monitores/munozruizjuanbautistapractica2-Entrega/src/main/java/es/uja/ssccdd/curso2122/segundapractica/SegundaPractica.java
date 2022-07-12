/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

import static es.uja.ssccdd.curso2122.segundapractica.Constantes.random;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
    * @author Juan Bautista Muñoz Ruiz
 */
public class SegundaPractica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hilo principal comienza ejecución");
        
        ExecutorService ejecutor=Executors.newCachedThreadPool();
        LinkedList<Future<?>> lista = new LinkedList();
        MonitorRestaurante monitor=new MonitorRestaurante();
        Cocina c=new Cocina(monitor);
        Future<?> cocina=ejecutor.submit(c);
        int id = 0;
        int clientes=0;
        while(clientes<Constantes.NUM_CLIENTES){
            
            int tardaLlegar=random.nextInt(Constantes.MAX_TIEMPO_LLEGAR_CLIENTE-Constantes.MIN_TIEMPO_LLEGAR_CLIENTE+1)+Constantes.MIN_TIEMPO_LLEGAR_CLIENTE;
            
            Cliente cliente=new Cliente(id,monitor);
            lista.add(ejecutor.submit(cliente));   
            id++;
            TimeUnit.SECONDS.sleep(tardaLlegar);
            clientes++;
        }

        TimeUnit.SECONDS.sleep(Constantes.ESPERA_CLIENTES);
        cocina.cancel(true);
        ejecutor.shutdown();
        ejecutor.awaitTermination(1, TimeUnit.DAYS);

        monitor.mostrarTotalRecaudado();
        System.out.println("Hilo principal finaliza ejecución");
    }
    
}
