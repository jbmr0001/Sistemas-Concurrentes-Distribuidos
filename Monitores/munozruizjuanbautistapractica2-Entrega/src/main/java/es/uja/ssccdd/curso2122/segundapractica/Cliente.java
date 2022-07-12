/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

import es.uja.ssccdd.curso2122.segundapractica.Constantes.*;
import static es.uja.ssccdd.curso2122.segundapractica.Constantes.random;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author Pc
 */
public class Cliente implements Runnable {

    private final int id;
    private final Tipo tipo;
    private final MonitorRestaurante monitor;
    private int pedidos;

    public Cliente(int id, MonitorRestaurante monitor) {
        this.id = id;
        this.tipo = tipo();
        this.monitor = monitor;
        this.pedidos = 0;
    }

    Tipo tipo() {
        if (id % 2 == 0) {
            return Constantes.Tipo.PREMIUM;
        } else {
            return Constantes.Tipo.ESTANDAR;
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("-----------Cliente " + id + " " + tipo + " Comienza ejecucion------------");
            System.out.println("||||||Cliente " + id + " Esperando entrar");
            monitor.solicitarEntrada(tipo);
            int numPlatos = random.nextInt(Constantes.MAX_NUM_PLATOS - Constantes.MIN_NUM_PLATOS + 1) + Constantes.MIN_NUM_PLATOS;
            System.out.println("||||||Cliente " + id + " Entra restaurante");
            LocalDateTime entrada=LocalDateTime.now();
            monitor.pedir(id, tipo, numPlatos);

            for (int i = 0; i < numPlatos; i++) {

                monitor.esperarPlato(id);
                //En caso de que tengamos mas de un plato servido tras el signal
                while (i <= monitor.verPlatosServidos(id)) {
                    int tiempoComiendo = random.nextInt(Constantes.MAX_TIEMPO_CLIENTE_COMIENDO - Constantes.MIN_TIEMPO_CLIENTE_COMIENDO + 1) + Constantes.MIN_TIEMPO_CLIENTE_COMIENDO;
                    TimeUnit.SECONDS.sleep(tiempoComiendo);
                    System.out.println("||||||Cliente "+id+" plato comido");
                    i++;
                }

            }
            

            System.out.println("||||||Cliente " + id + " se va del restaurante||||||");
            monitor.salir();
            LocalDateTime salida=LocalDateTime.now();
            System.out.println("--------------Cliente " + id + " finaliza ejecucion---------------------");
            System.out.println("Entrada |"+entrada.toString()+"| Salida|"+salida.toString()+"|");
            System.out.println("---------------------------------------------------------------------------");
        } catch (InterruptedException ex) {
            System.out.println("--------------Cliente " + id + "  interrumpido--------------");
            
        }
    }
}
