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
 * 2004-2009 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
*/

package org.icefaces.push;

import org.icefaces.application.WindowScopeManager;
import org.icefaces.push.http.MimeTypeMatcher;
import org.icefaces.push.servlet.BasicAdaptingServlet;
import org.icefaces.push.servlet.EnvironmentAdaptingServlet;
import org.icefaces.push.servlet.PathDispatcher;
import org.icefaces.push.servlet.SessionDispatcher;

import javax.servlet.http.HttpSession;
import java.util.Observable;

public class SessionBoundServer extends PathDispatcher {
    private static final String ICEFacesBridgeRequestPattern = "\\.icefaces\\.jsf$";

    public SessionBoundServer(final HttpSession session, MonitorRunner monitor, final SessionDispatcher.Monitor sessionMonitor, Configuration configuration) {
        Observable pingPongNotifier = new Observable() {
            public void notifyObservers(Object arg) {
                setChanged();
                super.notifyObservers(arg);
            }
        };
        MimeTypeMatcher mimeTypeMatcher = new MimeTypeMatcher() {
            public String mimeTypeFor(String path) {
                return session.getServletContext().getMimeType(path);
            }
        };
        dispatchOn(".*send\\-updated\\-views" + ICEFacesBridgeRequestPattern, new EnvironmentAdaptingServlet(new SendUpdatedViews(session, pingPongNotifier, monitor, configuration), configuration, session.getServletContext()));
        dispatchOn(".*ping" + ICEFacesBridgeRequestPattern, new BasicAdaptingServlet(new ReceivePing(pingPongNotifier)));
        dispatchOn(".*dispose\\-window" + ICEFacesBridgeRequestPattern, new BasicAdaptingServlet(new DisposeWindowScope(WindowScopeManager.lookup(session, configuration))));
        dispatchOn(".*icefaces\\/resource\\/.*", new BasicAdaptingServlet(new DynamicResourceDispatcher("icefaces/resource/", mimeTypeMatcher, sessionMonitor, session, configuration)));
    }

}
