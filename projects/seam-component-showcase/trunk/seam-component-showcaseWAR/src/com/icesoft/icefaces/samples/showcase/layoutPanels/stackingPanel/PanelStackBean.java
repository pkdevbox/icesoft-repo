/*
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

package com.icesoft.icefaces.samples.showcase.layoutPanels.stackingPanel;

import javax.faces.event.ValueChangeEvent;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import static org.jboss.seam.ScopeType.PAGE;
import java.io.Serializable;
/**
 * <p>The PanelStackBean class is a backing bean for the PanelStack showcase
 * demonstration and is used to store the selected panel state of the
 * ice:panelStack component. </p>
 *
 * @since 0.3.0
 */
@Scope(PAGE)
@Name("panelStack")
public class PanelStackBean implements Serializable{

    // currently selected panel
    private String selectedPanel = "mineral";
    // currently selected fruit
    private String selectedFruit = "1";
    // currently selected color
    private String selectedColor = "1";
    // urrently selected mineral
    private String selectedMineral = "1";

    /**
     * The panel stack is controlled by a group of radio buttons and their state
     * changes call this method to register the newly selected panel.
     *
     * @param event new value of event contains the new selected panel name.
     */
    public void selectedPanelChanged(ValueChangeEvent event) {
        selectedPanel = event.getNewValue().toString();
    }

    /**
     * Sets the selected panel name to the specified panel name.
     *
     * @param selectedPanel panel name to be set as selected.
     */
    public void setSelectedPanel(String selectedPanel) {
        this.selectedPanel = selectedPanel;
    }

    /**
     * Gets the selected panel name.
     *
     * @return currently selected panel.
     */
    public String getSelectedPanel() {
        return selectedPanel;
    }

    /**
     * Sets the selected fruit name to the specified fruit name.
     *
     * @param selectedFruit fruit name to be set as selected.
     */
    public void setSelectedFruit(String selectedFruit) {
        this.selectedFruit = selectedFruit;
    }

    /**
     * Gets the selected fruit name.
     *
     * @return currently selected fruit.
     */
    public String getSelectedFruit() {
        return selectedFruit;
    }

    /**
     * Sets the selected color name to the specified color name.
     *
     * @param selectedColor color name to be set as selected.
     */
    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Gets the selected color name.
     *
     * @return currently selected color.
     */
    public String getSelectedColor() {
        return selectedColor;
    }

    /**
     * Sets the selected mineral name to the specified mineral name.
     *
     * @param selectedMineral mineral name to be set as selected.
     */
    public void setSelectedMineral(String selectedMineral) {
        this.selectedMineral = selectedMineral;
    }

    /**
     * Gets the selected mineral name.
     *
     * @return currently selected mineral.
     */
    public String getSelectedMineral() {
        return selectedMineral;
    }
}