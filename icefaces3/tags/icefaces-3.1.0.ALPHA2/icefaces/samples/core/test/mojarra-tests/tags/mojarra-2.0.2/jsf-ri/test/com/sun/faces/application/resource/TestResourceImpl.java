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


package com.sun.faces.application.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;

import com.sun.faces.cactus.ServletFacesTestCase;
import com.sun.faces.cactus.TestingUtil;
import com.sun.faces.util.Util;
import com.sun.faces.util.RequestStateManager;
import javax.faces.context.FacesContext;
import org.apache.cactus.WebRequest;

/**
 * Test class for com.sun.faces.application.resource.ResourceImpl
 */
public class TestResourceImpl extends ServletFacesTestCase {

    public TestResourceImpl() {
        super("TestResourceImpl");
    }


    public TestResourceImpl(String name) {
        super(name);
    }


    @Override
    public void setUp() {
        super.setUp();
    }


    @Override
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------------------ Test Methods

    public void beginToURIPrefixMapping(WebRequest req) {
        req.setURL("localhost:8080", "/test", "/faces", "/foo.jsp", null);
    }

    public void testToURIPrefixMapping() throws Exception {
        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        Resource resource = handler.createResource("duke-nv.gif");
        assertTrue (resource != null);
        String expectedURI = "/test/faces/javax.faces.resource/duke-nv.gif";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke-nv.gif", "nvLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/faces/javax.faces.resource/duke-nv.gif?ln=nvLibrary";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif");
        assertTrue(resource != null);
        expectedURI = "/test/faces/javax.faces.resource/duke.gif?v=1_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif", "nvLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/faces/javax.faces.resource/duke.gif?ln=nvLibrary&v=1_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif", "vLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/faces/javax.faces.resource/duke.gif?ln=vLibrary&v=2_01_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke-nv.gif", "vLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/faces/javax.faces.resource/duke-nv.gif?ln=vLibrary&v=2_0";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

    }

    public void beginToURIExtensionMapping(WebRequest req) {
        req.setURL("localhost:8080", "/test", "/foo.faces", null, null);
    }

    public void testToURIExtensionMapping() throws Exception {
        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        Resource resource = handler.createResource("duke-nv.gif");
        assertTrue (resource != null);
        String expectedURI = "/test/javax.faces.resource/duke-nv.gif.faces";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke-nv.gif", "nvLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/javax.faces.resource/duke-nv.gif.faces?ln=nvLibrary";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif");
        assertTrue(resource != null);
        expectedURI = "/test/javax.faces.resource/duke.gif.faces?v=1_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif", "nvLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/javax.faces.resource/duke.gif.faces?ln=nvLibrary&v=1_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke.gif", "vLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/javax.faces.resource/duke.gif.faces?ln=vLibrary&v=2_01_1";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

        resource = handler.createResource("duke-nv.gif", "vLibrary");
        assertTrue(resource != null);
        expectedURI = "/test/javax.faces.resource/duke-nv.gif.faces?ln=vLibrary&v=2_0";
        assertTrue(expectedURI.equals(resource.getRequestPath()));

    }

    public void testWebppResourceGetInputStream() throws Exception {

        // validate the behavior of getInputStream() for a webapp-based resource
        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        // step 1 - non-versioned
        byte[] controlBytes = getBytes(getFacesContext().getExternalContext().getResource("/resources/duke-nv.gif"));
        Resource resource = handler.createResource("duke-nv.gif");
        assertTrue(resource != null);
        InputStream in = resource.getInputStream();
        assertTrue(in != null);
        byte[] underTest = getBytes(in);
        assertTrue(Arrays.equals(controlBytes, underTest));

        // step 2 - versioned
        controlBytes = getBytes(getFacesContext().getExternalContext().getResource("/resources/duke.gif/1_1.gif"));
        resource = handler.createResource("duke.gif");
        assertTrue(resource != null);
        in = resource.getInputStream();
        assertTrue(in != null);
        underTest = getBytes(in);
        assertTrue(Arrays.equals(controlBytes, underTest));
        
    }

    public void testEqualsOnResourceAndRelatedClasses() throws Exception {
        // validate the behavior of getInputStream() for a webapp-based resource
        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        Object
                x = handler.createResource("duke-nv.gif", "nvLibrary", "image/gif"),
                y = handler.createResource("duke-nv.gif", "nvLibrary", "image/gif"),
                z = handler.createResource("duke-nv.gif", "nvLibrary", "image/gif");
        this.verifyEqualsContractPositive(x, y, z);

        y = handler.createResource("simple.css");
        assertFalse(x.equals(y));


        VersionInfo
                viA = new VersionInfo("1.0", null),
                viB = new VersionInfo("1.0", null),
                viC = new VersionInfo("1.0", null);
        this.verifyEqualsContractPositive(viA, viB, viC);

        ResourceHelper helper = ClasspathResourceHelper.getInstance();
        FacesContext context = this.getFacesContext();

        LibraryInfo
                liA = helper.findLibrary("vLibrary-jar", null, context),
                liB = helper.findLibrary("vLibrary-jar", null, context),
                liC = helper.findLibrary("vLibrary-jar", null, context);
        this.verifyEqualsContractPositive(liA, liB, liC);

        liB = helper.findLibrary("vLibrary", null, context);
        assertFalse(liA.equals(liB));


    }


    public void testJarResourceGetInputStream() throws Exception {

        // validate the behavior of getInputStream() for a webapp-based resource
        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        // step 1 - non-versioned
        byte[] controlBytes = getBytes(Util.getCurrentLoader(this.getClass()).getResource("META-INF/resources/duke-jar-nv.gif"));
        Resource resource = handler.createResource("duke-jar-nv.gif");
        assertTrue(resource != null);
        InputStream in = resource.getInputStream();
        assertTrue(in != null);
        byte[] underTest = getBytes(in);
        assertTrue(Arrays.equals(controlBytes, underTest));

        // step 2 - versioned
        /*
        controlBytes = getBytes(Util.getCurrentLoader(this.getClass()).getResource("META-INF/resources/duke-jar.gif/1_1.gif"));
        resource = handler.createResource("duke-jar.gif");
        assertTrue(resource != null);
        in = resource.getInputStream();
        assertTrue(in != null);
        underTest = getBytes(in);
        assertTrue(Arrays.equals(controlBytes, underTest));
        */

    }


    public void testGetContentType() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue (handler != null);

        // non-versioned resource containing one path element
        Resource resource = handler.createResource("duke-jar.gif");
        assertTrue(resource != null);
        assertTrue("image/gif".equals(resource.getContentType()));

        // versioned resource containing one path element
        resource = handler.createResource("duke.gif");
        assertTrue(resource != null);
        assertTrue("image/gif".equals(resource.getContentType()));

        // non-versioned resource containing multiple path elements
        resource = handler.createResource("images/duke-nv.gif", "nvLibrary");
        assertTrue(resource != null);
        assertTrue("image/gif".equals(resource.getContentType()));

    }


    public void testDefaultHeaders() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue(handler != null);
        RequestStateManager.set(getFacesContext(),
                                RequestStateManager.RESOURCE_REQUEST,
                                Boolean.TRUE);
        Resource resource = handler.createResource("duke-jar.gif");
        assertTrue(resource != null);
        Map<String,String> headers = resource.getResponseHeaders();
        assertTrue(headers != null);
        assertTrue(headers.size() == 3);
        assertTrue(headers.get("Expires") != null);
        assertTrue(headers.get("ETag") != null);
        assertTrue(headers.get("Last-Modified") != null);

        // now assert that an empty map is returned if we're not servicing
        // a resource request
        RequestStateManager.set(getFacesContext(),
                                RequestStateManager.RESOURCE_REQUEST,
                                Boolean.FALSE);
        headers = resource.getResponseHeaders();
        assertTrue(headers.isEmpty());
        
    }


    @SuppressWarnings({"deprecation"})
    public void testUserAgentNeedsUpdate1() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue(handler != null);

        // no If-Modified-Since request header, so this should always
        // return true
        Resource resource = handler.createResource("duke-nv.gif");
        assertTrue(resource.userAgentNeedsUpdate(getFacesContext()));

        // set the creation date of the ResourceHandler back in time so that
        // if the header was present it would return true - the lack of the header
        // should result in true being returned in this case
