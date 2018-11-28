//IXGKOAG
//1. Collect Select Commands from Command List 
//2. 
package juxtanetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

public class Compare {

    JComboBox<String> BaseNodesCombo;
    DefaultMutableTreeNode commsTreeModel;

    String fileSeperator = System.getProperty("file.separator");
    private String LogsDirectory = "Data";
//    private String timeStampBase = "2018-10-11_16_42_17";  // will be retrieved later
//    private String timeStampTarget = "2018-10-11_16_42_18";  // will be retrieved later
    ArrayList<String> TimeStampBase = new ArrayList<String>();
    ArrayList<String> TimeStampTarget = new ArrayList<String>();

    private ArrayList<String> selectedCommands = new ArrayList<String>();
    private ArrayList<String> BaseNodes = new ArrayList<String>();
    private ArrayList<String> TargetNodes = new ArrayList<String>();
//============
    // Hash Maps to store Hieradically information
    // <BaseNode,<TargetNode,<CommandName,Command>>>
    private HashMap<String, HashMap<String, HashMap<String, Command>>> Structure = new HashMap<String, HashMap<String, HashMap<String, Command>>>();
//============

    /**
     * IXGKOAG Compare Constructor
     *
     * @param BaseNodesCombo
     * @param commsTreeModel
     */
    Compare(JComboBox<String> BaseNodesCombo, DefaultMutableTreeNode commsTreeModel) {
        this.BaseNodesCombo = BaseNodesCombo;
        this.commsTreeModel = commsTreeModel;
    }

    //Update Selected Commands ArrayList
    void updateSelectedCommands(JList<String> commList) {
        selectedCommands.clear();
        //This item should be selected Last 
        if ((this.BaseNodes.isEmpty()) || (this.TargetNodes.isEmpty())) {
            commList.clearSelection();
            return;
        }

        int[] selectedIndex = commList.getSelectedIndices();
        for (int i = 0; i < selectedIndex.length; i++) {
            selectedCommands.add(commList.getModel().getElementAt(selectedIndex[i]));
        }
        System.out.println("Selected Commads : " + selectedCommands);
        prepareHashStructure();
    }

    //Update BaseNodes ArrayList
    //Update to return the ArrayList to Mainframe
    ArrayList<String> updateBaseNodes(JList<String> compList1, ArrayList<String> TimeStampBase) {
        BaseNodes.clear();
        for (int i = 0; i < compList1.getModel().getSize(); i++) {
            BaseNodes.add(compList1.getModel().getElementAt(i));
        }
        this.TimeStampBase = TimeStampBase;
        prepareHashStructure();

        return findCommonCommands();
    }

    //Update TargetNodes ArrayList
    //Update to return the ArrayList to Mainframe
    ArrayList<String> updateTargetNodes(JList<String> compList2, ArrayList<String> TimeStampTarget) {
        TargetNodes.clear();
        for (int i = 0; i < compList2.getModel().getSize(); i++) {
            TargetNodes.add(compList2.getModel().getElementAt(i));
        }
        this.TimeStampTarget = TimeStampTarget;
        prepareHashStructure();

        return findCommonCommands();
    }

    //method for creating the overall common command list
    private ArrayList<String> findCommonCommands() {
        ArrayList<String> baseCommands = new ArrayList();
        ArrayList<String> targetCommands = new ArrayList();
        ArrayList<String> commands = new ArrayList();
        int iBase = 0;

        // Create Base nodes common commands
        for (String baseNode : BaseNodes) {
            commands.clear();
//            String basePath = getPath(baseNode, timeStampBase);
            String basePath = getPath(baseNode, TimeStampBase.get(iBase));
            File base = new File(basePath);
            for (File f : base.listFiles()) {
                int pos = f.getName().lastIndexOf(".");
                if (pos > 0) {
                    commands.add(f.getName().substring(0, pos));
                } else {
                    commands.add(f.getName());
                }
            }
            baseCommands = compareCommands(baseCommands, commands);
            iBase++;
        }

        int iTarget = 0;
        // Create Target nodes common commands
        for (String targetNode : TargetNodes) {
            commands.clear();
//            String targetPath = getPath(targetNode, timeStampTarget);
            String targetPath = getPath(targetNode, TimeStampTarget.get(iTarget));
            File target = new File(targetPath);
            for (File f : target.listFiles()) {
                int pos = f.getName().lastIndexOf(".");
                if (pos > 0) {
                    commands.add(f.getName().substring(0, pos));
                } else {
                    commands.add(f.getName());
                }
            }
            targetCommands = compareCommands(targetCommands, commands);
            iTarget++;
        }

        // Return overall common command list
        return compareCommands(baseCommands, targetCommands);
    }

