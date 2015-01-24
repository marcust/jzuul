/*
 * 	CVS: $Id: ObjectCacheTest.java,v 1.4 2004/07/16 16:22:33 marcus Exp $
 * 
 *  This file is part of zuul.
 *
 *  zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  zuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */
package org.jzuul.engine.test;

import org.jzuul.engine.ObjectCache;

import junit.framework.TestCase;


/**
 * 
 * Created on Jun 2, 2004
 * 
 * 
 * @version $Revision: 1.4 $
 */
public class ObjectCacheTest extends TestCase {
    ObjectCache small,big;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        small = new ObjectCache(2);
        big = new ObjectCache(1000);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        small = null;
        big = null;
    }

    public void testPut() {
        small.put("fist","first");
        small.put("second","second");
        small.put("third","third");
        
        assertFalse(small.containsKey("first"));
        assertTrue(small.containsKey("second"));
        assertTrue(small.containsKey("third"));
        
    }

}
