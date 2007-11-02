package com.icesoft.faces.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.el.ValueBinding;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.el.ELException;
import java.io.Serializable;

/**
 * There are some UIComponent properties which we want to set based on
 *  both user interactions and from changing ValueBinding values. If
 *  we follow the typical pattern for regular component properties,
 *  then the local field value takes precedence over the ValueBinding,
 *  so once a user interaction has occured, then the ValueBinding won't
 *  work. What we'd instead prefer, it to follow the UIInput value
 *  property pattern, where newly set values are pushed into the
 *  ValueBinding, so that both means of changing the value work.
 * 
 * Most of the methods here are loose adaptations of UIInput's equivalents. 
 *  
 * @author mcollette
 */
public class UpdatableProperty implements Serializable {
    private static final Log log = LogFactory.getLog(UpdatableProperty.class);
    
    private String name;
    private Object savedValue;
    private boolean haveSavedValue;
    private Object submittedValue;
    private boolean setSubmittedInLocalValue; 
    private Object value;
    private boolean setLocalValueInValueBinding;
    
    public UpdatableProperty(String name) {
        this.name = name;
    }
    
    /**
     * Before a component does all the phases, and goes through the several
     *  steps of updating its value, we sometimes need to save away the
     *  original value, so we can refer to it unmolested
     */
    public void saveValue(FacesContext context, UIComponent comp) {
//System.out.println(getDesc(comp, "SET_SAVE"));
        savedValue = getValue(context, comp);
        haveSavedValue = true;
//System.out.println("  savedValue: " + savedValue);
    }
    
    /**
     * If we did save away the value, then just access it, but if we didn't
     *  save it away, then we'll want the most up-to-date value
     */
    public Object getSavedValue(FacesContext context, UIComponent comp) {
//System.out.println(getDesc(comp, "GET_SAVE"));
//System.out.println("  savedValue: " + savedValue);
//System.out.println("  haveSavedValue: " + haveSavedValue);
        if(haveSavedValue)
            return savedValue;
        else
            return getValue(context, comp);
    }
    
    public Object getSubmittedValue(UIComponent comp) {
//System.out.println(getDesc(comp, "GET_SUB"));
//System.out.println("  submittedValue: " + submittedValue);
        return submittedValue;
    }
    
    public void setSubmittedValue(UIComponent comp, Object newSubmittedValue) {
//System.out.println(getDesc(comp, "SET_SUB"));
        submittedValue = newSubmittedValue;
        setSubmittedInLocalValue = true;
//System.out.println("  submittedValue: " + submittedValue);
    }
    
    protected void clearSubmittedValue() {
        submittedValue = null;
        setSubmittedInLocalValue = false;
    }
    
    public Object getValue(FacesContext context, UIComponent comp) {
//System.out.println(getDesc(comp, "GET"));
//System.out.println("  value: " + value);
        if(value != null) {
            return value;
        }
        ValueBinding vb = comp.getValueBinding(name);
//System.out.println("  vb: " + vb);
        if(vb != null) {
            Object modelValue = vb.getValue(context);
//System.out.println("  modelValue: " + modelValue);
            return modelValue;
        }
        return null;
    }
    
    /**
     * This method be called as a setter, but not from the decode, since
     *  for things to happen in the right phases, you should call
     *  setSubmittedValue(-) in the component or renderer's decode(-) method,
     *  and allow the validate(-) method here to bubble the submittedValue  
     *  into value in the correct phase.
     */
    public void setValue(UIComponent comp, Object newValue) {
//System.out.println(getDesc(comp, "SET"));
        value = newValue;
        setLocalValueInValueBinding = true;
//System.out.println("  value: " + value);
//StackTraceElement[] ste = Thread.currentThread().getStackTrace();
//for(int i = 0; i < ste.length; i++)
//    System.out.println("     " + ste[i]);
    }
    
    protected void clearValue() {
        value = null;
        setLocalValueInValueBinding = false;
    }
    
    protected void discontinueSettingLocalValueInValueBinding() {
        setLocalValueInValueBinding = false;
    }
    
    public void validate(FacesContext context, UIComponent comp) {
//System.out.println(getDesc(comp, "VALIDATE"));
//System.out.println("  valid: " + isComponentValid(comp));
//System.out.println("  submittedValue: " + submittedValue);
//System.out.println("  setSubmittedInLocalValue: " + setSubmittedInLocalValue);
//System.out.println("  old value: " + value);
        if( !isComponentValid(comp) ) {
            return;
        }
        // Unlike UIInput.value treatment, these properties require
        //  no conversion nor validation
        if(setSubmittedInLocalValue) {
            setValue(comp, submittedValue);
            clearSubmittedValue();
        }
    }
    
    public void updateModel(FacesContext context, UIComponent comp) {
//System.out.println(getDesc(comp, "UPDATE"));
//System.out.println("  valid: " + isComponentValid(comp));
//System.out.println("  setLocalValueInValueBinding: " + setLocalValueInValueBinding);
        if( !isComponentValid(comp) || !setLocalValueInValueBinding) {
            return;
        }
//System.out.println("  value: " + value);
        ValueBinding vb = comp.getValueBinding(name);
//System.out.println("  vb: " + vb);
        if(vb == null)
            return;
        try {
            vb.setValue(context, value);
//System.out.println("  vb.setValue worked");
            clearValue();
        }
        catch(PropertyNotFoundException e) {
//System.out.println("PropertyNotFoundException: " + e);
            // It's ok for the application to not use a settable ValueBinding,
            //  since the UIComponent can keep the value. But they probably
            //  don't mean to, so we should log something
            log.info(e);
            // Don't keep trying to set it
            discontinueSettingLocalValueInValueBinding();
        }
        catch(Exception e) {
//System.out.println("Exception: " + e);
            String msgId = comp.getClass().getName() + '.' + name;
            Object label = MessageUtils.getComponentLabel(context, comp);
            FacesMessage msg = MessageUtils.getMessage(
                context, msgId, new Object[]{label, value});
            if(msg != null)
                context.addMessage(comp.getClientId(context), msg);
            setInvalid(comp);
        }
    }
    
    protected boolean isComponentValid(UIComponent comp) {
        if(comp instanceof UIInput)
            return ((UIInput)comp).isValid();
        return true;
    }
    
    protected void setInvalid(UIComponent comp) {
        if(comp instanceof UIInput)
            ((UIInput)comp).setValid(false);
    }
    
    private String getDesc(UIComponent comp, String meth) {
        StringBuffer sb = new StringBuffer(256);
        if(comp != null) {
            sb.append(comp.getClass().getName());
            sb.append('.');
        }
        sb.append(meth);
        sb.append("  name: ");
        sb.append(name);
        if(comp != null) {
            sb.append("  clientId: ");
            sb.append(comp.getClientId(FacesContext.getCurrentInstance()));
        }
        return sb.toString();
    }
}
