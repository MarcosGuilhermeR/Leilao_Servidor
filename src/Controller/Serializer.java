/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;


/**
 *
 * @author Stephany
 * (emprestimo do Daniel - com devidas adaptações)
 */
public class Serializer {
 
     /**
     * Serializa o objeto.
     *
     * @param obj o objeto a ser serializado.
     * @return o vetor de bytes com o objeto serializado.
     */
    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    
    /**
     * Deserializa o array de bytes e o transforma em um Map
     * @param hashmap
     * @return o vetor de bytes deserializado e convertido para Map 
     */
    public static Map deserializeHashMap(byte[] hashmap)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(hashmap);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return (Map) o;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } 
    }
    
}
