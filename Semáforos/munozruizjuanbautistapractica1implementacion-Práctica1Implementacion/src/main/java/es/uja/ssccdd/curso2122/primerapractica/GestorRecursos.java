/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;

import static es.uja.ssccdd.curso2122.primerapractica.Constantes.*;
import es.uja.ssccdd.curso2122.primerapractica.Constantes.Tipo;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static es.uja.ssccdd.curso2122.primerapractica.Constantes.MINIMO_RECURSOS;

/**
 *
 * @author Pc
 */
public class GestorRecursos implements Runnable {

    private final LinkedList<Integer> recursosProcesos;
    private int fallosAsignacion;
    private final Monitor monitor;

    public GestorRecursos(Monitor monitor) {
        this.monitor = monitor;
        recursosProcesos = new LinkedList();
        fallosAsignacion = 0;
    }

    @Override
    public void run() {
        System.out.println("EL GESTOR HA COMENZADO");
        Peticion peticion;
        try {
            while (true) {
                peticion = obtenerPeticion();
                resolverPeticion(peticion);
            }

        } catch (InterruptedException ex) {
            //Logger.getLogger(GestorRecursos.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("GESTOR HA TERMINADO");
        }

    }

    Peticion obtenerPeticion() throws InterruptedException {
        Peticion peticion;
        
        monitor.getNuevaPeticion().acquire();
        monitor.getExmPeticiones().acquire();
        peticion = monitor.getPeticion();
        monitor.getExmPeticiones().release();
        monitor.getMaxPeticiones().release();

        return peticion;
    }

    boolean resolverPeticion(Peticion peticion) throws InterruptedException {
        boolean nuevoRecurso;

        if (peticion.getTipo() == Tipo.INICIO) {
            nuevoRecurso = obtenerRecurso(peticion);
            if (nuevoRecurso == false) {
                posponer(peticion);
            } else {
                asignarRecursos(peticion, nuevoRecurso);
            }
        } else {
            nuevoRecurso = obtenerRecurso(peticion);
            asignarRecursos(peticion, nuevoRecurso);
        }
        return nuevoRecurso;
    }

    boolean obtenerRecurso(Peticion peticion) throws InterruptedException {
        boolean nuevoRecurso;
        if (peticion.getTipo() == Tipo.INICIO) {
            monitor.getExmRecurso().acquire();
            if (monitor.getRecursosDisponibles() >= MINIMO_RECURSOS) {
                nuevoRecurso = true;
                monitor.actualizarRecursosDisponibles(monitor.getRecursosDisponibles() - MINIMO_RECURSOS);
                int aumentarLista=peticion.getId()-recursosProcesos.size();
                if(aumentarLista>=0){
                    for (int i=0; i<=aumentarLista; i++){
                        recursosProcesos.add(0);
                    }
                }
                recursosProcesos.set(peticion.getId(), MINIMO_RECURSOS);
                monitor.actualizarRecursosAsignados(MINIMO_RECURSOS);
            } else {
                nuevoRecurso = false;
                fallosAsignacion++;
            }
            monitor.getExmRecurso().release();
        } else {
            monitor.getExmRecurso().acquire();
            if (monitor.getRecursosDisponibles() > 0 && recursosProcesos.get(peticion.getId()) < MAXIMO_RECURSOS) {
                nuevoRecurso = true;
                monitor.actualizarRecursosDisponibles(monitor.getRecursosDisponibles() - 1);
                recursosProcesos.set(peticion.getId(), recursosProcesos.get(peticion.getId()) + 1);
                
                monitor.actualizarRecursosAsignados(1);
            } else {
                nuevoRecurso = false;
                fallosAsignacion++;
            }
            monitor.getExmRecurso().release();
        }
        return nuevoRecurso;
    }

    void posponer(Peticion peticion) throws InterruptedException {
        monitor.getExmPeticiones().acquire();
        monitor.añadirPeticion(peticion);
        monitor.getExmPeticiones().release();
        monitor.getNuevaPeticion().release();
    }

    void asignarRecursos(Peticion peticion, boolean nuevoRecurso) {
        int idProceso;
        idProceso = peticion.getId();
        monitor.getResultadoPeticion().set(idProceso, nuevoRecurso);
        monitor.getFinAsignacion().get(idProceso).release();
    }

}
