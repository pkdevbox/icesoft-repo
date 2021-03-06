/*
 * Version: MPL 1.1
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
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 */

package org.icefaces.application;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

public class WindowELResolver extends ELResolver {

    public Object getValue(ELContext elContext, Object base, Object property) {
        if (base == null && WindowScopeManager.ScopeName.equals(property)) {
            WindowScopeManager.ScopeMap customScope = getScope(elContext);
            elContext.setPropertyResolved(true);
            return customScope;
        } else if (base != null && base instanceof WindowScopeManager.ScopeMap) {
            return lookup(elContext, (WindowScopeManager.ScopeMap) base, property);
        } else if (base == null) {
            return lookup(elContext, getScope(elContext), property);
        }
        return null;
    }

    public Class getType(ELContext elContext, Object base, Object property) {
        return Object.class;
    }

    public void setValue(ELContext elContext, Object base, Object property, Object value) {
        //do nothing!
    }

    public boolean isReadOnly(ELContext elContext, Object base, Object property) {
        return true;
    }

    public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
        return Collections.<FeatureDescriptor>emptyList().iterator();
    }

    public Class getCommonPropertyType(ELContext elContext, Object base) {
        if (base != null) {
            return null;
        }
        return String.class;
    }

    private WindowScopeManager.ScopeMap getScope(ELContext elContext) {
        FacesContext ctx = (FacesContext) elContext.getContext(FacesContext.class);
        return WindowScopeManager.lookup(ctx).lookupWindowScope();
    }


    private Object lookup(ELContext elContext, WindowScopeManager.ScopeMap scope, Object key) {
        if (null == scope) {
            return null;
        }

        Object value = scope.get(key);
        elContext.setPropertyResolved(value != null);
        return value;
    }
}
