/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.Plato;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.BUZON_COCINA;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.CONNECTION;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionEntrada;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionPlato;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.BUZON_RESTAURANTE;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.TIEMPO_COCINA_PLATO;

/**
 *
 * @author Pc
 */
public class Cocina implements Runnable{
    LinkedList<Plato> listaPlatos;
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    Destination peticionesPlato;
    Destination buzonRestaurante;
    private MessageConsumer consumerPeticiones;
    private int sumaPlatos;

    public Cocina() {
        this.listaPlatos=new LinkedList();
        sumaPlatos=0;
    }
    
    @Override
    public void run() {
        System.out.println("--------------PROCESO COCINA EMPIEZA EJECUCIÓN--------------");
         try {
            before();
            tarea();
        } catch (Exception e) {
            System.out.println("Restaurante excepción" + e.getMessage());
        } finally {
             System.out.println("----------------- EL PRECIO DEL TOTAL DE LOS PLATOS SERVIDOS ES DE: " + sumaPlatos + " ------------------------------");
            after();
        }
        System.out.println("--------------PROCESO COCINA FINALIZA EJECUCIÓN--------------");
       
    }
    
    void before() throws JMSException{
        connectionFactory = new ActiveMQConnectionFactory(CONNECTION);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        buzonRestaurante = session.createQueue(BUZON_RESTAURANTE);

        peticionesPlato = session.createQueue(BUZON_COCINA);
        consumerPeticiones = session.createConsumer(peticionesPlato);
        consumerPeticiones.setMessageListener(new ListenerCocina(this));
    }
    void tarea() throws JMSException, InterruptedException{
        
         boolean interrumpido=false;
         while(!interrumpido){
            if(Thread.interrupted()){
                interrumpido=true;
            }
            if(listaPlatos.size()!=0){
                Plato platoPreparado=listaPlatos.removeFirst();
                sumaPlatos=platoPreparado.getPrecio() + sumaPlatos;
                MessageProducer producer = session.createProducer(buzonRestaurante);
                Gson gson = new GsonBuilder().create();
                String mensaje1 =gson.toJson(new PeticionPlato("PlatoPreparado",platoPreparado.getId(),platoPreparado));
                TextMessage message1 = session.createTextMessage(mensaje1);
                System.out.println("||||Cocina ha preparado plato"+platoPreparado.toString()+"||||");
                producer.send(message1);
                TimeUnit.SECONDS.sleep(TIEMPO_COCINA_PLATO);
            }
         }
    }
    
    void after(){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            // No hacer nada
        }
    }
    
    void añadirPlato(Plato plato){
        this.listaPlatos.add(plato);
    }
    
}
