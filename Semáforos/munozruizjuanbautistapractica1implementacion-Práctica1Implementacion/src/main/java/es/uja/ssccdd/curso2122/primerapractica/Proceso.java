/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;

import es.uja.ssccdd.curso2122.primerapractica.Constantes.Tipo;
import static es.uja.ssccdd.curso2122.primerapractica.Constantes.Tipo.INICIO;
import static es.uja.ssccdd.curso2122.primerapractica.Constantes.*;
import static es.uja.ssccdd.curso2122.primerapractica.Constantes.Tipo.EJECUCION;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pc
 */
public class Proceso implements Runnable{
    private final int id;
    private final LinkedList<Integer> tareaConRecurso;
    private int fallosRecurso;
    private final int totalTareas;
    private int tareaActual;
    private final Monitor monitor;
    

    public Proceso(int id, Monitor monitor) {
        this.id = id;
        this.monitor = monitor;
        this.fallosRecurso=0;
        this.tareaActual=0;
        this.totalTareas=generarTareas();
        this.tareaConRecurso=new LinkedList();
         
    }
    
    /**
     * Función para generar el número aleatorio de tareas
     * @return Entero con el número de tareas.
     */
    int generarTareas(){
        //System.out.println(random.nextInt(MAX_NUM_TAREAS-MIN_NUM_TAREAS+1)+MIN_NUM_TAREAS);
        return random.nextInt(MAX_NUM_TAREAS-MIN_NUM_TAREAS+1)+MIN_NUM_TAREAS;
    }
    
    /**
     * Función para generar el número aleatorio de ejecuciones
     * @return Entero con las ejecuciones.
     */
    int generarEjecuciones(){
        return random.nextInt(MAX_TAREAS_PROCESO-MIN_TAREAS_PROCESO+1)+ MIN_TAREAS_PROCESO ;
    }

    @Override
    public void run() {
        
        try {
            LocalDateTime inicio=LocalDateTime.now();
            System.out.println("PROCESO "+id+" INICIADO");
            inicio();
        
            System.out.println("PROCESO "+id+" EJECUTANDO");
            ejecucion();
            
            
            finalizacion();
            LocalDateTime fin=LocalDateTime.now();
            System.out.println("------------------------------");
            System.out.println("PROCESO "+id+" FINALIZADO");
            System.out.println("Inicio:"+inicio);
            System.out.println("Fin:"+fin);
            System.out.println("Numero Fallos: "+this.fallosRecurso);
            System.out.println("------------------------------");
        } catch (InterruptedException ex) {
            
            System.out.println("PROCESO "+id+" INTERRUMPIDO");
        }
        
    }
    
    void inicio() throws InterruptedException{
        
        this.monitor.addFinAsignacion(new Semaphore(0));
        this.monitor.addResultadoPeticion(false);
        
        peticionGestor(INICIO);
        esperarResolucion();
        
        actualizaRecursos();
    }
    
    void ejecucion() throws InterruptedException{
        boolean nuevoRecurso;
        int numEjecuciones;
        int ejecucion=0;
        numEjecuciones=generarEjecuciones();

        while(ejecucion<numEjecuciones){
            tareaActual=1+random.nextInt(totalTareas);
            System.out.println("Proceso"+id+" Ejecucion"+ejecucion);
            //Tiempo de espera por cada operacion
            TimeUnit.SECONDS.sleep(random.nextInt(Constantes.MAX_DURACION_OPERACION-Constantes.MIN_DURACION_OPERACION+1)+Constantes.MIN_DURACION_OPERACION);
            if(!tareaConRecurso.contains(tareaActual)){
                fallosRecurso++;
                peticionGestor(EJECUCION);
                nuevoRecurso=esperarResolucion();
                
                actualizaRecursos(nuevoRecurso,tareaActual);
                
            }
            ejecucion++;
        }
        
    }
    
    void finalizacion() throws InterruptedException{
        monitor.getExmRecurso().acquire();
        monitor.actualizarRecursosDisponibles(monitor.getRecursosDisponibles()+tareaConRecurso.size());
        monitor.incrementarProcesosFinalizados();
        monitor.actualizarFallosDeAsginacion(this.fallosRecurso);
        monitor.getExmRecurso().release();
        
    }
    
    void peticionGestor(Tipo tipo) throws InterruptedException{
        monitor.getMaxPeticiones().acquire();
        monitor.getExmPeticiones().acquire();
        monitor.añadirPeticion(new Peticion(id,tipo));
        System.out.println("Peticion de proceso "+id+" tipo "+tipo);
        monitor.getExmPeticiones().release();
        monitor.getNuevaPeticion().release();
            
    }
    
    boolean esperarResolucion() throws InterruptedException{
        boolean nuevoRecurso;
        monitor.getFinAsignacion(id).acquire();
        nuevoRecurso=monitor.getResultadoPeticion(id);
        System.out.println("Peticion de proceso "+id+" Aceptacion= "+nuevoRecurso);
        return nuevoRecurso;
    }
    
    void actualizaRecursos(){
        tareaConRecurso.add(1);
        tareaConRecurso.add(2);
    }
    
    void actualizaRecursos(boolean nuevoRecurso, int tarea){
        tareaConRecurso.add(tarea);
        if(nuevoRecurso==false){
            tareaConRecurso.remove(0);
        }
    }
     
}
