/*
 * 	CVS: $Id: MapEditorComposite.java,v 1.22 2004/07/25 21:40:56 marcus Exp $
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jzuul.engine.GameFileReader;
import org.jzuul.engine.GameMap;
import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.NoSuchRoomException;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.22 $
 */
public class MapEditorComposite extends Composite {

    java.util.List maps;

    Combo mapCombo;

    private List roomList;

    private RoomDetailsComposite roomDetailComp;

    private Combo startRoom;

    private Button verifyButton;

    /**
     * @param arg0
     * @param arg1
     */
    public MapEditorComposite(Composite arg0, int arg1) {
        super(arg0, arg1);
        maps = new ArrayList();

        this.setLayout(new GridLayout(5, true));
        GridData mapEditorDat = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(mapEditorDat);

        // first row: label, map combo, new button, delete button
        Label mapLabel = new Label(this, SWT.NONE);
        GridData labelDat = new GridData(GridData.HORIZONTAL_ALIGN_END);
        mapLabel.setLayoutData(labelDat);
        mapLabel.setText(Messages.getString("MAP_COLON")); //$NON-NLS-1$

        mapCombo = new Combo(this, SWT.READ_ONLY);
        GridData comboDat = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        mapCombo.setLayoutData(comboDat);
        mapCombo.add("default"); //$NON-NLS-1$
        mapCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] roomNames = getRoomNames(mapCombo.getText());
                roomList.setItems(roomNames);
                roomList.select(0);
                roomList.notifyListeners(SWT.Selection, new Event());
                startRoom.setItems(roomNames);
                String startroom = getMap(mapCombo.getText()).getAttributeValue("startroom"); //$NON-NLS-1$
                if (startroom != null) startRoom.select(startRoom.indexOf(startroom));

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        final int buttonWidthHint = 100;
        Button newButton = new Button(this, SWT.PUSH);
        GridData nbDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        nbDat.widthHint = buttonWidthHint;
        newButton.setLayoutData(nbDat);
        newButton.setText(Messages.getString("NEW_2")); //$NON-NLS-1$
        newButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                InputDialog id = new InputDialog(new Shell(arg0.display), SWT.NONE);
                id.setMessage(Messages.getString("MAP_NAME_ENTER")); //$NON-NLS-1$
                String retval = id.openNoWhitespace();
                if (retval != null) {
                    if (mapCombo.indexOf(retval) > -1) {
                        Object[] formatArgs = {retval};
                        MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                        mb.setMessage(MessageFormat.format(Messages.getString("MAP_SAME_NAME_MSG"),formatArgs)); //$NON-NLS-1$
                        mb.open();
                    } else {
                        mapCombo.add(retval);
                        maps.add(new Element("map").setAttribute("name", retval)); //$NON-NLS-1$ //$NON-NLS-2$
                        mapCombo.select(mapCombo.indexOf(retval));
                        mapCombo.notifyListeners(SWT.Selection, new Event());
                    }
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Button deleteButton = new Button(this, SWT.PUSH);
        GridData dbDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        dbDat.widthHint = buttonWidthHint;
        deleteButton.setLayoutData(dbDat);
        deleteButton.setText(Messages.getString("DELETE")); //$NON-NLS-1$
        deleteButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                //TODO add r u sure
                try {
                    String mapName = mapCombo.getText();
                    if (mapName.equals("default")) { //$NON-NLS-1$
                        MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                        mb.setMessage(Messages.getString("DELETE_DEFAULT_ERROR")); //$NON-NLS-1$
                        mb.open();
                        return;
                    }
                    roomDetailComp.clear();
                    roomList.removeAll();
                    getMap(mapName).detach();
                    mapCombo.setItems(getMapNames()); 
                    if (mapCombo.getItemCount() > 0 ) {
                        mapCombo.select(0);
                        mapCombo.notifyListeners(SWT.Selection, new Event());
                    }
                } catch (Exception e) {
                    System.err.println("Silent Catch: \nprevented nullpointer expection (renaming null-object)\n" //$NON-NLS-1$
                            + e.getLocalizedMessage());
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        verifyButton = new Button(this, SWT.PUSH);
        GridData vbDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        vbDat.widthHint = buttonWidthHint;
        verifyButton.setLayoutData(vbDat);
        verifyButton.setText(Messages.getString("VERIFY_MAP")); //$NON-NLS-1$
        verifyButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                verifyMap(true);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Group leftGroup = new Group(this, SWT.NONE);
        GridData leftGroupDat = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
        leftGroup.setLayoutData(leftGroupDat);
        leftGroup.setLayout(new GridLayout(1, false));

        roomList = new List(leftGroup, SWT.SINGLE | SWT.BORDER);
        GridData listData = new GridData(GridData.FILL_BOTH);
        roomList.setLayoutData(listData);

        roomList.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (roomList.getItemCount() > 0) {
                    Element room = getRoom(roomList.getItem(roomList.getSelectionIndex()));
                    if (room != null) roomDetailComp.showRoomDetails(room, getCurrentMapName());
                } else {
                    roomDetailComp.clear();
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        roomList.setToolTipText(Messages.getString("ROOM_TOOLTIP")); //$NON-NLS-1$

        Menu popupMenu = new Menu(roomList);
        roomList.setMenu(popupMenu);

        MenuItem add = new MenuItem(popupMenu, SWT.NONE);
        add.setText(Messages.getString("ADD")); //$NON-NLS-1$
        add.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                InputDialog id = new InputDialog(new Shell(arg0.display), SWT.NONE);
                id.setMessage(Messages.getString("ROOM_NAME_ENTER")); //$NON-NLS-1$
                String name = id.openNoWhitespace();
                if (name != null) {
                    if (roomList.indexOf(name) == -1) {
                        startRoom.add(name);

                        if (roomList.getItemCount() == 0) {
                            startRoom.select(0);
                            startRoom.notifyListeners(SWT.Selection, new Event());
                        }
                        getCurrentMap().addContent(
                                new Element("room").setAttribute("name", name).setAttribute("class", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                        "org.jzuul.engine.rooms.Room")); //$NON-NLS-1$
                        roomList.setItems(getRoomNames(getCurrentMapName()));
                        roomList.select(roomList.indexOf(name));
                        roomList.notifyListeners(SWT.Selection, new Event());
                    } else {
                        Object[] formatArgs = {name};
                        MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                        mb.setMessage(MessageFormat.format(Messages.getString("ROOM_SAME_NAME_ERROR"),formatArgs) ); //$NON-NLS-1$
                        mb.open();
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        MenuItem rename = new MenuItem(popupMenu, SWT.NONE);
        rename.setText(Messages.getString("RENAME")); //$NON-NLS-1$
        rename.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                try {
                    String oldName = roomList.getItem(roomList.getSelectionIndex());
                    InputDialog id = new InputDialog(new Shell(arg0.display), SWT.NONE);
                    id.setMessage(Messages.getString("ROOM_RENAME_ENTER") + oldName); //$NON-NLS-1$
                    id.setDefaultValue(oldName);
                    String newName = id.openNoWhitespace();
                    if (newName != null) {
                        getRoom(oldName).setAttribute("name", newName); //$NON-NLS-1$
                        roomList.setItems(getRoomNames(getCurrentMapName()));
                        startRoom.remove(oldName);
                        startRoom.add(newName);
                    }
                } catch (Exception e) {
                    System.err.println("Silent Catch: \nprevented nullpointer expection (renaming null-object)\n" //$NON-NLS-1$
                            + e.getLocalizedMessage());
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        MenuItem delete = new MenuItem(popupMenu, SWT.NONE);
        delete.setText(Messages.getString("DELETE")); //$NON-NLS-1$
        delete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                //TODO add r u sure
                try {
                    roomDetailComp.clear();
                    getRoom(roomList.getItem(roomList.getSelectionIndex())).detach();
                    roomList.setItems(getRoomNames(getCurrentMapName()));
                    if (roomList.getItemCount() > 0) {
                        roomList.select(0);
                        roomList.notifyListeners(SWT.Selection, new Event());
                    }
                    startRoom.removeAll();
                    startRoom.setItems(getRoomNames(getCurrentMapName()));
                    if (startRoom.getItemCount() > 0) {
                        startRoom.select(0);
                        startRoom.notifyListeners(SWT.Selection, new Event());
                    }
                } catch (Exception e) {
                    System.err.println("Silent Catch: \nprevented nullpointer expection (deleting null-object)\n" //$NON-NLS-1$
                            + e.getLocalizedMessage());
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Group startRoomGroup = new Group(leftGroup, SWT.NONE);
        startRoomGroup.setLayout(new GridLayout(2, false));
        startRoomGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label srLabel = new Label(startRoomGroup, SWT.NONE);
        srLabel.setText(Messages.getString("STARTROOM")); //$NON-NLS-1$

        startRoom = new Combo(startRoomGroup, SWT.READ_ONLY);
        startRoom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        startRoom.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                getCurrentMap().setAttribute("startroom", startRoom.getText()); //$NON-NLS-1$

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        // the right side
        roomDetailComp = new RoomDetailsComposite(this, SWT.NONE);

    }

    public void setMaps(java.util.List maps) {
        System.err.println("setMaps called " + maps); //$NON-NLS-1$
        this.maps = maps;
        if (maps != null) {
            System.err.println("Map names are " + getMapNames()); //$NON-NLS-1$
            this.mapCombo.setItems(getMapNames());
        }
    }

    public String[] getMapNames() {
        java.util.List mapNames = new ArrayList();
        for (Iterator mapIter = maps.iterator(); mapIter.hasNext();) {
            Element map = (Element) mapIter.next();
            mapNames.add(map.getAttributeValue("name")); //$NON-NLS-1$
        }
        String[] retval = new String[mapNames.size()];
        mapNames.toArray(retval);
        return retval;
    }

    public Element getMap(String name) {
        for (Iterator mapIter = maps.iterator(); mapIter.hasNext();) {
            Element map = (Element) mapIter.next();
            if (map.getAttributeValue("name").equals(name)) return map; //$NON-NLS-1$
        }
        System.err.println("Creating new Element for map named " + name); //$NON-NLS-1$
        Element newMap = new Element("map"); //$NON-NLS-1$
        newMap.setAttribute("name", name); //$NON-NLS-1$
        maps.add(newMap);
        return newMap;
    }

    public String[] getRoomNames(String mapName) {
        Element map = getMap(mapName);
        java.util.List roomNames = new ArrayList();
        for (Iterator roomIter = map.getChildren().iterator(); roomIter.hasNext();) {
            Element mapChildEl = (Element) roomIter.next();
            String tag = mapChildEl.getName();
            if (tag.equals("room") || tag.equals("transitionroom")) { //$NON-NLS-1$ //$NON-NLS-2$
                roomNames.add(mapChildEl.getAttributeValue("name")); //$NON-NLS-1$
            }
        }
        String[] retval = new String[roomNames.size()];
        roomNames.toArray(retval);
        return retval;
    }

    public Element getRoom(String roomName) {
        Element map = getMap(mapCombo.getText());
        java.util.List children = map.getChildren();
        for (Iterator childIter = children.iterator(); childIter.hasNext();) {
            Element childElement = (Element) childIter.next();
            String name = childElement.getAttributeValue("name"); //$NON-NLS-1$
            if (name != null && name.equals(roomName)) return childElement;
        }
        // we don't know the tag yet
        return null;

    }

    public void initalSelect() {
        if (maps != null && maps.size() > 0) {
            mapCombo.select(0);
            mapCombo.notifyListeners(SWT.Selection, new Event());
            roomList.select(roomList.getTopIndex());
            roomList.notifyListeners(SWT.Selection, new Event());
        } else {
            clean();
        }
    }

    public String getCurrentMapName() {
        return this.mapCombo.getText();
    }

    public Element getCurrentMap() {
        return getMap(getCurrentMapName());
    }

    public void updateData() {
        mapCombo.notifyListeners(SWT.Selection, new Event());
        roomList.notifyListeners(SWT.Selection, new Event());
    }

    public Element getCurrentRoom() {
        return getRoom(roomList.getItem(roomList.getSelectionIndex()));
    }

    public boolean verifyMap(boolean showMsg) {
        return verifyMap(showMsg, getCurrentMapName());
    }

    public boolean verifyMap(boolean showMsg, String mapName) {
        boolean retval = true;
        GameFileReader reader = new GameFileReader();
        reader.parseTree(JdomHelpers.getRoot());
        try {
            GameMap foo = reader.getMap(mapName);
            foo.verifyMap();
            if (showMsg) {
                MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_INFORMATION);
                mb.setMessage(Messages.getString("MAP_PERFECT")); //$NON-NLS-1$
                mb.open();
            }
        } catch (ConnectAllRoomsFailed e) {
            MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR);
            mb.setText(Messages.getString("ERROR_IN_MAP") + mapName); //$NON-NLS-1$
            mb.setMessage(e.getMessage());
            mb.open();
            retval = false;
        } catch (NoSuchRoomException e) {
            MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR);
            mb.setText(Messages.getString("ERROR_IN_MAP") + mapName); //$NON-NLS-1$
            mb.setMessage(e.getMessage());
            mb.open();
            retval = false;
        } catch (NullPointerException e) {
            MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR);
            mb.setText(Messages.getString("ERROR_IN_MAP") + mapName); //$NON-NLS-1$
            mb.setMessage(Messages.getString("MAP_FATAL_ERROR")); //$NON-NLS-1$
            mb.open();
            retval = false;
        }
        return retval;
    }

    public boolean verifyAllMaps(boolean showMsg) {
        boolean retval = true;
        String[] map = getMapNames();
        for (int i = 0; i < map.length; i++) {
            retval &= verifyMap(showMsg, map[i]);
        }
        return retval;
    }

    public void clean() {
        mapCombo.removeAll();
        roomList.removeAll();
        startRoom.removeAll();
        roomDetailComp.clear();
    }

}