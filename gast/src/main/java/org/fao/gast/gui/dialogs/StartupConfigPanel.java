//==============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.gast.gui.dialogs;

import org.dlib.gui.FlexLayout;
import org.fao.gast.boot.Config;
import org.fao.gast.localization.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

//==============================================================================

//==============================================================================

public class StartupConfigPanel extends JFrame {


    //---------------------------------------------------------------------------
    //---
    //--- Constructor
    //---
    //---------------------------------------------------------------------------

    public StartupConfigPanel(Config config) {
        super(Messages.getString("StartupConfigPanel.Title"));

        Method[] methods = config.getClass().getMethods();

        ArrayList<Method> setters = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                setters.add(method);
            }
        }

        BorderLayout border = new BorderLayout();
        getContentPane().setLayout(border);

        Label about = new Label(Messages.getString("StartupConfigPanel.about"));
        getContentPane().add(about, BorderLayout.NORTH);


        JPanel p = new JPanel(); 
        FlexLayout fl = new FlexLayout(3, methods.length + 1);
        fl.setColProp(2, FlexLayout.EXPAND);
        p.setLayout(fl);
        getContentPane().add(p, BorderLayout.CENTER);

        int pos = 1;
        for (Method method : setters) {
            addController(p,pos, config, method);
            pos++;
        }

        p.add("0," + (pos + 1), btnOk());
        p.add("1," + (pos + 1) + "", btnCancel());

    }

    private Component btnOk() {
        Button btnOk = new Button(Messages.getString("ok"));
        btnOk.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                StartupConfigPanel.this.setVisible(false);
            }
        });
        return btnOk;
    }

    private Component btnCancel() {
        Button btnCancel = new Button(Messages.getString("cancel"));
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        return btnCancel;
    }

    private void addController(JPanel p, int pos, final Config config, Method method) {
        final String value = lookupValue(config, method.getName().substring(3));
        String labelText = method.getName().substring(3);
        p.add("0," + pos, new JLabel(Messages.getString(labelText)));
        final JTextField txt = new JTextField(20);
        txt.setText(value);
        final PathListener pathListener = new PathListener(txt, config, method);
        txt.addKeyListener(pathListener);

        p.add("1," + pos + ",x", txt);
        if(method.getParameterTypes()[0] == String.class) {
            Button btn = createFileChooserButton(config, value, txt, pathListener);
            p.add("2," + pos + ",x", btn);
        }
    }

    private Button createFileChooserButton(final Config config, final String value, final JTextField txt, final PathListener pathListener) {
        Button btn = new Button(Messages.getString("browse"));
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser d = new JFileChooser();

                if (!setFile(d, value)) {
                    if (!setFile(d, config.getWebapp())) {
                        d.setCurrentDirectory(new File("."));
                    }
                }

                d.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                d.setMultiSelectionEnabled(false);

                d.showDialog(txt, Messages.getString("ok"));

                File file = d.getSelectedFile();
                if(!file.exists()) {
                    file = file.getParentFile();
                }

                try {
                    txt.setText(file.getCanonicalPath());
                } catch (IOException e1) {
                    txt.setText(file.getPath());
                }
                pathListener.keyTyped(null);


            }


            private boolean setFile(JFileChooser d, String value) {

                File file = new File(config.resolvePath(value, true));
                if (file.exists()) {
                    d.setCurrentDirectory(file);
                    return true;
                }
                return false;
            }
        });
        return btn;
    }

    private String lookupValue(Config config, String s) {
        try {
            Method m = config.getClass().getMethod("get" + s);

            final Object value = m.invoke(config);
            if(value instanceof String) {
                return (String) value;
            } else {
                return "" + value; 
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //---------------------------------------------------------------------------
    //---
    //--- Variables
    //---

    private class PathListener implements KeyListener {
        private Config config;
        private Method setter;
        private JTextField txt;

        public PathListener(JTextField txt, Config config, Method setter) {
            this.txt = txt;
            this.config = config;
            this.setter = setter;
        }

        private String resolvePath(JTextField txt) {
            final String path = txt.getText();
            if (setter.getName().equals("setWebapp")) {
                return path;
            } else {
                return config.resolvePath(path, false);
            }
        }

        public void keyTyped(KeyEvent e) {
            try {
                final String resolvedPath = resolvePath(txt);
                String path = txt.getText();
                if(setter.getParameterTypes()[0] == String.class) {
                    setter.invoke(config, resolvedPath);
                } else if(setter.getParameterTypes()[0] == Integer.class) {
                    setter.invoke(config, Integer.valueOf(path));
                } else if(setter.getParameterTypes()[0] == Boolean.class) {
                    setter.invoke(config, Boolean.valueOf(path));
                } else if(setter.getParameterTypes()[0] == Double.class) {
                    setter.invoke(config, Double.valueOf(path));
                } else if(setter.getParameterTypes()[0] == Float.class) {
                    setter.invoke(config, Float.valueOf(path));
                } else if(setter.getParameterTypes()[0] == Long.class) {
                    setter.invoke(config, Long.valueOf(path));
                } else if(setter.getParameterTypes()[0] == Character.class) {
                    setter.invoke(config, Character.valueOf(path.charAt(0)));
                }
                if (!new File(path).exists()) {
                    txt.setToolTipText("Path does not exist");
                    txt.setCaretColor(Color.RED);
                }
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }

        public void keyPressed(KeyEvent e) {
            // ignore
        }

        public void keyReleased(KeyEvent e) {
            // ignore
        }
    }

}