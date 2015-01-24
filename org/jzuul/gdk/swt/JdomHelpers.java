/*
 * 	CVS: $Id: JdomHelpers.java,v 1.13 2004/07/20 12:06:19 marcus Exp $
 * 
 *  This file is part of zuul.
 *
 *  zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  zuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */
package org.jzuul.gdk.swt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.naming.BinaryRefAddr;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.13 $
 */
public class JdomHelpers {

    private static Element root;

    /**
     *  
     */
    public JdomHelpers() {
        super();
    }

    public static Element getGamefileElement(Element someElement) {
        Parent parent = someElement;

        while (parent.getParent() != null)
            parent = parent.getParent();
        List contents = parent.getContent();
        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Content contentElement = (Content) contentIter.next();
            if (contentElement instanceof Element) {
                JdomHelpers.root = (Element) contentElement;
                return (Element) contentElement;
            }
        }
        System.err.println("WARNING: Couldn't get top level element <gamefile>");
        return null;
    }

    public static List getFirstLevelElements(Element someElement) {
        if (someElement == null) return null;
        if (JdomHelpers.getRoot() != null) { return root.getChildren(); }
        List elements = new ArrayList();
        Parent parent = someElement;
        while (parent.getParent() != null)
            parent = parent.getParent();
        List contents = parent.getContent();
        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Content contentElement = (Content) contentIter.next();
            if (contentElement instanceof Element) elements.add(contentElement);
        }
        if (elements.size() == 0) System.err.println("WARNING: Couldn't get any elements");
        if (elements.size() == 1) return ((Element) elements.get(0)).getChildren();
        return elements;
    }

    public static List getFirstLevelElements(Element someElement, String name) {
        if (someElement == null || name == null) return null;
        List elements = getFirstLevelElements(someElement);
        List retval = new ArrayList();
        for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
            Element element = (Element) elementIter.next();
            if (element.getName().equals(name)) retval.add(element);
        }

        return retval;
    }

    public static Element getFirstLevelElement(Element someElement, String name) {
        if (someElement == null || name == null) return null;
        List elements = JdomHelpers.getFirstLevelElements(someElement);
        if (elements == null) return null;
        for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
            Element anElement = (Element) elementIter.next();
            if (anElement.getName().equals(name)) return anElement;
        }
        System.err.println("WARNING: couldn't find element " + name);
        return null;
    }

    public static String[] getRoomListForMap(Element someElement, String mapName) {
        if (someElement == null || mapName == null) return null;
        List elements = JdomHelpers.getFirstLevelElements(someElement);
        List rooms = null;
        List roomNames = new ArrayList();
        for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
            Element element = (Element) elementIter.next();
            if (element.getName().equals("map") && element.getAttributeValue("name").equals(mapName))
                    rooms = element.getChildren();
        }
        if (rooms != null) {
            for (Iterator roomIter = rooms.iterator(); roomIter.hasNext();) {
                Element roomElement = (Element) roomIter.next();
                String name = roomElement.getAttributeValue("name");
                if (name != null) roomNames.add(name);
            }
            String[] retval = new String[roomNames.size()];
            roomNames.toArray(retval);
            return retval;
        }
        return null;
    }

    public static String[] getItemNames(Element someElement) {
        if (someElement == null) return null;
        List itemNames = new ArrayList();

        Element gameobject = JdomHelpers.getFirstLevelElement(someElement, "gameobjects");

        Element items = gameobject.getChild("items");
        List gameitems = items.getChildren("gameitem");
        for (Iterator gameItemIter = gameitems.iterator(); gameItemIter.hasNext();) {
            Element gameitem = (Element) gameItemIter.next();
            itemNames.add(gameitem.getAttributeValue("name"));
        }
        String[] retval = new String[itemNames.size()];
        itemNames.toArray(retval);
        return retval;
    }

    public static String[] getCharacterNames(Element someElement) {
        if (someElement == null) return null;

        List charNames = new ArrayList();

        Element gameobject = JdomHelpers.getFirstLevelElement(someElement, "gameobjects");
        Element items = gameobject.getChild("characters");
        if (items != null) {
            List gameitems = items.getChildren("person");
            for (Iterator gameItemIter = gameitems.iterator(); gameItemIter.hasNext();) {
                Element gameitem = (Element) gameItemIter.next();
                charNames.add(gameitem.getAttributeValue("name"));
            }
            String[] retval = new String[charNames.size()];
            charNames.toArray(retval);
            return retval;
        } else {
            return null;
        }

    }

    public static String[] getMapNames(Element someElement) {
        if (someElement == null) return null;
        List mapNames = new ArrayList();

        List maps = JdomHelpers.getFirstLevelElements(someElement, "map");

        for (Iterator gameItemIter = maps.iterator(); gameItemIter.hasNext();) {
            Element gameitem = (Element) gameItemIter.next();
            mapNames.add(gameitem.getAttributeValue("name"));
        }
        String[] retval = new String[mapNames.size()];
        mapNames.toArray(retval);
        return retval;
    }

    public static Attribute findAttribute(List attributes, Attribute search) {
        System.err.println("JDOM: Searching for " + search.getName() + "=" + search.getValue());
        for (Iterator attIter = attributes.iterator(); attIter.hasNext();) {
            Attribute att = (Attribute) attIter.next();
            System.err.println("JDOM: Attribute List " + att);
            if (att.getName().equals(search.getName()) && att.getValue().equals(search.getValue())) return att;
        }
        return null;
    }

    public static boolean attributeEquals(List attributes1, List attributes2) {
        Stack first = new Stack();
        first.addAll(attributes1);

        if (first.size() != attributes2.size()) return false;
        while (first.size() != 0) {
            Attribute a1 = (Attribute) first.pop();
            Attribute a2 = findAttribute(attributes2, a1);
            if (a2 == null) {
                System.err.println("JDOM: false (4)");
                return false;
            }
            System.err.println("JDOM: checking " + a1 + " == " + a2);
            if ((!a1.getName().equals(a2.getName())) || (!a1.getValue().equals(a2.getValue()))) {
                System.err.println("JDOM: false (3)");
                return false;
            }
        }
        return true;
    }

    public static boolean equalsDeep(Element firstElement, Element secondElement) {
        boolean retval = true;
        if ((firstElement == null) && (secondElement == null)) return true;
        System.err.println("JDOM: checking " + firstElement + " == " + secondElement);
        List contents = firstElement.getChildren();

        if (contents.size() != secondElement.getChildren().size()) return false;

        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Element subElement = (Element) contentIter.next();
            Element secondSubElement = secondElement.getChild(subElement.getName());
            if (secondSubElement != null) {
                if (attributeEquals(subElement.getAttributes(), secondSubElement.getAttributes())) {
                    System.err.println("JDOM: recursion " + subElement);
                    retval = equalsDeep(subElement, secondElement.getChild(subElement.getName()));
                } else {
                    System.err.println("JDOM: false (1)");
                    return false;
                }
            } else {
                System.err.println("JDOM: false (2)");
                return false;
            }
        }
        return retval;
    }

    public static boolean equals(Element firstElement, Element secondElement) {
        List first = flattenTreeAsStrings(firstElement);
        List second = flattenTreeAsStrings(secondElement);
        System.err.println("Tree contains " + first.size() + " values");
        if (first.size() != second.size()) return false;
        first.removeAll(second);
        if (first.size() != 0) {
            System.err.println("Trees differ, values which are in clone but not in data:");
            System.err.println(first);
            return false;
        }
        return true;
    }

    public static List flattenTreeAsStrings(Element root) {
        List retval = new ArrayList();
        if (root == null) return retval;
        List elements = root.getChildren();
        if (elements != null && elements.size() > 0) {
            for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
                Element element = (Element) elementIter.next();
                retval.addAll(flattenTreeAsStrings(element));
            }
        } else {
            retval.add(root.getName() + " " + attlistToString(root.getAttributes()) + " " + root.getText());
        }
        return retval;
    }

    public static String attlistToString(List attributes) {
        String retval = "";
        for (Iterator attIter = attributes.iterator(); attIter.hasNext();) {
            Attribute att = (Attribute) attIter.next();
            retval += att.getName() + "=" + att.getValue() + " ";
        }
        return retval;
    }

    public static Element getRoot() {
        return JdomHelpers.root;
    }

    public static void setRoot(Element root) {
        JdomHelpers.root = root;
    }

    public static void deepObjectDelete(String gameObjectName) {
        List objectList = deepObjectFind(gameObjectName);
        detachAll(objectList);
    }

    public static void deepObjectRename(String gameObjectName, String newName) {
        List objectList = deepObjectFind(gameObjectName);
        renameAll(objectList, newName);
    }

    public static List deepObjectFind(String gameObjectName) {
        Element gamefile = root;
        List retval = new ArrayList();

        List maps = gamefile.getChildren("map");
        for (Iterator mapIter = maps.iterator(); mapIter.hasNext();) {
            Element mapElement = (Element) mapIter.next();
            List rooms = mapElement.getChildren("room");
            for (Iterator roomIter = rooms.iterator(); roomIter.hasNext();) {
                Element roomElement = (Element) roomIter.next();
                retval.addAll(findObjectElementsInContents(gameObjectName, roomElement.getChild("contents")));
                retval.addAll(findObjectElementInEvents(gameObjectName, roomElement.getChildren("event")));
            }
            List transrooms = mapElement.getChildren("transitionroom");
            for (Iterator transIter = transrooms.iterator(); transIter.hasNext();) {
                Element transRoomElement = (Element) transIter.next();
                retval.addAll(getPreconItems(gameObjectName, transRoomElement.getChildren("precondition")));

            }
        }
        List player = gamefile.getChildren("player");
        for (Iterator playerIter = player.iterator(); playerIter.hasNext();) {
            Element playerElement = (Element) playerIter.next();
            retval.addAll(findObjectElementsInContents(gameObjectName, playerElement.getChild("contents")));
            retval.addAll(getItemFromTarget(gameObjectName, playerElement.getChildren("target")));
        }
        Element dialogs = gamefile.getChild("dialogs");
        List dialogOwners = dialogs.getChildren("dialog-owner");
        for (Iterator dialogOwnerIter = dialogOwners.iterator(); dialogOwnerIter.hasNext();) {
            Element dialogOwnerElement = (Element) dialogOwnerIter.next();
            if (dialogOwnerElement.getAttributeValue("name").equals(gameObjectName)) {
                retval.add(dialogOwnerElement);
            }
            List dialogsObj = dialogOwnerElement.getChildren("dialog");
            for (Iterator dialogIter = dialogsObj.iterator(); dialogIter.hasNext();) {
                Element dialogElement = (Element) dialogIter.next();
                retval.addAll(getPreconItems(gameObjectName, dialogElement.getChildren("precondition")));
            }

        }

        Element gameobjects = gamefile.getChild("gameobjects");
        Element character = gameobjects.getChild("characters");
        List persons = character.getChildren("person");
        for (Iterator personIter = persons.iterator(); personIter.hasNext();) {
            Element personElement = (Element) personIter.next();
            retval.addAll(findObjectElementInEvents(gameObjectName, personElement.getChildren("event")));
        }

        Element items = gameobjects.getChild("items");
        List gameitems = items.getChildren("gameitem");
        for (Iterator gameItemIter = gameitems.iterator(); gameItemIter.hasNext();) {
            Element gameItemElement = (Element) gameItemIter.next();
            retval.addAll(findObjectElementInEvents(gameObjectName, gameItemElement.getChildren("event")));
            Element combinations = gameItemElement.getChild("combinations");
            if (combinations != null) {
                List withObjects = combinations.getChildren("with-object");
                for (Iterator withObjIter = withObjects.iterator(); withObjIter.hasNext();) {
                    Element withObjElement = (Element) withObjIter.next();
                    if (withObjElement.getAttributeValue("name").equals(gameObjectName)) {
                        retval.add(withObjElement);
                        retval.addAll(getActionItems(gameObjectName, withObjElement.getChild("actions")));
                    }

                }
            }

        }
        System.err.println("Found " + retval.size() + " occurences of gameobject " + gameObjectName);
        return retval;
    }

    public static List findObjectElementsInContents(String gameObjectName, Element contents) {
        List retval = new ArrayList();
        if (contents == null) return retval;
        List childs = contents.getChildren();
        for (Iterator contentIter = childs.iterator(); contentIter.hasNext();) {
            Element itemElement = (Element) contentIter.next();
            if (itemElement.getAttributeValue("name").equals(gameObjectName)) {
                retval.add(itemElement);
            }
        }
        return retval;
    }

    public static void detachAll(List elements) {
        for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
            Element element = (Element) elementIter.next();
            element.detach();
        }
    }

    public static void renameAll(List elements, String newName) {
        for (Iterator elementIter = elements.iterator(); elementIter.hasNext();) {
            Element element = (Element) elementIter.next();
            if (element.getAttributeValue("name") != null) {
                element.setAttribute("name", newName);
            } else if (element.getAttributeValue("item") != null) {
                element.setAttribute("item", newName);
            } else if (element.getAttributeValue("value") != null) {
                element.setAttribute("value", newName);
            } else if (element.getText() != null) {
                element.setText(newName);
            }
        }
    }

    public static List getPreconItems(String gameObjectName, List precons) {
        List retval = new ArrayList();
        if (precons == null) return retval;
        for (Iterator preconIter = precons.iterator(); preconIter.hasNext();) {
            Element element = (Element) preconIter.next();
            if (element.getAttributeValue("item").equals(gameObjectName)) {
                retval.add(element);
            }
        }
        return retval;

    }

    public static List findObjectElementInEvents(String gameObjectName, List events) {
        List retval = new ArrayList();
        if (events == null) return retval;
        for (Iterator eventIter = events.iterator(); eventIter.hasNext();) {
            Element eventElement = (Element) eventIter.next();
            retval.addAll(getActionItems(gameObjectName, eventElement.getChild("actions")));
        }
        return retval;
    }

    public static List getActionItems(String gameObjectName, Element actions) {
        List retval = new ArrayList();
        if (actions == null) return retval;
        List actionsElements = actions.getChildren();
        for (Iterator actionIter = actionsElements.iterator(); actionIter.hasNext();) {
            Element actionElement = (Element) actionIter.next();
            String n = actionElement.getName();
            if (n.equals("target")) {
                List tmp = new ArrayList();
                tmp.add(actionElement);
                retval.addAll(getItemFromTarget(gameObjectName, tmp));
            }
            if (n.equals("inventory-item") || n.equals("room-item") || n.equals("delete-item")) {
                retval.add(actionElement);
            }
        }
        return retval;
    }

    public static List getItemFromTarget(String name, List target) {
        List retval = new ArrayList();
        if (target == null) return retval;
        for (Iterator targetIter = target.iterator(); targetIter.hasNext();) {
            Element targetElement = (Element) targetIter.next();
            List conditions = targetElement.getChildren("condition");
            for (Iterator condIter = conditions.iterator(); condIter.hasNext();) {
                Element condElement = (Element) condIter.next();
                if (condElement.getAttributeValue("value").equals(name)) {
                    retval.add(condElement);
                }
            }
        }
        return retval;

    }

}