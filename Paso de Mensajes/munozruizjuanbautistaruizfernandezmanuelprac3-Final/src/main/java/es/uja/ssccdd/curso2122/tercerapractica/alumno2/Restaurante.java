/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.PeticionSalida;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.Plato;
import es.uja.ssccdd.curso2122.tercerapractica.utils.Peticion;
import es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.BUZON_RESTAURANTE;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.CONNECTION;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.BUZON_CLIENTE;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionEntrada;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionPlato;
import java.time.LocalDateTime;
import java.util.LinkedList;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import static org.apache.activemq.store.kahadb.data.KahaDestination.DestinationType.QUEUE;

/**
 *
 * @author Pc
 */
//Clase restaurante que hace de intermediaria entre la cocina y los clientes, permitiendo la entrada y salida de estos
public class Restaurante implements Runnable{
    //variables para el manejo de los buzones y los envios
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private int capacidad; //capacidad del restaurante
    private MessageConsumer consumerEntrada;
    
    //colas usadas 
    private final LinkedList<Integer> colaPremium; //cola de id de clientes premium
    private final LinkedList<Integer> colaEstandar; //cola id de clientes estandar
    //mas variables para los buzones
    Destination peticionesEntrada;
    Destination buzonCocina;
    private final LinkedList<Destination> respuestaEntrada;
    
    private int seguidosPremium; //numero de clientes seguidos de tipo premium
    private final LinkedList<Plato> platosPedidos; //lista con los platos que se han pedido y aun no han sido atendidos
    LinkedList<Plato> colaEnvio; //cola con los platos listos para ser enviados a los clientes 
    LinkedList<Destination> respuestaPlato; //cola con los destinos para los platos terminados
    LinkedList<PeticionSalida> clientesServidos; //cola con los datos de los clientes uqe ya terminaron

    public Restaurante() {
        this.capacidad=Constantes.CAPACIDAD_RESTAURANTE;
        this.colaPremium=new LinkedList();
        this.respuestaEntrada=new LinkedList();
        this.seguidosPremium=0;
        this.colaEstandar=new LinkedList();
        this.platosPedidos=new LinkedList();
        this.colaEnvio=new LinkedList();
        this.respuestaPlato=new LinkedList();
        this.clientesServidos=new LinkedList();
    }

    @Override //metodo run con el que ejecutaremos la practica, primero se prepararan los buzones y envios 
    public void run() {
        System.out.println("--------------RESTAURANTE COMIENZA EJECUCION--------------");
         try {
            before();
            tarea(); //luego se ejecutan las funciones de la practica
        } catch (Exception e) {
            System.out.println("Restaurante excepción" + e.getMessage());
        } finally {
            after(); //finalmente se destruyen los buzones
        }
        System.out.println("--------------RESTAURANTE FINALIZANDO EJECUCION--------------");
    }
    
    
    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(CONNECTION);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        peticionesEntrada = session.createQueue(BUZON_RESTAURANTE);
        consumerEntrada = session.createConsumer(peticionesEntrada);
        consumerEntrada.setMessageListener(new ListenerRestaurante(this));
        
        buzonCocina= session.createQueue(Constantes.BUZON_COCINA);
        for(int i=0;i<Constantes.NUM_CLIENTES;i++){
            respuestaEntrada.add(session.createQueue(BUZON_CLIENTE+i+".respuestaEntrada"));  
        }
        
