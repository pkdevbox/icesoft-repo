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

package org.icefaces.domDiff;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.icefaces.impl.util.DOMUtils;
import org.junit.Test;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import static org.junit.Assert.*;

import java.io.File;


/**
 *
 */
public class DomDiffTest {

    @Test
    /**
     * Test identical Documents
     */
    public void test01() {
        Document test01_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test01_A.xml");
        Document test01_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test01_B.xml");
       File f = new File("./");
        System.out.println("Location of file is: " + f.getAbsolutePath());
        Node[] diff = DOMUtils.domDiff( test01_A, test01_B);
        assertTrue (diff != null && diff.length == 0);
    }

    @Test
    /**
     * Test if one node has a different value. This is the first case in DomUtils
     */
    public void test02() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test02_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test02_B.xml");
        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("topLevelDiv"));
    }

    @Test
    /**
     * Test if two nodes have different id's.
     */
    public void test03() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test03_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test03_B.xml");
        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("topLevelDiv"));
    }

    @Test
    /**
     * Test if two nodes have different attributes. This test finds the nodes
     * themselves, not the parent nodes
     */
    public void test04() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test04_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test04_B.xml");
        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();

        assertTrue( id.equals("h1_id2"));
    }

    @Test
    /**
     * Test if two nodes have different values
     */
    public void test05() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test05_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test05_B.xml");
        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();

        assertTrue( id.equals("h1_id3"));
    }

    @Test
    /**
     * Test if two nodes have a different number of children
     */
    public void test06() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test06_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test06_B.xml");
        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();

        assertTrue( id.equals("topLevelDiv"));
    }


    @Test
    /**
     * Test the amount of time for a 200 input field diff
     */
    public void test07() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test07_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test07_B.xml");

        long start = System.nanoTime();
        Node[] diff = DOMUtils.domDiff( test_A, test_B);
        long delta = System.nanoTime() - start;

        // This assertion guarantees the full diff was run through. 
        assertTrue(diff != null && diff.length == 0);
        System.out.println("time for delta = " + delta/1e9 + " seconds");

    }

    @Test
    /**
     * Test the amount of time for a 400 input field diff. Should be linear time
     * and not exponential.
     */
    public void test08() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test08_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test08_B.xml");

        long start = System.nanoTime();
        Node[] diff = DOMUtils.domDiff( test_A, test_B);
        long delta = System.nanoTime() - start;

        // This assertion guarantees the full diff was run through.
        assertTrue(diff != null && diff.length == 0);
        System.out.println("time for delta = " + delta/1e9 + " seconds");
    }

    @Test
    /**
     * Check the difference in the number of attributes
     */
    public void test09() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test09_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test09_B.xml");

        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("h1_id2"));
    }

    @Test
    /**
     * Check the difference in name by case. 
     */
    public void test10() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test10_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test10_B.xml");

        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("topLevelDiv"));
    }

    @Test
    /**
     * Check the difference in attributes by case
     */
    public void test11() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test11_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test11_B.xml");

        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("h1_id2"));
    }

    @Test
    /**
     * Check the difference in id by case 
     */
    public void test12() {
        Document test_A = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test12_A.xml");
        Document test_B = parseFile( "src/test/java/org/icefaces/domDiff/scripts/test12_B.xml");

        Node[] diff = DOMUtils.domDiff( test_A, test_B);

        assertTrue(diff != null);
        NamedNodeMap nnp = diff[0].getAttributes();
        Node idNode = nnp.getNamedItem("id");
        String id = idNode.getNodeValue();
        assertTrue( id.equals("topLevelDiv"));
    }


    // utility parser. 
    private Document parseFile(String filename) {

        Document returnVal = null;
        DOMParser dp = new DOMParser();

        try {
            dp.parse( filename );
            returnVal = dp.getDocument();

        } catch (Exception e) {
            System.out.println("Exception parsing document: " + e);
            e.printStackTrace();
        }
        return returnVal;
    }
}
