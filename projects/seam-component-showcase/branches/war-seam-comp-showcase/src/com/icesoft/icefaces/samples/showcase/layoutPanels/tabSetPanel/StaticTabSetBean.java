/*
 * StaticTabSetBean.java
 *
 * Created on May 23, 2007, 9:23 AM
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
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
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */


package com.icesoft.icefaces.samples.showcase.layoutPanels.tabSetPanel;

import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.component.ext.HtmlSelectOneRadio;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;

import javax.ejb.Remove;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import org.jboss.seam.core.Manager;

import java.io.Serializable;
/**
 * The StaticTabSetBean class is a backing bean for the TabbedPane showcase
 * demonstration and is used to store the various states of the the
 * ice:panelTabSet component.  These states are visibility, tab selection and
 * tab placement. 
 *
 * @since 0.3.0
 */

@Scope(ScopeType.PAGE)
@Name("staticTabbedPane")
public class StaticTabSetBean implements  Serializable{
//	public class StaticTabSetBean implements TabChangeListener, Serializable{
    /**
     * The demo contains three tabs and thus we need three variables to store
     * their respective rendered states.
     */
    private boolean tabbedPane1Visible=true;
    private boolean tabbedPane2Visible=true;
    private boolean tabbedPane3Visible=true;
    private int selectedIndex=1;
	private static Log log =
           LogFactory.getLog(StaticTabSetBean.class);    
    /**
     * Tabbed placement, possible values are "top" and "bottom", the default is
     * "bottom".
     */
    private String tabPlacement = "bottom";


    /**
     * Binding used by example to listen
     */

 // private PanelTabSet tabSet;
    

 	@Begin
	public void startConv(){
	  log.info("starting Conversation");
	}   
    /**
     * Return the visibility of tab panel 1.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane1Visible() {
        return tabbedPane1Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane1Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane1Visible(boolean tabbedPane1Visible) {
        this.tabbedPane1Visible = tabbedPane1Visible;
    }

    /**
     * Return the visibility of tab panel 2.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane2Visible() {
        return tabbedPane2Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane2Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane2Visible(boolean tabbedPane2Visible) {
        this.tabbedPane2Visible = tabbedPane2Visible;
    }

    /**
     * Return the visibility of tab panel 3.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane3Visible() {
        return tabbedPane3Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane3Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane3Visible(boolean tabbedPane3Visible) {
        this.tabbedPane3Visible = tabbedPane3Visible;
    }
    
    /**
     * Gets the tabbed pane object bound to this bean.
     *
     * @return bound tabbed pane.
     */
//     public PanelTabSet getTabSet() {
   // 	if (tabSet!=null)log.info("getTabSet and tabSet="+tabSet.getId());
    //	else log.info("getTabSet and tabSet is null");
 //       return tabSet;
 //   }
    
    /**
     * Set a tabbed pane object which will be bound to this object
     *
     * @param tabSet new PanelTabSet object.
     */
    public void setTabSet(PanelTabSet tabSet) {
 //        this.tabSet = tabSet;
//    	if (tabSet==null){
  //  		log.info("settabSet is null");
 //   	}
    //	else log.info("setTabSet & tabSet="+tabSet.getId());

    } 
    
    /**
     * Gets the tab placement, either top or bottom.
     *
     * @return top if the tab is on the top of the component, bottom if the tab
     *         is on the bottom of the component.
     */
    public String getTabPlacement() {
        return tabPlacement;
    }

    /**
     * Sets the tab placement.
     *
     * @param tabPlacement two options top or bottom.
     */
    public void setTabPlacement(String tabPlacement) {
        this.tabPlacement = tabPlacement;
    }

    /**
     * Method is called when there has been a request to change the tab
     * placement.
     *
     * @param event contains new tab placement data.
     */
    public void selectTabPlacement(ValueChangeEvent event) {
        tabPlacement = (String) event.getNewValue();
    }
    
     /**
     * Called when the table binding's tab focus changes.
     *
     * @param tabChangeEvent used to set the tab focus.
     * @throws AbortProcessingException An exception that may be thrown by event
      * listeners to terminate the processing of the current event.
     */
    public void processTabChange(TabChangeEvent tabChangeEvent)
            throws AbortProcessingException {
    	/*
        setSelectedTabFocus(String.valueOf(tabChangeEvent.getNewTabIndex()));
//        if (selectedTabObject != null) {
//        	log.info("selectedTabObject!=null");
//            selectedTabObject.setSubmittedValue(selectedTabFocus);
//        }
        */
    }
    
//    /**
//     * Gets the currently selected tab. --don't need this either
//     *
//     * @return selectedTabFocus of the currently selected tab.
//     */
//    public String getSelectedTabFocus() {
//    	log.info("getSelectedTabFocus value="+selectedTabFocus);
//        return Integer.toString(selectedIndex);
//    }

//    /**
//     * Sets the currently selected tab. don't need this anymore!!!
//     *
//     * @param selectedTabFocus new selected tab.
//     */
//    public void setSelectedTabFocus(String selectedTabFocus) {
//    	log.info("setSelectedTabFocus to "+selectedTabFocus);
//    	try{
//    	selectedIndex = Integer.parseInt(selectedTabFocus);
//    	}catch (NumberFormatException e){
//    		log.info("problem with parsing for selectedTabFocus="+selectedTabFocus);
//    		log.info("exception :-"+e);
//    	}
//    }
    


    private String convId;
    private String longRunning;
    private String convParam;

	public String getConvId() {
		return Manager.instance().getCurrentConversationId();
	}

	public String getLongRunning() {
		return String.valueOf(Manager.instance().isLongRunningConversation());
	}

	public String getConvParam() {
		return Manager.instance().getConversationIdParameter();
	}
	   @Remove
	   public void destroy() {
		   log.info("destroying StaticTabSetBean");
	   }

	public int getSelectedIndex() {
//		log.info("getSelectedIndex and value is="+selectedIndex);
		return selectedIndex;
	}
	public void setSelectedIndex(int selectedIndex) {
//		log.info("setSelectedIndex to value="+selectedIndex);
		this.selectedIndex = selectedIndex;
	}


}
