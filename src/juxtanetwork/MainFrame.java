package juxtanetwork;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;


/**
 *  
 * @author Java Project Team
 */
public class MainFrame extends javax.swing.JFrame {

    File rootInputFolder;
    File rootOutFolder = new File("Data"); // Name of the Audits folder

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        aboutFrame = new javax.swing.JFrame();
        aboutPanel = new javax.swing.JPanel();
        infoNameLBL = new javax.swing.JLabel();
        aboutScrollPane = new javax.swing.JScrollPane();
        aboutTextArea = new javax.swing.JTextArea();
        aboutOkBTN = new javax.swing.JButton();
        settingsDialog = new javax.swing.JDialog();
        discardSettingsBTN = new javax.swing.JButton();
        applySettingsBTN = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        openTLB = new javax.swing.JButton();
        saveTLB = new javax.swing.JButton();
        aboutTLB = new javax.swing.JButton();
        nodesScrollPane = new javax.swing.JScrollPane();
        NodesTree = new javax.swing.JTree();
        mainTabbedPane = new javax.swing.JTabbedPane();
        mainScrollTab1 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        mainScrollTab2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMN = new javax.swing.JMenu();
        OpenMN = new javax.swing.JMenuItem();
        exitMN = new javax.swing.JMenuItem();
        editMN = new javax.swing.JMenu();
        settingsMN = new javax.swing.JMenuItem();
        helpMN = new javax.swing.JMenu();

        fileChooser.setDialogTitle("");
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        aboutFrame.setTitle("About");
        aboutFrame.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        aboutFrame.setLocation(new java.awt.Point(800, 500));
        aboutFrame.setMinimumSize(new java.awt.Dimension(470, 250));
        aboutFrame.setResizable(false);

        infoNameLBL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        infoNameLBL.setForeground(new java.awt.Color(204, 0, 51));
        infoNameLBL.setText("JuxtaNetworks");

        aboutTextArea.setBackground(java.awt.SystemColor.menu);
        aboutTextArea.setColumns(20);
        aboutTextArea.setRows(5);
        aboutTextArea.setText("JuxtaNetworks is an auditing tool for a multiple-CP system,which would \nbe used to observe and highlight configuration differences between blades \nand between members of  MSC in pool, by comparing the relative printouts.\n\nSimilar functionality is currently used in an OSS-RC tool - AXE Audit Tool");
        aboutTextArea.setEnabled(false);
        aboutTextArea.setRequestFocusEnabled(false);
        aboutScrollPane.setViewportView(aboutTextArea);

