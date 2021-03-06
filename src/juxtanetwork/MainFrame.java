package juxtanetwork;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

// CHMA-GGEW-SOVL
import java.io.FileWriter;
import java.io.BufferedWriter;
import javax.swing.JFileChooser;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.tree.TreeModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileFilter;
import java.awt.Point;

//VAAG-CHRE
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author Java Project Team
 */
public class MainFrame extends javax.swing.JFrame {

    File rootInputFolder;
    String rootDataPath;
    File rootOutFolder;// = new File("Data");                              // Name of the Audits folder
    final DefaultListModel compList1Model = new DefaultListModel();     // Compare nodes
    final DefaultListModel compList2Model = new DefaultListModel();     // CompareWith Nodes
    final DefaultListModel commListModel = new DefaultListModel();      // Command list for selectiona
    DefaultListModel refListModel = new DefaultListModel();     // Compare nodes

    DefaultMutableTreeNode nodeTreeModel = new DefaultMutableTreeNode("Nodes");     // Nodes Tree Root TreeNode
    DefaultMutableTreeNode commsTreeModel = new DefaultMutableTreeNode("Commands"); // Commands Tree Root TreeNode
    DefaultMutableTreeNode nodeTreeModelMSC = new DefaultMutableTreeNode("MSC");    // MSC subtree of nodes Tree
    DefaultMutableTreeNode nodeTreeModelHLR = new DefaultMutableTreeNode("HLR");    // HLR subtree of nodes Tree
    DefaultMutableTreeNode nodeTreeModelPool = new DefaultMutableTreeNode("Pool");  // Pool subtree of nodes Tree

    ImageIcon hlrIcon = new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/hlr16.gif"));
    ImageIcon mscIcon = new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/MSC16.jpg"));
    ImageIcon bladeIcon = new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/blade16.png"));
    ImageIcon poolIcon = new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/pool.jpg"));
    ImageIcon comIcon = new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/comm-16.png"));

    Highlight highliter = new Highlight();
    Highlight highliterSearch = new Highlight();
    ArrayList<int[]> diffs1 = new ArrayList<int[]>();
    ArrayList<int[]> diffs2 = new ArrayList<int[]>();
    ArrayList<String> TimeStampBase = new ArrayList<String>();
    ArrayList<String> TimeStampTarget = new ArrayList<String>();
    HashMap<Integer, Integer> validated = new HashMap<Integer, Integer>();
    int currDiff = 0;
    
    // CHMA - GGEW - SOVL
    int numOfFiles;
    int filesDone;
    int progress;
    JFileChooser saveAllChooser;
    boolean saveAllIndic;
    
    Color diffsColor = Color.ORANGE;
    Color searchColor = Color.YELLOW;
    Color searchFoundColor = Color.CYAN;
    Color diffsCurrColor = Color.MAGENTA;

    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    //IXGKOAG --  DEFINE and Initialize Compare Object
    Compare cmp;
    boolean concurendScroll = false;

    //CHMA-GGEW-SOVL  -- Define Common Command List
    ArrayList<String> arrayCommList = new ArrayList<String>();
    
    
    // CHMA - GGEW - SOVL
    /**
     * This class handles the save all functionality actions. It runs in the 
     * background with 100ms interval between each file saved to allow other 
     * background tasks to run in parallel.
     */
   
    private class SaveWorker extends SwingWorker {
        
