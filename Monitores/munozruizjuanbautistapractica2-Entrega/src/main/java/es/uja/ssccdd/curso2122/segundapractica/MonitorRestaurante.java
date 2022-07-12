/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.segundapractica;

import es.uja.ssccdd.curso2122.segundapractica.Constantes.Tipo;
import static es.uja.ssccdd.curso2122.segundapractica.Constantes.random;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Pc
 */
public class MonitorRestaurante {

    LinkedList<Plato> platosPedidos;
    Lock exclusionMutua;
    int capadidadDisponible;
    LinkedList<Condition> colaEntrada;
    LinkedList<Condition> bloqueoCliente;
    int idPlatoActual;
    LinkedList<Integer> platosPreparadosServir;
    int premiumEsperando;
    int premiumSeguidos;
    int totalGanado;
    int clientesRecibidos;
 

    public MonitorRestaurante() {
        this.exclusionMutua = new ReentrantLock();
        this.platosPedidos = new LinkedList();
        this.bloqueoCliente = new LinkedList();
        this.platosPreparadosServir = new LinkedList();
        for (int i = 0; i < Constantes.NUM_CLIENTES; i++) { //Inicializamos listas
            bloqueoCliente.add(exclusionMutua.newCondition());
            platosPreparadosServir.add(0);
        }
        this.capadidadDisponible = Constantes.MAX_CLIENTES_RESTAURANTE;
        this.colaEntrada = new LinkedList();
        this.colaEntrada.add(exclusionMutua.newCondition());
        this.colaEntrada.add(exclusionMutua.newCondition());
        this.idPlatoActual = 0;
        this.premiumEsperando = 0;
        this.premiumSeguidos = 0;
        this.totalGanado = 0;
        this.clientesRecibidos = 0;
    }

    void pedir(int cliente, Tipo tipo, int numPlatos) {
        exclusionMutua.lock();
        try {
            for (int i = 0; i < numPlatos; i++) {

                int precio;
                if (tipo == Tipo.ESTANDAR) {
                    precio = random.nextInt(Constantes.MAX_PRECIO_ESTANDAR_PLATO - Constantes.MIN_PRECIO_ESTANDAR_PLATO + 1) + Constantes.MIN_PRECIO_ESTANDAR_PLATO;
                } else {
                    precio = random.nextInt(Constantes.MAX_PRECIO_PREMIUM_PLATO - Constantes.MIN_PRECIO_PREMIUM_PLATO + 1) + Constantes.MIN_PRECIO_PREMIUM_PLATO;
                }
                Plato plato = new Plato(idPlatoActual, precio, cliente);
                idPlatoActual++;
                platosPedidos.add(plato);
                System.out.println("Plato pedido "+plato.toString());

            }
            //System.out.println("Lista de platos pedidos:");
            //System.out.println(platosPedidos.toString());
        } finally {
            exclusionMutua.unlock();
        }

    }

    public Plato siguientePlato() {
        Plato plato = new Plato(0, 0, 0);//Plato por defecto
        exclusionMutua.lock();
        //System.out.println("Platos pendientes:");
        //System.out.println(platosPedidos.toString());

        if (platosPedidos.size() > 0) {
            int mayorPrecio = 0;
            int indiceMayor = 0;
            for (int i = 0; i < platosPedidos.size(); i++) {
                if (platosPedidos.get(i).getPrecio() > mayorPrecio) {
                    mayorPrecio = platosPedidos.get(i).getPrecio();
                    plato = platosPedidos.get(i);
                    indiceMayor = i;
                }
            }
            plato = platosPedidos.remove(indiceMayor);
        }
        exclusionMutua.unlock();

        return plato;

    }

    void prepararServir(Plato plato) {
        exclusionMutua.lock();
        platosPreparadosServir.set(plato.getCliente(), platosPreparadosServir.get(plato.getCliente()) + 1);
        bloqueoCliente.get(plato.getCliente()).signal();
        this.totalGanado += plato.getPrecio();
        exclusionMutua.unlock();
    }

    void esperarPlato(int cliente) throws InterruptedException {
        exclusionMutua.lock();
        this.bloqueoCliente.get(cliente).await();
        exclusionMutua.unlock();
    }

    void solicitarEntrada(Tipo tipo) throws InterruptedException {
        exclusionMutua.lock();
        System.out.println("||||||Mesas disponibles=" + capadidadDisponible);
        if (capadidadDisponible > 0) {
            capadidadDisponible--;
        } else {
            System.out.println("||||||Cliente bloqueado en la cola");
            if (tipo == Tipo.PREMIUM) {
                this.premiumEsperando++;
            }
            colaEntrada.get(tipo.getOrdinal()).await();
            capadidadDisponible--;
            System.out.println("||||||Cliente desbloqueado de la cola");
            
        }
        exclusionMutua.unlock();
    }

    void salir() {
        exclusionMutua.lock();
            if (premiumEsperando != 0 && premiumSeguidos <Constantes.MAX_RACHA_PREMIUM) {
                this.colaEntrada.get(Tipo.PREMIUM.getOrdinal()).signal();
                this.premiumSeguidos++;
                premiumEsperando--;
            } else {
                premiumSeguidos = 0;
                this.colaEntrada.get(Tipo.ESTANDAR.getOrdinal()).signal();
            }

        capadidadDisponible++;
        clientesRecibidos++;
        exclusionMutua.unlock();
        
    }
    int verPlatosServidos(int cliente) throws InterruptedException {
        exclusionMutua.lock();
        int platos = platosPreparadosServir.get(cliente);
        exclusionMutua.unlock();
        return platos;
    }

    void mostrarTotalRecaudado() {
        exclusionMutua.lock();
        System.out.println("||||||||Se ha recaudado un total de: " + this.totalGanado + " Euros||||||||");
        System.out.println("||||||||Se han atentdido: " + this.clientesRecibidos + " Clientes||||||||");
        exclusionMutua.unlock();
    }
    
}
