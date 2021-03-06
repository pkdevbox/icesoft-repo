/*
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2011 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 */

package com.icesoft.faces.component.menubar;

import com.icesoft.faces.component.ext.HtmlCommandLink;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import java.util.List;
import java.util.Iterator;


/**
 *
 */
public abstract class MenuItemBase extends UICommand
        implements NamingContainer {

    protected static String DEFAULT_CSS_IMAGE_DIR = "";

    public MenuItemBase() {
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
     */
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }

        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processDecodes(context);
        }
        
        try {
            decode(context);
        } catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent event) {
        super.queueEvent(event);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        return;

    }
    
    boolean isChildrenMenuItem() {
        if(getChildCount() == 0)
            return false;
        Iterator children = getChildren().iterator();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            if (child instanceof MenuItem) {
                return true;
            }
            else if(child instanceof MenuItems) {
                List menuItemsChildren = (List) child.getAttributes().get("value");
                if(menuItemsChildren != null && menuItemsChildren.size() > 0)
                    return true;
            }
        }
        return false;
    }
    
    public boolean invokeOnComponent(FacesContext context, String clientId,
            ContextCallback callback)
        throws FacesException {
        return true;
    }    

}
