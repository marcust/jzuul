/*
 * 	CVS: $Id: SwtGui.java,v 1.30 2004/07/23 15:18:07 marcus Exp $
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

package org.jzuul.engine.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jzuul.engine.Engine;
import org.jzuul.engine.ObjectCache;
import org.jzuul.engine.gui.splash.SwtSplash;
import org.jzuul.engine.gui.utils.Util;

/**
 * SwtGui, provides a graphical user interface using the Standard widget toolkit
 * (Eclipse).
 * swt provides a native look and feel
 * CAUTION: some jni magic is need, check lib/ for the swt stuff
 *  
 */


public class SwtGui implements GuiInterface {

    /**
     * Der Traverselistener regelt die Handhabung der Focus-Events. Dieser wird
     * hauptsächlich genutzt, damit wir die spezial Tasten TAB KEY_UP KEY_DOWN
     * handeln können
     * 
     * 
     *  
     */
    protected class MyTraverseListener implements TraverseListener {

        /**
         * Erstellt ein neues MyTraverseListener Objekt
         *  
         */
        public MyTraverseListener() {
            // empty creator
        }

        /**
         * behandelt die TraverseEvents, so z.B. das drücken der TAB Taste
         * 
         * @param e
         *            das TraverseEvent welches behandelt wird
         */
        public void keyTraversed(TraverseEvent e) {
            e.doit = false;
            Engine.debug("Key Event " + e.toString(), 5);
            switch (e.keyCode) {
            case SWT.CR:
                ActionEvent ev = new ActionEvent(this, 0, eingabe.getText());
                current.actionPerformed(ev);
                break;
            case SWT.TAB:
            case SWT.ARROW_UP:
            case SWT.ARROW_DOWN:
                java.awt.event.KeyEvent kev = new java.awt.event.KeyEvent(new java.awt.Label(), 0, e.time, e.stateMask,
                        e.keyCode, e.character);
                keylistener.keyPressed(kev);
                break;
            }
        }
    }

    private Display display;

    private StyledText ausgabe;

    private Shell shell;

    private Text eingabe;

    private Label imageLabel;

    private Group imageGroup;

    private Composite imageComposite;

    private Composite textComposite;

    /**
     * current definiert den derzeitigen Actionlistener
     */
    protected ActionListener current;

    /**
     * definiert den derzeitigen default Actionlistener
     */
    protected ActionListener defaultListener;

    /**
     * definiert den derzeitigen Keylistener
     */
    protected KeyListener keylistener;

    /**
     * definiert das SplashScreen Objekt
     */
    protected SwtSplash spl;

    /**
     * definiert den imageCache
     */
    protected ObjectCache imageCache;

    /**
     * definiert die anzuzeigenede Bildhöhe
     */
    protected final double imageHeight = 300;

    /**
     * definiert die anzuzeigende Bildbreite
     */
    protected final double imageWidth = 400;

    /**
     * definiert die Standardhöhe der Applikation
     */
    protected final int windowHeight = 600;

    /**
     * definiert die Standardbreite der Applikation
     */
    protected final int windowWidth = 600;

    /**
     * Comment for <code>black</code>
     */
    protected Color black;
    
    protected boolean isChildWindow;

    /**
     * Comment for <code>white</code> definiert die Color Objekte für die
     * Farben black and white
     */
    protected Color white;

    public SwtGui() {
        this(null);
    }

    /**
     * Konstruktor für das SwtGui
     *  
     */
    public SwtGui(Display display) {
        if (display == null) {
            this.display = new Display();
            isChildWindow = false;
        } else {
            this.display = display;
            isChildWindow = true;
        }
        imageCache = new ObjectCache(10);
        black = new Color(display, 0, 0, 0);
        white = new Color(display, 255, 255, 255);
        this.spl = new SwtSplash(display);

        this.open();
    }