//   handler);
    }


    public void beginUserAgentNeedsUpdate2(WebRequest req) {
        req.addHeader("If-Modified-Since", "Sat, 29 Oct 1994 19:43:31 GMT");
    }

    public void testUserAgentNeedsUpdate2() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue(handler != null);
        // If-Modified-Since request header, so this should always
        // return true
        Resource resource = handler.createResource("duke-nv.gif");
        assertTrue(!resource.userAgentNeedsUpdate(getFacesContext()));

    }


    public void testResourceImplSerialization() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertTrue(handler != null);

        Resource resource = handler.createResource("duke-nv.gif");
        byte[] serializedBytes = serialize(resource);
        resource = (Resource) deserialize(serializedBytes);
        assertNotNull(resource);
        assertNull(resource.getLibraryName());
        assertEquals("duke-nv.gif", "duke-nv.gif", resource.getResourceName());
        assertEquals("image/gif", "image/gif", resource.getContentType());


        resource = handler.createResource("duke-nv.gif", "nvLibrary");
        serializedBytes = serialize(resource);
        resource = (Resource) deserialize(serializedBytes);
        assertNotNull(resource);
        assertEquals("nvLibrary", "nvLibrary", resource.getLibraryName());
        assertEquals("duke-nv.gif", "duke-nv.gif", resource.getResourceName());
        assertEquals("image/gif", "image/gif", resource.getContentType());

    }


    /**
     * Added for issue 1274.
     */
    public void testResourceELEval() throws Exception {

        ResourceHandler handler = getFacesContext().getApplication().getResourceHandler();
        assertNotNull(handler);

        Resource resource = handler.createResource("simple-with-el.css");
        assertNotNull(resource);

        byte[] bytes = getBytes(resource.getInputStream());
      
        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bai));
        List<String> lines = new ArrayList<String>();

        for (String l = reader.readLine(); l != null; l = reader.readLine()) {
            String t = l.trim();
            if (t.length() > 0) {
                lines.add(t);
            }
        }

        assertEquals(4, lines.size());

        final String[] expectedLines = {
            "# /test",
            "# /test",
            "h2 { color: red }",
            "# /test}"
        };

        for (int i = 0, len = expectedLines.length; i < len; i++) {
            assertEquals(expectedLines[i], expectedLines[i], lines.get(i));
        }
        
    }


    // ---------------------------------------------------------- Helper Methods


    private byte[] serialize(Object object) throws Exception {

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bytesOut);
        oout.writeObject(object);
        oout.flush();
        oout.close();
        return bytesOut.toByteArray();

    }


    private Object deserialize(byte[] bytes) throws Exception {

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bytesIn);
        return in.readObject();

    }


    private byte[] getBytes(URL url) throws Exception {

        URLConnection c = url.openConnection();
        c.setUseCaches(false);
        InputStream in = c.getInputStream();
        return getBytes(in);

    }

    private byte[] getBytes(InputStream in) throws Exception {

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        for (int i = in.read(); i != -1; i = in.read()) {
            o.write(i);
        }
        in.close();
        return o.toByteArray();

    }    

}
