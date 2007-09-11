package com.icesoft.faces.context;

import com.icesoft.faces.env.PortletEnvironmentRenderRequest;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.NOOP;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.http.portlet.PortletExternalContext;
import com.icesoft.faces.webapp.http.servlet.ServletExternalContext;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.util.SeamUtilities;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class View implements CommandQueue {
    private static final Log Log = LogFactory.getLog(View.class);
    private static final NOOP NOOP = new NOOP();
    private Lock lock = new ReentrantLock();
    private BridgeExternalContext externalContext;
    private BridgeFacesContext facesContext;
    private PersistentFacesState persistentFacesState;
    private Map bundles = Collections.EMPTY_MAP;
    private String requestURI;
    private Command currentCommand = NOOP;
    private String viewIdentifier;
    private ArrayList onPutListeners = new ArrayList();
    private ArrayList onTakeListeners = new ArrayList();
    private Collection viewListeners = new ArrayList();
    private String sessionID;
    private Configuration configuration;
    private SessionDispatcher.Listener.Monitor sessionMonitor;
    private ResourceDispatcher resourceDispatcher;

    public View(final String viewIdentifier, String sessionID, Request request, final ViewQueue allServedViews, final Configuration configuration, final SessionDispatcher.Listener.Monitor sessionMonitor, ResourceDispatcher resourceDispatcher) throws Exception {
        this.sessionID = sessionID;
        this.configuration = configuration;
        this.viewIdentifier = viewIdentifier;
        this.sessionMonitor = sessionMonitor;
        this.resourceDispatcher = resourceDispatcher;

        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                ServletEnvironmentRequest wrappedRequest = new ServletEnvironmentRequest(request);
                requestURI = wrappedRequest.getRequestURI();
                externalContext = new ServletExternalContext(viewIdentifier, wrappedRequest, response, View.this, configuration, sessionMonitor);
            }

            public void portlet(Object request, Object response, Object portletConfig) {
                PortletEnvironmentRenderRequest wrappedRequest = new PortletEnvironmentRenderRequest(request);
                externalContext = new PortletExternalContext(viewIdentifier, wrappedRequest, response, View.this, configuration, sessionMonitor, portletConfig);
            }
        });
        this.facesContext = new BridgeFacesContext(externalContext, viewIdentifier, sessionID, this, configuration, resourceDispatcher);
        this.persistentFacesState = new PersistentFacesState(facesContext, viewListeners, configuration);
        this.onPut(new Runnable() {
            public void run() {
                try {
                    allServedViews.put(viewIdentifier);
                } catch (InterruptedException e) {
                    Log.warn("Failed to queue updated view", e);
                }
            }
        });
        this.notifyViewCreation();
    }

    public void updateOnXMLHttpRequest(Request request) throws Exception {
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                externalContext.update((HttpServletRequest) request, (HttpServletResponse) response);
            }

            public void portlet(Object request, Object response, Object config) {
                //this call cannot arrive from a Portlet
            }
        });
        makeCurrent();
    }

    public void updateOnRequest(Request request) throws Exception {
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                HttpServletRequest wrappedRequest = new ServletEnvironmentRequest(request);
                if (differentURI(wrappedRequest)) {
                    //page redirect
                    requestURI = wrappedRequest.getRequestURI();
                    externalContext = new ServletExternalContext(viewIdentifier, wrappedRequest, response, View.this, configuration, sessionMonitor);
                    facesContext = new BridgeFacesContext(externalContext, viewIdentifier, sessionID, View.this, configuration, resourceDispatcher);
                    //reuse  PersistentFacesState instance when page redirects occur                    
                    persistentFacesState.setFacesContext(facesContext);
                } else {
                    //page reload
                    externalContext.updateOnReload(wrappedRequest, response);
                }
            }

            public void portlet(Object request, Object response, Object config) {
                //page reload
                PortletEnvironmentRenderRequest wrappedRequest = new PortletEnvironmentRenderRequest(request);
                externalContext.updateOnReload(wrappedRequest, response);
            }
        });
        makeCurrent();
    }

    public void switchToNormalMode() {
        facesContext.switchToNormalMode();
        externalContext.switchToNormalMode();
    }

    public void switchToPushMode() {
        //collect bundles put by Tag components when the page is parsed
        bundles = externalContext.collectBundles();
        facesContext.switchToPushMode();
        externalContext.switchToPushMode();
    }

    /**
     * Check to see if the URI is different in any material (or Seam) way.
     *
     * @param request ServletRequest
     * @return true if the URI is considered different
     */
    public boolean differentURI(HttpServletRequest request) {
        // As a temporary fix, all GET requests are non-faces requests, and thus,
        // are considered different to force a new ViewRoot to be constructed.
        return (SeamUtilities.isSeamEnvironment()) ||
                !request.getRequestURI().equals(requestURI);
    }

    public void put(Command command) {
        lock.lock();
        currentCommand = currentCommand.coalesceWith(command);
        lock.unlock();
        broadcastTo(onPutListeners);
    }

    public Command take() {
        lock.lock();
        Command command = currentCommand;
        currentCommand = NOOP;
        lock.unlock();
        broadcastTo(onTakeListeners);
        return command;
    }

    public void onPut(Runnable listener) {
        onPutListeners.add(listener);
    }

    public void onTake(Runnable listener) {
        onTakeListeners.add(listener);
    }

    private void broadcastTo(Collection listeners) {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            Runnable listener = (Runnable) i.next();
            try {
                listener.run();
            } catch (Exception e) {
                Log.error("Failed to notify listener: " + listener, e);
            }
        }
    }

    public void release() {
        facesContext.release();
        persistentFacesState.release();
        externalContext.resetRequestMap();
    }

    public BridgeFacesContext getFacesContext() {
        return facesContext;
    }

    public void dispose() {
        this.persistentFacesState.setCurrentInstance();
        this.facesContext.setCurrentInstance();
        this.notifyViewDisposal();
        this.release();
        this.facesContext.dispose();
        this.externalContext.dispose();
    }

    public void makeCurrent() {
        externalContext.injectBundles(bundles);
        persistentFacesState.setCurrentInstance();
        facesContext.setCurrentInstance();
        facesContext.applyBrowserDOMChanges();
    }

    private void notifyViewCreation() {
        Iterator i = viewListeners.iterator();
        while (i.hasNext()) {
            try {
                ViewListener listener = (ViewListener) i.next();
                listener.viewCreated();
            } catch (Throwable t) {
                Log.warn("Failed to invoke view listener", t);
            }
        }
    }

    private void notifyViewDisposal() {
        Iterator i = viewListeners.iterator();
        while (i.hasNext()) {
            try {
                ViewListener listener = (ViewListener) i.next();
                listener.viewDisposed();
            } catch (Throwable t) {
                Log.warn("Failed to invoke view listener", t);
            }
        }
    }
}