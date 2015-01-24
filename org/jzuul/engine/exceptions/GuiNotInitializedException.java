/*
 * 	CVS: $Id: GuiNotInitializedException.java,v 1.2 2004/07/16 16:22:34 marcus Exp $
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

package org.jzuul.engine.exceptions;

/**
 * Diese Exception wird geworfen wenn Engine.gui noch nicht initalisiert ist.
 * 
 * 
  */
public class GuiNotInitializedException extends RuntimeException {

	/**
	 * 
	 */
	public GuiNotInitializedException() {
		super();
	}

	/**
	 * @param message
	 */
	public GuiNotInitializedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GuiNotInitializedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GuiNotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

}
