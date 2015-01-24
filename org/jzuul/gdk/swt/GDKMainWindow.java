/*
 * 	CVS: $Id: GDKMainWindow.java,v 1.49 2004/07/25 21:40:55 marcus Exp $
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

package org.jzuul.gdk.swt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jzuul.engine.CommandList;
import org.jzuul.engine.Engine;
import org.jzuul.engine.gui.SwtGui;
import org.jzuul.engine.gui.utils.Util;

//TODO Deploy

/**
 * THIS IS EARLY BETA CODE... DON'T TRY THIS AT HOME :-D
 * 
 * 
 * @version $Revision: 1.49 $
 *  
 */
public class GDKMainWindow {

    private Element data;

    private Element dataClone;

    private Shell shell;

    /**
     * <code>appName</code> definiert den Namen der Application, welcher in
     * der Titelleiste angezeigt wird
     */
    protected final String appName = Messages.getString("GDK_NAME"); //$NON-NLS-1$

    private MenuItem saveMenuItem;

    private Menu menubar;

    private TabFolder tabFolder;

    private Display display;

    private String filename;

    private MapEditorComposite mapEditor;

    private PlayerEditorComposite playerComposite;

    private CharacterEditorComposite characterComposite;

    private ItemEditorComposite itemComposite;

    private RunValues runValues;

    /**
     * Constructor for Mainwindow
     *  
     */
    public GDKMainWindow() {
        runValues = new RunValues();
    }