        aboutOkBTN.setText("OK");
        aboutOkBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutOkBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout aboutPanelLayout = new javax.swing.GroupLayout(aboutPanel);
        aboutPanel.setLayout(aboutPanelLayout);
        aboutPanelLayout.setHorizontalGroup(
            aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(aboutScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(aboutOkBTN))
            .addGroup(aboutPanelLayout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addComponent(infoNameLBL)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        aboutPanelLayout.setVerticalGroup(
            aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoNameLBL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(aboutOkBTN)
                .addContainerGap())
        );

        javax.swing.GroupLayout aboutFrameLayout = new javax.swing.GroupLayout(aboutFrame.getContentPane());
        aboutFrame.getContentPane().setLayout(aboutFrameLayout);
        aboutFrameLayout.setHorizontalGroup(
            aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        aboutFrameLayout.setVerticalGroup(
            aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(aboutPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        settingsDialog.setTitle("Settings");
        settingsDialog.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        settingsDialog.setLocation(new java.awt.Point(800, 500));
        settingsDialog.setMinimumSize(new java.awt.Dimension(400, 300));

        discardSettingsBTN.setText("Discard");
        discardSettingsBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardSettingsBTNActionPerformed(evt);
            }
        });

        applySettingsBTN.setText("Apply");
        applySettingsBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySettingsBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingsDialogLayout = new javax.swing.GroupLayout(settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                .addContainerGap(258, Short.MAX_VALUE)
                .addComponent(applySettingsBTN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discardSettingsBTN)
                .addContainerGap())
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                .addContainerGap(266, Short.MAX_VALUE)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discardSettingsBTN)
                    .addComponent(applySettingsBTN))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JuxtaNetwork");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        setLocation(new java.awt.Point(500, 200));

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        openTLB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Open16.png"))); // NOI18N
        openTLB.setToolTipText("Open");
        openTLB.setFocusable(false);
        openTLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openTLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openTLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openTLBActionPerformed(evt);
            }
        });
        toolBar.add(openTLB);

        saveTLB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Save16.png"))); // NOI18N
        saveTLB.setToolTipText("Save");
        saveTLB.setFocusable(false);
        saveTLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveTLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveTLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTLBActionPerformed(evt);
            }
        });
        toolBar.add(saveTLB);

        aboutTLB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/about16.png"))); // NOI18N
        aboutTLB.setToolTipText("About");
        aboutTLB.setFocusable(false);
        aboutTLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutTLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aboutTLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutTLBActionPerformed(evt);
            }
        });
        toolBar.add(aboutTLB);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Nodes");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("MSC");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("MSC1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("MSC2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("MSC3");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("HLR");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("HLR1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("HLR2");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Pool");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Pool1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Pool2");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        NodesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        NodesTree.setToggleClickCount(1);
        nodesScrollPane.setViewportView(NodesTree);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        mainScrollTab1.setViewportView(jTextArea2);

        mainTabbedPane.addTab("tab1", mainScrollTab1);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        mainScrollTab2.setViewportView(jList1);

        mainTabbedPane.addTab("tab2", mainScrollTab2);

        jButton1.setText("Next");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(nodesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(41, 41, 41))))
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nodesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))))
        );

        fileMN.setText("File");

        OpenMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Open16.png"))); // NOI18N
        OpenMN.setText("Open");
        OpenMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMNActionPerformed(evt);
            }
        });
        fileMN.add(OpenMN);

        exitMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/exit16.png"))); // NOI18N
        exitMN.setText("exit");
        exitMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMNActionPerformed(evt);
            }
        });
        fileMN.add(exitMN);

        menuBar.add(fileMN);

        editMN.setText("Edit");

        settingsMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/settings16.png"))); // NOI18N
        settingsMN.setText("Settings");
        settingsMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMNActionPerformed(evt);
            }
        });
        editMN.add(settingsMN);

        menuBar.add(editMN);

        helpMN.setText("Help");
        menuBar.add(helpMN);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method getPrintouts opens a file chooser to select the data input folder.
     * Then calls createStructure to create the Data structure and copies input files
     */
    private void getPrintouts() {

        fileChooser.setDialogTitle("Open Printouts Folder");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(1);

        fileChooser.showOpenDialog(this);
        try {
            rootInputFolder = fileChooser.getSelectedFile();
            createStructure();
        } catch (Exception e) {
            if (rootInputFolder.getName() == "") {
                System.out.println("No file selected!");
            }
        }
    }

    /**
     * Method createStructure creates in Data a timestamp folder and copies all files and
     * directories of input folder to that timestamp directory
     * @throws IOException 
     */
    private void createStructure() throws IOException {
        if (!rootOutFolder.exists()) {
            rootOutFolder.mkdir();
        }
        String path = rootOutFolder.getCanonicalPath() + System.getProperty("file.separator")
                + String.valueOf(new Timestamp(System.currentTimeMillis())).replace(':', '_').replace(' ', '_')
                        .substring(0, 19);    // timestamp path in Data folder. Format: 2018-09-13_22_04_59

        CopyUtil.copyDirectoryContent(new File(rootInputFolder.getCanonicalPath()), new File(path));
    }
    
    private void openTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTLBActionPerformed
        getPrintouts();
    }//GEN-LAST:event_openTLBActionPerformed

    private void OpenMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMNActionPerformed
        getPrintouts();
    }//GEN-LAST:event_OpenMNActionPerformed

    private void exitMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMNActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMNActionPerformed

    private void settingsMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMNActionPerformed
        settingsDialog.setVisible(true);
    }//GEN-LAST:event_settingsMNActionPerformed

    private void aboutTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutTLBActionPerformed
        aboutFrame.setVisible(true);
    }//GEN-LAST:event_aboutTLBActionPerformed

    private void aboutOkBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutOkBTNActionPerformed
        aboutFrame.setVisible(false);
    }//GEN-LAST:event_aboutOkBTNActionPerformed

    private void discardSettingsBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardSettingsBTNActionPerformed
        settingsDialog.setVisible(false);
    }//GEN-LAST:event_discardSettingsBTNActionPerformed

    private void applySettingsBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySettingsBTNActionPerformed
        settingsDialog.setVisible(false);
    }//GEN-LAST:event_applySettingsBTNActionPerformed

    private void saveTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTLBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveTLBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree NodesTree;
    private javax.swing.JMenuItem OpenMN;
    private javax.swing.JFrame aboutFrame;
    private javax.swing.JButton aboutOkBTN;
    private javax.swing.JPanel aboutPanel;
    private javax.swing.JScrollPane aboutScrollPane;
    private javax.swing.JButton aboutTLB;
    private javax.swing.JTextArea aboutTextArea;
    private javax.swing.JButton applySettingsBTN;
    private javax.swing.JButton discardSettingsBTN;
    private javax.swing.JMenu editMN;
    private javax.swing.JMenuItem exitMN;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMN;
    private javax.swing.JMenu helpMN;
    private javax.swing.JLabel infoNameLBL;
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollTab1;
    private javax.swing.JScrollPane mainScrollTab2;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JScrollPane nodesScrollPane;
    private javax.swing.JButton openTLB;
    private javax.swing.JButton saveTLB;
    private javax.swing.JDialog settingsDialog;
    private javax.swing.JMenuItem settingsMN;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
