/*
 * 	CVS: $Id: GameObjectTest.java,v 1.2 2004/07/16 16:22:33 marcus Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jzuul.engine.Character;
import org.jzuul.engine.GameObject;
import org.jzuul.engine.Item;

import junit.framework.TestCase;

/**
 * 
 */
public class GameObjectTest extends TestCase {
	GameObject go;

	public void testSetDescription() {
		go.setDescription("foo");
		assertEquals(go.getDescription(), "foo");

		go.setDescription("bar");
		assertEquals(go.getDescription(), "bar");

		try {
			go.setDescription(null);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
		}

	}

	public void testSetName() {
		go.setName("foo");
		assertEquals(go.getName(), "foo");

		go.setName("bar");
		assertEquals(go.getName(), "bar");
		
		go.setName("BAZ");
		assertEquals(go.getName(), "baz");
		
		try {
			go.setName(null);
			fail("exception expected");
		} catch (IllegalArgumentException e) {
		}

	}

	public void testSetUsability() {
		go.setUsability(true);
		assertTrue(go.isUsable());

		go.setUsability(false);
		assertFalse(go.isUsable());
	}

	public void testIsCharacter() {
		assertEquals(go.isCharacter(), go instanceof Character);
	}

	public void testIsItem() {
		assertEquals(go.isItem(), go instanceof Item);
	}

	public void testToElement() {
		assertNotNull(go.toElement());
		assertTrue(go.toElement() instanceof Element);
	}

	public void testSetTakeable() {
		go.setTakeable(true);
		assertTrue(go.isTakeable());

		go.setTakeable(false);
		assertFalse(go.isTakeable());
	}

	public void testSetSize() {
		go.setSize(50);
		assertEquals(go.getSize(), 50);

		go.setSize(20);
		assertEquals(go.getSize(), 20);

		try {
			go.setSize(-10);
			fail("Illegal argument expected");
		} catch (IllegalArgumentException e) {
		}

	}

	public void testSetProperties() {
		Map testMap = new HashMap();
		testMap.put("description", "test");

		go.setProperties(testMap);

		assertEquals(go.getDescription(), "test");
	}

	public void testGetTargetList() {
		assertNotNull(go.getTargetList());
	}

}
