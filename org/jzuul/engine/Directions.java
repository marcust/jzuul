/*
 * 	CVS: $Id: Directions.java,v 1.4 2004/07/16 16:22:33 marcus Exp $
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
 * Diese Klasse definiert Konstanten für Type Variablen in der {@link org.jzuul.engine.GameMap} 
 * 
 * 
 * @version $Revision: 1.4 $
 */
public class Directions {
	/**
	 * Die Anzahl der unterschiedlichen Richtungen
	 */
	public static final int NUMBER_OF_DIRECTIONS = 6;

	/**
	 * Room befindet sich im Norden von Room
	 */
	public static final int NORTH_OF = 0;

	/**
	 * Room befindet sich im Osten von Room
	 */
	public static final int EAST_OF = 1;

	/**
	 * Room befindes sich im Süden von Room
	 */
	public static final int SOUTH_OF = 2;

	/**
	 * Room befindes sich im Westen von Room
	 */
	public static final int WEST_OF = 3;

	/**
	 * Room befindet sich über Room (es existiert eine Treppe) 
	 */
	public static final int TOP_OF = 4;

	/**
	 * Room befindet sich unter Room (es existiert eine Treppe)
	 */
	public static final int BELOW_OF = 5;

	/**
	 * Semantischer Alias für NORTH_OF
	 */
	public static final int NORTH = NORTH_OF;

	/**
	 * Semantischer Alias für EAST_OF
	 */
	public static final int EAST = EAST_OF;

	/**
	 * Semantischer Alias für SOUTH_OF
	 */

	public static final int SOUTH = SOUTH_OF;

	/**
	 * Semantischer Alias für WEST_OF
	 */
	public static final int WEST = WEST_OF;

	/**
	 * Semantischer Alias für TOP_OF
	 */
	public static final int TOP = TOP_OF;

	/**
	 * Semantischer Alias für BELOW_OF
	 */
	public static final int BELOW = BELOW_OF;

	/**
	 * Ein Richtungsfehler
	 */
	public static final int DIRECTION_ERROR = -1;

	/**
	 * Wandelt eine Direction Konstante in die text Representation um
	 * 
	 * @param direction	Eine direction Konstante
	 * @return Die Stringrepresentation der Konstant, null bei fehler
	 */
	public static String getDirectionName(int direction) {
		switch (direction) {
			case NORTH :
				return "north";
			case EAST :
				return "east";
			case SOUTH :
				return "south";
			case WEST :
				return "west";
			case BELOW :
				return "below";
			case TOP :
				return "above";

			case DIRECTION_ERROR :
				return "DIRECTION ERROR";
		}
		return null;
	}
	
	/**
	 * Wandelt einen Richtungsnamen in seine Zahl um
	 * 
	 * @param direction	der Name einer Himmelrichtung
	 * @return	die zugehörige Direction Nummer, DIRECTION_ERROR bei unbekanntem Namen
	 */
	public static int directionToInt(String direction){
		if(direction.equalsIgnoreCase("north")){
			return NORTH;
		}
		else if(direction.equalsIgnoreCase("east")){
			return EAST;
		}
		else if(direction.equalsIgnoreCase("south")){
			return SOUTH;
		}
		else if(direction.equalsIgnoreCase("west")){
			return WEST;
		}
		else if(direction.equalsIgnoreCase("above")){
			return TOP;
		}
		else if(direction.equalsIgnoreCase("below")){
			return BELOW;
		}
		else if(direction.equalsIgnoreCase("up")){
			return TOP;
		}
		else if(direction.equalsIgnoreCase("down")){
			return BELOW;
		}
		else{
			return Directions.DIRECTION_ERROR;
		}
	}


}
