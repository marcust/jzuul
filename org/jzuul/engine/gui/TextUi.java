/*
 * 	CVS: $Id: TextUi.java,v 1.11 2004/07/16 16:22:34 marcus Exp $
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

package org.jzuul.engine.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.text.AttributeSet;

import org.jzuul.engine.Engine;

/**
 * Diese Klasse Implementiert ein minimales Benutzerinterface auf Basis
 * von Text Ein-/Ausgabe.
 * 
 * 
 * @version $Revision: 1.11 $
 */
public class TextUi implements GuiInterface {

	protected ActionListener current;
	protected ActionListener defaultListener;
	protected boolean enabled;

	/**
	 * 
	 */
	public TextUi() {
		super();
		this.enabled = true;
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#println(java.lang.String, java.awt.Color)
	 */
	public void println(String out, int[] color) {
		println(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#print(java.lang.String, javax.swing.text.SimpleAttributeSet)
	 */
	public void print(String out, AttributeSet a) {
		print(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#print(java.lang.String)
	 */
	public void print(String out) {
		System.out.print(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#println(java.lang.String)
	 */
	public void println(String out) {
		System.out.println(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#println()
	 */
	public void println() {
		System.out.println();
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#printU(java.lang.String)
	 */
	public void printU(String out) {
		print(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#printlnB(java.lang.String)
	 */
	public void printlnB(String out) {
		println(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#printlnB(java.lang.String, java.awt.Color)
	 */
	public void printlnB(String out, int[] c) {
		println(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#printlnI(java.lang.String)
	 */
	public void printlnI(String out) {
		println(out);
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#printlnI(java.lang.String, java.awt.Color)
	 */
	public void printlnI(String out, int[] c) {
		println(out);
	}

	public void setActionListener(ActionListener al) {
		current = al;
	}

	public void setDefaultActionListener() {
		this.current = this.defaultListener;
	}

	public void setDefaultActionListener(ActionListener defaultListener) {
		this.defaultListener = defaultListener;
		this.current = defaultListener;
	}

	public void setKeyListener(KeyListener list) {
		// not implemented, we don't get single listeners
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#enableInput()
	 */
	public void enableInput() {
		this.enabled = true;
		System.out.print("> ");
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#disableInput()
	 */
	public void disableInput() {
		this.enabled = false;
	}

	/* (non-Javadoc)
	 * @see jzuul.engine.gui.GuiInterface#redraw()
	 */
	public void redraw() {
		// not implemented, we can't redraw the screen
	}

	public void start() {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				if (enabled) {
					try {
						System.out.print("> ");
						ActionEvent e = new ActionEvent(this, 0, r.readLine());
						this.current.actionPerformed(e);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Engine.delay(1000);
				}

			} finally {
			}
		}
	}

	public void resetInput() {
		// doesn't apply here as well
	}
	
	public boolean isApplet() {
		return false;
	}

	public ActionListener getActionListener() {
		return this.current;
	}
	
	public String getInput() {
		return null;
	}
	
	public void setInput(String newline) {
			
	}
	
	public void splash_start(int values) {
		
	}
	
	public void splash_next(String message) {
		println(message);
	}
	
	public void close() {
		enabled = false;
		System.exit(0);
	}
	
	public void showImage(InputStream imageStream) {
		//hier wird wohl nichts mehr stehn
		//ausser wir können aalib per jni ansprechen :-D
	}
	
	
}