        @Override
        public Object doInBackground() {
            numOfFiles = 0;
            filesDone = 0;      
            saveAllIndic = true;
            Object root = TargetNodesTree.getModel().getRoot();
            numOfFiles = findNumOfFiles(0, root, TargetNodesTree.getModel());
            
            try {
                File f1 = new File(saveAllChooser.getSelectedFile(), "File_Report.txt");
                BufferedWriter writer1 = new BufferedWriter(new FileWriter(f1));

                traverseTree(saveAllChooser, TargetNodesTree.getModel(), root, writer1);

                writer1.close();
                saveAllIndic = false;
            } catch (IOException ex) {
                saveAllIndic = false;
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    // CHMA - GGEW - SOVL
    /**
     * This class handles the progress bar in the gui regarding progress of save 
     * all functionality. It runs in the background with 100ms interval to check
     * the progress of the files saved.
     */
    private class ProgressWorker extends SwingWorker {
        
        @Override
        public Object doInBackground() {
            progress = 0;
            while (progress < 100){
                if (numOfFiles != 0) {
                    if (!jProgressBar1.isVisible()){
                        jProgressBar1.setVisible(true);
                        jProgressBar1.setStringPainted(true);
                    }
                    progress = 100 * filesDone/numOfFiles;
                }
                jProgressBar1.setValue(progress);
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException ex){
                    java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
            return null;
        }

        @Override
        public void done() {
            jProgressBar1.setVisible(false);
            jProgressBar1.setStringPainted(false);
            jProgressBar1.setValue(0);
        }
    }
    
    
    // CHMA - GGEW - SOVL
    /**
     * This class implements FilenameFilter in order to select MSC files
     * in data directory and show them in the information tab.
     */
    private class DirFilter implements FileFilter {
        
        @Override
        public boolean accept(File f) {
            return f.isDirectory();
        }
        
    }
    
    
    // CHMA - GGEW - SOVL
    /**
     * This class implements FilenameFilter in order to select MSC files
     * in data directory and show them in the information tab.
     */
    private class MSCFilter implements FilenameFilter {
        
        @Override
        public boolean accept(File dir, String file) {
            return (file.startsWith("MSC")) && (dir.isDirectory());
        }
        
    }
    
    // CHMA - GGEW - SOVL
    /**
     * This class implements FilenameFilter in order to select HLR files
     * in data directory and show them in the information tab.
     */
    private class HLRFilter implements FilenameFilter {
        
        @Override
        public boolean accept(File dir, String file) {
            return (file.startsWith("HLR")) && (dir.isDirectory());
        }
        
    }
    



    /**
     * Creates new form MainFrame
     */
    public MainFrame() {

        initComponents();
        initializations();
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
        settingPanel1 = new javax.swing.JPanel();
        lastTimeCheck = new javax.swing.JCheckBox();
        fontLBL = new javax.swing.JLabel();
        fontBTN = new javax.swing.JButton();
        chooseDataFolderTxt = new javax.swing.JTextField();
        chooseDataFolderLBL = new javax.swing.JLabel();
        chooseDataFolderBTN = new javax.swing.JButton();
        settingPanel2 = new javax.swing.JPanel();
        colorDifBTN = new javax.swing.JButton();
        colorDifLBL = new javax.swing.JLabel();
        colorSearchBTN = new javax.swing.JButton();
        colorSearchLBL = new javax.swing.JLabel();
        colorSearchFoundLBL = new javax.swing.JLabel();
        colorSearchFoundBTN = new javax.swing.JButton();
        colorCurrDifLBL = new javax.swing.JLabel();
        colorCurrDifBTN = new javax.swing.JButton();
        chooseFromRefDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        chooseRefLBL = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        refChooseList = new javax.swing.JList<>();
        cancelChooseRefBTN = new javax.swing.JButton();
        applyChooseRefBTN = new javax.swing.JButton();
        chooseRefLBL1 = new javax.swing.JLabel();
        jColorChooser1 = new javax.swing.JColorChooser();
        errorDialog = new javax.swing.JDialog();
        errorMessageLBL = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        textPopUpMN = new javax.swing.JPopupMenu();
        CopyPopUp = new javax.swing.JMenuItem(new DefaultEditorKit.CopyAction());
        nextDiffMN = new javax.swing.JMenuItem();
        prevDiffMN = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        nextBTN = new javax.swing.JButton();
        prevBTN = new javax.swing.JButton();
        mainSplitPane = new javax.swing.JSplitPane();
        nodesScrollPane = new javax.swing.JScrollPane();
        TargetNodesTree = new javax.swing.JTree();
        mainTabbedPane = new javax.swing.JTabbedPane();
        infoPanel = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        comparePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        compList1 = new javax.swing.JList<>();
        clear1BTN = new javax.swing.JButton();
        insertElem1BTN = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        compList2 = new javax.swing.JList<>();
        clear2BTN = new javax.swing.JButton();
        insertElem2BTN = new javax.swing.JButton();
        mainScrollTab3 = new javax.swing.JScrollPane();
        commList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        removeElem1BTN = new javax.swing.JButton();
        removeElem2BTN = new javax.swing.JButton();
        resultsPanel = new javax.swing.JPanel();
        diffSplitPane = new javax.swing.JSplitPane();
        po1ScrollPane = new javax.swing.JScrollPane();
        po1TextArea = new javax.swing.JTextArea();
        po2ScrollPane = new javax.swing.JScrollPane();
        po2TextArea = new javax.swing.JTextArea();
        BaseNodesCombo = new javax.swing.JComboBox<>();
        prevHiliteBTN = new javax.swing.JButton();
        nextHiliteBTN = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        BackFindBTN = new javax.swing.JButton();
        ForFindBTN = new javax.swing.JButton();
        concurendScrollButton = new javax.swing.JToggleButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();
        openTLB = new javax.swing.JButton();
        saveTLB = new javax.swing.JButton();
        saveAllBTN = new javax.swing.JButton();
        sidebarBTN = new javax.swing.JButton();
        aboutTLB = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        AnalysisBTN = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        menuBar = new javax.swing.JMenuBar();
        fileMN = new javax.swing.JMenu();
        OpenMN = new javax.swing.JMenuItem();
        SaveMN = new javax.swing.JMenuItem();
        saveAllMN = new javax.swing.JMenuItem();
        exitMN = new javax.swing.JMenuItem();
        editMN = new javax.swing.JMenu();
        analysisMN = new javax.swing.JMenuItem();
        settingsMN = new javax.swing.JMenuItem();
        helpMN = new javax.swing.JMenu();
        aboutMN = new javax.swing.JMenuItem();

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
        settingsDialog.setMinimumSize(new java.awt.Dimension(558, 400));

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

        lastTimeCheck.setText("Always use the last inserted data (last timestamp) of a node to check");
        lastTimeCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastTimeCheckActionPerformed(evt);
            }
        });

        fontLBL.setText("Font:");

        fontBTN.setText("Choose...");
        fontBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontBTNActionPerformed(evt);
            }
        });

        chooseDataFolderTxt.setFont(new java.awt.Font("Lucida Console", 0, 10)); // NOI18N

        chooseDataFolderLBL.setText("Define new  root path for Data Folder");

        chooseDataFolderBTN.setText("Choose");
        chooseDataFolderBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseDataFolderBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingPanel1Layout = new javax.swing.GroupLayout(settingPanel1);
        settingPanel1.setLayout(settingPanel1Layout);
        settingPanel1Layout.setHorizontalGroup(
            settingPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingPanel1Layout.createSequentialGroup()
                .addComponent(fontLBL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fontBTN)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(settingPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingPanel1Layout.createSequentialGroup()
                        .addComponent(chooseDataFolderLBL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseDataFolderBTN))
                    .addComponent(lastTimeCheck)
                    .addComponent(chooseDataFolderTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        settingPanel1Layout.setVerticalGroup(
            settingPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lastTimeCheck)
                .addGap(18, 18, 18)
                .addGroup(settingPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseDataFolderLBL)
                    .addComponent(chooseDataFolderBTN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseDataFolderTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(settingPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontLBL)
                    .addComponent(fontBTN))
                .addGap(6, 6, 6))
        );

        colorDifBTN.setBackground(getDiffsColor());
        colorDifBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorDifBTNActionPerformed(evt);
            }
        });

        colorDifLBL.setText("Color for Differences:");

        colorSearchBTN.setBackground(getSearchColor());
        colorSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSearchBTNActionPerformed(evt);
            }
        });

        colorSearchLBL.setText("Color for Search:");

        colorSearchFoundLBL.setText("Color for Search Found:");

        colorSearchFoundBTN.setBackground(getSearchFoundColor());
        colorSearchFoundBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSearchFoundBTNActionPerformed(evt);
            }
        });

        colorCurrDifLBL.setText("Color for Current Difference:");

        colorCurrDifBTN.setBackground(getDiffsCurrColor());
        colorCurrDifBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorCurrDifBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingPanel2Layout = new javax.swing.GroupLayout(settingPanel2);
        settingPanel2.setLayout(settingPanel2Layout);
        settingPanel2Layout.setHorizontalGroup(
            settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorDifLBL)
                    .addComponent(colorSearchLBL)
                    .addComponent(colorSearchFoundLBL)
                    .addComponent(colorCurrDifLBL))
                .addGap(47, 47, 47)
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(colorCurrDifBTN)
                    .addComponent(colorDifBTN)
                    .addComponent(colorSearchBTN)
                    .addComponent(colorSearchFoundBTN))
                .addContainerGap(244, Short.MAX_VALUE))
        );
        settingPanel2Layout.setVerticalGroup(
            settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingPanel2Layout.createSequentialGroup()
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(colorDifLBL)
                    .addComponent(colorDifBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(colorCurrDifLBL)
                    .addComponent(colorCurrDifBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(colorSearchLBL)
                    .addComponent(colorSearchBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(colorSearchFoundLBL)
                    .addComponent(colorSearchFoundBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout settingsDialogLayout = new javax.swing.GroupLayout(settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addComponent(settingPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                                .addComponent(applySettingsBTN)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(discardSettingsBTN))
                            .addComponent(settingPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                .addComponent(settingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(settingPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discardSettingsBTN)
                    .addComponent(applySettingsBTN))
                .addContainerGap())
        );

        chooseFromRefDialog.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        chooseFromRefDialog.setMinimumSize(new java.awt.Dimension(180, 270));

        chooseRefLBL.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        chooseRefLBL.setText("Choose from Reference");

        refChooseList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(refChooseList);

        cancelChooseRefBTN.setText("Cancel");
        cancelChooseRefBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelChooseRefBTNActionPerformed(evt);
            }
        });

        applyChooseRefBTN.setText("Apply");
        applyChooseRefBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyChooseRefBTNActionPerformed(evt);
            }
        });
        applyChooseRefBTN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                applyChooseRefBTNKeyPressed(evt);
            }
        });

        chooseRefLBL1.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        chooseRefLBL1.setText("Base");
        chooseRefLBL1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(applyChooseRefBTN)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancelChooseRefBTN))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chooseRefLBL))
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chooseRefLBL1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(chooseRefLBL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseRefLBL1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelChooseRefBTN)
                    .addComponent(applyChooseRefBTN))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout chooseFromRefDialogLayout = new javax.swing.GroupLayout(chooseFromRefDialog.getContentPane());
        chooseFromRefDialog.getContentPane().setLayout(chooseFromRefDialogLayout);
        chooseFromRefDialogLayout.setHorizontalGroup(
            chooseFromRefDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        chooseFromRefDialogLayout.setVerticalGroup(
            chooseFromRefDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        errorDialog.setTitle("Notification");
        errorDialog.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        errorDialog.setMinimumSize(new java.awt.Dimension(420, 150));
        errorDialog.setModal(true);
        errorDialog.setType(java.awt.Window.Type.POPUP);

        errorMessageLBL.setText("Error");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Exclamation-icon64.png"))); // NOI18N
        jLabel4.setText("jLabel4");
        jLabel4.setIconTextGap(1);

        javax.swing.GroupLayout errorDialogLayout = new javax.swing.GroupLayout(errorDialog.getContentPane());
        errorDialog.getContentPane().setLayout(errorDialogLayout);
        errorDialogLayout.setHorizontalGroup(
            errorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(errorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorDialogLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(errorMessageLBL, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        errorDialogLayout.setVerticalGroup(
            errorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorDialogLayout.createSequentialGroup()
                .addGroup(errorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(errorMessageLBL, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1))
        );

        CopyPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/copy16.png"))); // NOI18N
        CopyPopUp.setText("Copy");
        CopyPopUp.setToolTipText("Copy text");
        CopyPopUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CopyPopUpActionPerformed(evt);
            }
        });
        textPopUpMN.add(CopyPopUp);

        nextDiffMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Next16.png"))); // NOI18N
        nextDiffMN.setText("Next Diff");
        nextDiffMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextDiffMNActionPerformed(evt);
            }
        });
        textPopUpMN.add(nextDiffMN);

        prevDiffMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Prev16.png"))); // NOI18N
        prevDiffMN.setText("Prev Diff");
        prevDiffMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevDiffMNActionPerformed(evt);
            }
        });
        textPopUpMN.add(prevDiffMN);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JuxtaNetwork");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/dual-mobile.png")).getImage());
        setLocation(new java.awt.Point(500, 200));
        setMinimumSize(new java.awt.Dimension(600, 640));

        nextBTN.setText("Next");
        nextBTN.setToolTipText("Next tab");
        nextBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBTNActionPerformed(evt);
            }
        });

        prevBTN.setText("Prev");
        prevBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBTNActionPerformed(evt);
            }
        });

        mainSplitPane.setDividerLocation(150);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        TargetNodesTree.setToggleClickCount(1);
        TargetNodesTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                TargetNodesTreeValueChanged(evt);
            }
        });
        TargetNodesTree.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                TargetNodesTreePropertyChange(evt);
            }
        });
        nodesScrollPane.setViewportView(TargetNodesTree);

        mainSplitPane.setLeftComponent(nodesScrollPane);

        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        infoTextArea.setColumns(20);
        infoTextArea.setRows(5);
        infoPanel.setViewportView(infoTextArea);

        mainTabbedPane.addTab("Information", infoPanel);

        compList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        compList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        compList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                compList1MouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(compList1);

        clear1BTN.setText("X");
        clear1BTN.setToolTipText("Clear Element from Compare");
        clear1BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear1BTNActionPerformed(evt);
            }
        });

        insertElem1BTN.setText(">");
        insertElem1BTN.setToolTipText("Insert Element to Compare");
        insertElem1BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertElem1BTNActionPerformed(evt);
            }
        });

        compList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        compList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        compList2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                compList2FocusLost(evt);
            }
        });
        compList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                compList2MouseEntered(evt);
            }
        });
        jScrollPane2.setViewportView(compList2);

        clear2BTN.setText("X");
        clear2BTN.setToolTipText("Clear Element from Compare with");
        clear2BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear2BTNActionPerformed(evt);
            }
        });

        insertElem2BTN.setText(">");
        insertElem2BTN.setToolTipText("Insert Element to Compare with");
        insertElem2BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertElem2BTNActionPerformed(evt);
            }
        });

        commList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "PCORP", "MGNDP", "DBTSP", "MGEPP", "Comm1", "Comm2", "Comm3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        commList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                commListFocusLost(evt);
            }
        });
        commList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                commListMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                commListMouseReleased(evt);
            }
        });
        mainScrollTab3.setViewportView(commList);

        jLabel1.setText("Compare Element");

        jLabel2.setText("With Element");

        jLabel3.setText("Compare Commands");

        removeElem1BTN.setText("<");
        removeElem1BTN.setToolTipText("Remove Element from Compare");
        removeElem1BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeElem1BTNActionPerformed(evt);
            }
        });

        removeElem2BTN.setText("<");
        removeElem2BTN.setToolTipText("Remove Element from Compare with");
        removeElem2BTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeElem2BTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout comparePanelLayout = new javax.swing.GroupLayout(comparePanel);
        comparePanel.setLayout(comparePanelLayout);
        comparePanelLayout.setHorizontalGroup(
            comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comparePanelLayout.createSequentialGroup()
                .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(comparePanelLayout.createSequentialGroup()
                            .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(insertElem1BTN)
                                .addComponent(insertElem2BTN))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(removeElem1BTN)
                                .addComponent(removeElem2BTN))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(clear1BTN, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(clear2BTN, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, comparePanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, comparePanelLayout.createSequentialGroup()
                                    .addGap(0, 44, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addGap(18, 18, 18)
                .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(comparePanelLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 312, Short.MAX_VALUE))
                    .addComponent(mainScrollTab3))
                .addContainerGap())
        );
        comparePanelLayout.setVerticalGroup(
            comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comparePanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(comparePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clear1BTN)
                            .addComponent(insertElem1BTN)
                            .addComponent(removeElem1BTN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(comparePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clear2BTN)
                            .addComponent(insertElem2BTN)
                            .addComponent(removeElem2BTN)))
                    .addGroup(comparePanelLayout.createSequentialGroup()
                        .addComponent(mainScrollTab3, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        mainTabbedPane.addTab("Compare", comparePanel);

        diffSplitPane.setDividerLocation(250);
        diffSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        diffSplitPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                diffSplitPaneComponentResized(evt);
            }
        });

        po1ScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                po1ScrollPaneMouseReleased(evt);
            }
        });

        po1TextArea.setColumns(20);
        po1TextArea.setRows(5);
        po1TextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                po1TextAreaMouseReleased(evt);
            }
        });
        po1ScrollPane.setViewportView(po1TextArea);

        diffSplitPane.setTopComponent(po1ScrollPane);

        po2ScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                po2ScrollPaneMouseReleased(evt);
            }
        });

        po2TextArea.setColumns(20);
        po2TextArea.setRows(5);
        po2TextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                po2TextAreaMouseReleased(evt);
            }
        });
        po2ScrollPane.setViewportView(po2TextArea);

        diffSplitPane.setRightComponent(po2ScrollPane);

        BaseNodesCombo.setToolTipText("Compared Elements");
        BaseNodesCombo.setMaximumSize(new java.awt.Dimension(100, 20));
        BaseNodesCombo.setPreferredSize(new java.awt.Dimension(100, 20));
        BaseNodesCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                BaseNodesComboItemStateChanged(evt);
            }
        });
        BaseNodesCombo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                BaseNodesComboFocusLost(evt);
            }
        });
        BaseNodesCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BaseNodesComboActionPerformed(evt);
            }
        });

        prevHiliteBTN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Prev24.png"))); // NOI18N
        prevHiliteBTN.setToolTipText("Previous Difference");
        prevHiliteBTN.setBorder(null);
        prevHiliteBTN.setMargin(new java.awt.Insets(0, 2, 0, 2));
        prevHiliteBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevHiliteBTNActionPerformed(evt);
            }
        });

        nextHiliteBTN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Next24.png"))); // NOI18N
        nextHiliteBTN.setToolTipText("Next Difference");
        nextHiliteBTN.setBorder(null);
        nextHiliteBTN.setBorderPainted(false);
        nextHiliteBTN.setMargin(new java.awt.Insets(0, 2, 0, 2));
        nextHiliteBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextHiliteBTNActionPerformed(evt);
            }
        });

        searchField.setForeground(new java.awt.Color(102, 102, 102));
        searchField.setText("Search..");
        searchField.setToolTipText("Search Field");
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
        });
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        BackFindBTN.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        BackFindBTN.setText("<");
        BackFindBTN.setToolTipText("Previous Difference");
        BackFindBTN.setMargin(new java.awt.Insets(2, 8, 2, 8));
        BackFindBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackFindBTNActionPerformed(evt);
            }
        });

        ForFindBTN.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ForFindBTN.setText(">");
        ForFindBTN.setToolTipText("Previous Difference");
        ForFindBTN.setMargin(new java.awt.Insets(2, 8, 2, 8));
        ForFindBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ForFindBTNActionPerformed(evt);
            }
        });

        concurendScrollButton.setText("Concurrent Scroll");
        concurendScrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concurendScrollButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultsPanelLayout = new javax.swing.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(diffSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addComponent(BaseNodesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BackFindBTN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ForFindBTN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(concurendScrollButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(prevHiliteBTN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextHiliteBTN)
                .addContainerGap())
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanelLayout.createSequentialGroup()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BaseNodesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(prevHiliteBTN)
                        .addComponent(nextHiliteBTN)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BackFindBTN)
                        .addComponent(ForFindBTN)
                        .addComponent(concurendScrollButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diffSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Results", resultsPanel);

        mainSplitPane.setRightComponent(mainTabbedPane);

        jProgressBar1.setPreferredSize(new java.awt.Dimension(146, 23));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(prevBTN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextBTN))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainSplitPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nextBTN)
                        .addComponent(prevBTN))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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
        saveTLB.setToolTipText("Save current report");
        saveTLB.setFocusable(false);
        saveTLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveTLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveTLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTLBActionPerformed(evt);
            }
        });
        toolBar.add(saveTLB);

        saveAllBTN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/save-all16.png"))); // NOI18N
        saveAllBTN.setToolTipText("Save all reports");
        saveAllBTN.setFocusable(false);
        saveAllBTN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveAllBTN.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveAllBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllBTNActionPerformed(evt);
            }
        });
        toolBar.add(saveAllBTN);

        sidebarBTN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/sidebar16.png"))); // NOI18N
        sidebarBTN.setToolTipText("Toggle Sidebar");
        sidebarBTN.setFocusable(false);
        sidebarBTN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sidebarBTN.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sidebarBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sidebarBTNActionPerformed(evt);
            }
        });
        toolBar.add(sidebarBTN);

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
        toolBar.add(jSeparator1);

        AnalysisBTN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/apply_execute16.png"))); // NOI18N
        AnalysisBTN.setText("Analysis");
        AnalysisBTN.setToolTipText("Run Analysis");
        AnalysisBTN.setFocusable(false);
        AnalysisBTN.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        AnalysisBTN.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AnalysisBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalysisBTNActionPerformed(evt);
            }
        });
        toolBar.add(AnalysisBTN);
        toolBar.add(jSeparator2);

        fileMN.setText("File");

        OpenMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Open16.png"))); // NOI18N
        OpenMN.setText("Open");
        OpenMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMNActionPerformed(evt);
            }
        });
        fileMN.add(OpenMN);

        SaveMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/Save16.png"))); // NOI18N
        SaveMN.setText("Save");
        SaveMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTLBActionPerformed(evt);
            }
        });
        fileMN.add(SaveMN);

        saveAllMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/save-all16.png"))); // NOI18N
        saveAllMN.setText("SaveAll");
        saveAllMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllBTNActionPerformed(evt);
            }
        });
        fileMN.add(saveAllMN);

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

        analysisMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/apply_execute16.png"))); // NOI18N
        analysisMN.setText("Analysis");
        analysisMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analysisMNActionPerformed(evt);
            }
        });
        editMN.add(analysisMN);

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

        aboutMN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/juxtanetwork/about16.png"))); // NOI18N
        aboutMN.setText("About");
        aboutMN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMNActionPerformed(evt);
            }
        });
        helpMN.add(aboutMN);

        menuBar.add(helpMN);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Initializations method
     */
    private void initializations() {
        compList1.setModel(compList1Model);             // Set model for Compare nodes
        compList2.setModel(compList2Model);             // Set model for Compare With nodes
        commList.setModel(commListModel);
        refChooseList.setModel(refListModel);
        setRootOutFolder();
        //vaag
        createNodesTree();                              // Create the Nodes Tree Model
        createCommTree();                               // Create the Commands Tree Model
        TargetNodesTree.setRootVisible(false);                // Do not diaplsy the Name of the root of the tree
        TargetNodesTree.setCellRenderer(new MyRenderer());    // Assign icons and tooltips per type of node in TargetNodesTree
        ToolTipManager.sharedInstance().registerComponent(TargetNodesTree); // Tooltips on Nodes Tree enabled
        managePrevNextBTN();
        TargetNodesTree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);

        prevHiliteBTN.setEnabled(false);
        nextHiliteBTN.setEnabled(false);
        prevDiffMN.setEnabled(false);
        nextDiffMN.setEnabled(false);
        lastTimeCheck.setSelected(prefs.getBoolean("lastTimeCheck", false));
        //IXGKOAG -- Get concurentScroll property
        //concurrentScroll.setSelected(prefs.getBoolean("concurrentScroll", false));

        //IXGKOAG --  Initialize Compare Object
        this.cmp = new Compare(BaseNodesCombo, commsTreeModel);
        
        // CHMA - GGEW - SOVL
        // progress bar handling
        jProgressBar1.setVisible(false);
        saveAllIndic = false;
    }

    private void setRootOutFolder() {
        rootDataPath = getRootPathFromPrefs();
        try {
            rootOutFolder = new File(rootDataPath + System.getProperty("file.separator") + "Data");
            if (!rootOutFolder.exists()) {
                rootOutFolder = new File("Data");       // set default value to current directory
            }
        } catch (Exception e) {
            rootOutFolder = new File("Data");
        }

    }

    private String getRootPathFromPrefs() {
        String rootPathInfoTmp = prefs.get("rootDataPath", "");
        chooseDataFolderTxt.setText(rootPathInfoTmp);

        return rootPathInfoTmp;
    }

    /**
     * Method createNodesTree creates the nodeTree categories: MSC, HLR, Pool
     * and calls updateNodesTree to put the node leafs on the tree
     */
    private void createNodesTree() {
        nodeTreeModel.removeAllChildren();
        nodeTreeModelMSC.removeAllChildren();
        nodeTreeModelHLR.removeAllChildren();
        nodeTreeModel.add(nodeTreeModelMSC);
        nodeTreeModel.add(nodeTreeModelHLR);
        updateNodesTree();
    }

    /**
     * Method createCommsList creates the common command list in GUI whenever a
     * new node is inserted/removed
     */
    private void createCommsList() {

        //CHMA-GGEW-SOVL 
        // Insert common commands in the GUI list
        commListModel.clear();
        for (String s : arrayCommList) {
            commListModel.addElement(s);
        }

    }

    //VAAG-CHRE, MODIFIED
    /**
     * Method updateNodesTree inserts a new node to the tree under the correct
     * category. If the node already exists in the tree, it is not added.
     */
    private void updateNodesTree() {
        DefaultMutableTreeNode[] nodesTreeModel = new DefaultMutableTreeNode[20];
        DefaultMutableTreeNode[] subnodesTreeModel = new DefaultMutableTreeNode[20];
        if (rootOutFolder.exists()) {
            int currIndex = 0;
            int subcurrIndex = 0;
            for (File node : rootOutFolder.listFiles()) {
                if (node.isDirectory()) {
                    nodesTreeModel[currIndex] = new DefaultMutableTreeNode(node.getName());   //timestamp folders
                    for (File subnode : node.listFiles()) {
                        if (subnode.getName().contains("info")) {
                            try {
                                FileReader fileReader = new FileReader(subnode);
                                BufferedReader bufferedReader = new BufferedReader(fileReader);
                                String currentLine;                             //BC or info
                                try {
                                    while ((currentLine = bufferedReader.readLine()) != null) {
                                        if (currentLine.contains("MSC") && isNotIncluded(nodeTreeModel, node.getName())) {
                                            nodeTreeModelMSC.add(nodesTreeModel[currIndex]);
                                        } else if (currentLine.contains("HLR") && isNotIncluded(nodeTreeModel, node.getName())) {
                                            nodeTreeModelHLR.add(nodesTreeModel[currIndex]);
                                        }
                                    }
                                } catch (IOException ex) {
                                    System.out.println("Unable to open file '" + subnode.getName() + "' Error in bufferedReader");
                                }

                            } catch (FileNotFoundException ex) {
                                System.out.println("Unable to open file '" + subnode.getName() + "'");
                            }
                        } else if (subnode.isDirectory()) {                         //more recent timestamp 
                            subcurrIndex = 0;
                            for (File subsubnode : subnode.listFiles()) {
                                if (subsubnode.isDirectory()) {
                                    subnodesTreeModel[subcurrIndex] = new DefaultMutableTreeNode(subsubnode.getName());
                                    if (subsubnode.getName().startsWith("BC") && isNotIncluded(nodesTreeModel[currIndex], subsubnode.getName())) {
                                        nodesTreeModel[currIndex].add(subnodesTreeModel[subcurrIndex]);
                                    }
                                }
                                subcurrIndex++;
                            }
                        }

                    }
                }
                currIndex++;
            }
        }
        TargetNodesTree.updateUI();
    }

    /**
     * Method isNotIncluded checks if a specified treeModel name already exists
     * in the tree provided in the parameter.
     *
     * @param treeModel the Tree model to be checked
     * @param name the node checked
     * @return true if the node name is not included in the tree, i.e. will be a
     * new node on the tree
     */
    private boolean isNotIncluded(DefaultMutableTreeNode treeModel, String name) {
        boolean included = true;

        Enumeration<DefaultMutableTreeNode> e = treeModel.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode nodeCheck = e.nextElement();
            if (name.equals(nodeCheck.toString())) {
                included = false;
            }
        }

        return included;
    }
    
    /**
     * Inner class MyRenderer is used to render specific icon for the different
     * types of nodes in the Nodes Tree and also specific Tooltips. The
     * categories are MSC node, HLR node, other
     */
    class MyRenderer extends DefaultTreeCellRenderer {

        Icon nodeIcon;

        public MyRenderer(Icon icon) {
            nodeIcon = icon;
        }

        public MyRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(
                javax.swing.JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);

//            setBackgroundSelectionColor(Color.BLACK);
//            setBackgroundNonSelectionColor(Color.BLACK);
//            setTextSelectionColor(Color.MAGENTA);
//            setTextNonSelectionColor(Color.BLACK);
            if (isHLR(value)) {
                setIcon(hlrIcon);
                setToolTipText("HLR Node");
                setTextSelectionColor(Color.WHITE);
                setOpaque(false);
            } else if (isMSC(value)) {
                setIcon(mscIcon);
                setToolTipText("MSC Node");
                setTextSelectionColor(Color.WHITE);
                setOpaque(false);
            } else if (isBC(value)) {
                setIcon(bladeIcon);
                setToolTipText("BC Node");
                setTextSelectionColor(Color.WHITE);
                setOpaque(false);
//                setBackground(Color.WHITE);
            } else {
                setIcon(comIcon);
                try {
                    if (validated.get(row) == 1) {
                        setForeground(Color.RED);
                    } else if (validated.get(row) == 2) {
                        setForeground(new Color(50, 205, 50));
                    }
                } catch (Exception e) {

                }
            }

            return this;
        }

        protected boolean isHLR(Object value) {
            String nodeName = value.toString();

            if (nodeName.startsWith("HLR")) {
                return true;
            }

            return false;
        }

        protected boolean isMSC(Object value) {
            String nodeName = value.toString();

            if (nodeName.startsWith("MSC")) {
                return true;
            }

            return false;
        }
        //VAAG,CHRE

        protected boolean isBC(Object value) {
            String nodeName = value.toString();

            if (nodeName.startsWith("BC")) {
                return true;
            }

            return false;
        }
    }

    /**
     * Method createCommTree creates the command tree. This method inserts in
     * the tree the selected from the user commands for the comparison of the
     * nodes. The commands are grouped on two categories, based on whether the
     * command should validate to identical printout or not
     */
    private void createCommTree() {
        DefaultMutableTreeNode[] commsTreeModelNode = new DefaultMutableTreeNode[200];
        int countNodesSelected = 0;
        commsTreeModel.removeAllChildren();
        for (int i = 0; i < compList2Model.size(); i++) {
            commsTreeModelNode[i] = new DefaultMutableTreeNode(compList2Model.getElementAt(i).toString());
            commsTreeModel.add(commsTreeModelNode[i]);
            countNodesSelected++;
        }
        int[] comms = commList.getSelectedIndices();
        for (int i = 0; i < comms.length; i++) {
            for (int j = 0; j < countNodesSelected; j++) {
                DefaultMutableTreeNode commandTreeModelComm = new DefaultMutableTreeNode(commListModel.getElementAt(comms[i]).toString());

                commsTreeModelNode[j].add(commandTreeModelComm);
            }
        }
        resetValidatePOs();

        TargetNodesTree.updateUI();
    }

    private void resetValidatePOs() {
        validated.clear();
        for (int i = 0; i < 200; i++) {
            validated.put(i, 0);
        }
    }

    /**
     * Method getPrintouts opens a file chooser to select the data input folder.
     * Then calls createStructure to create the Data structure and copies input
     * files
     */
    private void getPrintouts() {

        // CHMA - GGEW - SOVL
        
        if (saveAllIndic){
            errorMessageLBL.setText("Cannot open new network while saving");
            errorDialog.setVisible(true);
            return;
        }
        
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
        updateNodesTree();
        expandTreeAll();
    }

    /**
     * Method createStructure creates in Data a timestamp folder and copies all
     * files and directories of input folder to that timestamp directory
     *
     * @throws IOException
     */
    private void createStructure() throws IOException {
        if (!rootOutFolder.exists()) {
            rootOutFolder.mkdir();
        }

        String path = rootOutFolder.getCanonicalPath();    // path in Data folder
        CopyUtil.copyDirectoryContent(new File(rootInputFolder.getCanonicalPath()), new File(path));
    }

    /**
     * Method expandTreeAll expands all the nodes of the tree
     */
    public void expandTreeAll() {
        for (int i = 0; i < TargetNodesTree.getRowCount(); i++) {
            TargetNodesTree.expandRow(i);
        }
    }

    /**
     * Method managePrevNextBTN will manage the Next and Prev buttons of the
     * tabs. When on last tab, NextBTN will not be enabled. When on first tab,
     * prevBTN will not be enabled. In all other cases both buttons are enabled
     */
    private void managePrevNextBTN() {
        int current = mainTabbedPane.getSelectedIndex();

        switch (current) {
            case 2:
                nextBTN.setEnabled(false);
                prevBTN.setEnabled(true);
                TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(commsTreeModel));
                expandTreeAll();
                break;
            case 0:
                nextBTN.setEnabled(true);
                prevBTN.setEnabled(false);
                TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(nodeTreeModel));
                expandTreeAll();
                break;
            default:
                nextBTN.setEnabled(true);
                prevBTN.setEnabled(true);
                TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(nodeTreeModel));
                expandTreeAll();
                break;
        }
    }

    /**
     * Insert an element node from Nodes Tree to model of parameter if not
     * already there
     *
     * @param model
     */
    public void insertElem(DefaultListModel model) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) TargetNodesTree
                .getLastSelectedPathComponent();
        String selectedNodeName = selectedNode.getParent() + "/" + selectedNode.toString();
        if (selectedNode.isLeaf()) {
            if (!model.contains(selectedNodeName)) {
                model.addElement(selectedNodeName);
            }
        }
    }

    // CHMA-GGEW-SOVL
    /**
     * This method writes the line number and whole line of a difference for the
     * two poTextAreas to file created from writer.
     *
     * @param writer is the writer to the file where differences are written
     * @param diffs1 are the start and end of a difference for po1textArea
     * @param diffs2 are the start and end of a difference for po2textArea
     * @param lastDiffLine are the last lines of potextAreas where a difference
     * is found
     * @param diffCount is the number of the current difference to be stored in
     * the file
     * @param printout is the name of the node where each printout is taken
     * @return the new lines of the two potextAreas where the new difference is
     * found
     */
    private int[] writeDiffToFile(BufferedWriter writer, int[] diffs1, int[] diffs2, int[] lastDiffLine, int diffCount, String[] printout, String[] pos) {
        int[] temps = new int[2];
        try {
            int line1po1 = findLine(pos[0],diffs1[0]);
            int line1po2 = findLine(pos[1],diffs2[0]);
            temps[0] = line1po1;
            temps[1] = line1po2;
            if (!((diffs1[0] == diffs1[1]) && (diffs2[0] == diffs2[1]))) {
                if ((line1po1 != lastDiffLine[0]) || (line1po2 != lastDiffLine[1])) {
                    writer.write("Difference: " + diffCount + "\n");
                    writer.write("-------------\n");
                    writer.write(printout[0] + "\n");
                    int line2po1 = findLine(pos[0],diffs1[1]);
                    int line2po2 = findLine(pos[1],diffs2[1]);
                    if (getLineStart(pos[0],line2po1) == diffs1[1]) {
                        line2po1 -= 1;
                    }
                    if (getLineStart(pos[1],line2po2) == diffs2[1]) {
                        line2po2 -= 1;
                    }
                    if (line1po1 >= line2po1) {
                        writer.write("line " + line1po1 + ":\n");
                        writer.write(getLine(pos[0],line1po1) + "\n");
                    } else {
                        writer.write("lines " + line1po1 + "-" + line2po1 + ":\n");
                        writer.write(getLines(pos[0], line1po1, line2po1));
                    }
                    writer.write("\n\n");
                    writer.write(printout[1] + "\n");
                    if (line1po2 >= line2po2) {
                        writer.write("line " + line1po2 + ":\n");
                        writer.write(getLine(pos[1],line1po2) + "\n");
                    } else {
                        writer.write("lines " + line1po2 + "-" + line2po2 + ":\n");
                        writer.write(getLines(pos[1], line1po2, line2po2));
                    }
                    writer.write("\n\n");
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return temps;
    }
    
    private int findLine(String po, int index) {
	int line = 1;
	int charIndex = 0;
	while (charIndex < index) {
            if (po.charAt(charIndex) == '\n'){
		line++;
            }
            charIndex++;
	}
	return line;
    }

    private String getLine(String po, int line) {
	int lineIndex = 1;
	int charIndex = 0;
	String lineString = null;
	while (lineIndex < line) { 
            if (po.charAt(charIndex) == '\n'){
                lineIndex++;
                if (line == lineIndex) {
                    charIndex++;
                    if (po.indexOf('\n',charIndex) != -1){
                        lineString = po.substring(charIndex,po.indexOf('\n',charIndex));
                    }
                    else {
                        lineString = po.substring(charIndex,po.length());
                    }
                    return lineString;
		}
            }
            charIndex++;
	}
        
	return lineString;
    } 
    
    private String getLines(String po, int line1, int line2) {
	int lineIndex = 1;
	int charIndex = 0;
        int startIndex = -1;
	String lineString = null;
	while (lineIndex < line2) { 
            if (po.charAt(charIndex) == '\n'){
                lineIndex++;
                if (line1 == lineIndex) {
                    charIndex++;
                    if (startIndex == -1) {
                        startIndex = charIndex;
                    }
		}
                if (line2 == lineIndex) {
                    charIndex++;
                    if (po.indexOf('\n',charIndex) != -1){    
                        lineString = po.substring(startIndex,po.indexOf('\n',charIndex));
                    }
                    else {
                        lineString = po.substring(startIndex,po.length());
                    }
                    return lineString;
		}
            }
            charIndex++;
	}
	return lineString;
    } 
    
    private int getLineStart(String po, int line) {
	int lineIndex = 1;
	int charIndex = 0;
	while (lineIndex < line) {
            if (po.charAt(charIndex) == '\n'){
                lineIndex++;
                if (line == lineIndex) {
                    charIndex++;
                    return charIndex;
		}
            }
            charIndex++;
	}
	return charIndex;
    } 


    private void OpenMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMNActionPerformed
        getPrintouts();
    }//GEN-LAST:event_OpenMNActionPerformed

    private void exitMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMNActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMNActionPerformed

    private void settingsMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMNActionPerformed
        settingsDialog.setVisible(true);
    }//GEN-LAST:event_settingsMNActionPerformed

    private void aboutOkBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutOkBTNActionPerformed
        aboutFrame.setVisible(false);
    }//GEN-LAST:event_aboutOkBTNActionPerformed

    private void discardSettingsBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardSettingsBTNActionPerformed
        settingsDialog.setVisible(false);
    }//GEN-LAST:event_discardSettingsBTNActionPerformed

    private void applySettingsBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySettingsBTNActionPerformed
        settingsDialog.setVisible(false);
        String rootDataPathTmp = chooseDataFolderTxt.getText();
        if (!chooseDataFolderTxt.getText().isEmpty()) {
            System.out.println("pref init: " + prefs.get("rootDataPath", ""));

            rootOutFolder = new File(rootDataPathTmp + System.getProperty("file.separator") + "Data");
            if (!rootOutFolder.exists()) {
                rootOutFolder = new File("Data");       // set default value to current directory
                rootDataPathTmp = "";
            }
            rootDataPath = rootDataPathTmp;
            prefs.put("rootDataPath", rootDataPathTmp);
            System.out.println("pref final: " + prefs.get("rootDataPath", ""));
            createNodesTree();
            expandTreeAll();
        }
        prefs.putBoolean("lastTimeCheck", lastTimeCheck.isSelected());
        //IXGKOAG -SetProperty
