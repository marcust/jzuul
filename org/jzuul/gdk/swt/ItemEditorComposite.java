/*
 * 	CVS: $Id: ItemEditorComposite.java,v 1.14 2004/07/25 21:40:56 marcus Exp $
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
import java.util.Iterator;

import org.eclipse.swt.SWT;
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
 * @version $Revision: 1.14 $
 */
public class ItemEditorComposite extends Composite {
	private List itemsList;
	private ItemDetailsComposite itemComp;
	private Element gameobj;


    
    /**
     * @param arg0
     * @param arg1
     */
    public ItemEditorComposite(Composite arg0, int arg1) {
        super(arg0, arg1);
		final GridLayout gridLayout = new GridLayout();

		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 5;
		this.setLayout(gridLayout);
	
		{
			final Group group_1 = new Group(this, SWT.NONE);
			group_1.setText(Messages.getString("ITEMS")); //$NON-NLS-1$

			group_1.setLayout(new GridLayout());
			group_1.setLayoutData(
				new GridData(
					GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));

			{
				itemsList =
					new List(
						group_1,
						SWT.BORDER
							| SWT.H_SCROLL
							| SWT.V_SCROLL
							| SWT.PUSH);
				itemsList.setLayoutData(
					new GridData(GridData.FILL_BOTH));
	
				itemsList
				.addSelectionListener(new SelectionListener() {

				    public void widgetSelected(SelectionEvent e) {
				        int index = itemsList.getSelectionIndex();
				        if (index >= 0) { 
				            itemComp.showItemElement(getItem(itemsList.getItem(index)));
				        } else {
				            itemComp.showItemElement(null);
				        }
					}


					public void widgetDefaultSelected(SelectionEvent e) {
						// Doppelklick
					}

				});
				itemsList.setToolTipText(Messages.getString("ITEMS_TOOLTIP")); //$NON-NLS-1$

				{
					Menu popupmenu = new Menu(itemsList);
					itemsList.setMenu(popupmenu);
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem.setText(Messages.getString("ADD")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                                InputDialog id = new InputDialog(new Shell(arg0.display));
                                id.setMessage(Messages.getString("ENTER_ITEM_NAME")); //$NON-NLS-1$
                                String result = id.openNoWhitespace();
                                if (result != null && (!result.equals(""))) { //$NON-NLS-1$
                                    if (itemsList.indexOf(result) == -1) {
                                    addItem(result);
                                    itemComp.enable();
                                    } else {
                                        Object[] formatArgs = { result};
                                        MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                                        mb.setMessage(MessageFormat.format(Messages.getString("ITEM_SAME_NAME_ERROR"), formatArgs)); //$NON-NLS-1$
                                        mb.open();
                                    }
                                }
                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {}
                        });
					}
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem.setText(Messages.getString("RENAME")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                            	InputDialog id = new InputDialog(new Shell(arg0.display));
                            	if (id != null && itemsList.getSelectionIndex() > -1) {
                            	    String oldname = itemsList.getItem(itemsList.getSelectionIndex());
                            	    id.setMessage(Messages.getString("ENTER_ITEM_RENAME") + oldname); //$NON-NLS-1$
                            	    id.setDefaultValue(oldname);
                            	    renameItem(oldname,id.openNoWhitespace());
                            	}
                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {}
                        });
					}
					{
						final MenuItem popupItem =
							new MenuItem(popupmenu, SWT.NONE);
						popupItem.setText(Messages.getString("DELETE")); //$NON-NLS-1$
						popupItem.addSelectionListener(new SelectionListener() {

                            public void widgetSelected(SelectionEvent arg0) {
                            	try{
                                String name = itemsList.getItem(itemsList.getSelectionIndex());
                                //TODO add are U sure
                                deleteItem(name);
                            	}catch (Exception e) {
									// System.err.println("Silent Catch: \nprevented nullpointer expection (deleting null-object)\n"+e.getLocalizedMessage());
								}

                            }

                            public void widgetDefaultSelected(SelectionEvent arg0) {}
                        });
					}
				}
			}
		}
		{
			itemComp = new ItemDetailsComposite(this,SWT.NULL);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 4;
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
		}
    }
    
	

	public Element getItem(String name) {
		if (gameobj != null) {
			Element items = gameobj.getChild("items"); //$NON-NLS-1$
			if (items != null) {
				java.util.List itemList = items.getChildren("gameitem"); //$NON-NLS-1$
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
	
	public void addItem(String name) {
	    Element newItem = new Element("gameitem"); //$NON-NLS-1$
	    newItem.setAttribute("name", name); //$NON-NLS-1$
	    newItem.addContent(new Element("description")); //$NON-NLS-1$
		Element items = gameobj.getChild("items"); //$NON-NLS-1$
		items.addContent(newItem);
		showItemList();
		itemComp.enable();
		itemsList.select(itemsList.indexOf(name));
		itemsList.notifyListeners(SWT.Selection, new Event());
	}
	
	public void renameItem(String oldname, String newname) {
	    Element item = getItem(oldname);
	    item.setAttribute("name",newname); //$NON-NLS-1$
	    JdomHelpers.deepObjectRename(oldname, newname);
	    showItemList();
	}
	
	public void deleteItem(String name) {
	    Element item = getItem(name);
	    item.detach();
	    JdomHelpers.deepObjectDelete(name);
	    showItemList();
	}

	public void showItems(Element gameobjects) {
	    this.gameobj = gameobjects;
	    showItemList();
	}
	

	protected void showItemList() {
	    String[] items = getItems();
	    if (items != null) {
	        itemsList.setItems(items);
	        itemsList.select(itemsList.getTopIndex());
	        itemsList.notifyListeners(SWT.Selection, new Event());
	    } else {
	        itemComp.clear();
	        itemsList.removeAll();
	    }
	}

	protected String[] getItems() {
		String[] retval = null;
		if (gameobj != null) {
			Element items = gameobj.getChild("items"); //$NON-NLS-1$
			if (items != null) {
				java.util.List itemList = items.getChildren("gameitem"); //$NON-NLS-1$
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
	
	public void updateData() {
	    this.itemsList.notifyListeners(SWT.Selection, new Event());
	}

}