    /**
     * Öffnet das Fenster
     */
    protected void open() {
        this.shell = new Shell(display, SWT.MAX | SWT.MIN | SWT.RESIZE | SWT.CLOSE);
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        shell.setText("JZuul");

        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                Engine.exit(0);
            }
        });
        shell.setImage(Util.getImagefromResource(display,"etc/artwork/jz.png"));

        //shell.setImage(Util.getImagefromResource(display,"/org/jzuul/engine/jz.png"));
        {
            imageComposite = new Composite(shell, SWT.NONE);
            imageComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
            imageComposite.setLayout(new GridLayout());
            {
                imageGroup = new Group(imageComposite, SWT.NONE);
                imageGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER
                        | GridData.GRAB_VERTICAL));
                imageGroup.setLayout(new FillLayout());
                {
                    imageLabel = new Label(imageGroup, SWT.NONE);
                    imageLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER
                            | GridData.GRAB_VERTICAL));
                    //TODO leh, ich würde das gerne von extern bekommen, also
                    // brauchen wir nen artwork-container oder so
                    Image img = Util.getImagefromResource(display, "etc/artwork/logo-big.png");
                    if (img != null) imageLabel.setImage(img);

                }
            }

        }
        {
            textComposite = new Composite(shell, SWT.NONE);
            textComposite.setLayout(new GridLayout());
            {
                final Group outputGroup = new Group(textComposite, SWT.V_SCROLL | SWT.NO_FOCUS);

                final GridData gridData = new GridData(GridData.FILL_BOTH);
                gridData.verticalSpan = 2;

                outputGroup.setLayoutData(gridData);
                outputGroup.setText("Ausgabe:");
                outputGroup.setLayout(new GridLayout());
                {
                    ausgabe = new StyledText(outputGroup, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL
                            | SWT.WRAP | SWT.VERTICAL);
                    ausgabe.setEditable(false);
                    ausgabe.setWordWrap(false);

                    Font foo = new Font(display, "Arial", 10, SWT.NONE);
                    ausgabe.setFont(foo);
                    //ausgabe.setEnabled(false);

                    ausgabe.setLayoutData(new GridData(GridData.FILL_BOTH));
                }
            }
            {
                final Group inputGroup = new Group(textComposite, SWT.NONE);
                inputGroup.setFocus();
                inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                inputGroup.setText("Eingabe:");
                inputGroup.setLayout(new GridLayout());
                {
                    eingabe = new Text(inputGroup, SWT.BORDER);
                    eingabe.addTraverseListener(new MyTraverseListener());
                    eingabe.setFocus();
                    eingabe.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                }
            }
        }
        shell.pack();

        shell.setSize(windowHeight, windowWidth);
        shell.open();

    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#print(java.lang.String)
     */
    public void print(String string) {
        // fucking SWT does need thread magic:
        final String fstring = new String(string);
        display.syncExec(new Runnable() {

            public void run() {
                ausgabe.append(fstring);
                //Engine.debug("ausgabe widget cursor position "
                // +ausgabe.getSelection().x + ausgabe.getSelection().y +"Lines:
                // "+ ausgabe.getLineCount(), 5);
                ausgabe.setSelection(ausgabe.getText().length());
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#disableInput()
     */
    public void disableInput() {
        display.syncExec(new Runnable() {

            public void run() {
                eingabe.setEnabled(false);
                eingabe.setVisible(false);
            }
        });

    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#enableInput()
     */
    public void enableInput() {
        display.syncExec(new Runnable() {

            public void run() {
                eingabe.setEnabled(true);
                eingabe.setVisible(true);
            }
        });
    }

    //	public ActionListener getActionListener() {
    //		return this.currentListener.toAwt();
    //	}

    /**
     * @see org.jzuul.engine.gui.GuiInterface#isApplet()
     */
    public boolean isApplet() {
        return false;
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#println()
     */
    public void println() {
        println("");
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#println(java.lang.String, int[])
     */
    public void println(String out, int[] color) {
        final String fout = out;
        final int[] fcolor = color;
        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), new Color(display, fcolor[0],
                        fcolor[1], fcolor[2]), white);
                println(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#println(java.lang.String)
     */
    public void println(String out) {
        print(out + "\n");
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#printlnB(java.lang.String, int[])
     */
    public void printlnB(String out, int[] color) {
        final String fout = out;
        final int[] fcolor = color;
        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), new Color(display, fcolor[0],
                        fcolor[1], fcolor[2]), white, SWT.BOLD | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL
                        | SWT.WRAP | SWT.VERTICAL);
                println(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#printlnB(java.lang.String)
     */
    public void printlnB(String out) {
        final String fout = out;
        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), black, white, SWT.BOLD
                        | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP | SWT.VERTICAL);
                println(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#printlnI(java.lang.String, int[])
     */
    public void printlnI(String out, int[] color) {
        final String fout = out;
        final int[] fcolor = color;
        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), new Color(display, fcolor[0],
                        fcolor[1], fcolor[2]), white, SWT.ITALIC | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY
                        | SWT.V_SCROLL | SWT.WRAP | SWT.VERTICAL);
                println(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#printlnI(java.lang.String)
     */
    public void printlnI(String out) {
        final String fout = out;
        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), black, white, SWT.ITALIC
                        | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP | SWT.VERTICAL);
                println(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#printU(java.lang.String)
     */
    public void printU(String out) {
        final String fout = out;

        display.syncExec(new Runnable() {

            public void run() {
                StyleRange range = new StyleRange(ausgabe.getCharCount(), fout.length(), black, white, SWT.LINE_SOLID
                        | SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP | SWT.VERTICAL);
                print(fout);
                ausgabe.setStyleRange(range);
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#redraw()
     */
    public void redraw() {
        shell.update();
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#resetInput()
     */
    public void resetInput() {
        display.syncExec(new Runnable() {

            public void run() {
                eingabe.setText("");
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#start()
     */
    public void start() {
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }

    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#setInput(java.lang.String)
     */
    public void setInput(String newline) {
        eingabe.setText(newline);
        eingabe.setSelection(eingabe.getCharCount());
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#getInput()
     */
    public String getInput() {
        return eingabe.getText();
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#splash_start(int)
     */
    public void splash_start(int count) {
        spl.show(count, "Initializing Gui");
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#splash_next(java.lang.String)
     */
    public void splash_next(String message) {
        spl.nextTask(message);
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#close()
     */
    public void close() {
        display.syncExec(new Runnable() {

            public void run() {
                if (!isChildWindow) {
                    Engine.debug("Shell & display are being closed", 5);
                    shell.dispose();
                    display.dispose();
                    System.exit(0);
                } else {
                    shell.dispose();
                }
            }
        });
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#showImage(java.io.InputStream)
     */
    public void showImage(InputStream imageStream) {

        if (imageStream == null) {
            Engine.debug("Imagestream was null, no new image showed!", 1);
        } else {

            // The reason for caching is a) we don't want to read them all the
            // time
            // and b) we can resize them one time and guarantee a size.
            String id = imageStream.toString();

            if (!imageCache.containsKey(id)) {
                Image image = new Image(display, imageStream);
                Image newImage = new Image(display, 400, 300);
                GC gc = new GC(newImage);
                Rectangle bound = image.getBounds();
                // This does not really take aspect ratio into account:
                gc.drawImage(image, 0, 0, bound.width, bound.height, 0, 0, (int) imageWidth, (int) imageHeight);
                gc.dispose();
                imageCache.put(id, newImage);
            }

            final Image image = (Image) imageCache.get(id);

            display.syncExec(new Runnable() {

                public void run() {

                    Engine.debug("new image: " + image.toString() + " with bounds: X " + image.getBounds().height
                            + " Y " + image.getBounds().width, 5);

                    imageLabel.setImage(image);

                    Engine.debug("Current ImageComposite size: X" + imageComposite.getBounds().height + " Y "
                            + imageComposite.getBounds().width, 5);
                    Engine.debug("Current ImageGroup size: X" + imageGroup.getBounds().height + " Y "
                            + imageGroup.getBounds().width, 5);
                    if (imageComposite.getBounds().height < image.getBounds().height
                            || imageComposite.getBounds().width < image.getBounds().width) {

                        shell.pack();
                        //pack();
                    }
                    Engine.debug("Current ImageComposite size: X" + imageComposite.getBounds().height + " Y "
                            + imageComposite.getBounds().width, 5);
                    Engine.debug("Current ImageGroup size: X" + imageGroup.getBounds().height + " Y "
                            + imageGroup.getBounds().width, 5);

                }
            });
        }

    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#setActionListener(java.awt.event.ActionListener)
     */
    public void setActionListener(ActionListener al) {
        current = al;
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#setDefaultActionListener()
     */
    public void setDefaultActionListener() {
        this.current = this.defaultListener;
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#setDefaultActionListener(java.awt.event.ActionListener)
     */
    public void setDefaultActionListener(ActionListener defaultListener) {
        this.defaultListener = defaultListener;
        this.current = defaultListener;
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#setKeyListener(java.awt.event.KeyListener)
     */
    public void setKeyListener(KeyListener list) {
        this.keylistener = list;
    }

    /**
     * @see org.jzuul.engine.gui.GuiInterface#getActionListener()
     */
    public ActionListener getActionListener() {
        return this.current;
    }

    /*	*//**
             * führt ein Pack auf folgende Elemente aus: ausgabe, eingabe,
             * imageLabel, imageGroup, imageComposite, textComposite,shell und
             * updated dann die shell
             */
    /*
     * private void pack() { ausgabe.pack(); eingabe.pack(); imageLabel.pack();
     * imageGroup.pack(); imageComposite.pack(); textComposite.pack();
     * shell.pack(); shell.update(); }
     */

}