//        prefs.putBoolean("concurrentScroll", concurrentScroll.isSelected());

    }//GEN-LAST:event_applySettingsBTNActionPerformed

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
        managePrevNextBTN();
    }//GEN-LAST:event_mainTabbedPaneStateChanged

    private void removeElem2BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeElem2BTNActionPerformed
        if (!compList2.isSelectionEmpty()){
            TimeStampTarget.remove(compList2.getSelectedIndex());
            compList2Model.removeElementAt(compList2.getSelectedIndex());
            refListModel.clear();
            arrayCommList = cmp.updateTargetNodes(compList2, TimeStampTarget);
            compList2.setToolTipText(null);
            createCommsList();
        }
    }//GEN-LAST:event_removeElem2BTNActionPerformed

    private void removeElem1BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeElem1BTNActionPerformed
        if (!compList1.isSelectionEmpty()){
            TimeStampBase.remove(compList1.getSelectedIndex());
            compList1Model.removeElementAt(compList1.getSelectedIndex());
            refListModel.clear();
            arrayCommList = cmp.updateBaseNodes(compList1, TimeStampBase);
            compList1.setToolTipText(null);
            createCommsList();
        }
    }//GEN-LAST:event_removeElem1BTNActionPerformed

    private void insertElem2BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertElem2BTNActionPerformed
        if (constructRefList()) {
            chooseRefLBL1.setText("Target");
            if (lastTimeCheck.isSelected()) {
                chooseRef(refListModel.getElementAt(refListModel.getSize() - 1).toString());
            } else {
                refChooseList.setSelectedIndex(refListModel.getSize() - 1);
                chooseFromRefDialog.setVisible(true);
                chooseFromRefDialog.requestFocus();
                applyChooseRefBTN.requestFocus();
            }
        }
    }//GEN-LAST:event_insertElem2BTNActionPerformed

    private void clear2BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear2BTNActionPerformed
        compList2Model.clear();
        TimeStampTarget.clear();
        refListModel.clear();
        arrayCommList = cmp.updateTargetNodes(compList2, TimeStampTarget);
        compList2.setToolTipText(null);
        createCommsList();
    }//GEN-LAST:event_clear2BTNActionPerformed

    private void insertElem1BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertElem1BTNActionPerformed
        if (constructRefList()) {
            chooseRefLBL1.setText("Base");
            if (lastTimeCheck.isSelected()) {
                chooseRef(refListModel.getElementAt(refListModel.getSize() - 1).toString());
            } else {
                refChooseList.setSelectedIndex(refListModel.getSize() - 1);
                chooseFromRefDialog.setVisible(true);
                chooseFromRefDialog.requestFocus();
                applyChooseRefBTN.requestFocus();
            }
        }
    }//GEN-LAST:event_insertElem1BTNActionPerformed

    private boolean constructRefList() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) TargetNodesTree
                .getLastSelectedPathComponent();
        if (selectedNode == null) {
            return false;
        }
        String selectedNodeName = selectedNode.getParent().toString();
        refListModel.clear();
        if (selectedNode.isLeaf()) {
            File base = new File(getPath(selectedNodeName));
            for (File f : base.listFiles()) {
                if (f.isDirectory()) {
                    refListModel.addElement(f.getName());
                }
            }
        }
        return true;
    }

    String getPath(String node) {
        String fileSeperator = System.getProperty("file.separator");
        String LogsDirectory = "Data";

        return LogsDirectory + fileSeperator + node;
    }

    private void clear1BTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear1BTNActionPerformed
        compList1Model.clear();
        TimeStampBase.clear();
        refListModel.clear();
        arrayCommList = cmp.updateBaseNodes(compList1, TimeStampBase);
        compList1.setToolTipText(null);
        createCommsList();
    }//GEN-LAST:event_clear1BTNActionPerformed

    private void prevBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBTNActionPerformed
        int current = mainTabbedPane.getSelectedIndex();
        if (current > 0) {
            mainTabbedPane.setSelectedIndex(current - 1);
        }
        managePrevNextBTN();
    }//GEN-LAST:event_prevBTNActionPerformed

    private void nextBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBTNActionPerformed
        int current = mainTabbedPane.getSelectedIndex();
        if (current < 2) {
            mainTabbedPane.setSelectedIndex(current + 1);
        }
        managePrevNextBTN();
    }//GEN-LAST:event_nextBTNActionPerformed

    private void aboutTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutTLBActionPerformed
        aboutFrame.setVisible(true);
    }//GEN-LAST:event_aboutTLBActionPerformed

    private void saveTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTLBActionPerformed

        // CHMA-GGEW-SOVL -- Code for saving the differences beween certain printouts
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) TargetNodesTree
                .getLastSelectedPathComponent();
        if (mainTabbedPane.getSelectedIndex() != 2) {
            errorMessageLBL.setText("Go to results panel to save");
            errorDialog.setVisible(true);
            return;
        }
        if ((selectedNode == null) || (selectedNode.toString().contains("/"))) {
            errorMessageLBL.setText("Please select a command for saving");
            errorDialog.setVisible(true);
            return;
        }
        
        String command = selectedNode.toString();
        String target = selectedNode.getParent().toString();
        String base = BaseNodesCombo.getSelectedItem().toString();
        String refs[] = cmp.getCommandReferences(base, target, command);
        JFileChooser fC = new JFileChooser(rootOutFolder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
        fC.setFileFilter(filter);
        if (fC.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fC.getSelectedFile().getAbsolutePath() + ".txt"));
                int[] lastLine = new int[]{-1, -1};
                int diffCount = 1;
                String[] pos = new String[2];
                pos[0] = po1TextArea.getText().trim();
                pos[1] = po2TextArea.getText().trim();
                for (int i = 0; i < diffs1.size(); i++) {
                    int tempLine[] = writeDiffToFile(writer, diffs1.get(i), diffs2.get(i), lastLine, diffCount, refs, pos);
                    if ((tempLine[0] != lastLine[0]) || (tempLine[1] != lastLine[1])) {
                        diffCount++;
                    }
                    lastLine = tempLine;
                }
                writer.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_saveTLBActionPerformed

    private void openTLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTLBActionPerformed
        getPrintouts();
    }//GEN-LAST:event_openTLBActionPerformed

    private void sidebarBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sidebarBTNActionPerformed
        if (mainSplitPane.getDividerLocation() > 1) {
            mainSplitPane.setDividerLocation(0);
            mainSplitPane.setDividerSize(3);
        } else {
            mainSplitPane.setDividerLocation(150);
            mainSplitPane.setDividerSize(5);
        }
    }//GEN-LAST:event_sidebarBTNActionPerformed

    private void doAnalysis() {

        findDiffs();

        highliter.highlightremove(po1TextArea);
        highliter.highlightremove(po2TextArea);
        for (int i = diffs1.size() - 1; i >= 0; i--) {
            highlightDiffs(diffs1.get(i)[0], diffs1.get(i)[1], diffs2.get(i)[0], diffs2.get(i)[1], diffsColor);
        }

        if (diffs1.size() > 0) {
            validated.replace(TargetNodesTree.getLeadSelectionRow(), 1);
            prevHiliteBTN.setEnabled(true);
            nextHiliteBTN.setEnabled(true);
            prevDiffMN.setEnabled(true);
            nextDiffMN.setEnabled(true);
        } else {
            validated.replace(TargetNodesTree.getLeadSelectionRow(), 2);
            prevHiliteBTN.setEnabled(false);
            nextHiliteBTN.setEnabled(false);
            prevDiffMN.setEnabled(false);
            nextDiffMN.setEnabled(false);
        }
    }

    private void AnalysisBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalysisBTNActionPerformed
        performAnalysis();
    }//GEN-LAST:event_AnalysisBTNActionPerformed

    private void performAnalysis() {
        if (compList1Model.isEmpty()) {
            errorMessageLBL.setText("Please set Base Nodes for comparison");
            errorDialog.setVisible(true);
            return;
        }
        if (compList2Model.isEmpty()) {
            errorMessageLBL.setText("Please set Target Nodes for comparison");
            errorDialog.setVisible(true);
            return;
        }
        if (commList.getSelectedIndex() == -1) {
            errorMessageLBL.setText("Please select 1 or more commands");
            errorDialog.setVisible(true);
            return;
        }
        
        
        // CHMA - GGEW - SOVL
        if (saveAllIndic) {
            errorMessageLBL.setText("Cannot perform another analysis while saving");
            errorDialog.setVisible(true);
            return;
        }

        cmp.updateSelectedCommands(commList);
        cmp.prepareHashStructure();
        createCommTree();

//        doAnalysis();
        mainTabbedPane.setSelectedIndex(2);
        TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(commsTreeModel));
        expandTreeAll();
    }

    // CHMA-GGEW-SOVL
    /**
     * Is for creating the list of differences in means of INSERT, DELETE and
     * store indices of start and end of the difference in diff1 and diff2
     * lists.
     */
    private void findDiffs() {
        LinkedList<diff_match_patch.Diff> diffs = new LinkedList<diff_match_patch.Diff>();
        diff_match_patch dmp = new diff_match_patch();
        dmp.Diff_Timeout = 0;
//      diffs = dmp.diff_main(po1TextArea.getText(), po2TextArea.getText());
        diffs = dmp.diff_main(po1TextArea.getText().trim(), po2TextArea.getText().trim());
        dmp.diff_cleanupSemantic(diffs);
        diffs1 = diff_Fortext(diffs);
//      diffs = dmp.diff_main(po2TextArea.getText(), po1TextArea.getText());
        diffs = dmp.diff_main(po2TextArea.getText().trim(), po1TextArea.getText().trim());
        dmp.diff_cleanupSemantic(diffs);
        diffs2 = diff_Fortext(diffs);
        cleanUpDiffs();
    }
    
    
    
    // CHMA - GGEW - SOVL
    
    private ArrayList<ArrayList<int[]>> findDiffs(String po1, String po2) {  
        ArrayList<ArrayList<int[]>> saveDiffs = new ArrayList<ArrayList<int[]>>(2);
        LinkedList<diff_match_patch.Diff> diffs = new LinkedList<diff_match_patch.Diff>();
        diff_match_patch dmp = new diff_match_patch();
        dmp.Diff_Timeout = 0;
        diffs = dmp.diff_main(po1.trim(), po2.trim());
        dmp.diff_cleanupSemantic(diffs);
        saveDiffs.add(diff_Fortext(diffs));
        diffs = dmp.diff_main(po2.trim(), po1.trim());
        dmp.diff_cleanupSemantic(diffs);
        saveDiffs.add(diff_Fortext(diffs));
        saveDiffs = cleanUpDiffs(saveDiffs);
        return saveDiffs;
    }

    /**
     * If for a specific index difference both diff1 and diff2 lists have start
     * point equal to end point then this difference should be deleted from both
     * diff1 and diff2 lists. These are cases when some characters are different
     * between two texts and the differences is a DELETE(start,end) and
     * INSERT(end,end) for both texts
     */
    private void cleanUpDiffs() {
        if (diffs1.size() != diffs2.size()) {
            return;
        }
        for (int i = diffs1.size() - 1; i >= 0; i--) {
            if ((diffs1.get(i)[0] == diffs1.get(i)[1]) && (diffs2.get(i)[0] == diffs2.get(i)[1])) {
                diffs1.remove(i);
                diffs2.remove(i);
            }
        }
    }
    
    
    private ArrayList<ArrayList<int[]>> cleanUpDiffs(ArrayList<ArrayList<int[]>> diffs) {

        if (diffs.get(0).size() != diffs.get(1).size()) {
            return diffs;
        }
        for (int i = diffs.get(0).size()- 1; i >= 0; i--) {
            if ((diffs.get(0).get(i)[0] == diffs.get(0).get(i)[1]) && (diffs.get(1).get(i)[0] == diffs.get(1).get(i)[1])) {
                diffs.get(0).remove(i);
                diffs.get(1).remove(i);
            }
        }
        return diffs;
    }

    /**
     * Method diff_Fortext will calculate the start and end point of each
     * difference given a List of diffs. A internal StringBuilder is used. For
     * every item in the input diffs list: If the operation is not Equal then a
     * difference starts and start point is saved. Then if the operation is not
     * Insert the difference corresponds to some characters on the present text,
     * so the characters are appended to the StringBuilder. The absolute end
     * point of the difference in the entire text can then be found if we check
     * (for not Equal operation only, since that is the if we kept the start
     * point) the final size of the text.
     *
     * @param diffs linked list of differences that includes the operation and
     * text of each difference
     * @return and ArrayList of absolute [start,end] pair for each difference
     */
    private static ArrayList<int[]> diff_Fortext(List<diff_match_patch.Diff> diffs) {
        ArrayList<int[]> diffsList = new ArrayList<int[]>();
        StringBuilder text = new StringBuilder();
        int prevSize = 0;
        for (diff_match_patch.Diff aDiff : diffs) {
            int[] diffIndexes = {0, 0};
            prevSize = 0;
            if (aDiff.operation != diff_match_patch.Operation.EQUAL) {
                diffIndexes[0] = text.length();
                if (aDiff.operation == diff_match_patch.Operation.DELETE) {
                    if (diffsList.size() != 0 && diffIndexes[0] == diffsList.get(diffsList.size() - 1)[1]) {
                        prevSize = diffIndexes[0] - diffsList.get(diffsList.size() - 1)[0];
                        diffIndexes[0] = diffsList.get(diffsList.size() - 1)[0];
                        diffsList.remove(diffsList.size() - 1);
                    }
                }
            }
            if (aDiff.operation != diff_match_patch.Operation.INSERT) {
                text.append(aDiff.text);
            }
            if (aDiff.operation != diff_match_patch.Operation.EQUAL) {
                diffIndexes[1] = text.length() + prevSize;
                diffsList.add(diffIndexes);
            }

        }
        return diffsList;
    }

    /**
     * Method highlightDiffs will order the highlight function for both PrintOut
     * text areas given the start/end points and the desired color
     *
     * @param index11 start index of difference for text area 1
     * @param index12 end index of difference for text area 1
     * @param index21 start index of difference for text area 2
     * @param index22 end index of difference for text area 2
     * @param color desired color
     */
    private void highlightDiffs(int index11, int index12, int index21, int index22, Color color) {
        highliter.highlight(po1TextArea, index11, index12, color);
        highliter.highlight(po2TextArea, index21, index22, color);
    }

    private Color getDiffsColor() {
        return diffsColor;
    }

    private Color getDiffsCurrColor() {
        return diffsCurrColor;
    }

    private Color getSearchColor() {
        return searchColor;
    }

    private Color getSearchFoundColor() {
        return searchFoundColor;
    }

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        highliterSearch.highlightremove(po1TextArea);
        highliterSearch.highlightText1(po1TextArea, searchField.getText(), searchColor, searchFoundColor);
        highliterSearch.highlightremove(po2TextArea);
        highliterSearch.highlightText2(po2TextArea, searchField.getText(), searchColor, searchFoundColor);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void ForFindBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ForFindBTNActionPerformed
        highliterSearch.highlightremove(po1TextArea);
        highliterSearch.highlightremove(po2TextArea);
        highliterSearch.highlightText1(po1TextArea, searchField.getText(), searchColor, searchFoundColor);
        highliterSearch.highlightText2(po2TextArea, searchField.getText(), searchColor, searchFoundColor);
    }//GEN-LAST:event_ForFindBTNActionPerformed

    private void BackFindBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackFindBTNActionPerformed
        highliterSearch.highlightremove(po1TextArea);
        highliterSearch.highlightremove(po2TextArea);
        highliterSearch.backhighlightText1(po1TextArea, searchField.getText(), searchColor, searchFoundColor);
        highliterSearch.backhighlightText2(po2TextArea, searchField.getText(), searchColor, searchFoundColor);
    }//GEN-LAST:event_BackFindBTNActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText("");
    }//GEN-LAST:event_searchFieldMouseClicked

    /**
     * Moves the current Highlight (different color) to the next difference if
     * forBack is true or to the previous difference if forBack is false. It
     * removes all highlights and repaints them for current difference will have
     * a different color
     *
     * @param forBack boolean for forward or backward move
     */
    private void moveHighlight(boolean forBack) {
        if (forBack) {
            if (currDiff < diffs1.size() - 1) {
                currDiff++;
            }
        } else if (currDiff > 0) {
            currDiff--;
        }
        highliter.highlightremove(po1TextArea);
        highliter.highlightremove(po2TextArea);
        for (int i = 0; i < diffs1.size(); i++) {
            if (i == currDiff) {
                highlightDiffs(diffs1.get(i)[0], diffs1.get(i)[1], diffs2.get(i)[0], diffs2.get(i)[1], diffsCurrColor);
            } else {
                highlightDiffs(diffs1.get(i)[0], diffs1.get(i)[1], diffs2.get(i)[0], diffs2.get(i)[1], diffsColor);
            }
        }
        if (diffs1.isEmpty()) {
            return;
        }
        po1TextArea.select(diffs1.get(currDiff)[0], diffs1.get(currDiff)[1]);
        po2TextArea.select(diffs2.get(currDiff)[0], diffs2.get(currDiff)[1]);

    }
    private void nextHiliteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextHiliteBTNActionPerformed
        moveHighlight(true);
    }//GEN-LAST:event_nextHiliteBTNActionPerformed

    private void prevHiliteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevHiliteBTNActionPerformed
        moveHighlight(false);
    }//GEN-LAST:event_prevHiliteBTNActionPerformed

    private void colorDifBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorDifBTNActionPerformed
        JFrame frame = new JFrame("JColorChooser");
        Color newColor = jColorChooser1.showDialog(
                frame,
                "Choose Background Color",
                colorDifBTN.getBackground());
        if (newColor != null) {
            diffsColor = newColor;
            colorDifBTN.setBackground(diffsColor);
        }
    }//GEN-LAST:event_colorDifBTNActionPerformed

    private void colorSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSearchBTNActionPerformed
        JFrame frame = new JFrame("JColorChooser");
        Color newColor = jColorChooser1.showDialog(
                frame,
                "Choose Background Color",
                colorSearchBTN.getBackground());
        if (newColor != null) {
            searchColor = newColor;
            colorSearchBTN.setBackground(searchColor);
        }
    }//GEN-LAST:event_colorSearchBTNActionPerformed

    private void colorSearchFoundBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSearchFoundBTNActionPerformed
        JFrame frame = new JFrame("JColorChooser");
        Color newColor = jColorChooser1.showDialog(
                frame,
                "Choose Background Color",
                colorSearchFoundBTN.getBackground());
        if (newColor != null) {
            searchFoundColor = newColor;
            colorSearchFoundBTN.setBackground(searchFoundColor);
        }
    }//GEN-LAST:event_colorSearchFoundBTNActionPerformed

    private void colorCurrDifBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorCurrDifBTNActionPerformed
        JFrame frame = new JFrame("JColorChooser");
        Color newColor = jColorChooser1.showDialog(
                frame,
                "Choose Background Color",
                colorCurrDifBTN.getBackground());
        if (newColor != null) {
            diffsCurrColor = newColor;
            colorCurrDifBTN.setBackground(diffsCurrColor);
        }
    }//GEN-LAST:event_colorCurrDifBTNActionPerformed


    private void compList2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_compList2FocusLost

    }//GEN-LAST:event_compList2FocusLost

    private void commListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_commListFocusLost
        //IXGKOAG - Update SelectedCommands ArrayList in Compare Object
        cmp.updateSelectedCommands(commList);
    }//GEN-LAST:event_commListFocusLost


    private void BaseNodesComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BaseNodesComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BaseNodesComboActionPerformed

    private void fontBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontBTNActionPerformed
        JFontChooser font = new JFontChooser(po1TextArea.getFont());
        font.showDialog(settingsDialog);

        po1TextArea.setFont(font.getSelectedFont());
        po2TextArea.setFont(font.getSelectedFont());
    }//GEN-LAST:event_fontBTNActionPerformed

    private void BaseNodesComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_BaseNodesComboItemStateChanged
        resetValidatePOs();
        
        
        TargetNodesTree.updateUI();
        po1TextArea.setText("");
        po2TextArea.setText("");
                
        //IXGKOAG