    /**
     * erstellt die Gui des GDK
     * 
     * @param display1
     */
    public void createGui(Display display1) {
        display = display1;
        shell = new Shell(SWT.BORDER | SWT.MIN  | SWT.RESIZE | SWT.MAX);
        FillLayout lay = new FillLayout(SWT.VERTICAL);
        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                exit();
            }
        });
        shell
                .setImage(Util.getImagefromResource(display,
                        "etc/artwork/jz.png")); //$NON-NLS-1$
        shell.setLayout(lay);

        shell.setText(appName);
        {
            menubar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menubar);
            {
                final MenuItem fileMenu = new MenuItem(menubar, SWT.CASCADE);
                fileMenu.setText(Messages.getString("FILE")); //$NON-NLS-1$
                {
                    Menu popupmenu = new Menu(fileMenu);
                    fileMenu.setMenu(popupmenu);
                    {
                        final MenuItem newMenuItem = new MenuItem(popupmenu,
                                SWT.CASCADE);
                        newMenuItem.setText(Messages.getString("NEW")); //$NON-NLS-1$
                        newMenuItem.setAccelerator(SWT.CONTROL | 'n');
                        newMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        // das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        newFile();
                                    }

                                });
                    }
                    {
                        final MenuItem openMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        openMenuItem.setText(Messages.getString("OPEN")); //$NON-NLS-1$
                        openMenuItem.setAccelerator(SWT.CONTROL | 'o');
                        openMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        FileDialog foo = new FileDialog(shell,
                                                SWT.OPEN);
                                        foo.setText(Messages.getString("LOAD")); //$NON-NLS-1$
                                        String[] bar = new String[1];
                                        bar[0] = "*.xml"; //$NON-NLS-1$
                                        foo.setFilterExtensions(bar);
                                        String file;
                                        file = foo.open();
                                        if (file != null)
                                            filename = file;
                                        openFile(file);

                                    }

                                });
                    }
                    {
                        saveMenuItem = new MenuItem(popupmenu, SWT.NONE);
                        saveMenuItem.setText(Messages.getString("SAVE")); //$NON-NLS-1$
                        saveMenuItem.setAccelerator(SWT.CONTROL | 's');
                        saveMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        saveFile();
                                    }

                                });

                    }
                    {
                        saveMenuItem = new MenuItem(popupmenu, SWT.NONE);
                        saveMenuItem.setText(Messages.getString("SAVE_AS")); //$NON-NLS-1$
                        //saveMenuItem.setAccelerator(SWT.CONTROL | 's');
                        saveMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        filename = null;
                                        saveFile();
                                    }

                                });

                    }
                    {
                        final MenuItem separater = new MenuItem(popupmenu,
                                SWT.SEPARATOR);
                    }
                    {
                        final MenuItem quitMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        quitMenuItem.setText(Messages.getString("QUIT")); //$NON-NLS-1$
                        quitMenuItem.setAccelerator(SWT.CONTROL | 'q');
                        quitMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        exit();
                                    }

                                });

                    }
                }
            }
            {
                final MenuItem gameMenu = new MenuItem(menubar, SWT.CASCADE);
                gameMenu.setText(Messages.getString("GAME")); //$NON-NLS-1$
                {
                    Menu popupmenu = new Menu(gameMenu);
                    gameMenu.setMenu(popupmenu);
                    {
                        final MenuItem newMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        newMenuItem.setText(Messages.getString("DESCRIPTION")); //$NON-NLS-1$
                        newMenuItem.setAccelerator(SWT.CONTROL | 'd');
                        newMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //fpp
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        InputDialog id = new InputDialog(
                                                new Shell(e.display), SWT.NONE);
                                        id.setStyle(SWT.MULTI | SWT.WRAP
                                                | SWT.V_SCROLL);
                                        id
                                                .setMessage(Messages.getString("ENTER_DESCRIPTION")); //$NON-NLS-1$
                                        id.setDefaultValue(data
                                                .getChildText("description")); //$NON-NLS-1$
                                        String retval = (String) id.open();
                                        if (retval != null) {
                                            Element des = data
                                                    .getChild("description"); //$NON-NLS-1$
                                            if (!(des == null)) {
                                                des.detach();
                                            }
                                            data.addContent(new Element(
                                                    "description") //$NON-NLS-1$
                                                    .setText(retval));
                                        }

                                    }

                                });

                    }
                    {
                        final MenuItem newMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        newMenuItem.setText(Messages.getString("RUN")); //$NON-NLS-1$
                        newMenuItem.setAccelerator(SWT.F9);
                        newMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        updateAllData();
                                        if (mapEditor.verifyAllMaps(false)) {
                                            if (changed() || (filename == null)) {
                                                if (askChanged() == SWT.YES)
                                                    saveFile();
                                            }
                                            try {
                                                Engine engine = new Engine(
                                                        filename, CommandList
                                                                .defaultList(),
                                                        new SwtGui(e.display),
                                                        runValues.threadedNPCs,
                                                        runValues.numOfPlayers);
                                                engine.run(false);
                                            } catch (NullPointerException npe) {
                                                System.err
                                                        .println("jzuul engine died with nullpointerexception"); //$NON-NLS-1$
                                                npe.printStackTrace();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }

                                    }

                                });

                    }
                    {
                        final MenuItem newMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        newMenuItem.setText(Messages.getString("RUN_DOTS")); //$NON-NLS-1$
                        //newMenuItem.setEnabled(false); //FIXME crashes
                        // sometimes
                        newMenuItem.setAccelerator(SWT.F11);
                        newMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        updateAllData();
                                        if (mapEditor.verifyAllMaps(false)) {
                                            if (changed() || (filename == null)) {
                                                if (askChanged() == SWT.YES)
                                                    saveFile();
                                            }
                                            RunDialog id = new RunDialog(shell,
                                                    SWT.NONE);
                                            id.setRunValues(runValues);
                                            RunValues tmpValues = id.open();
                                            id = null;
                                            System.gc();
                                            if (tmpValues != null) {
                                                runValues = tmpValues;
                                                System.err
                                                        .println("Map filename: " //$NON-NLS-1$
                                                                + filename);
                                                try {
                                                    Engine engine = new Engine(
                                                            filename,
                                                            CommandList
                                                                    .defaultList(),
                                                            new SwtGui(
                                                                    e.display),
                                                            runValues.threadedNPCs,
                                                            runValues.numOfPlayers);
                                                    engine.run(runValues.askPlayerName);
                                                    System.gc();
                                                } catch (NullPointerException npe) {
                                                    System.err
                                                            .println("Jzuul engine died with nullpointexception"); //$NON-NLS-1$
                                                    npe.printStackTrace();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }

                                        }
                                    }

                                });

                    }

                
                {
                    final MenuItem newMenuItem = new MenuItem(popupmenu,
                            SWT.NONE);
                    newMenuItem.setText(Messages.getString("DEPLOY")); //$NON-NLS-1$
                    newMenuItem.setAccelerator(SWT.CONTROL | 'e');
                    newMenuItem
                            .addSelectionListener(new SelectionListener() {

                                public void widgetDefaultSelected(
                                        SelectionEvent e) {
                                    //fpp
                                }

                                public void widgetSelected(SelectionEvent e) {
                                    deploy();
                                    
                                }

                            });
                    newMenuItem.setEnabled(false);
                }
            }
            }

            {
                final MenuItem debugMenu = new MenuItem(menubar, SWT.CASCADE);
                debugMenu.setText(Messages.getString("DEBUG")); //$NON-NLS-1$
                debugMenu.setEnabled(false);
                
                {
                    Menu popupmenu = new Menu(debugMenu);
                    debugMenu.setMenu(popupmenu);
                    {
                        final MenuItem newMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        newMenuItem.setText(Messages.getString("CHECK_CHANGED")); //$NON-NLS-1$
                        newMenuItem.setAccelerator(SWT.CONTROL | 'c');
                        newMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //fpp
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        MessageBox mb = new MessageBox(
                                                new Shell(e.display),
                                                SWT.APPLICATION_MODAL);
                                        mb.setMessage("Check changed: " //$NON-NLS-1$
                                                + changed());
                                        mb.open();
                                    }

                                });

                    }
                    debugMenu.dispose();
                }
            }

            {
                final MenuItem helpMenu = new MenuItem(menubar, SWT.CASCADE);
                helpMenu.setText(Messages.getString("HELP")); //$NON-NLS-1$
                {
                    Menu popupmenu = new Menu(helpMenu);
                    helpMenu.setMenu(popupmenu);
                    {
                        final MenuItem cutMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        cutMenuItem.setText(Messages.getString("ABOUT")); //$NON-NLS-1$
                        cutMenuItem.setAccelerator(SWT.CONTROL | 'a');
                        cutMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        MessageBox mb = new MessageBox(shell,
                                                SWT.ICON_INFORMATION | SWT.OK);
                                        mb.setText(Messages.getString("ABOUT_TEXT")); //$NON-NLS-1$
                                        mb
                                                .setMessage(Messages.getString("ABOUT_START")  //$NON-NLS-1$
                                                        + " $Revision: 1.49 $\n\n" //$NON-NLS-1$
                                                        + "2004 by\n" //$NON-NLS-1$
                                                        + "\tMarcus Thiesen (marcus@jzuul.org)\n" //$NON-NLS-1$
                                                        + "\tDaniel Lehmann (leh@jzuul.org)\n\n" //$NON-NLS-1$
                                                        + Messages.getString("TRANSLATION_CREDIT")  //$NON-NLS-1$
                                                        + Messages.getString("GPL_1")  //$NON-NLS-1$
                                                        + Messages.getString("GPL_2")  //$NON-NLS-1$
                                                        + "www.jzuul.org"); //$NON-NLS-1$
                                        mb.open();
                                    }

                                });

                    }
                    {
                        final MenuItem helpMenuItem = new MenuItem(popupmenu,
                                SWT.NONE);
                        helpMenuItem.setText(Messages.getString("HELP_MENU")); //$NON-NLS-1$
                        helpMenuItem.setAccelerator(SWT.CONTROL | 'h');
                        helpMenuItem.setAccelerator(SWT.F1);
                        helpMenuItem
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                        //das soll leer sein
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        try {
                                        HelpViewerDialog d = new HelpViewerDialog(new Shell(e.display));
                                        d.open();
                                        
                                        } catch (org.eclipse.swt.SWTError ex) {
                                            MessageBox mv = new MessageBox(new Shell(e.display), SWT.ICON_ERROR);
                                            String message = Messages.getString("BROWSER_ERROR");  //$NON-NLS-1$
                                            if (SWT.getPlatform().equals("gtk")) { //$NON-NLS-1$
                                                message += Messages.getString("MOZILLA_WARNING_1"); //$NON-NLS-1$
                                                message += Messages.getString("MOZILLA_WARNING_2"); //$NON-NLS-1$
                                            } 
                                            message += Messages.getString("ERROR_WAS"); //$NON-NLS-1$
                                            mv.setMessage(message + ex.getMessage());
                                            mv.open();
                                        }
                                        
                                    }

                                });

                    }
                }
            }

        }
        {
            tabFolder = new TabFolder(shell, SWT.BORDER);

            tabFolder.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    updateAllData();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            tabFolder.setLayout(new GridLayout());
            {
                final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
                tabItem.setToolTipText(Messages.getString("MAP_TOOLTIP")); //$NON-NLS-1$
                tabItem.setText(Messages.getString("MAP")); //$NON-NLS-1$
                {
                    mapEditor = new MapEditorComposite(tabFolder, SWT.NONE);
                    tabItem.setControl(mapEditor);
                }

            }
            {
                final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
                tabItem.setToolTipText(Messages.getString("CHARACTER_TAB_TOOLTIP")); //$NON-NLS-1$
                tabItem.setText(Messages.getString("CHARACTERS")); //$NON-NLS-1$
                {
                    characterComposite = new CharacterEditorComposite(
                            tabFolder, SWT.NONE);
                    tabItem.setControl(characterComposite);
                }
            }
            {
                final TabItem itemsTab = new TabItem(tabFolder, SWT.NONE);
                itemsTab.setToolTipText(Messages.getString("ITEM_TOOLTIP")); //$NON-NLS-1$
                itemsTab.setText(Messages.getString("ITEMS")); //$NON-NLS-1$
                {
                    itemComposite = new ItemEditorComposite(tabFolder, SWT.NONE);
                    itemsTab.setControl(itemComposite);

                }
            }
            {
                final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
                tabItem.setToolTipText(Messages.getString("PLAYER_TOOLTIP")); //$NON-NLS-1$
                tabItem.setText(Messages.getString("PLAYER")); //$NON-NLS-1$
                {
                    playerComposite = new PlayerEditorComposite(tabFolder,
                            SWT.NONE);
                    tabItem.setControl(playerComposite);
                }

            }

        }

        shell.pack();
        shell.setSize(800, 600);
        Util.centerWindow(shell);

    }

    /**
     * parsed das zu Ã¶ffnende File ins DOM
     * 
     * @param filename
     *            des zu Ã¶ffnenden Files
     */
    public void openFile(String filename) {
        if (filename == null)
            return;
        if (changed()) {
            if (askChanged() == SWT.YES)
                saveFileDialog();
        }
        this.filename = filename;

        SAXBuilder builder = new SAXBuilder();

        try {
            FileInputStream is = new FileInputStream(new File(filename));
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8")); //$NON-NLS-1$
            
            Document doc = builder.build(reader);
            Element el = doc.getRootElement();
            System.err.println("Element Name " + el.getName()); //$NON-NLS-1$
            if (el.getName().equals("gamefile")) { //$NON-NLS-1$
                this.setData(el);
            } else {
                MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
                mb.setMessage(Messages.getString("FILE_ERROR")); //$NON-NLS-1$
                mb.open();
                newFile();
            }
            
            //this.data.detach();
        } catch (JDOMException e) {
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
            mb.setMessage(Messages.getString("PARSE_ERROR_MSG") + filename + "\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            mb.open();
        } catch (IOException e) {
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
            mb.setMessage(Messages.getString("IO_ERROR_MSG") + filename + "\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            mb.open();
        }
        shell.setText(appName + " - " + filename); //$NON-NLS-1$
        showMap();
        showItems();
        showCharacter();
        showPlayer();
    }

    /**
     * schreibt den DOM-Tree in ein File
     * 
     * @param filename
     *            des zu speichernden Files
     */
    public void saveFile() {
        if (this.filename == null) {
            this.filename = saveFileDialog();
            if (filename == null)
                return;
        }

        DocType type = new DocType("gamefile", //$NON-NLS-1$
                "file:org/jzuul/engine/dtd/gamefile.dtd"); //$NON-NLS-1$
        data.detach();

        Document doc = new Document(data, type);

        try {
            FileOutputStream out = new FileOutputStream(new File(filename),false);
            OutputStreamWriter ow = new OutputStreamWriter(out, Charset.forName("UTF-8")); //$NON-NLS-1$
            
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8"); //$NON-NLS-1$
            XMLOutputter fmt = new XMLOutputter(format);
            
            fmt.output(doc, ow);
            ow.flush();
            ow.close();
        } catch (IOException e) {
            e.printStackTrace();
            MessageBox mb = new MessageBox(this.shell, SWT.ICON_ERROR);
            mb.setMessage(e.getMessage());
            mb.open();
        }
        this.setData(doc.getRootElement());
    }

    public void open(Display display) {
        shell.open();
        if (this.data == null) {
            newFile();
        }
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    public String saveFileDialog() {
        FileDialog foo = new FileDialog(shell, SWT.SAVE);
        foo.setText(Messages.getString("SAVE_DOT")); //$NON-NLS-1$
        String[] bar = new String[1];
        bar[0] = "*.xml"; //$NON-NLS-1$
        foo.setFilterExtensions(bar);
        String filename = foo.open();
        if (filename != null) {
            shell.setText(appName + " - " + filename); //$NON-NLS-1$
            this.filename = "/" + filename; //$NON-NLS-1$

        }
        return filename;
    }

    protected int askChanged() {
        return askChanged(SWT.YES | SWT.NO | SWT.CANCEL);
    }
    
    protected int askChanged(int flags) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | flags);
        mb
                .setMessage(Messages.getString("SAVE_QUESTION")); //$NON-NLS-1$
        mb.setText(Messages.getString("GDK_QUESTION")); //$NON-NLS-1$
        return mb.open();
    }

    protected void exit() {
        if (!changed()) {
            shell.dispose();
        } else {
            switch (askChanged(SWT.YES | SWT.NO)) {
            case SWT.YES:
                saveFileDialog();
            // intended fallthrough
            case SWT.NO:
                shell.dispose();
                break;
            case SWT.CANCEL: // do nothing
                break;
            }
        }
    }

    protected void newFile() {
        if (changed()) {
            if (askChanged() == SWT.YES)
                saveFileDialog();
        }
        shell.setText(appName + Messages.getString("UNTITLED")); //$NON-NLS-1$
        filename = null;

        Element freshFile = new Element("gamefile"); //$NON-NLS-1$
        freshFile.addContent(new Element("description")); //$NON-NLS-1$
        freshFile
                .addContent(new Element("map").setAttribute("name", "default")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        freshFile.addContent(new Element("player")); //$NON-NLS-1$
        freshFile.addContent(new Element("dialogs")); //$NON-NLS-1$
        freshFile.addContent(new Element("gameobjects")); //$NON-NLS-1$
        freshFile.getChild("gameobjects").addContent(new Element("characters")); //$NON-NLS-1$ //$NON-NLS-2$
        freshFile.getChild("gameobjects").addContent(new Element("items")); //$NON-NLS-1$ //$NON-NLS-2$

        setData(freshFile);

        showItems();
        showCharacter();
        showMap();
        showPlayer();

    }

    public void close() {
        display.syncExec(new Runnable() {

            public void run() {

                Engine.debug("Shell & display are being closed", 5); //$NON-NLS-1$
                shell.dispose();
                display.dispose();
            }
        });

    }

    public void showMap() {
        mapEditor.setMaps(data.getChildren("map")); //$NON-NLS-1$
        mapEditor.initalSelect();
    }

    public void showPlayer() {
        playerComposite.showPlayer(data.getChild("player")); //$NON-NLS-1$
    }

    public void showCharacter() {
        characterComposite.showCharacters(data.getChild("gameobjects")); //$NON-NLS-1$
    }

    public void showItems() {
        itemComposite.showItems(data.getChild("gameobjects")); //$NON-NLS-1$
    }

    public boolean changed() {
        return !JdomHelpers.equals(this.data, dataClone);
    }

    public void setData(Element newData) {
        System.err.println("Set data called"); //$NON-NLS-1$
        dataClone = (Element) newData.clone();
        this.data = newData;
        JdomHelpers.setRoot(newData);
    }

    public void updateAllData() {
        if (mapEditor != null)
            mapEditor.updateData();
        if (playerComposite != null)
            playerComposite.updateData();
        if (characterComposite != null)
            characterComposite.updateData();
        if (itemComposite != null)
            itemComposite.updateData();
    }
    
    public void deploy() {
        updateAllData();
        if (mapEditor.verifyAllMaps(false)) {
            if (changed() || (filename == null)) {
                if (askChanged() == SWT.YES)
                    saveFile();
            }
        InputDialog in = new InputDialog(shell);
        in.setMessage(Messages.getString("ENTER_GAME_NAME")); //$NON-NLS-1$
        String name = in.openNoWhitespace();
        if (name != null) {
            
            try {
                FileOutputStream fo = new FileOutputStream(new File(name + ".zip"),false); //$NON-NLS-1$
                ZipOutputStream zip = new ZipOutputStream(fo);
                
                copyFileToStream(filename, zip);
            
            
                zip.close();
            } catch (FileNotFoundException e) {
                MessageBox mb = new MessageBox(shell);
                mb.setMessage("An error occured creating " + name + ".zip\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                mb.open();
            }  catch (IOException e) {
                MessageBox mb = new MessageBox(shell);
                mb.setMessage("An I/O error occured creating " + name + ".zip\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                mb.open();
            }
        
            
        	}
        
        }
        
        
        
    }
    
    public void copyFileToStream(String filename, ZipOutputStream zip) {
        	byte[] buf = new byte[1024];
        
            FileInputStream in;
            try {
                in = new FileInputStream(filename);
         
            // Add ZIP entry to output stream.
            zip.putNextEntry(new ZipEntry(filename));
    
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
    
            // Complete the entry
            zip.closeEntry();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
    }
    
    public static void saveSelectFirst(Combo box) {
        if (box != null) {
            if (box.getItemCount() > 0) {
                box.select(0);
                box.notifyListeners(SWT.Selection, new Event());
            }
            
        }
    }
    
}

class RunValues {
    boolean threadedNPCs;
    boolean askPlayerName;
    int numOfPlayers;

    public RunValues() {
        numOfPlayers = 1;
        threadedNPCs = false;
        askPlayerName = false;
    }

    public RunValues(boolean threadedNPC, int numberOfPlayers, boolean askPlayerName) {
        numOfPlayers = numberOfPlayers;
        threadedNPCs = threadedNPC;
        this.askPlayerName = askPlayerName;
    }
}