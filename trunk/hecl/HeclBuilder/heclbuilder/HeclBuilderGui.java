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

package heclbuilder;

import jarhack.JarHack;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 * <code>HeclBuilderGui</code>
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclBuilderGui extends javax.swing.JFrame {

    private javax.swing.JLabel createLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel outputLabel;
    private javax.swing.JMenu mainMenu;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem menuQuit;
    private javax.swing.JLabel midletLabel;
    private javax.swing.JTextField midletTextField;
    private javax.swing.JButton outputSelectButton;
    private javax.swing.JTextField outputTextField;
    private javax.swing.JButton runButton;
    private javax.swing.JFileChooser scriptFileChooser;
    private javax.swing.JFileChooser outputDirChooser;
    private javax.swing.JTextField scriptTextField;
    private javax.swing.JButton selectScriptButton;
    private javax.swing.JLabel selectLabel;

    /** Creates new form HeclBuilderGui */
    public HeclBuilderGui() {
        initComponents();
    }

    /**
     * The <code>initComponents</code> method creates all the GUI
     * elements.
     *
     */
    private void initComponents() {
/*         scriptFileChooser = new javax.swing.JFileChooser();
	outputDirChooser = new javax.swing.JFileChooser();  */
        selectLabel = new javax.swing.JLabel();
        midletLabel = new javax.swing.JLabel();
        outputLabel = new javax.swing.JLabel();
        createLabel = new javax.swing.JLabel();
        scriptTextField = new javax.swing.JTextField();
        midletTextField = new javax.swing.JTextField();
        selectScriptButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        outputTextField = new javax.swing.JTextField();
        outputSelectButton = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        mainMenu = new javax.swing.JMenu();
        menuQuit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HeclBuilder");
        selectLabel.setText("Select a script");

        midletLabel.setText("MIDlet name");

        createLabel.setText("Create .jar and .jad files");

        scriptTextField.setColumns(30);
        scriptTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                scriptTextFieldCaretUpdate(evt);
            }
        });

        midletTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                midletTextFieldCaretUpdate(evt);
            }
        });

        selectScriptButton.setText("...");
        selectScriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectScriptButtonActionPerformed(evt);
            }
        });

        runButton.setText("Go");
        runButton.setToolTipText("Select a script and MIDlet name to activate this button");
        runButton.setEnabled(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        runButton.getAccessibleContext().setAccessibleDescription("");

        editButton.setText("View/Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        outputLabel.setText("Output directory");

        outputTextField.setText("/tmp");

        outputSelectButton.setText("...");
        outputSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputSelectActionPerformed(evt);
            }
        });

        mainMenu.setText("File");
        menuQuit.setText("Quit");
        menuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuQuitActionPerformed(evt);
            }
        });

        mainMenu.add(menuQuit);

        mainMenuBar.add(mainMenu);

        setJMenuBar(mainMenuBar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
	hGroup.add(layout.createParallelGroup().
		   add(selectLabel).
		   add(midletLabel).
		   add(outputLabel).
		   add(createLabel));
	hGroup.add(layout.createParallelGroup().
		   add(scriptTextField).
		   add(midletTextField).
		   add(outputTextField));

	hGroup.add(layout.createParallelGroup().
		   add(layout.createSequentialGroup().
		       add(selectScriptButton).
		       add(editButton)).
		   add(outputSelectButton).
		   add(runButton));

	layout.setHorizontalGroup(hGroup);

	GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
	vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		   add(selectLabel).
		   add(scriptTextField).
		   add(selectScriptButton).
		   add(editButton));
	vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		   add(midletLabel).
		   add(midletTextField));
	vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		   add(outputLabel).
		   add(outputTextField).
		   add(outputSelectButton));
	vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		   add(createLabel).
		   add(runButton));
	layout.setVerticalGroup(vGroup);

	pack();
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        InputStream in = this.getClass().getResourceAsStream("/j2me/Hecl.jar");
        String scriptfile = scriptTextField.getText();
        String newname = midletTextField.getText();
        String outfile = outputTextField.getText() + File.separatorChar +
	    newname + ".jar";
        try {
            JarHack.substHecl(in, outfile, newname, scriptfile);
	    JarHack.createJadForJar(outfile, newname);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
		this, "Problem creating " + outfile + "\n" + e.toString(),
		"Hecl Builder Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scriptTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {
        midletTextFieldCaretUpdate(evt);
    }

    private void midletTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {
        String midletName = midletTextField.getText();
        String scriptName = scriptTextField.getText();
        if (midletName.length() > 0 && scriptName.length() > 0) {
            runButton.setEnabled(true);
        }
    }

    private void menuQuitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(1);
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String filename = null;
        File fl = new File(scriptTextField.getText());

        if (fl.exists()) {
            filename = fl.toString();
        } else {
            if (JOptionPane.showConfirmDialog(this, "The file " + fl + " does not exist.  Create it?")
                != JOptionPane.YES_OPTION) {
                return;
            }
            filename = fl.toString();
        }
        System.out.println(filename);
        HeclEditor he = new HeclEditor(filename);
        he.setVisible(true);
    }

    private void outputSelectActionPerformed(java.awt.event.ActionEvent evt) {
        outputDirChooser = new JFileChooser(outputTextField.getText());
	outputDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retval = outputDirChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = outputDirChooser.getSelectedFile();
            outputTextField.setText(file.toString());
        }
    }

    private void selectScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {
        scriptFileChooser = new JFileChooser();
	scriptFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retval = scriptFileChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = scriptFileChooser.getSelectedFile();
            scriptTextField.setText(file.toString());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HeclBuilderGui().setVisible(true);
            }
        });
    }
}