//        this.TargetNodesTree.setModel(null);
//        String item = this.BaseNodesCombo.getSelectedItem().toString();
//        cmp.updateTargetNodesTree(item);
//        this.TargetNodesTree.setModel(new javax.swing.tree.DefaultTreeModel(commsTreeModel));
//        expandTreeAll();
//        this.TargetNodesTree.repaint();
    }//GEN-LAST:event_BaseNodesComboItemStateChanged

    private void BaseNodesComboFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BaseNodesComboFocusLost
    }//GEN-LAST:event_BaseNodesComboFocusLost

    private void TargetNodesTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_TargetNodesTreeValueChanged
        //IXGKOAG
//        TreePath selectedPath = this.TargetNodesTree.getSelectionPath();

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) TargetNodesTree
                .getLastSelectedPathComponent();
        if (mainTabbedPane.getSelectedIndex() == 2) {
            if (selectedNode != null) {
                String base = BaseNodesCombo.getSelectedItem().toString();
                String target = selectedNode.getParent().toString();
                String command = selectedNode.toString();
                cmp.nodeSelected(base, target, command, po1TextArea, po2TextArea);
                doAnalysis();
            }
        }
        
        // CHMA - GGEW - SOVL
        // Information shown in the information tab
        
        if (mainTabbedPane.getSelectedIndex() == 0) {
            if (selectedNode != null) {
                StringBuilder infoText = new StringBuilder();
                if (selectedNode.isLeaf()){
                    String selectedNodeName = selectedNode.toString();
                    File node = new File(getPath(selectedNode.getParent().toString()));
                    infoText.append("**************************************************\n");
                    infoText.append("               NODE INFORMATION             \n");
                    infoText.append("**************************************************\n\n");
                    infoText.append("Node: ");
                    infoText.append(node.getName() + " - " + selectedNodeName + "\n");
                    infoText.append("---------------------------------------------\n");
                    infoText = TextAppender(infoText, node, selectedNodeName);
                } else {
                    if (selectedNode.getParent() == TargetNodesTree.getModel().getRoot()){
                        File root = rootOutFolder;
                        FilenameFilter nameFilter;
                        if (selectedNode.toString().startsWith("MSC")){
                            nameFilter = new MSCFilter();
                            infoText.append("******************************************\n");
                            infoText.append("               ALL MSC NODES\n");
                            infoText.append("******************************************\n\n");
                        } else {
                            nameFilter = new HLRFilter();
                            infoText.append("******************************************\n");
                            infoText.append("               ALL HLR NODES" + "\n");
                            infoText.append("******************************************\n\n");
                        }
                        File[] nodes = root.listFiles(nameFilter);
                        if (nodes.length != 0) {
                            for (File node : nodes){
                                infoText.append("---------------------------------------------\n");
                                infoText.append("Node: ");
                                infoText.append(node.getName() + "\n");
                                infoText.append("---------------------------------------------\n");
                                infoText = TextAppender(infoText, node, null);
                            }
                        } else {
                            infoText.append("No available data for this type of node");
                        }
                    } else {
                        File node = new File(getPath(selectedNode.toString()));
                        infoText.append("**************************************************\n");
                        infoText.append("               NODE INFORMATION             \n");
                        infoText.append("**************************************************\n\n");
                        infoText.append("Node: ");
                        infoText.append(node.getName() + "\n");
                        infoText.append("---------------------------------------------\n");
                        infoText = TextAppender(infoText, node, null);
                    }
                } 
                infoTextArea.setText(infoText.toString()); 
            }
        }
    }//GEN-LAST:event_TargetNodesTreeValueChanged

    
    // CHMA - GGEW - SOVL
    /**
     * This method is used to append node information text via executing loops 
     * in the Mainframe's TextArea.
     *
     * @param text is the StringBuilder containing the output so far.
     * @param node is the file of current node of the tree that is being processed 
     * @param selectedNodeName is the name of the selected node (valid only for blades)
     */
    
    private StringBuilder TextAppender (StringBuilder text, File node, String selectedNodeName) {
        FileFilter dirFilter = new DirFilter();
        File[] timestamps = node.listFiles(dirFilter);
        if (timestamps.length != 0){
            if (selectedNodeName == null) {
                text.append("Available timestaps, blades and commands per timestamp:\n");
            } else {
                text.append("Available timestaps and commands per timestamp:\n");
            }
            for (File stamp: timestamps){
                if (selectedNodeName == null) {
                    text.append("-Timestamp: " + stamp.getName() + "\n");
                }
                File[] blades = stamp.listFiles(dirFilter);
                    if (blades.length != 0){
                        boolean bladeFound = false;
                        for (File blade : blades){
                            if (selectedNodeName != null) {
                                if (blade.getName().equals(selectedNodeName)){
                                    bladeFound = true;
                                    text.append("-Timestamp: " + stamp.getName() + "\n");
                                    File[] commands = blade.listFiles();
                                    if (commands.length != 0) {
                                        for (File command : commands){
                                            text.append("  -" + command.getName() + "\n");
                                        }
                                    } else {
                                        text.append("No available data for this timestamp\n");
                                    }
                                }
                            } else {
                                text.append(blade.getName() + "\n");
                                File[] commands = blade.listFiles();
                                if (commands.length != 0){
                                    for (File command : commands){
                                        text.append("  -" + command.getName() + "\n");
                                    }
                                } else {
                                    text.append("No available commands for this blade\n");
                                }
                            }
                        }
                        if ((!bladeFound) && (selectedNodeName != null)){
                            text.append("No available data for this timestamp\n");
                        }
                    } else {
                        text.append("No available data for this timestamp\n");
                    }
                    text.append("\n");
                } 
            } else {
                text.append("No available data for this node\n");    
            }
        return text;
    } 
    
    
    private void applyChooseRefBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyChooseRefBTNActionPerformed
        chooseRef(refChooseList.getSelectedValue());

        chooseFromRefDialog.setVisible(false);
    }//GEN-LAST:event_applyChooseRefBTNActionPerformed

    private void chooseRef(String selectedRef) {
        if (selectedRef == null) {
            return;
        }
        if (chooseRefLBL1.getText().equals("Base")) {
            TimeStampBase.add(selectedRef);
            insertElem(compList1Model);
            arrayCommList = cmp.updateBaseNodes(compList1, TimeStampBase);
        } else {
            TimeStampTarget.add(selectedRef);
            insertElem(compList2Model);
            arrayCommList = cmp.updateTargetNodes(compList2, TimeStampTarget);
        }
        createCommsList();

    }
    private void cancelChooseRefBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelChooseRefBTNActionPerformed
        chooseFromRefDialog.setVisible(false);
    }//GEN-LAST:event_cancelChooseRefBTNActionPerformed

    private void commListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_commListMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_commListMouseClicked

    private void commListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_commListMouseReleased

    }//GEN-LAST:event_commListMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        errorDialog.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void TargetNodesTreePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_TargetNodesTreePropertyChange

    }//GEN-LAST:event_TargetNodesTreePropertyChange

    private void saveAllBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllBTNActionPerformed

        if (mainTabbedPane.getSelectedIndex() != 2) {
            errorMessageLBL.setText("Go to results panel to save");
            errorDialog.setVisible(true);
            return;
        }
        
        // CHMA - GGEW - SOVL 
        
        if (saveAllIndic){
            errorMessageLBL.setText("Already Saving");
            errorDialog.setVisible(true);
            return;
        }
        
        Object root = TargetNodesTree.getModel().getRoot();
        if (TargetNodesTree.getModel().getChildCount(root) == 0) {
            errorMessageLBL.setText("Node/command selection not made");
            errorDialog.setVisible(true);
            return;
        }
        
        saveAllChooser = new JFileChooser(rootOutFolder);
        saveAllChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (saveAllChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {       

            SaveWorker sworker = new SaveWorker();
            sworker.execute();       
            ProgressWorker pworker = new ProgressWorker();
            pworker.execute();
        }

    }//GEN-LAST:event_saveAllBTNActionPerformed

    // CHMA - GGEW - SOVL
    /**
     * This method is used to find the total number of files that are needed to 
     * be saved when the differences between each target and base node per command
     * are to be saved.
     *
     * @param num is the number of files that are found so far.
     * @param node is the current node of the tree that is being processed
     * @param model is the model of the Target nodes tree
     */
    private int findNumOfFiles(int num, Object node, TreeModel model){
        int cc;
        cc = model.getChildCount(node);
        for (int i = 0; i < cc; i++) {
            Object child = model.getChild(node, i);
            if (model.isLeaf(child)) {
                for (int j = 0; j < BaseNodesCombo.getItemCount(); j++) {
                    num+=1;
                }
            } else {
                num = findNumOfFiles(num, child, model);
            }
        }
        return num;
    }
    
    /**
     * This method is used to find the commands in the target nodes tree and for
     * each combination of base node, target node and command to create a new
     * file in the directory selected from the file chooser. In this file the
     * differences between the command printouts will be stored.
     *
     * @param chooser is the file chooser from which the directory to store the
     * files is selected.
     * @param model is the model of the Target nodes tree
     * @param node is the current node of the tree that is being processed
     * @param write is the writer to the results file
     */
    private void traverseTree(JFileChooser chooser, TreeModel model, Object node, BufferedWriter write) {
        int cc;
        cc = model.getChildCount(node);
        for (int i = 0; i < cc; i++) {
            Object child = model.getChild(node, i);
            if (model.isLeaf(child)) {
                for (int j = 0; j < BaseNodesCombo.getItemCount(); j++) {
                    String base = BaseNodesCombo.getItemAt(j);
                    String target = node.toString();
                    String command = child.toString();
                    String[] pos = cmp.nodeSelected(base, target, command);
                    ArrayList<ArrayList<int[]>> diffs = findDiffs(pos[0],pos[1]);
                    if (!diffs.get(0).isEmpty()) {
                        String refs[] = cmp.getCommandReferences(base, target, command);

                        try {
                            write.write(refs[0].replace('\\', '_') + "__" + refs[1].replace('\\', '_') + "_" + command + " Differences found \n");
                            File f = new File(chooser.getSelectedFile(), refs[0].replace('\\', '_') + "__" + refs[1].replace('\\', '_') + "_" + command + ".txt");
                            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                            int[] lastLine = new int[]{-1, -1};
                            int diffCount = 1;
                            for (int k = 0; k < diffs.get(0).size(); k++) {
                                int tempLine[] = writeDiffToFile(writer, diffs.get(0).get(k), diffs.get(1).get(k), lastLine, diffCount, refs, pos);
                                if ((tempLine[0] != lastLine[0]) || (tempLine[1] != lastLine[1])) {
                                    diffCount++;
                                }
                                lastLine = tempLine;
                            }
                            writer.close();
                            Thread.sleep(100);
                        } catch (IOException ex) {
                            saveAllIndic = false;
                            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                        catch (InterruptedException ex) {
                            saveAllIndic = false;
                            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            String refs[] = cmp.getCommandReferences(base, target, command);
                            write.write(refs[0].replace('\\', '_') + "__" + refs[1].replace('\\', '_') + "_" + command + " Differences not found\n");
                            Thread.sleep(100);
                        } catch (IOException ex) {
                            saveAllIndic = false;
                            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                        catch (InterruptedException ex) {
                            saveAllIndic = false;
                            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                    filesDone++;
                }
            } else {
                traverseTree(chooser, model, child, write);
            }
        }
    }

    private void prevDiffMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevDiffMNActionPerformed
        moveHighlight(false);
    }//GEN-LAST:event_prevDiffMNActionPerformed

    private void po1TextAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_po1TextAreaMouseReleased
        if (evt.isPopupTrigger()) {
            textPopUpMN.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_po1TextAreaMouseReleased

    private void po2TextAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_po2TextAreaMouseReleased
        if (evt.isPopupTrigger()) {
            textPopUpMN.show(evt.getComponent(), evt.getX(), evt.getY());
        }

    }//GEN-LAST:event_po2TextAreaMouseReleased

    private void CopyPopUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyPopUpActionPerformed

//        javax.swing.JTextArea source = (javax.swing.JTextArea) evt.getSource();
//        Action actions = new DefaultEditorKit.CopyAction();
//        String text = po1TextArea.getSelectedText();
//        StringSelection stsel = new StringSelection(text);
//        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
//        system.setContents(stsel, stsel);
    }//GEN-LAST:event_CopyPopUpActionPerformed

    private void nextDiffMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextDiffMNActionPerformed
        moveHighlight(true);
    }//GEN-LAST:event_nextDiffMNActionPerformed

    private void applyChooseRefBTNKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_applyChooseRefBTNKeyPressed
        if (evt.getKeyCode() == 10) {
            chooseRef(refChooseList.getSelectedValue());
            chooseFromRefDialog.setVisible(false);
        }
    }//GEN-LAST:event_applyChooseRefBTNKeyPressed

    private void chooseDataFolderBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseDataFolderBTNActionPerformed
        fileChooser.setDialogTitle("Select the Data Folder");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(1);

        fileChooser.showOpenDialog(this);
        try {
            File parentFile = fileChooser.getSelectedFile();
            if (parentFile.getName().equals("Data")) {
                chooseDataFolderTxt.setText(parentFile.getParent());
                return;
            }
            errorMessageLBL.setText("Data folder not found");
            errorDialog.setVisible(true);
            return;
        } catch (Exception e) {
            if (rootInputFolder.getName() == "") {
                System.out.println("No folder selected!");
            }
        }
    }//GEN-LAST:event_chooseDataFolderBTNActionPerformed


    private void analysisMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analysisMNActionPerformed
        performAnalysis();
    }//GEN-LAST:event_analysisMNActionPerformed

    private void aboutMNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMNActionPerformed
        aboutFrame.setVisible(true);
    }//GEN-LAST:event_aboutMNActionPerformed

    private void po1ScrollPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_po1ScrollPaneMouseReleased
