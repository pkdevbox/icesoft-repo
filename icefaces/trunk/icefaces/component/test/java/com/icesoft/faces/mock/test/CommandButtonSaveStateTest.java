/*
 *
 *
 */
package com.icesoft.faces.mock.test;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlForm;
import com.sun.faces.application.StateManagerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * @author fye
 */
public class CommandButtonSaveStateTest extends MockTestCase {

    public static Test suite() {
        return new TestSuite(CommandButtonSaveStateTest.class);
    }

    public void testSaveState() {

        UIViewRoot uiViewRoot = getViewHandler().createView(getFacesContext(), this.getClass().getName()+"_view_id");
        getFacesContext().setViewRoot(uiViewRoot);

        HtmlForm form = new HtmlForm();
        //form.setPartialSubmit(true);
        HtmlCommandButton button = new HtmlCommandButton();
        button.setDisabled(true);
        button.setPartialSubmit(true);
        form.getChildren().add(button);
        uiViewRoot.getChildren().add(form);

        Object state = uiViewRoot.processSaveState(getFacesContext());

        StateManagerImpl stateManager = new StateManagerImpl();
        List treeList = new ArrayList();
        invokePrivateMethod("captureChild",
                new Class[]{List.class, Integer.TYPE, UIComponent.class},
                new Object[]{treeList, 0, uiViewRoot},
                StateManagerImpl.class,
                stateManager);

        Object[] comps = treeList.toArray();
        UIViewRoot restoreViewRoot = (UIViewRoot) invokePrivateMethod("restoreTree",
                new Class[]{Object[].class},
                new Object[]{comps}, StateManagerImpl.class,
                stateManager);

        restoreViewRoot.processRestoreState(getFacesContext(), (Object[]) state);

        UIComponent firstForm = (UIComponent) restoreViewRoot.getChildren().get(0);
        UIComponent first = (UIComponent) firstForm.getChildren().get(0);
        myBooleanAttribute(first, "disabled");
        myBooleanAttribute(first, "partialSubmit");
    }

    private void myBooleanAttribute(UIComponent first, String attributeName) {

        //TODO: all object type
        Object value = first.getAttributes().get(attributeName);
        if (value != null) {
            Boolean booleanValue = (Boolean) value;
            String message = " component=" + first + " property " + attributeName + " value=" + booleanValue.toString();
            Logger.getLogger(CommandButtonSaveStateTest.class.getName()).log(Level.INFO, message);
        } else {
            String message = " component=" + first + " property " + attributeName + "=null";
            Logger.getLogger(CommandButtonSaveStateTest.class.getName()).log(Level.INFO, message);
        }
    }
}
