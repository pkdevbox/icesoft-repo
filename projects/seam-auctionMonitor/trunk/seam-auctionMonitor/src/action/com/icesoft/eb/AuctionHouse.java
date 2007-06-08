package com.icesoft.eb;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;

import com.icesoft.faces.async.render.RenderManager;

@Scope(ScopeType.APPLICATION)
public class AuctionHouse {

    @In
    private RenderManager renderManager;
    private static List auctionitemBeans;

    AuctionHouse(){
        init();
    }
    
    private void init(){
        //This will have to populate the initial AuctionitemBeans.       
    }
    
    public void itemCreated(AuctionitemBean itemBean){
                
    }
    
    public void itemExpired(AuctionitemBean itemBean){
        
    }

}
