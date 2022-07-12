/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uja.ssccdd.curso2122.tercerapractica.alumno1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class ListenerCliente implements MessageListener {
    Cliente cliente;

    public ListenerCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    @Override
    public void onMessage(Message msg) {
        Gson gson = new GsonBuilder().create();
        try {
            Plato p= gson.fromJson(((TextMessage) msg).getText(), Plato.class);
            System.out.println("||||||Cliente "+cliente.getId()+" recibe plato"+p.toString());
            cliente.recicirPlato(p);
        } catch (JMSException ex) {
            Logger.getLogger(ListenerCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ListenerCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
