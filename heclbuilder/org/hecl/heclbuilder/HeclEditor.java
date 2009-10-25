/* Copyright 2006 David N. Welton

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.hecl.heclbuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import org.hecl.HeclException;

import org.hecl.files.HeclFileUtils;

/**
 * <code>HeclEditor</code> -- This class provides a very, very crude
 * editor/viewer for Hecl scripts.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclEditor extends javax.swing.JFrame {

    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuBar editMenuBar1;
    private javax.swing.JEditorPane editPanel;
    private javax.swing.JScrollPane editScrollPane;
    private javax.swing.JMenuItem menuClose;
    private javax.swing.JMenuItem menuSave;

    private StringBuffer script;
    private File scriptfile;

    public HeclEditor() {
        initComponents();
    }

    public HeclEditor(String filename) {
        initComponents();
        scriptfile = new File(filename);
        if (scriptfile.exists()) {
            try {
                script = new StringBuffer(HeclFileUtils.readFile(filename).toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
		    null, "File Error", "Error reading file: " + filename + "\n" + e.toString(),
		    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            script = new StringBuffer("");
        }
        editPanel.setText(script.toString());
    }

    private void initComponents() {
        editScrollPane = new javax.swing.JScrollPane();
        editPanel = new javax.swing.JEditorPane();
        editMenuBar1 = new javax.swing.JMenuBar();
        editMenu = new javax.swing.JMenu();
        menuSave = new javax.swing.JMenuItem();
        menuClose = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        editScrollPane.setAutoscrolls(true);
        editScrollPane.setPreferredSize(null);
        editPanel.setDragEnabled(true);
        editScrollPane.setViewportView(editPanel);

        editMenu.setText("Menu");
        menuSave.setText("Save");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });

        editMenu.add(menuSave);

        menuClose.setText("Close");
        menuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCloseActionPerformed(evt);
            }
        });


        editMenu.add(menuClose);

        editMenuBar1.add(editMenu);

        setJMenuBar(editMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .add(editScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .add(editScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
    }

    private void menuCloseActionPerformed(java.awt.event.ActionEvent evt) {
        script = null;
        this.dispose();
    }

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            writeFile(scriptfile.toString(), editPanel.getText());
        } catch (HeclException e) {
            JOptionPane.showMessageDialog(
		null, "File Error",
		"Could not write to: " + scriptfile + "\n" + e.toString(),
		JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void writeFile(String filename, String data) throws HeclException {
	File realfn = new File(filename).getAbsoluteFile();
	BufferedOutputStream fos = null;

	try {
	    char[] chars = new char[data.length()];
	    data.getChars(0, data.length(), chars, 0);

	    fos = new BufferedOutputStream(new FileOutputStream(realfn));
	    for (int i = 0; i < chars.length; i++) {
		fos.write(chars[i]);
	    }
	} catch (IOException e) {
	    throw new HeclException("error writing to " + realfn +
				    " : " + e.toString());
	} finally {
	    try {
		if (fos != null) {
		    fos.close();
		}
	    } catch (IOException e) {
		throw new HeclException("error closing " + realfn +
					" stream" + " : " + e.toString());
	    }
	}
    }
}
