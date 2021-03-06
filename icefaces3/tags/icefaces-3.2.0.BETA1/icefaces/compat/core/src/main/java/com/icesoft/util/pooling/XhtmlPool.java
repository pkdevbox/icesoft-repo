/*
 * Copyright 2004-2012 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.icesoft.util.pooling;

public class XhtmlPool {

    private static StringInternMapLRU pool = new StringInternMapLRU("com.icesoft.faces.xhtmlPoolMaxSize");
    
    public static Object get(Object value) {
        return pool.get(value);
    }
    
    public static int getSize() {
        return pool.getSize();
    }
}