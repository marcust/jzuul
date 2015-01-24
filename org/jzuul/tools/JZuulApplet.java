/*
 * 	CVS: $Id: JZuulApplet.java,v 1.1 2004/07/21 11:13:46 marcus Exp $
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
 * 	Initially based on an example by Michael Kolling and David J. Barnes
 * 
 */

package org.jzuul.tools;

import java.util.List;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.RootPaneContainer;

import org.jzuul.engine.CommandList;
import org.jzuul.engine.Engine;
import org.jzuul.engine.gui.GuiInterface;
import org.jzuul.engine.gui.SwingGui;
import org.jzuul.engine.gui.TextUi;

/**
 * Starterklasse für das supertolle ZuulSpiel
*
* 
* @version $Revision: 1.1 $
*/

public class JZuulApplet extends JApplet implements Runnable {
	boolean init;
	Engine engine;
	JZuulApplet game;
	Thread runner;
	List cmdargs;
	
	public static String gamefile, demo;
	

	public JZuulApplet() {
	    
	}
	
	public JZuulApplet(List cmdargs) {
	    this.cmdargs = cmdargs;
	}
	
	private void initEngine(RootPaneContainer root) {

		GuiInterface gui = null;
		if (cmdargs.contains("text")) {
			gui = new TextUi();
		} else {
			gui = new SwingGui(root);
		}
		this.engine = new Engine(JZuulApplet.gamefile, CommandList.defaultList(), gui);
	}

	private void runGame() {

	    boolean withPlayerName = false;

		if (!this.cmdargs.isEmpty()) {
			if (cmdargs.contains("demo") && (JZuulApplet.demo != null)) {
				Engine.debug("Doing a history run", 1);
				engine.runHistory(JZuulApplet.demo);
				Engine.debug("Finished History run", 1);
			}
			if (cmdargs.contains("name")) {
				withPlayerName = true;
			}

		}

		engine.run(withPlayerName);
	}

	// to run as Program
	public static void main(String[] args) {
		Vector v = new Vector(args.length);
		for (int i = 0; i < args.length; i++) {
			v.add(args[i].replaceFirst("-+", ""));
		}
		JZuulApplet game = new JZuulApplet(v);
		
		game.initEngine(null);
		game.runGame();
	}

	// Applet part:
	public void init() {
		runner = new Thread(this);
		Vector v = new Vector();
		if (this.getParameter("demo") != null) {
			v.add("demo");
		}

		game = new JZuulApplet(v);
		game.init = true;
		game.initEngine(this);
	}

	public void start() {
		Engine.debug("Now calling run\n", 1);
		runner.start();
		Engine.debug("JApplet start returned", 1);
	}

	public void run() {
		game.runGame();
	}

	public boolean isActive() {
		return this.init;
	}

	public String getAppletInfo() {
		return "This is Zuul, $Revision: 1.1 $";
	}

	public void stop() {
	    Engine.exit(0);
	    engine = null;
	    JZuulApplet.gamefile = null;
	    JZuulApplet.demo = null;
	}
	
}
