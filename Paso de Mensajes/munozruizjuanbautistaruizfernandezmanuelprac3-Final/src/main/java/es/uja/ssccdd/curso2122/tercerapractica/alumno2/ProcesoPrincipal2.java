/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno2;

import es.uja.ssccdd.curso2122.tercerapractica.utils.Constantes;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
public class ProcesoPrincipal2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        LinkedList<String> lista= new LinkedList(); 
        lista.add(Constantes.BUZON_RESTAURANTE);
        lista.add(Constantes.BUZON_COCINA);
        try {
            System.out.println("Borrando mensajes anteriores");
            limpieza(lista);
        } catch (JMSException ex) {
            System.out.println("HiloPrincipal: Problema con la conexión JMS");
        }
        ExecutorService ejecutor = Executors.newCachedThreadPool(); //se crea el ejecutor
        Restaurante rest= new Restaurante(); //se crea el restaurante
        Cocina coc=new Cocina(); //se crea la cocina
        Future <?> pr= ejecutor.submit(rest); //se ejecuta el restaurante
        Future <?> pc=ejecutor.submit(coc); //se ejecuta la cocina 
        TimeUnit.SECONDS.sleep(Constantes.TIEMPO_ESPERA_RESTAURANTE); //el main espera 150 segs hasta terminar con la ejecucion de ambas
        pr.cancel(true);
        pc.cancel(true);
    }
    //funcion para limpiar los buzones
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
