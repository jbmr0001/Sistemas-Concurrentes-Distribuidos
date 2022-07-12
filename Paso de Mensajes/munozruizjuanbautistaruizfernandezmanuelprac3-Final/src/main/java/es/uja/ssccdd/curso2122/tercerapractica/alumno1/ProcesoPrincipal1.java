/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno1;

import static es.uja.ssccdd.curso2122.tercerapractica.alumno2.ProcesoPrincipal2.limpieza;
import es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes;
import static es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes.*;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
 * @author pedroj
 */
public class ProcesoPrincipal1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        LinkedList<String> lista= new LinkedList();
        for(int i=0;i<Constantes.NUM_CLIENTES;i++){
            lista.add(BUZON_CLIENTE+i+".respuestaEntrada");
        }
        for(int i=0;i<Constantes.NUM_CLIENTES;i++){
            lista.add(BUZON_CLIENTE+i+".recibePlato");
        }
        lista.add(Constantes.BUZON_RESTAURANTE);
        try {
            System.out.println("Borrando mensajes anteriores");
            limpieza(lista);
        } catch (JMSException ex) {
            System.out.println("HiloPrincipal: Problema con la conexión JMS");
        }
        System.out.println("PROCESO PRINCIPAL HA COMENZADO LA EJECUCION");
        ExecutorService ejecutor = Executors.newCachedThreadPool();
        boolean par=true;
        AtomicInteger idsPlatos = new AtomicInteger();
        for(int i=0;i<NUM_CLIENTES;i++){
            Cliente c;
            if(par){
                c = new Cliente(i,Tipo.ESTANDAR,idsPlatos);
                par=false;
            }else{
                c = new Cliente(i,Tipo.PREMIUM,idsPlatos);
                par=true;
            }
            
            ejecutor.submit(c);
            int tardaLlegar=random.nextInt(Constantes.MAX_TIEMPO_LLEGAR_CLIENTE-Constantes.MIN_TIEMPO_LLEGAR_CLIENTE+1)+Constantes.MIN_TIEMPO_LLEGAR_CLIENTE;
            TimeUnit.SECONDS.sleep(tardaLlegar);
        }
        
        ejecutor.shutdown();
        ejecutor.awaitTermination(1, TimeUnit.DAYS);
        
        System.out.println("PROCESO PRINCIPAL HA FINALIZADO LA EJECUCION");
    }
    public static void limpieza(LinkedList<String> lista) throws JMSException {
        // Creación de la conexión.
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constantes.CONNECTION);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Estableciendo una sesión.
        Session sesion = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Inicializando variables auxiliares.
        Destination destination = null;
        MessageConsumer consumer = null;
        TextMessage mensaje = null;

        // Limpieza de los buffers.
        for (String cadena : lista) {
            consumer = sesion.createConsumer(sesion.createQueue(cadena));
            MessageProducer producer = sesion.createProducer(sesion.createQueue(cadena));

            do {
                mensaje = (TextMessage) consumer.receiveNoWait();
            } while (mensaje != null);  // Obtenemos mensajes hasta que esté vacío.

            consumer.close();
        }

        connection.close();
    }
}
    
