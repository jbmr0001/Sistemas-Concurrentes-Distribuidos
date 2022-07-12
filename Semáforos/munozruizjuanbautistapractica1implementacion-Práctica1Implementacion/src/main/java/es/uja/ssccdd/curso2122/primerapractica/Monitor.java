/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.primerapractica;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Pc
 */
public class Monitor {
    private final Semaphore maxPeticiones;
    private final Semaphore exmPeticiones;
    private final Semaphore exmRecurso;
    private final Semaphore nuevaPeticion;
    private final LinkedList<Semaphore> finAsignacion;
    private  int recursosDisponibles;
    
    private final LinkedList<Peticion> bufferPeticiones;
    private final LinkedList<Boolean>resultadoPeticion;
    
    //Variables para las estadisticas
    int procesosFinalizados;
    int totalProcesos;
    int totalFallosAsignacion;
    int totalAsignacion;
    
    public Monitor(int tamBuffer,int max) {
      exmPeticiones=new Semaphore(1);
      exmRecurso=new Semaphore(1);
      maxPeticiones=new Semaphore(tamBuffer);
      nuevaPeticion=new Semaphore(0);
      bufferPeticiones=new LinkedList();
      recursosDisponibles=max;
      this.finAsignacion=new LinkedList();
      this.resultadoPeticion=new LinkedList();
    }

    /////MÉTODOS DE ACCESO A LOS SEMÁFOROS////////
    public Semaphore getMaxPeticiones() {
        return maxPeticiones;
    }

    public Semaphore getExmPeticiones() {
        return exmPeticiones;
    }

    public Semaphore getExmRecurso() {
        return exmRecurso;
    }

    public Semaphore getNuevaPeticion() {
        return nuevaPeticion;
    }
    
    /////////GETTERS////////
    Peticion getPeticion(){
        return bufferPeticiones.removeFirst(); 
    }
    
    public int getRecursosDisponibles() {
        return recursosDisponibles;
    }
    
    public Semaphore getFinAsignacion(int id) {
        return this.finAsignacion.get(id);  
    }
    ////MÉTODOS PARA ACTUALIZAR Y AÑADIR/////
    public void actualizarRecursosDisponibles(int recursos){
        this.recursosDisponibles=recursos;
    }
    
    public void añadirPeticion(Peticion peticion){
        bufferPeticiones.add(peticion);
    }
   
    public void addFinAsignacion(Semaphore s){
        this.finAsignacion.add(s);
    }
    
    public void addResultadoPeticion(Boolean r){
        this.resultadoPeticion.add(r);
    }
    
    public Boolean getResultadoPeticion(int id) {
        return this.resultadoPeticion.get(id);
        
    }
    
    public LinkedList<Semaphore> getFinAsignacion() {
        return this.finAsignacion;
        
    }
    
    public LinkedList<Boolean> getResultadoPeticion() {
        return this.resultadoPeticion;
    }
    
    public void incrementarProcesosFinalizados(){
        this.procesosFinalizados++;
    }
    
    public void incrementarTotalProcesos(){
        totalProcesos++;
    }
    
    public void actualizarFallosDeAsginacion(int num){
        this.totalFallosAsignacion+=num;
    }
    
    public void actualizarRecursosAsignados(int num){
        this.totalAsignacion+=num;
    }
    /**
     * Función para mostrar los datos pedidos al final de la ejecución
     */
    public void informacion(){
        System.out.println("Num de procesos finalizados: "+this.procesosFinalizados);
        System.out.println("Media de Fallos de Asignacion: "+(float)totalFallosAsignacion/totalProcesos);
        System.out.println("Media de Recursos asignados: "+(float)this.totalAsignacion/totalProcesos);
    }
}
