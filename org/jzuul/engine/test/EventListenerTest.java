/*
 * 	CVS: $Id: EventListenerTest.java,v 1.4 2004/07/22 18:02:08 marcus Exp $
 * 
 *  This file is part of Zuul.
 *
 *  Zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Zuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */

package org.jzuul.engine.test;

import junit.framework.TestCase;

import org.jzuul.engine.Event;
import org.jzuul.engine.EventHandler;
import org.jzuul.engine.EventListener;
import org.jzuul.engine.exceptions.NoSuchEventException;



/**
 * 
 */
public class EventListenerTest extends TestCase {
	EventListener el;

	public void testDoEvent() {
		// this should not blow up
		el.doEvent(0);
		el.doEvent(Event.COUNT - 1);
		//neither should this
		try {
			el.doEvent(Event.COUNT);
			fail("Illegal argument expected");
		} catch (IllegalArgumentException e) {
		    // Silent Catch
		}
		try {
			el.doEvent(-1);
			fail("Illegal argument expected");
		} catch (IllegalArgumentException e) {
		    // Silent Catch
		}
	}

	public void testSetHandler() {
		//work tests
		el.setHandler("default", new EventHandler());

		// blow up tests
		try {
			el.setHandler("Fooobar", new EventHandler());
			 fail("Exception expected");
		} catch (NoSuchEventException e) {
		}

		
		//TODO Fix those tests
		try {
		    el.setHandler(null, null);

		} catch (IllegalArgumentException e) {
		    
		}
		
		try {
		el.setHandler(null, new EventHandler());
		
		} catch (IllegalArgumentException e) {
		    
		}
		
		try {
		el.setHandler("default", null);
		} catch (IllegalArgumentException e) {
		    
		}
		

	}

}
