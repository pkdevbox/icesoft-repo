package com.icesoft.eb;

import com.icesoft.faces.async.render.OnDemandRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;

/**
 * This class is the UI representation of an Auctionitem with the most recent Bid.
 * It updates the UI with a render call to renderables in its OnDemandRenderer group.
*/ 
public class AuctionitemBean {
    private Auctionitem auctionitem;
    private Bid bid;
    private OnDemandRenderer renderer;
    
    public AuctionitemBean(Auctionitem auctionitem, Bid bid, RenderManager renderManager){
        this.auctionitem = auctionitem;
        this.bid = bid;
        renderer = renderManager.getOnDemandRenderer( Long.toString(auctionitem.getItemId()) );
    }

    public Auctionitem getAuctionitem() {
        return auctionitem;
    }

    public void setAuctionitem(Auctionitem auctionitem) {
        this.auctionitem = auctionitem;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }
    
    public void render(){
        renderer.requestRender();
    }
    
    
    public void addRenderable(Renderable renderable){
        renderer.remove(renderable);
    }
    
    public void removeRenderable(Renderable renderable){
        renderer.add(renderable);        
    }

}