    //method for comparing two Command lists and return the common list
    private ArrayList<String> compareCommands(ArrayList<String> listOne, ArrayList<String> listTwo) {
        ArrayList<String> similar = new ArrayList<String>();
        similar.clear();
        if (listOne.isEmpty()) {
            if (!listTwo.isEmpty()) {
                similar.addAll(listTwo);
            }
        } else if (listTwo.isEmpty()) {
            similar.addAll(listOne);
        } else {
            listOne.retainAll(listTwo);
            similar = listOne;
        }

        return similar;
    }

    //Update timeStampBase String
    void updateTimeStampBase(JList<String> compList2) {
        // To be implemented when timestamp selection implemented

    }

    //Update timeStampBase String
    void updateTimeStampTarget(JList<String> compList2) {
        // To be implemented when timestamp selection implemented
    }

//get Path to  Files
    String getPath(String node, String time) {
        // check if node name contains / or slash or dash in case of cluster node, to be agreed
        String[] items = node.split("\\/");
        String path = "";
        if (items.length > 1) {
            path = LogsDirectory + fileSeperator + items[0] + fileSeperator + time + fileSeperator + items[1];
        } else {
            path = LogsDirectory + fileSeperator + node + fileSeperator + time + fileSeperator;
        }
        System.out.println("Current path: " + path);
        return path;
    }

    void prepareHashStructure() {
        //======Check if all data exists, do something.. e.g. popup
        if (selectedCommands.size() == 0) {
            return;
        }
        if (BaseNodes.size() == 0) {
            return;
        }
        if (TargetNodes.size() == 0) {
            return;
        }
//        if (timeStampBase.equals("")) {
        if (TimeStampBase.size() == 0) {
            return;
        }
//        if (timeStampTarget.equals("")) {
        if (TimeStampTarget.size() == 0) {
            return;
        }
//=============================================
//Clear Structure HashMap
        Structure.clear();
//--- Create Structure HashMap with All needed info to
//    construct GUI items and Calculate Data
        int iBase = 0;

        for (String baseNode : BaseNodes) {
//            String BasePath = getPath(baseNode, timeStampBase);
            String BasePath = getPath(baseNode, TimeStampBase.get(iBase));
            HashMap<String, HashMap<String, Command>> TargetCommands = new HashMap<String, HashMap<String, Command>>();

            int iTarget = 0;
            for (String targetNode : TargetNodes) {
//                String TargetPath = getPath(targetNode, timeStampTarget);
                String TargetPath = getPath(targetNode, TimeStampTarget.get(iTarget));
                HashMap<String, Command> Commands = new HashMap<String, Command>();

                for (String command : selectedCommands) {
                    String BaseFile = BasePath + fileSeperator + command;
                    String TargetFile = TargetPath + fileSeperator + command;

                    //=== If both Files exist
                    File f1 = new File(BaseFile);
                    File f2 = new File(TargetFile);

                    if ((f1.exists() && f1.isFile()) && (f2.exists() && f2.isFile())) {
                        //create CommandObject and appendit to the targetNode Commands
                        Command CommandObject = new Command(command, f1, f2);
                        // Perform Some Tasks in Command Object
                        Commands.put(command, CommandObject);
                        //==========================================================
                        // IXGKOAG - MOD AFTER COMMENT FOR CONCURRENT SCROLLING
                        CommandObject.getPOtexts();
                        int diff = CommandObject.getPrintOutLines() - CommandObject.getPrintOut2Lines();

                        if (diff > 0) {
                            String po2 = CommandObject.getPrintOut2();
                            while (diff > 0) {
                                po2 = po2 + "\n";
                                //po2.concat("\n");
                                diff--;
                            }
                            CommandObject.setPrintOut2(po2);
                        }
                        if (diff < 0) {
                            String po1 = CommandObject.getPrintOut();
                            while (0 >= diff) {
                                po1 = po1 + "\n";
                                //po1.concat("\n"); 
                                diff++;
                            }
                            CommandObject.setPrintOut(po1);
                        }
                        // END MOD  

                    }
                }
                TargetCommands.put(targetNode, Commands);
                iTarget++;
            }
            Structure.put(baseNode, TargetCommands);
            iBase++;
        }
        //Hash prepared do additional Stuff 
        updateBaseNodesCombo();
        //updateTargetNodesTree();
    }

