/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controller;

import Leilao.Servidor;
import Leilao.ServidorImpl;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author stephany
 */
public class ThreadHandler extends Thread {
    
    public Thread t;
    public final String threadExec;
   
    
    public ThreadHandler(String name){
       threadExec = name;
    }
    
    /**
     * Mostra na tela data em um formato 'amigável'
     * @param milliseconds
     */
    public static void printHumanTime(long milliseconds)
    {
        Calendar calendario = GregorianCalendar.getInstance();
        calendario.setTimeInMillis(milliseconds);
        System.out.println(" Hora: " + calendario.get(Calendar.HOUR_OF_DAY));
        System.out.println(" Minuto: " + calendario.get(Calendar.MINUTE));
        System.out.println(" Segundo: " + calendario.get(Calendar.SECOND));
        System.out.println(" Millisegundo: " + calendario.get(Calendar.MILLISECOND));
    }
    
    /**
     * Thread que verifica os leilões ativos
     * Realiza verificação do tempo atual para o tempo em que o leilão foi cadastrado (tempo total de atuação)
     * Caso tenha ultrapassado o tempo em que o usuário cadastrou para o leilão ficar vigente,
     * chama a função encerraLeilao
     */
    public void checkAuctionTime()
    {
        while(true)
        {
            //Verifica leilões ativos;
            Servidor server = new Servidor();
            Map activeAuctions = server.getActiveAuctions();
            // Se houverem leilões ativos, começa a verificação
            if(!activeAuctions.isEmpty())
            {
                System.out.println("temmmm " + activeAuctions.size());
                Set<Long> chaves = activeAuctions.keySet();  
                for (Long chave : chaves)  
                {  
                    if(chave != null)  
                    {
                        Product objeto = (Product) activeAuctions.get(chave);
                        // Tempo que o leilão começou
                        long startTime = objeto.getStartTime();
                        // Tempo agora
                        long currentTime = System.currentTimeMillis();
                        // Quanto tempo passou desde que o leilão começou
                        int diff = (int) (currentTime - startTime);
                        
                        System.out.println("QUANDO COMEÇOU");
                        printHumanTime(startTime);
                        
                        System.out.println("AGORA");
                        printHumanTime(currentTime);
                        
                        // Verifica quanto tempo era para durar o leilão (em minutos)
                        int auctionTime = objeto.getAuctionTime();
                        
                        System.out.println("quantos minutos ficara? " + auctionTime);
                        // Minutos do millisegundos
                        long auctionTimeMilli = auctionTime * 60000;
                        
                        if(diff > auctionTimeMilli)
                        {
                            try {
                                ServidorImpl serverImpl = new ServidorImpl();
                                serverImpl.encerrarLeilao(objeto.codProduct);
                            } catch (RemoteException ex) {
                                Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                    }
                }
                
                
               
            }else
                System.out.println("Não tem leilão ativo");
            
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Cliente ativos: " + server.getActiveClients());
        }
    }
    
    /**
     * Método override para rodar threads
     */
    @Override
    public void run() 
    {
        if("checkAuctions".equals(threadExec))
        {
            checkAuctionTime();
        }
    }
    
    @Override
    public void start ()
    {
        if (t == null)
        {
            t = new Thread (this, threadExec);
            t.start ();
        }
    }

    
    
}

