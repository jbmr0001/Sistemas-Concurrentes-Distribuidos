/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.uja.ssccdd.curso2122.tercerapractica.alumno1.Plato;
import es.uja.ssccdd.curso2122.tercerapractica.utils.PeticionEntrada;
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
//listener de la cocina que recibirá los platos desde el restaurante
public class ListenerCocina implements MessageListener{
    Cocina cocina;

    public ListenerCocina(Cocina cocina) {
        this.cocina = cocina;
    }
    
    @Override
    public void onMessage(Message msg) { //Cuando llega el mensaje
        TextMessage textMessage = (TextMessage) msg;
        Gson gson = new GsonBuilder().create();
        //saca el plato y lo añade a la cocina
        try {
             Plato p= gson.fromJson(((TextMessage) msg).getText(), Plato.class);
             //System.out.println("Plato recibido");
             this.cocina.añadirPlato(p);
        } catch (JMSException ex) {
            Logger.getLogger(ListenerCocina.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
