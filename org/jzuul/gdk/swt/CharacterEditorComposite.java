/*
 * 	CVS: $Id: CharacterEditorComposite.java,v 1.13 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;


/**
 * 
 * Created on Jul 15, 2004
 * 
 * 
 * @version $Revision: 1.13 $
 */
public class CharacterEditorComposite extends Composite {
	private List characterList;
	private CharacterDetailsComposite characterComp;
	private Element gameobj;
    
    /**
     * @param arg0
     * @param arg1
     */
    public CharacterEditorComposite(Composite arg0, int arg1) {
        super(arg0, arg1);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 5;
		this.setLayout(gridLayout);


		{
			final Group group_1 =
				new Group(this, SWT.NONE);
			group_1.setText(Messages.getString("CHARACTERS")); //$NON-NLS-1$

			group_1.setLayout(new GridLayout());
			final GridData gridData =
				new GridData(
					GridData.HORIZONTAL_ALIGN_FILL
						| GridData.FILL_VERTICAL);
			gridData.horizontalSpan = 1;
			group_1.setLayoutData(gridData);

			{
				characterList =
					new List(
						group_1,
						SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
				characterList.setLayoutData(
					new GridData(GridData.FILL_BOTH));

				
		characterList.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
				    characterComp.updateElement();
				    int index = characterList.getSelectionIndex();
				    if (index >= 0) {
				        characterComp.showCharacterElement(getCharacter(characterList.getItem(index)));
				    } else {
				        characterComp.showCharacterElement(null);
				    }

				}

					public void widgetDefaultSelected(SelectionEvent e) {
						// Doppelklick
					}

				});
				characterList.setToolTipText(Messages.getString("CHARACTER_TOOLTIP")); //$NON-NLS-1$
				
				{
					Menu popupmenu = new Menu(characterList);
					characterList.setMenu(popupmenu);
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem
							.addSelectionListener(
								new SelectionAdapter() {

							public void widgetSelected(SelectionEvent e) {
								//das soll leer sein
							}
						});
						popupItem.setText(Messages.getString("ADD")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                                InputDialog input = new InputDialog(new Shell(arg0.display), SWT.APPLICATION_MODAL);
                                input.setMessage(Messages.getString("CHARACTER_NAME_ENTER")); //$NON-NLS-1$
                                String retval = input.openNoWhitespace();
                                if (retval != null && (!retval.equals(""))) { //$NON-NLS-1$
                                    if (characterList.indexOf(retval) == -1) {
                                        addCharacter(retval);
                                        characterComp.enable();
                                    } else {
                                        MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                                        Object[] formatArgs = {retval };
                                        mb.setMessage(MessageFormat.format(Messages.getString("CHARACTER_SAME_NAME_ERROR"),formatArgs)); //$NON-NLS-1$
                                        mb.open();

                                    }
                                }
                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {
                            }
						    
						});
					}
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem.setText(Messages.getString("RENAME")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                            	try{
                                String oldname = characterList.getItem(characterList.getSelectionIndex());
                                InputDialog input = new InputDialog(new Shell(arg0.display),SWT.APPLICATION_MODAL);
                                input.setMessage(Messages.getString("CHARACTER_RENAME_ENTER") + oldname + ": "); //$NON-NLS-1$ //$NON-NLS-2$
                                input.setDefaultValue(oldname);
                                renameCharacter(oldname,input.openNoWhitespace());
                            	}
                            	catch (Exception e) {
                            		System.err.println("Silent Catch: \nprevented nullpointer expection (renaming null-object)\n"+e.getLocalizedMessage()); //$NON-NLS-1$
								}
                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {
                            }
						    
						});
					}
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem.setText(Messages.getString("DELETE")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                                //TODO add are you sure dialog
                            	try{
                                deleteCharacter(characterList.getItem(characterList.getSelectionIndex()));
                            	}
                            	catch (Exception e) {
                            		System.err.println("Silent Catch: \nprevented nullpointer expection (deleting null-object)\n"+e.getLocalizedMessage()); //$NON-NLS-1$
								}
                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {
                            }
						    
						});
					}
				}
			}
		}
		{
			characterComp = new CharacterDetailsComposite(this,SWT.NULL);						

			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 4;
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
		}

	}
    
	public Element getCharacter(String name) {
		if (gameobj != null) {
			Element items = gameobj.getChild("characters"); //$NON-NLS-1$
			if (items != null) {
				java.util.List itemList = items.getChildren("person"); //$NON-NLS-1$
				for (Iterator goIter = itemList.iterator(); goIter.hasNext();) {
					Element element = (Element) goIter.next();
					
					if (element.getAttributeValue("name").equals(name)) { //$NON-NLS-1$
					    return element;
					}
				}
			}
		}
		return null;
 	}
	
	public void addCharacter(String name) {
	    if (name.equals("")) return; //$NON-NLS-1$
		if (gameobj != null) {
			Element items = gameobj.getChild("characters"); //$NON-NLS-1$
			if (items != null) {
			    Element newChar = new Element("person"); //$NON-NLS-1$
			    newChar.setAttribute("name",name); //$NON-NLS-1$
			    newChar.addContent(new Element("description").setText("")); //$NON-NLS-1$ //$NON-NLS-2$
			    items.addContent(newChar);
			} else {
			    gameobj.addContent(new Element("characters")); //$NON-NLS-1$
			    addCharacter(name);
			} 
		} 
		showCharacterList();
		characterList.select(characterList.indexOf(name));
		characterList.notifyListeners(SWT.Selection, new Event());
	}
	
	public void renameCharacter(String oldname, String newname) {
	    if (newname.equals("")) return; //$NON-NLS-1$
	    Element c = getCharacter(oldname);
	    c.setAttribute("name", newname); //$NON-NLS-1$
	    JdomHelpers.deepObjectRename(oldname, newname);
	    showCharacterList();
	}
	
	public void deleteCharacter(String name) {
	    java.util.List newList = new ArrayList();
		if (gameobj != null) {
			Element items = gameobj.getChild("characters"); //$NON-NLS-1$
			if (items != null) {
				java.util.List itemList = items.getChildren("person"); //$NON-NLS-1$
				for (Iterator goIter = itemList.iterator(); goIter.hasNext();) {
					Element element = (Element) goIter.next();
					
					if (!element.getAttributeValue("name").equals(name)) { //$NON-NLS-1$
					    newList.add(element);
					}
				}
			} 
			items.removeChildren("person"); //$NON-NLS-1$
			items.addContent(newList);
		}
		JdomHelpers.deepObjectDelete(name);
		showCharacterList();
 	}

	protected void showCharacterList() {
	    String[] chars = getCharacters();
	    if (chars != null) {
	        characterList.setItems(chars);
	        characterList.select(characterList.getTopIndex());
	        characterList.notifyListeners(SWT.Selection, new Event());
	    } else {
	        characterList.removeAll();
	        characterComp.clean();
	    }
	}

	protected String[] getCharacters() {
		String[] retval = null;
		if (gameobj != null) {
			Element items = gameobj.getChild("characters"); //$NON-NLS-1$
			if (items != null) {
				java.util.List itemList = items.getChildren("person"); //$NON-NLS-1$
				retval = new String[itemList.size()];
				int count = 0;
				for (Iterator goIter = itemList.iterator(); goIter.hasNext();) {
					Element element = (Element) goIter.next();
					retval[count++] = element.getAttributeValue("name"); //$NON-NLS-1$
				}
			}
		}
		return retval;
	}
	
	public void showCharacters(Element gameobjects) {
	    this.gameobj = gameobjects;
	    this.showCharacterList();
	}
	
	public void updateData() {
	    characterList.notifyListeners(SWT.Selection, new Event());
	}
	
}
