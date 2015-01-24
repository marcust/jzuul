/*
 * 	CVS: $Id: SwingGui.java,v 1.21 2004/07/21 11:12:24 marcus Exp $
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.jzuul.engine.Engine;
import org.jzuul.engine.ObjectCache;

/**
 * Diese Klasse Implementiert ein GUI auf Suns Swing Toolkit.
 * 
 * 
 * @version $Revision: 1.21 $
 */
public class SwingGui implements GuiInterface {

    protected Container mainFrame;

    protected JTextPane outputArea;

    protected JLabel imageArea;

    protected JTextField inputArea;

    protected ActionListener backup;

    protected HashMap history;

    protected StyledDocument doc;

    protected JScrollPane scrollPane;

    protected ActionListener current;

    protected ActionListener defaultListener;

    protected boolean applet = false;

    public static final int maxImageSize = 1024 * 1024; // 1 MB

    protected ObjectCache imageCache;

    final double imageWith = 400;

    final double imageHeight = 300;

    public SwingGui() {
        this(null);
    }

    public SwingGui(RootPaneContainer root) {

        //		Make sure we have nice window decorations.
        //JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.

        imageCache = new ObjectCache(10);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel imagePanel = new JPanel(new GridLayout(1, 1));
        imagePanel.setBackground(Color.WHITE);
        Dimension dim = new Dimension((int) imageWith, (int) imageHeight);
        imagePanel.setSize(dim);
        imagePanel.setPreferredSize(dim);
        JPanel outputPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new BorderLayout());
        // fot static input line size:
        inputPanel.setLayout(new GridLayout(1, 1));

        if (root != null) {
            mainFrame = root.getContentPane();
            mainFrame.add(mainPanel);
            applet = true;
            Engine.debug("Running as applet",1);
        } else {
            mainFrame = new JFrame();
            JFrame frame = (JFrame) mainFrame;
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("JZuul");
            URL imageUrl = this.getClass().getResource("/etc/artwork/jz.png");
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                frame.setIconImage(icon.getImage());
            }
            frame.getContentPane().add(mainPanel);
            applet = false;
            Engine.debug("Running as stand-alone",1);
        }

        mainPanel.add(imagePanel);
        mainPanel.add(outputPanel);
        mainPanel.add(inputPanel);

        imageArea = new JLabel();
        imageArea.setHorizontalAlignment(SwingConstants.CENTER);

        outputArea = new JTextPane();
        outputArea.setEditorKit(new RTFEditorKit());
        outputArea.setEditable(false);
        outputArea.setFocusable(false);

        doc = outputArea.getStyledDocument();

        inputArea = new JTextField();
        inputArea.setFocusTraversalKeysEnabled(false);

        scrollPane = new JScrollPane(outputArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        imagePanel.add(imageArea);
        outputPanel.add(scrollPane);
        inputPanel.add(inputArea);

        //Display the window.
        if (mainFrame instanceof JFrame) {
            ((JFrame) mainFrame).pack();
            ((JFrame) mainFrame).setVisible(true);
        }

    }

    public void println(String out, int[] color) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        Color colobj = new Color(color[0], color[1], color[2]);
        StyleConstants.setForeground(a, colobj);
        this.print(out + "\n", a);
    }

    public void print(String out, AttributeSet a) {

        try {
            this.doc.insertString(doc.getLength(), out, a);
            this.outputArea.setCaretPosition(doc.getLength());

        } catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }
    }

    public void print(String out) {
        this.print(out, new SimpleAttributeSet());
    }

    public void println(String out) {
        println(out, GuiInterface.BLACK);
    }

    public void println() {
        this.println("");
    }

    public void printU(String out) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setUnderline(a, true);
        this.print(out, a);
    }

    public void printlnB(String out) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setBold(a, true);
        this.print(out + "\n", a);
    }

    public void printlnB(String out, int[] color) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setBold(a, true);
        Color colobj = new Color(color[0], color[1], color[2]);
        StyleConstants.setForeground(a, colobj);
        this.print(out + "\n", a);
    }

    public void printlnI(String out) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setItalic(a, true);
        this.print(out + "\n", a);
    }

    public void printlnI(String out, int[] color) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setItalic(a, true);
        Color colobj = new Color(color[0], color[1], color[2]);
        StyleConstants.setForeground(a, colobj);
        this.print(out + "\n", a);
    }

    public void setActionListener(ActionListener al) {
        if (current != null) {
            inputArea.removeActionListener(current);
        }
        inputArea.addActionListener(al);
        current = al;
    }

    public void setDefaultActionListener() {
        if (current != null) {
            inputArea.removeActionListener(current);
        }
        inputArea.addActionListener(this.defaultListener);
        this.current = this.defaultListener;
    }

    public void setDefaultActionListener(ActionListener defaultListener) {
        if (current != null) {
            inputArea.removeActionListener(current);
        }
        inputArea.addActionListener(defaultListener);
        this.defaultListener = defaultListener;
        this.current = defaultListener;
    }

    public void setKeyListener(KeyListener list) {
        this.inputArea.addKeyListener(list);

    }

    public void redraw() {
        if (mainFrame instanceof JApplet) {
            ((JApplet) mainFrame).paint(mainFrame.getGraphics());
        }
    }

    public void disableInput() {
        inputArea.setEnabled(false);
        inputArea.setVisible(false);
    }

    public void enableInput() {
        inputArea.setEnabled(true);
        inputArea.setVisible(true);
    }

    public void start() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
            }
        });

    }

    public void resetInput() {
        this.inputArea.setText("");
    }

    public boolean isApplet() {
        return this.applet;
    }

    public ActionListener getActionListener() {
        return this.current;
    }

    public void setInput(String newline) {
        inputArea.setText(newline);
    }

    public String getInput() {
        return inputArea.getText();
    }

    public void close() {
        mainFrame.setVisible(false);
        mainFrame.removeAll();
        if (!isApplet()) {
            System.exit(0);
        }
    }

    public void splash_start(int count) {

    }

    public void splash_next(String message) {
        println(message);
    }

    public void showImage(InputStream imageStream) {
        String id = null;
        if (imageStream != null) {
            id = imageStream.toString();

            if (!imageCache.containsKey(id)) {
                BufferedInputStream bufis = new BufferedInputStream(imageStream);
                byte[] b = new byte[maxImageSize];
                try {
                    bufis.mark(maxImageSize + 1);
                    bufis.read(b);
                    bufis.reset();
                } catch (Exception e) {
                    Engine.debug("Exception: " + e.getMessage(), 1);
                }

                ImageIcon roomImage = new ImageIcon(b);

                int w = roomImage.getIconWidth();
                int h = roomImage.getIconHeight();

                Engine.debug("Image size is " + w + "x" + h, 1);

                BufferedImage bi = new BufferedImage((int) imageWith, (int) imageHeight, BufferedImage.TYPE_INT_ARGB);

                // Set the scale.
                AffineTransform tx = new AffineTransform();
                double scaleW = imageWith / w;
                double scaleH = imageHeight / h;
                tx.scale(scaleW, scaleH);
                Engine.debug("Scaling fact: " + scaleW + " " + scaleH, 1);

                // Paint image.
                Graphics2D g2d = bi.createGraphics();
                ImageObserver obs = imageArea;
                g2d.drawImage(roomImage.getImage(), tx, obs);
                g2d.dispose();

                roomImage = new ImageIcon(bi);
                imageCache.put(id, roomImage);
            }

            Engine.debug("Setting new image", 1);
        }
        ImageIcon cachedImage = (ImageIcon) imageCache.get(id);
        imageArea.setIcon(cachedImage);

    }

}