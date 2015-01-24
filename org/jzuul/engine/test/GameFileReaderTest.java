/*
 * 	CVS: $Id: GameFileReaderTest.java,v 1.7 2004/07/22 18:02:08 marcus Exp $
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
 *  Copyrigth 2004 by mthies2s
 * 
 */

package org.jzuul.engine.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;
import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.NoSuchRoomException;

import org.jzuul.engine.Engine;
import org.jzuul.engine.GameFileReader;
import junit.framework.TestCase;

/**
 * 
 */
public class GameFileReaderTest extends TestCase {

	/**
	 * Constructor for GameFileReaderTest.
	 * @param arg0
	 */
	public GameFileReaderTest(String arg0) {
		super(arg0);
		Engine.DEBUG = 0;
	}

	public void testGameFileReader() {
		GameFileReader malformed = null;
		try {
			malformed = new GameFileReader(new FileInputStream("org/jzuul/engine/test/resources/malformed.xml"));
			fail("Exception expected");
		} catch (Exception e) {
		}
		assertNull(malformed);

		GameFileReader wellformed = null;
		try {
			wellformed = new GameFileReader(new FileInputStream("org/jzuul/engine/test/resources/wellformed.xml"));
		} catch (Exception e) {
			fail("Not expected " + e.getMessage()); // should never happen
		}
		assertNotNull(wellformed);

		//without dtd:
		wellformed = null;
		try {
			wellformed = new GameFileReader(new FileInputStream("org/jzuul/engine/test/resources/wellformed.xml"),false);
		} catch (Exception e) {
			fail("Not expected " + e.getMessage()); // should never happen
		}
		assertNotNull(wellformed);



	}

	public void testGetMap() {
		GameFileReader r = null;
		try {
			r = new GameFileReader(new FileInputStream("org/jzuul/engine/test/resources/circle.xml"));
		} catch (Exception e) {
			fail("Not expected " + e.getMessage()); // should never happen
			
		}
		assertNotNull(r);
		try {
			assertNotNull(r.getMap("default"));
		} catch (ConnectAllRoomsFailed e1) {
			fail("not expected " + e1.getMessage());
		} catch (NoSuchRoomException e1) {
			fail("not expected " + e1.getMessage() );
		}
		r = null;

		try {
			r = new GameFileReader(new FileInputStream("org/jzuul/engine/test/resources/missing_room.xml"));
			r.getMap("default").verifyMap();
			fail("exception expected");
		} catch (FileNotFoundException e2) {
			fail("not expected " + e2.getMessage());
		} catch (IOException e2) {
			fail("not expected " + e2.getMessage());
		} catch (JDOMException e2) {
			fail("not expected " + e2.getMessage());
		} catch (ConnectAllRoomsFailed e1) {
			fail("not expected " + e1.getMessage());
		} catch (NoSuchRoomException e1) {
			// this is what we expect
		}

		assertNotNull(r);
		

	}

}
