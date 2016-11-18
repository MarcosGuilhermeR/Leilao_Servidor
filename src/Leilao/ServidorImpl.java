/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Leilao;

import Controller.Product;
import Controller.Serializer;
import java.io.Serializable;


/**
 *
 * @author stephany
 */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;



public class ServidorImpl extends UnicastRemoteObject implements InterfaceServ, Serializable {
    
    public ServidorImpl() throws RemoteException {
        super(0);    
        
    }

    /**
     * Ao receber um novo lance no produto, esta função atualiza os dados do produto
     * e envia aos interessados no mesmo uma mensagem assíncrona informando quem está ganhando
     * @param codProduct
     * @param Value
     * @param bidderName
     * @return boolean (status da realização do lance)
     * @throws RemoteException 
     */
    @Override
    public synchronized boolean darLance(long codProduct, double Value, String bidderName) throws RemoteException {
        
        Servidor server = new Servidor();
        Map activeAuctions = server.getActiveAuctions();
        
        if(activeAuctions.containsKey(codProduct))
        {
            Product curProduct  = (Product) activeAuctions.get(codProduct);
            
            // Mapeia o interessado dentro do mapa do produto (subscribers)
            server.mapRefProductSubscribers(bidderName, curProduct);
            
            curProduct.setHighestBid(Value);        // Define como o lance mais alto
            curProduct.setWinnerName(bidderName);   // Define como o nome do vencedor

            // Notifica Clientes/Vendedor
            String bidMsg = "Um novo lance no produto " + curProduct.getNameProduct() + " realizado pelo cliente " + curProduct.getWinnerName() + " no valor de " + curProduct.gethighestBid();
            server.notifProductSubscribers(curProduct, bidMsg);
            
            return true;
        }
        return false;
    }

    /**
     * 
     * @param productCod
     * @param name
     * @param description
     * @param beginPrice
     * @param timeauction
     * @param sellerName
     * @return mapeamento do novo produto foi feita com sucesso
     * @throws RemoteException 
     */
    @Override
    public synchronized boolean cadastrarProduto(long productCod, String name, String description, double beginPrice, int timeauction, String sellerName) throws RemoteException {
        Product prod = new Product(productCod, name, description, beginPrice, timeauction, sellerName);
        Servidor server = new Servidor();
        
        server.notificaTodos("atualizar");
        
        return server.mapActiveAuctions(prod);
    }

    /**
     * Encerra o leilão, caso o tempo ultrapasse do cadastrado ou o dono do produto tenha cancelado
     * Esta operação dispara mensagens assíncronas a todos os interessados no produto
     * Avisando quem ganhou o leilão (se tiver havido algum lance)
     * @param productCod
     * @return (status da realização do término do leilão)
     * @throws RemoteException 
     */
    @Override
    public synchronized boolean encerrarLeilao(long productCod) throws RemoteException {
        
        boolean finishAuction = false;
        
        Servidor server = new Servidor();
        Map activeAuctions = server.getActiveAuctions();
        if(activeAuctions.containsKey(productCod))
        {
            Product curProduct = (Product) activeAuctions.get(productCod);
            
            // Mensagem Notificação Encerramento
            String finalAuctionMsg = "Leilão do produto " + curProduct.getNameProduct() + " foi finalizado.\n ";
            if(curProduct.getWinnerName() != null)
                finalAuctionMsg += "O lance de arremate foi no valor de " +  curProduct.gethighestBid() + " \n dado pelo cliente " + curProduct.getWinnerName();
            else
                finalAuctionMsg += "\nNão foi realizado nenhum lance para este produto.";
            
            // Notificação assíncrona
            server.notifProductSubscribers(curProduct, finalAuctionMsg);
            
            if(server.removeInactiveAuctions(curProduct.getCodProduct()))
            {
                curProduct.setEndAuctionTime(System.currentTimeMillis());
                finishAuction =  true;
            }
        }
        
        return finishAuction;
    }

    /**
     * Lista produtos ativos
     * @return array de bytes com os produtos ativos do leilão
     * @throws RemoteException 
     */
    @Override
    public byte[] listaProdutosAtivos() throws RemoteException {
        
        Servidor server = new Servidor();
        Map activeAuctions = server.getActiveAuctions();
         
        byte[] productMap = null;
        if(!activeAuctions.isEmpty())
        {
            productMap = Serializer.serialize((Serializable) activeAuctions);
        }
        
        
       return productMap;
    }

    
    /**
     * Mapeia o cliente e sua respectiva referência
     * @param name
     * @param cli
     * @return (status da realização do mapeamento)
     */
    @Override
    public boolean cadastrarRefCli(String name, InterfaceCli cli) {
        Servidor server = new Servidor();
        return server.MapActiveClients(name, cli);
    }

   

    
}
