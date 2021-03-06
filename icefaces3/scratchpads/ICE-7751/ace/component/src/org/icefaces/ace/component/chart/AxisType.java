package org.icefaces.ace.component.chart;

import java.io.Serializable;

/**
 * Copyright 2010-2011 ICEsoft Technologies Canada Corp.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p/>
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * User: Nils
 * Date: 12-05-07
 * Time: 11:00 AM
 */
public enum AxisType implements Serializable {
    LINEAR, LOGARITHMIC, CATEGORY, DATE;

    @Override
    public String toString() {
        if (this.equals(LINEAR)) return "ice.ace.jq.jqplot.LinearAxisRenderer";
        else if (this.equals(LOGARITHMIC)) return "ice.ace.jq.jqplot.LogAxisRenderer";
        else if (this.equals(CATEGORY)) return "ice.ace.jq.jqplot.CategoryAxisRenderer";
        else if (this.equals(DATE)) return "ice.ace.jq.jqplot.DateAxisRenderer";
        return null;
    }
}
