/*
 * 	CVS: $Id: Helpers.java,v 1.3 2004/07/16 16:22:33 marcus Exp $
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

package org.jzuul.engine;

/**
 * Diese Klasse enthällt Hilfsfunktionen für das gesamte JZuul Engine
 * 
 * 
 * @version $Revision: 1.3 $
  */
public class Helpers {
	/**
	 * Wandelt das erste Zeichen eines Strings in einen Großbuchstaben um
	 * 
	 * @param value	der String dessen erstes Zeichen umgewandelt werden soll
	 * @return				der gleiche String mit dem umgewandelten ersten Zeichen
	 */
	public static String firstToUpper(String value) {
		if (value == null) {
			return null;
			} 
		char upcase = java.lang.Character.toUpperCase(value.charAt(0));
		return String.valueOf(upcase) + value.substring(1);
		}


}
