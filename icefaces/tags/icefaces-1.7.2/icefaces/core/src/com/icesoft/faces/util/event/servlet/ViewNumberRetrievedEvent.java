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

package com.icesoft.faces.util.event.servlet;

import javax.servlet.http.HttpSession;

/**
 * The <code>ViewNumberRetrievedEvent</code> class represents an event that
 * should be fired whenever a view number is retrieved. A view number is always
 * associated with an ICEfaces ID. </p>
 */
public class ViewNumberRetrievedEvent
        extends AbstractSessionEvent
        implements ContextEvent {
    private int viewNumber;

    /**
     * Constructs a <code>ViewNumberRetrievedEvent</code> with the specified
     * <code>source</code> and <code>viewNumber</code>. </p>
     *
     * @param source     the HTTP session.
     * @param iceFacesId the ICEfaces ID identifying the session.
     * @param viewNumber the view number that has been retrieved.
     * @throws IllegalArgumentException if the one of the following happens:
     *                                  <ul> <li> the specified
     *                                  <code>source</code> is
     *                                  <code>null</code>. </li> <li> the
     *                                  specified <code>iceFacesId</code> is
     *                                  either <code>null</code> or empty. </li>
     *                                  </ul>
     */
    public ViewNumberRetrievedEvent(
            HttpSession source, String iceFacesId, int viewNumber)
            throws IllegalArgumentException {
        super(source, iceFacesId);
        this.viewNumber = viewNumber;
    }

    /**
     * Gets the view number of this <code>ViewNumberRetrievedEvent</code>. </p>
     *
     * @return the view number.
     * @see #getICEfacesID()
     */
    public int getViewNumber() {
        return viewNumber;
    }
}