    public void updateBaseNodesCombo() {
        this.BaseNodesCombo.removeAllItems();
        for (String key : Structure.keySet()) {
            this.BaseNodesCombo.addItem(key);
        }
        // this.BaseNodesCombo.setSelectedIndex(0);
    }

//public HashMap<String, HashMap<String, HashMap<String, Command>>> getStructure(){
//    return Structure;
//}
    public void updateTargetNodesTree(String selectedNode) {
        if (selectedNode.equals("")) {
            return;
        }
        System.out.println("Selected Base Node :" + selectedNode);

        //Clear tree view Model
        this.commsTreeModel.removeAllChildren();

        // Get the CompareTo Target Nodes Based on User Selection  
        HashMap<String, HashMap<String, Command>> TargetNodes = this.Structure.get(selectedNode);

        //treeview
        int currIndex1 = 0;
        int currIndex2 = 0;
        DefaultMutableTreeNode[] nodesTreeModel = new DefaultMutableTreeNode[100];
        DefaultMutableTreeNode[] commandsTreeModel = new DefaultMutableTreeNode[100];

        //For each Node
        for (String CompareNode : TargetNodes.keySet()) {
            System.out.println("Target Node :" + CompareNode);
            //Append TargetNode Element
            nodesTreeModel[currIndex1] = new DefaultMutableTreeNode(CompareNode);
            this.commsTreeModel.add(nodesTreeModel[currIndex1]);

            //For each CompareTo Target Node get Commands
            HashMap<String, Command> TargetCommands = TargetNodes.get(CompareNode);
            for (String CommandOnNode : TargetCommands.keySet()) {
                commandsTreeModel[currIndex2] = new DefaultMutableTreeNode(CommandOnNode);
                nodesTreeModel[currIndex1].add(commandsTreeModel[currIndex2]);
                currIndex2++;
            }
            currIndex1++;
        }
    }

    public void nodeSelected(String base, String target, String comm, JTextArea po1TextArea, JTextArea po2TextArea) {
//    String test = Structure.get(base).get(target).get(comm).getName();
//        System.out.println("command name= "+test);
        if (comm.isEmpty()) {
            return;
        }
        if (target.isEmpty()) {
            return;
        }
        if (base.isEmpty()) {
            return;
        }
        try {
            Command comm1 = Structure.get(base).get(target).get(comm);
            // comm1.getPOtexts(); Moved to prepareHashStructure()
            String po1 = comm1.getPrintOut();
            String po2 = comm1.getPrintOut2();
            po1TextArea.setText(po1);
            po2TextArea.setText(po2);
        } catch (Exception e) {
            System.out.println("No command selected");
        }
    }

    // CHMA-GGEW-SOVL
    /**
    * Method for getting the paths where the command file for both base and target 
    * node is located.
    *
    * @param base is the name of the base node
    * @param target is the name of the target node
    * @param command is the name of the command that is processed
    *
    * @return the paths where the command file for base and target node is stored
    */    
    public String[] getCommandReferences(String base, String target, String command) {
        Command comm = Structure.get(base).get(target).get(command);
        String[] paths = new String[2];
        paths[0] = comm.getBasePath();
        paths[1] = comm.getTargetPath();
        return paths;
    }

}
