/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.PeticionSalida;
import es.uja.ssccdd.curso2122.tercerapractica.utils.Peticion;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionEntrada;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionPlato;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Pc
 */
//Listener del restaurante
public class ListenerRestaurante implements MessageListener{
    Restaurante restaurante;
    String tipo;
    public ListenerRestaurante(Restaurante restaurante) {
        this.restaurante=restaurante;
    }

    @Override
    public void onMessage(Message msg) {//segun el mensaje que le llegue así hace
        
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            Gson gson = new GsonBuilder().create();
            try {
                //System.out.println("Hay mensaje"+textMessage.getText());
                String[] parts = textMessage.getText().split(",");  //se divide el mensaje en 2
                String[] parts2 = parts[parts.length-2].split(":"); 
                String a=parts2[1]; //de la parte 2 se obtiene el tipo de mensaje que es
                //System.out.println(a);
                if("\"Entrada\"".equals(a)){ //si es un mensaje de entrada se muestra que ha llegado la petición y los huecos restantes
                    PeticionEntrada p=gson.fromJson(((TextMessage) msg).getText(), PeticionEntrada.class);
                    System.out.println("Peticion entrada recibida "+p.toString());
                    System.out.println("******Personas pueden entrar: "+restaurante.getCapacidad()+"******");
                    
                    this.restaurante.añadirClienteCola(p.getId(),p.getTipoCliente()); //se añade el cliente a la lista de clientes para entrar
                }else if("\"Plato\"".equals(a)){ //si es un mensaje de tipo plato 
                    PeticionPlato p=gson.fromJson(((TextMessage) msg).getText(), PeticionPlato.class);  
                    //System.out.println("Peticion plato recibida"+p.toString());
                    this.restaurante.añadirPlatoCola(p.getPlato()); //se añade el plato a la cola de platos del restaurante
                }else if("\"PlatoPreparado\"".equals(a)){ //si es un plato preparado por la cocina
                    PeticionPlato p=gson.fromJson(((TextMessage) msg).getText(), PeticionPlato.class);  
                    //System.out.println("Cocina recibe plato preparado"+p.toString());
                    this.restaurante.añadirColaEnvio(p.getPlato()); //se añade a la cola de envios de los platos
                }else if("\"Salida\"".equals(a)){ //si es un mensaje de tipo salida
                    System.out.println("Peticion de salida recibida"); //se notifica  que llegó
                    PeticionSalida p=gson.fromJson(((TextMessage) msg).getText(), PeticionSalida.class);
                    this.restaurante.eliminarCliente(p); //se elimina al cliente de la cola de clientes
                    System.out.println("******Personas pueden entrar: "+restaurante.getCapacidad()+"******");
                }
            } catch (JMSException ex) {
                Logger.getLogger(ListenerRestaurante.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

