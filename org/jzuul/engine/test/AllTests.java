/*
 * 	CVS: $Id: AllTests.java,v 1.5 2004/07/16 16:22:33 marcus Exp $
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

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * 
  */
public class AllTests  {

    /**
     * Lässt alle Tests laufen
     * 
     * @param args	die Kommandozeilenparameter
     */
	public static void main(String[] args) {
		
		if ((args.length == 0) || (args[0].equals("-swing"))) {
			junit.swingui.TestRunner.run(AllTests.class);
		} else if (args[0].equals("-text")) {
			junit.textui.TestRunner.run(suite());
		} else if (args[0].equals("-awt")) {
		junit.awtui.TestRunner.run(AllTests.class);
		} else { 
			System.out.println("Only -text, -swing and -awt are allowed options");
		}
	}

	/**
	 * Erstellt eine Test Objekt aus allen existierenden Tests
	 * 
	 * @return	ein Test objekt.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for JZuul");
		
		suite.addTest(new TestSuite(PlayerTest.class));
		suite.addTest(new TestSuite(InventoryTest.class));
		suite.addTest(new TestSuite(GameFileReaderTest.class));
		
		
		// EventListener Implementations:
		suite.addTest(new TestSuite(ItemEventListenerTest.class));
		suite.addTest(new TestSuite(CharacterEventListenerTest.class));
		suite.addTest(new TestSuite(PlayerEventListenerTest.class));
		suite.addTest(new TestSuite(RoomEventListenerTest.class));			
		
		//GameObject methods in Item and Character:
		suite.addTest(new TestSuite(ItemGameObjectTest.class));
		suite.addTest(new TestSuite(CharacterGameObjectTest.class));
		
		return suite;
	}
	
}
