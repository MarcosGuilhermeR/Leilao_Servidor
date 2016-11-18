/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Leilao;


import java.rmi.Remote;
import java.rmi.RemoteException;

 
/**
 *
 * @author stephany
 */

public interface InterfaceServ extends Remote {
    
    String LOOKUP_NAME = "LeilaoService";
    
    byte[] listaProdutosAtivos() throws RemoteException;
    boolean darLance(long codProduct, double Value, String bidderName) throws RemoteException;
    boolean cadastrarProduto(long codProduct, String name, String description, double beginPrice, int timeauction, String sellerName) throws RemoteException;
    boolean encerrarLeilao(long codProduct) throws RemoteException;
    boolean cadastrarRefCli(String name, InterfaceCli cli) throws RemoteException;
    
    
}
