package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.SessionMap;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.xmlhttp.BlockingResponseState;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.SeamUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EnvironmentWrappingServlet implements ServletServer {
    private int viewCount = 0;
    private HttpSession session;
    private Map sessionMap;
    private String sessionID;

    private Server server;
    private UpdateManager updateManager;
    private Map views = new HashMap();
    private Map bundles = new HashMap();

    public EnvironmentWrappingServlet(HttpSession session, Configuration configuration, IdGenerator idGenerator) {
        this.sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);

        this.session = session;
        this.sessionMap = new SessionMap(session);
        this.updateManager = new UpdateManager(session);
        this.server = new PushServer(updateManager);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = session.isNew() ? null : request.getParameter("viewNumber");
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        final View view;
        if (viewNumber == null) {
            viewNumber = String.valueOf(++viewCount);
            view = new View(viewNumber, request, response);
            views.put(viewNumber, view);

            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);
            ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
            checkSeamRequestParameters(view.externalContext.getRequestParameterMap());
            // Allow for downstream removal of Seam PageContext objects
            doClearSeamContexts(view.externalContext.getRequestMap());
            //collect bundles put by Tag components when the page is parsed
            bundles = collectBundles(view.externalContext.getRequestMap());
            //run lifecycle
            server.service(new ServletRequestResponse(request, response));
            // If the GET request handled by this servlet results in a
            // redirect (not likely under icefaces demo apps, but happens
            // all the time in Seam) then, we're stuck. We don't use the
            // X-REDIRECT mechanism in this servlet, so resort to some
            // good old fashioned manual redirect code.
            if (view.externalContext.redirectRequested()) {
                response.sendRedirect(view.externalContext.redirectTo());
            }

            PersistentFacesState.resetInstance(sessionMap, viewNumber, view.facesContext);
            //by making the request null DOMResponseWriter will redirect its output to the coresponding ResponseState
            //todo: find better (less subversive) solution -- like creating two different implementions for DOMResponseWriter
            view.externalContext.updateResponse(null);
        } else {
            view = (View) views.get(viewNumber);
            PersistentFacesState.getInstance(sessionMap, viewNumber).setFacesContext(view.facesContext);
            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);

            view.externalContext.updateRequest(request);
            view.externalContext.getRequestMap().putAll(bundles);
            view.facesContext.setCurrentInstance();

            server.service(new ServletRequestResponse(request, response));
        }

        view.facesContext.release();
        PersistentFacesState.clearLocalInstance();
    }

    public void shutdown() {
        updateManager.shutdown();
    }

    //todo: refactor this structure into an object with behavior 
    private class View {
        private ServletExternalContext externalContext;
        private ServletFacesContext facesContext;
        private BlockingResponseState responseState;

        public View(String viewIdentifier, HttpServletRequest request, HttpServletResponse response) {
            externalContext = new ServletExternalContext(session.getServletContext(), new ServletEnvironmentRequest(request), response);
            facesContext = new ServletFacesContext(externalContext, viewIdentifier);
            //the call has the side effect of creating and setting up the state
            //todo: make this concept more visible and less subversive
            responseState = (BlockingResponseState) ResponseStateManager.getState(session, viewIdentifier);
        }
    }

    //todo: see if we can execute full JSP cycle all the time (not only when page is parsed)
    //todo: this way the bundles are put into the request map every time, so we don't have to carry
    //todo: them between requests
    static Map collectBundles(Map requestMap) {
        Map result = new HashMap();
        Iterator entries = requestMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object value = entry.getValue();
            if (value != null) {
                String className = value.getClass().getName();
                if ((className.indexOf("LoadBundleTag") > 0) ||  //Sun RI
                        (className.indexOf("BundleMap") > 0)) {     //MyFaces
                    result.put(entry.getKey(), value);
                }
            }
        }

        return result;
    }

    /**
     * Any request handled by the PersistentFacesServlet should have the Seam
     * PageContext removed from our internal context complex. But we cannot do
     * it here, since the machinery is not yet in place, so put a flag into
     * the external context, allowing someone else to do it later.
     */
    private void doClearSeamContexts(Map requestMap) {
        requestMap.put(PersistentFacesCommonlet.REMOVE_SEAM_CONTEXTS, Boolean.TRUE);
    }

    /**
     * Check to see if Seam specific keywords are in the request. If so,
     * then flag that a new ViewRoot is to be constructed.
     *
     * @param externalContextMap Map to insert Flag
     */
    private void checkSeamRequestParameters(Map externalContextMap) {
        // Always on a GET request, create a new ViewRoot. New theory.
        // This works now that the ViewHandler only calls restoreView once,
        // as opposed to calling it again from createView
        if (SeamUtilities.isSeamEnvironment()) {
            externalContextMap.put(
                    PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                    Boolean.TRUE);
        }
    }
}
