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

package com.icesoft.faces.component.menubar;

import com.icesoft.faces.component.InvalidComponentTypeException;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import java.io.IOException;
import java.util.List;

public class MenuItemsRenderer extends DomBasicRenderer {

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {

        List children = (List) ((MenuItems) component).getValue();
        // extract the actionListener and action methodBindings from the MenuItems
        // then attach them to the child MenuItem objects
        MethodBinding almb = ((MenuItems) component).getActionListener();
        MethodBinding amb = ((MenuItems) component).getAction();
        setParentsRecursive(component, children, almb, amb);
        renderRecursive(context, children);
    }

    private void renderRecursive(FacesContext context, List children)
            throws IOException {
        for (int i = 0; i < children.size(); i++) {
            UIComponent nextChildMenuNode = (UIComponent) children.get(i);
            encodeParentAndChildren(context, nextChildMenuNode);
        }
    }

    private void setParentsRecursive(UIComponent parent, List children,
                                     MethodBinding almb, MethodBinding amb) {
        for (int i = 0; i < children.size(); i++) {
            UIComponent nextChild = null;
            if (!((nextChild =
                    (UIComponent) children.get(i)) instanceof MenuItemBase)) {
                throw new InvalidComponentTypeException(
                        "MenuItemsRenderer expects MenuItemBase children");
            }
            nextChild.setParent(parent);

            // here's where we attach the action and actionlistener methodBindings to the MenuItem
            if ((null != almb) && (nextChild instanceof MenuItemBase)) {
                ((MenuItemBase) nextChild).setActionListener(almb);
            }
            if ((null != amb) && (nextChild instanceof MenuItemBase)) {
                ((MenuItemBase) nextChild).setAction(amb);
            }

            List grandChildren = nextChild.getChildren();
            setParentsRecursive(nextChild, grandChildren, almb, amb);
        }
    }
}
