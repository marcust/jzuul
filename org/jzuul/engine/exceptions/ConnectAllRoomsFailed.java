/*
 * 	CVS: $Id: ConnectAllRoomsFailed.java,v 1.3 2004/07/18 17:07:01 marcus Exp $
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
 
package org.jzuul.engine.exceptions;

/**
 * 
 */
public class ConnectAllRoomsFailed extends Exception {

	/**
	 * 
	 */
	public ConnectAllRoomsFailed() {
		super();
	}

	/**
	 * @param message
	 */
	public ConnectAllRoomsFailed(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConnectAllRoomsFailed(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnectAllRoomsFailed(String message, Throwable cause) {
		super(message, cause);
	}

}