/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Leilao;

import Controller.Product;
import Controller.ThreadHandler;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author stephany
 */
public class Servidor {
    
    public static Map<Long, Product> activeAuctions = new HashMap<Long, Product>();
    public static Map<String, InterfaceCli> activeClients = new HashMap<String, InterfaceCli>();
    
    public void setActiveAuctions(Map aauctions)
    {
        Servidor.activeAuctions = aauctions;
    }
    
    public Map getActiveAuctions()
    {
        return Servidor.activeAuctions;
    }
    
    public void setActiveClients(Map aclients)
    {
        Servidor.activeClients = aclients;
    }
    
    public Map getActiveClients()
    {
        return Servidor.activeClients;
    }
    
    /**
     * Inicializa o servidor de consulta de nomes 
     * @throws RemoteException 
     */
    public static void startRMIServerName() throws RemoteException
    {
        // Segundo parâmetro do rebind é a instância da implementação da interface do servidor
        ServidorImpl servidorImplements = new ServidorImpl();
        
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind(InterfaceServ.LOOKUP_NAME, servidorImplements);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {

        // Inicializa o servidor de nomes
        try {
            startRMIServerName();
        } catch (RemoteException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        // Inicializa thread para verificação do término do leilão
        ThreadHandler thandler = new ThreadHandler("checkAuctions");
        thandler.start();
    }
    
    
    /**
     * Mapeia os leilões ativos
     * @param prod 
     * @return boolean
     */
    public boolean mapActiveAuctions(Product prod)
    {
        long codProduct = prod.getCodProduct();
        
        Map<Long, Product> activeProducts = getActiveAuctions();
        if(!activeProducts.containsKey(codProduct))
        {
            activeProducts.put(codProduct, prod);
            setActiveAuctions(activeProducts);
            return true;
        }
        
        return false;
    }
    
    /**
     * Mapeamento de clientes que entram na plataforma pela primeira vez
     * @param name
     * @param cliRef
     * @return 
     */
    public boolean MapActiveClients(String name, InterfaceCli cliRef)
    {
        Map<String, InterfaceCli> actCli = getActiveClients();
        if(!actCli.containsKey(name))
        {
            actCli.put(name.toLowerCase().trim(), cliRef);
            setActiveClients(actCli);
            
            return true;
        }else // quando o nome já foi cadastrado 
        {
            return false;
        }
    }
    
    
    /**
     * Mapeia interessados em determinado produto
     * @param bName
     * @param curProduct
     */
    public void mapRefProductSubscribers(String bName, Product curProduct)
    {
        ArrayList subscribers = curProduct.getSubscribers();
        if(!subscribers.contains(bName))
        {
            subscribers.add(bName);
            curProduct.setSubscribers(subscribers);
        }
    }
    
    
    
    /**
     * Remove leilões inativos
     * @param codProduct 
     * @return  
     */
    public boolean removeInactiveAuctions(long codProduct)
    {
        Map activeAuctions1 = getActiveAuctions();
        if(activeAuctions1.containsKey(codProduct))
        {
            activeAuctions1.remove(codProduct);
            setActiveAuctions(activeAuctions1);
            return true;
        }
        
        return false;
    }
    
    /**
     * Envia notificação de acordo com a referencia de objeto do cliente
     * @param name
     * @param msgToSend 
     */
    public void sendNotif(String name, String msgToSend)
    {
        @SuppressWarnings("LocalVariableHidesMemberVariable")
        Map activeClients = getActiveClients();
        
        // Se houver referência do cliente cadastrada
        if(activeClients.containsKey(name.toLowerCase().trim())) 
        {
            InterfaceCli refObjCli = (InterfaceCli) activeClients.get(name.toLowerCase().trim());
            System.out.println("tem referenciiiiiia: " + refObjCli );
            try {
                refObjCli.receberNotificacao(msgToSend);
            } catch (RemoteException ex) {
                /* Se caiu aqui, provavelmente o cliente fechou sua plataforma
                 * Pois tem a referencia dele mapeada, mas não foi possível enviar a mensagem
                 *  através do canal de comunicação
                 */
                System.out.println("REMOTE EXCEPTION: Não conseguiu enviar ao usuário mensagem assíncrona");
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else
        {
            System.out.println("Referência de Objeto do cliente " + name + " não encontrada");
        }
    }
    
    /**
     * Gerencia envio de notificação ao leiloeiro e interessados no respectivo produto
     * Método tem retorno void pelo fato da comunicação ser assíncrona;
     * (sem garantia de entrega)
     * @param curProduct
     * @param msg 
     */
    public void notifProductSubscribers(Product curProduct, String msg)
    {
        // Envia notificação para o dono 
        sendNotif(curProduct.getSellerName(), msg);
            
        // Envia notificação para os 'subscribers' do produto
        ArrayList subscribers = curProduct.getSubscribers();
        for(int n=0; n< subscribers.size(); n++)
        {
            String clientSubs = (String) subscribers.get(n);
            sendNotif(clientSubs, msg);
        }
        
    }
    
    
    
    
}
