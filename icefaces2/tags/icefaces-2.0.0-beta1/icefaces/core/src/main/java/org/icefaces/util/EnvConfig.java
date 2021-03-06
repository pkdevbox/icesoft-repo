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

package org.icefaces.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.icefaces.util.EnvUtils;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvConfig {
    private static Logger log = Logger.getLogger(EnvConfig.class.getName());
    public static String ICEFACES_ENV_CONFIG = "org.icefaces.env.config";
    //configuration parameter names and member variables
    public static String ICEFACES_AUTO = "org.icefaces.render.auto";
    private boolean autoRenderFlag;
    public static String ICEFACES_AUTOID = "org.icefaces.autoid";
    private boolean autoIdFlag;
    private boolean ariaEnabledFlag;
    private boolean compressDOMFlag;
    
    public EnvConfig(Map initMap)  {
        init(initMap);
    }

    public static EnvConfig getEnvConfig(FacesContext facesContext)  {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map appMap = externalContext.getApplicationMap();
        EnvConfig envConfig = (EnvConfig) appMap.get(ICEFACES_ENV_CONFIG);
        if (null == envConfig) {
            envConfig = new EnvConfig(externalContext.getInitParameterMap());
            appMap.put(ICEFACES_ENV_CONFIG, envConfig);
        }
        return envConfig;
    }
    
    public void init(Map initMap)  {
        StringBuilder info = new StringBuilder();
        autoRenderFlag = decodeBoolean(initMap, ICEFACES_AUTO, true, info);
        autoIdFlag = decodeBoolean(initMap, ICEFACES_AUTOID, true, info);
        ariaEnabledFlag = decodeBoolean(initMap, EnvUtils.ARIA_ENABLED, true, info);
        compressDOMFlag = decodeBoolean(initMap, EnvUtils.COMPRESS_DOM, false, info);
        log.info("ICEfaces Configuration: " + info);
    }

    boolean decodeBoolean(Map map, String name, boolean defaultValue, StringBuilder info)  {
        String paramValue = (String) map.get(name);
        if (null == paramValue)  {
            info.append(name + " = " + defaultValue + " [default]  "); 
            return defaultValue;
        }
        if ("true".equalsIgnoreCase(paramValue)) {
            info.append(name + " = true  "); 
            return true;
        }
        if ("false".equalsIgnoreCase(paramValue)) {
            info.append(name + " = false  "); 
            return false;
        }
        info.append(name + " = " + defaultValue + 
                " [default replacing malformed non-boolean value: " + 
                paramValue + "]  "); 
        return defaultValue;
    }

    String decodeString(Map map, String name, String defaultValue, StringBuilder info)  {
        String paramValue = (String) map.get(name);
        if (null == paramValue)  {
            info.append(name + " = " + defaultValue + " [default]  "); 
            return defaultValue;
        }
            info.append(name + " = " + paramValue + "  "); 
        return paramValue;
    }

    /**
     * Return the init param for org.icefaces.autoid.
     *
     * @return true if IDs should be automatically assigned to components
     */
    public boolean getAutoId() {
        return autoIdFlag;
    }

    /**
     * Return the init param for org.icefaces.render.auto.
     *
     * @return true if ICEfaces rendering should apply to all pages by default
     */
    public boolean getAutoRender() {
        return autoRenderFlag;
    }

    /**
     * Return the init param for org.icefaces.aria.enabled.
     *
     * @return true if ARIA is enabled at application level
     */
    public boolean isAriaEnabled() {
        return ariaEnabledFlag;
    }

    /**
     * Return the init param for org.icefaces.compressDOM
     *
     * @return true if DOM compression should be applied
     */
    public boolean getCompressDOM() {
        return compressDOMFlag;
    }

}
