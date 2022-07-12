/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Pc
 */
public class Cocina implements Runnable{
    private final MonitorRestaurante monitor;

    public Cocina(MonitorRestaurante monitor) {
        this.monitor=monitor;
    }

    @Override
    public void run() {
        boolean interrumpido=false;
        System.out.println("------------Cocina comienza ejecucion------------");
        while(!interrumpido){
            try {
                Plato plato=monitor.siguientePlato();
            
                if(plato.getPrecio()==0){
                    System.out.println("//////Cocina no tiene mas pedidos, esperando finalizacion");
                }else{
                    monitor.prepararServir(plato);
                    //System.out.println("//////Cocina plato preparado"+plato.toString());
                }
                TimeUnit.SECONDS.sleep(Constantes.TIEMPO_COCINA);
                
            } catch (InterruptedException ex) {
                System.out.println("---------Cocina interrumpida-------------");
                interrumpido=true;
               
            }
        }
        System.out.println("-------------Cocina finaliza ejecucion------------");
    }
    
}
