/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno1;

import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionPlato;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionEntrada;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.CONNECTION;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author Pc
 */
public class Cliente implements Runnable{
    //Objetos que usaremos para el envío y recibo de mensajes
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination buzonRestaurante;
    private Destination respuestaEntrada;
    private Destination buzonPlatos;
    private MessageConsumer consumerAsincronoPlatos;
    //Objetos propios de la clase
    private final int id; //id del cliente
    private final Tipo tipo; //tipo Premium o Estandar
    private final int numPlatos; //Numero de platos que consumirá el cliente
    private LocalDateTime llegada; //Hora de llegada del cliente a la cola del restaurante
    private LocalDateTime entrada; //Hora de entrada del cliente al restaurante
    private LocalDateTime salida; //Hora de salida del cliente del restaurante
    private final LinkedList<Plato> platosRecibidos; //Lista de platos
    private final AtomicInteger idPlatos; //id generado para cada plato
    
    public Cliente(int id, Tipo tipo,AtomicInteger IDsPlatos) { 
        this.id = id;
        this.tipo = tipo;
        this.numPlatos=random.nextInt(Constantes.MAX_NUM_PLATOS - Constantes.MIN_NUM_PLATOS + 1) + Constantes.MIN_NUM_PLATOS; //platos generados entre 5 y 3
        this.platosRecibidos=new LinkedList();
        this.idPlatos=IDsPlatos;
    }

    @Override
    public void run() {
        System.out.println("--------------CLIENTE "+this.id+" HA COMENZADO LA EJECUCION--------------");
        try {
            before(); //Primero preparamos la estructura para mandar y recibir mensajes
            tarea(); //despues realizamos la ejecución con la que realizaremos la practica por parte del cliente
        } catch (Exception e) {
            System.out.println("Cliente " + id + "excepción" + e.getMessage());
        } finally {
            after(); //finalmente borramos la estructura creada
        }
        System.out.println("--------------CLIENTE "+this.id+" HA FINALIZADO LA EJECUCION--------------");
    }
    
    
    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(CONNECTION);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        buzonRestaurante = session.createQueue(BUZON_RESTAURANTE);
        respuestaEntrada = session.createQueue(BUZON_CLIENTE+id+".respuestaEntrada");   
    }
    
    public void tarea() throws JMSException{
        //Enviamos peticion entrada
        MessageProducer producer = session.createProducer(buzonRestaurante);
        Gson gson = new GsonBuilder().create();
        String mensaje1 =gson.toJson(new PeticionEntrada("Entrada",id,this.tipo.toString()));
        TextMessage message1 = session.createTextMessage(mensaje1);
        System.out.println("||Cliente "+this.id+" solicita entrar"+message1.getText()+"||");
        this.llegada=LocalDateTime.now();
        producer.send(message1);
        
        //Esperamos permiso para entrar
        MessageConsumer respuestaEntra = session.createConsumer(respuestaEntrada);
        TextMessage respuesta = (TextMessage) respuestaEntra.receive();
        this.entrada=LocalDateTime.now();
        
        //Enviamos platos
        producer = session.createProducer(buzonRestaurante);
        System.out.println("||Cliente "+this.id+" entra al restaurante");
        int precio;
        for(int i=0;i<this.numPlatos;i++){ 
            //Generamos precio
            if(tipo==Constantes.Tipo.ESTANDAR){
                precio=random.nextInt(Constantes.MAX_PRECIO_PLATO_ESTANDAR - Constantes.MIN_PRECIO_PLATO_ESTANDAR + 1) + Constantes.MIN_PRECIO_PLATO_ESTANDAR;
            }else{
                precio=random.nextInt(Constantes.MAX_PRECIO_PLATO_PREMIUM - Constantes.MIN_PRECIO_PLATO_PREMIUM + 1) + Constantes.MIN_PRECIO_PLATO_PREMIUM;
            }
            Plato plato=new Plato(this.idPlatos.getAndIncrement(),precio,this.id);
            String mensaje2=gson.toJson(new PeticionPlato("Plato",this.id,plato));
            TextMessage message2 = session.createTextMessage(mensaje2);
            System.out.println("||||Cliente "+this.id+" pide plato de precio "+plato.getPrecio());
            producer.send(message2);
        }
        
        //Esperamos a recibir todos los platos, invocamos listener
        buzonPlatos=session.createQueue(BUZON_CLIENTE+id+".recibePlato");
        consumerAsincronoPlatos=session.createConsumer(buzonPlatos);
        consumerAsincronoPlatos.setMessageListener(new ListenerCliente(this)); 
        while(platosRecibidos.size()<numPlatos){//Esperamos hasta recibir todos los pedidos
            System.out.print("");
        }
        System.out.println("||Cliente "+this.id+" sale del restaurante");
        salida=LocalDateTime.now();
        //Enviamos mensaje de salida
        producer = session.createProducer(buzonRestaurante);
        String mensaje3 =gson.toJson(new PeticionSalida("Salida",id,this.llegada,this.entrada,this.salida,this.platosRecibidos));
        TextMessage message3 = session.createTextMessage(mensaje3);
        
        producer.send(message3);
        producer.close();
        
        //System.out.println(this.toString());
    }
  

    public void after(){
        try {
            if (consumerAsincronoPlatos != null) {
                consumerAsincronoPlatos.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            // No hacer nada
        }
    }
    
    void recicirPlato(Plato plato) throws InterruptedException{
        this.platosRecibidos.add(plato);
        
        int comiendo=random.nextInt(Constantes.MAX_TIEMPO_COMIENDO - Constantes.MAX_TIEMPO_COMIENDO + 1) + Constantes.MIN_TIEMPO_COMIENDO;
        TimeUnit.SECONDS.sleep(comiendo);
        System.out.println("||||||||Cliente "+this.getId()+" ha comido plato"+plato.toString());
    }

    public int getId() {
        return id;
    }

    public int getNumPlatos() {
        return numPlatos;
    }

    public int getPlatosRecibidos() {
        return platosRecibidos.size();
    }

    @Override
    public String toString() {
        return "--------Datos Cliente finalizado--------\n"
                +"ID: "+this.id+"\n"+"Llegada:"+llegada+"\n"
             + "Entrada:"+entrada+"\n"
             + "Salida:"+salida+"\n"
             + "Lista de platos: \n"
             + this.platosRecibidos.toString() +"\n"
             +"--------Datos Cliente finalizado--------\n";
    }
   
}
