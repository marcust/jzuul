/*
 * 	CVS: $Id: InventoryTest.java,v 1.3 2004/07/16 16:22:33 marcus Exp $
 * 
 *  This file is part of JZuul.
 *
 *  JZuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  JZuul is distributed in the hope that it will be useful,
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


import org.jzuul.engine.Inventory;
import org.jzuul.engine.Player;
import org.jzuul.engine.rooms.Room;
import junit.framework.TestCase;

/**
 * 
  */
public class InventoryTest extends TestCase {
	Inventory small, big, unlimited;
	Player p;
	Room r;
	
	/**
	 * Constructor for InventoryTest.
	 * @param arg0
	 */
	public InventoryTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		small = new Inventory(2);
		big = new Inventory(1000);
		unlimited = new Inventory(Inventory.UNLIMITED_INVENTORY);
		r = new Room("test","a test room");
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddGameObject() {
		
		
	}

	public void testGetGameObject() {
	
	
	}

	public void testGameObjectNamesIterator() {
	}

	public void testContainsGameObject() {
	}

	public void testDeleteGameObject() {
	}

	public void testPopGameObject() {
		
	}

	public void testGetCharacterObjects() {
		
	}

}
