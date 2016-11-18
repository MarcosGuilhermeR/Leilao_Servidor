/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controller;

import java.io.Serializable;
import java.util.ArrayList;


/**
 *
 * @author stephany
 */
public class Product implements Serializable {
    
    protected long codProduct; // time em millisegundos no momento do cadastro do produto - unicidade
    protected String nameProduct;
    protected String descriptionProduct;
    protected double beginPriceProduct;
    protected int auctionTime;
    protected double highestBid = 0;
    protected String sellerName;
    protected long startAuctionTime = System.currentTimeMillis();  //inicializa o contador do leilão no momento do cadastro do novo produto
    protected String winnerName;
    protected ArrayList subscribers = new ArrayList(); // pessoas (possiveis compradores) interessados no leilão
    protected long endAuctionTime;
    
    
    public long getEndAuctionTime()
    {
        return this.endAuctionTime;
    }
    
    public void setEndAuctionTime(long endtime)
    {
        this.endAuctionTime = endtime;
    }
    
    public ArrayList getSubscribers()
    {
        return this.subscribers;
    }
    
    public void setSubscribers(ArrayList subs)
    {
        this.subscribers = subs;
    }
    
    public String getWinnerName()
    {
        return this.winnerName;
    }
    
    public void setWinnerName(String wname)
    {
        this.winnerName = wname;
    }
    
    
    public void setHighestBid(double bid)
    {
        this.highestBid = bid;
    }
    
    public long getCodProduct()
    {
        return this.codProduct;
    }
    
    public String getNameProduct()
    {
        return this.nameProduct;
    }
    
    public String getDescriptionProduct()
    {
        return this.descriptionProduct;
    }
    
    public double getBeginPrice()
    {
        return this.beginPriceProduct;
    }
    
    public int getAuctionTime()
    {
        return this.auctionTime;
    }
    
    public double gethighestBid()
    {
        if(this.highestBid == 0)        
        {
            return this.beginPriceProduct;
        }else
            return this.highestBid;
    }
    
    public long getStartTime()
    {
        return this.startAuctionTime;
    }
   
    public String getSellerName()
    {
        return this.sellerName;
    }
    
    public Product(long codProd, String nProduto, String dProduto, double pProduto, int tLeilao, String sellerNameS)
    {
        this.codProduct = codProd;
        this.nameProduct = nProduto;
        this.descriptionProduct = dProduto;
        this.beginPriceProduct = pProduto;
        this.auctionTime = tLeilao;
        this.sellerName = sellerNameS;
    }
    
}