//po1ScrollPane.getVerticalScrollBar().setModel(po1ScrollPane.getVerticalScrollBar().getModel());


    }//GEN-LAST:event_po1ScrollPaneMouseReleased

    private void lastTimeCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastTimeCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastTimeCheckActionPerformed

    private void concurendScrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concurendScrollButtonActionPerformed

        this.concurendScroll = concurendScrollButton.isSelected();
        System.out.println("concurendScroll is " + this.concurendScroll);

        if (this.concurendScroll) {
            //change color of this button
            this.concurendScrollButton.setForeground(diffsColor);
            this.concurendScrollButton.setBackground(new java.awt.Color(0,102,0));
            //Resize Panel
            int splitPaneHeight = diffSplitPane.getHeight();
            diffSplitPane.setDividerLocation(splitPaneHeight / 2);
            //Set to first line the print outs
            po1ScrollPane.getVerticalScrollBar().setValue(0);
            po2ScrollPane.getVerticalScrollBar().setValue(0);
            //equalize models
            po2ScrollPane.getVerticalScrollBar().setModel(po1ScrollPane.getVerticalScrollBar().getModel());
            po1ScrollPane.getVerticalScrollBar().setModel(po2ScrollPane.getVerticalScrollBar().getModel());
        } else {
            //change color of this button
            this.concurendScrollButton.setForeground(new java.awt.Color(0,0,0));
            this.concurendScrollButton.setBackground(new java.awt.Color(212,208,200));
            //create dummy JscrollBars
            javax.swing.JScrollPane ScrollPanex1 = new javax.swing.JScrollPane();
            BoundedRangeModel DefaultScrollmodel1 = ScrollPanex1.getVerticalScrollBar().getModel();
            javax.swing.JScrollPane ScrollPanex2 = new javax.swing.JScrollPane();
            BoundedRangeModel DefaultScrollmodel2 = ScrollPanex2.getVerticalScrollBar().getModel();
            //Assign them Model to existing ones
            po2ScrollPane.getVerticalScrollBar().setModel(DefaultScrollmodel1);
            po1ScrollPane.getVerticalScrollBar().setModel(DefaultScrollmodel2);
        }

    }//GEN-LAST:event_concurendScrollButtonActionPerformed


    private void po2ScrollPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_po2ScrollPaneMouseReleased

    }//GEN-LAST:event_po2ScrollPaneMouseReleased

    private void diffSplitPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_diffSplitPaneComponentResized
        if(this.concurendScroll){
        int splitPaneHeight = diffSplitPane.getHeight();
        diffSplitPane.setDividerLocation(splitPaneHeight / 2);
        }
    }//GEN-LAST:event_diffSplitPaneComponentResized

    private void compList1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compList1MouseEntered
        compList1.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent me) {
                Point p = new Point(me.getX(),me.getY());
                compList1.setSelectedIndex(compList1.locationToIndex(p));
                if (!compList1.isSelectionEmpty()){
                    compList1.setToolTipText(TimeStampBase.get(compList1.getSelectedIndex()));
                }
            }
        });
    }//GEN-LAST:event_compList1MouseEntered

    private void compList2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compList2MouseEntered
        compList2.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent me) {
                Point p = new Point(me.getX(),me.getY());
                compList2.setSelectedIndex(compList2.locationToIndex(p));
                if (!compList2.isSelectionEmpty()){
                    compList2.setToolTipText(TimeStampTarget.get(compList2.getSelectedIndex()));
                }
            }
        });
    }//GEN-LAST:event_compList2MouseEntered
  
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
    private javax.swing.JButton AnalysisBTN;
    private javax.swing.JButton BackFindBTN;
    private javax.swing.JComboBox<String> BaseNodesCombo;
    private javax.swing.JMenuItem CopyPopUp;
    private javax.swing.JButton ForFindBTN;
    private javax.swing.JMenuItem OpenMN;
    private javax.swing.JMenuItem SaveMN;
    private javax.swing.JTree TargetNodesTree;
    private javax.swing.JFrame aboutFrame;
    private javax.swing.JMenuItem aboutMN;
    private javax.swing.JButton aboutOkBTN;
    private javax.swing.JPanel aboutPanel;
    private javax.swing.JScrollPane aboutScrollPane;
    private javax.swing.JButton aboutTLB;
    private javax.swing.JTextArea aboutTextArea;
    private javax.swing.JMenuItem analysisMN;
    private javax.swing.JButton applyChooseRefBTN;
    private javax.swing.JButton applySettingsBTN;
    private javax.swing.JButton cancelChooseRefBTN;
    private javax.swing.JButton chooseDataFolderBTN;
    private javax.swing.JLabel chooseDataFolderLBL;
    private javax.swing.JTextField chooseDataFolderTxt;
    private javax.swing.JDialog chooseFromRefDialog;
    private javax.swing.JLabel chooseRefLBL;
    private javax.swing.JLabel chooseRefLBL1;
    private javax.swing.JButton clear1BTN;
    private javax.swing.JButton clear2BTN;
    private javax.swing.JButton colorCurrDifBTN;
    private javax.swing.JLabel colorCurrDifLBL;
    private javax.swing.JButton colorDifBTN;
    private javax.swing.JLabel colorDifLBL;
    private javax.swing.JButton colorSearchBTN;
    private javax.swing.JButton colorSearchFoundBTN;
    private javax.swing.JLabel colorSearchFoundLBL;
    private javax.swing.JLabel colorSearchLBL;
    private javax.swing.JList<String> commList;
    private javax.swing.JList<String> compList1;
    private javax.swing.JList<String> compList2;
    private javax.swing.JPanel comparePanel;
    private javax.swing.JToggleButton concurendScrollButton;
    private javax.swing.JSplitPane diffSplitPane;
    private javax.swing.JButton discardSettingsBTN;
    private javax.swing.JMenu editMN;
    private javax.swing.JDialog errorDialog;
    private javax.swing.JLabel errorMessageLBL;
    private javax.swing.JMenuItem exitMN;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMN;
    private javax.swing.JButton fontBTN;
    private javax.swing.JLabel fontLBL;
    private javax.swing.JMenu helpMN;
    private javax.swing.JLabel infoNameLBL;
    private javax.swing.JScrollPane infoPanel;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JButton insertElem1BTN;
    private javax.swing.JButton insertElem2BTN;
    private javax.swing.JButton jButton1;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JCheckBox lastTimeCheck;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollTab3;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton nextBTN;
    private javax.swing.JMenuItem nextDiffMN;
    private javax.swing.JButton nextHiliteBTN;
    private javax.swing.JScrollPane nodesScrollPane;
    private javax.swing.JButton openTLB;
    private javax.swing.JScrollPane po1ScrollPane;
    private javax.swing.JTextArea po1TextArea;
    private javax.swing.JScrollPane po2ScrollPane;
    private javax.swing.JTextArea po2TextArea;
    private javax.swing.JButton prevBTN;
    private javax.swing.JMenuItem prevDiffMN;
    private javax.swing.JButton prevHiliteBTN;
    private javax.swing.JList<String> refChooseList;
    private javax.swing.JButton removeElem1BTN;
    private javax.swing.JButton removeElem2BTN;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JButton saveAllBTN;
    private javax.swing.JMenuItem saveAllMN;
    private javax.swing.JButton saveTLB;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel settingPanel1;
    private javax.swing.JPanel settingPanel2;
    private javax.swing.JDialog settingsDialog;
    private javax.swing.JMenuItem settingsMN;
    private javax.swing.JButton sidebarBTN;
    private javax.swing.JPopupMenu textPopUpMN;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