        for(int i=0;i<Constantes.NUM_CLIENTES;i++){
            respuestaPlato.add(session.createQueue(BUZON_CLIENTE+i+".recibePlato"));  
        }
    }
    
    public void tarea() throws JMSException{
        boolean interrumpido=false;
        MessageProducer producer;
        TextMessage men;
        //mientras no se interrumpa al restaurante
        while(!interrumpido){
            
            if(Thread.interrupted()){
                interrumpido=true;
            }
             //se comprueba si hay hueco y se añaden mas clientes
            if(capacidad!=0){
                int cliente=siguienteCliente();
                if(cliente!=-1){
                    capacidad--;
                    producer=session.createProducer(respuestaEntrada.get(cliente));
                    men=session.createTextMessage();
                    Gson gson = new GsonBuilder().create();
                    String mensaje ="Confirmado";
                    TextMessage message = session.createTextMessage(mensaje);
                    producer.send(message);
                    producer.close();
                }
            }
            //Si hay platos ya pedidos por el cliente
            if(this.platosPedidos.size()!=0){
                //System.out.println("--------Platos pedidos--------");
                //System.out.println(platosPedidos.toString());
                //System.out.println("--------Platos pedidos--------");
                Plato min=new Plato(-1,-1,-1);
                int indice=0; //se obtiene el palto más caro
                for(int i=0;i<platosPedidos.size();i++){
                    if(platosPedidos.get(i).getPrecio()>min.getPrecio()){
                        min=platosPedidos.get(i); 
                        indice=i;
                    }
                }
                
                platosPedidos.remove(indice); 
                producer=session.createProducer(this.buzonCocina);
                Gson gson = new GsonBuilder().create();
                String mensaje1 =gson.toJson(min); //se crea el mensaje para mandarlo a la cocina con el plato
                TextMessage message1 = session.createTextMessage(mensaje1); 
                //System.out.println("CocinaSolicitaPlatoCocina"+message1.getText());
                producer.send(message1); //se manda el plato
                producer.close();
            } 
                //si hay platos ya cocinados
            if(this.colaEnvio.size()!=0){
                Plato plato=colaEnvio.removeFirst(); //se saca el primero
                
                producer = session.createProducer(this.respuestaPlato.get(plato.getCliente())); //se busca el buzon del cliente concreto
                Gson gson = new GsonBuilder().create();
                String mensaje1 =gson.toJson(plato);
                TextMessage message1 = session.createTextMessage(mensaje1); 
                //System.out.println("Plato enviado "+plato.toString()+"a"+this.respuestaEntrada.get(plato.getCliente())); //se notifica que dicho plato fue enviado
                producer.send(message1); //se le manda al cliente
                producer.close();
            }
            
        }
        
        this.mostrarClientes(); //una vez se termina todo se muestran los clientes
    }
    
    public void after() {
        try {
            if (consumerEntrada != null) {
                consumerEntrada.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            // No hacer nada
        }
    }
     //funcion para añadir un cliente a una cola segun su tipo
    void añadirClienteCola(int id,String tipo){
        if(tipo.equals("ESTANDAR")){
            this.colaEstandar.add(id);
        }else{
            this.colaPremium.add(id);
        }
        
    }
     //funcion que añade peticiones de platos a la cola
    void añadirPlatoCola(Plato plato){
        this.platosPedidos.add(plato); 
    }
     //Funcion que elige el cliente que va a entrar siguiente
    int siguienteCliente(){
        //System.out.println("Capacidad"+capacidad);
        if(colaPremium.size()!=0){
            if(seguidosPremium==Constantes.MAX_PREMIUM_SEGUIDOS && colaEstandar.size()!=0){
                seguidosPremium=0;
                return colaEstandar.removeFirst();  
            }
            seguidosPremium++;
            return colaPremium.removeFirst();
        }else if(colaEstandar.size()!=0){
            return colaEstandar.removeFirst();
        }else{
            return -1;
        }
      
    } //funcion que saca al cliente del todo del restaurante
    void eliminarCliente(PeticionSalida p){
        this.clientesServidos.add(p); // se añaden sus datos a los clientes servidos
        this.capacidad++; // se suma 1 a la capacidad
    }
    //se añaden platos ya preparados a la cola para ser enviados
    void añadirColaEnvio(Plato plato){
        this.colaEnvio.add(plato);
    }
    //se obtiene la capacidad
    public int getCapacidad() {
        return capacidad;
    }
    //se muestran los clientes y sus datos
    void mostrarClientes(){
        for (int i=0;i<clientesServidos.size();i++){
            PeticionSalida cliente=this.clientesServidos.get(i);
            System.out.println( "--------Datos Cliente finalizado--------\n"
                +"ID: "+cliente.getId()+"\n"+"Llegada:"+cliente.getLlegada()+"\n"
             + "Entrada:"+cliente.getEntrada()+"\n"
             + "Salida:"+cliente.getSalida()+"\n"
             + "Lista de platos: \n"
             + cliente.getPlatosRecibidos().toString() +"\n"
             +"--------Datos Cliente finalizado--------\n");
        }
    }
}
