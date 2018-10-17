//IXGKOAG
//1. Collect Select Commands from Command List 
//2. 
package juxtanetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Compare {

    JComboBox<String> BaseNodesCombo;
    DefaultMutableTreeNode commsTreeModel;

    String fileSeperator = "/";
    private String LogsDirectory = "Data";
    private String timeStampBase = "2018-10-11_16_42_17";  // will be retrieved later
    private String timeStampTarget = "2018-10-11_16_42_18";  // will be retrieved later

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
        int[] selectedIndex = commList.getSelectedIndices();
        for (int i = 0; i < selectedIndex.length; i++) {
            selectedCommands.add(commList.getModel().getElementAt(selectedIndex[i]));
        }
        System.out.println("Selected Commads : " + selectedCommands);
        prepareHashStructure();
    }

    //Update BaseNodes ArrayList
    void updateBaseNodes(JList<String> compList1) {
        BaseNodes.clear();
        for (int i = 0; i < compList1.getModel().getSize(); i++) {
            BaseNodes.add(compList1.getModel().getElementAt(i));
        }
        System.out.println("Selected Base Nodes : " + BaseNodes);
        prepareHashStructure();

    }

    //Update TargetNodes ArrayList
    void updateTargetNodes(JList<String> compList2) {
        TargetNodes.clear();
        for (int i = 0; i < compList2.getModel().getSize(); i++) {
            TargetNodes.add(compList2.getModel().getElementAt(i));
        }
        System.out.println("Selected Target Nodes : " + TargetNodes);
        prepareHashStructure();
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
        // check if node name contains dot or slash or dash in case of cluster node, to be agreed
        String[] items = node.split("\\.");
        if (items.length > 1) {
            String path = "./" + LogsDirectory + fileSeperator + items[0] + fileSeperator + time + fileSeperator + items[1];
        }
        String path = "./" + LogsDirectory + fileSeperator + node + fileSeperator + time + fileSeperator;

        //------- this is for test --------------
        path += "BC0";

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
        if (timeStampBase.equals("")) {
            return;
        }
        if (timeStampTarget.equals("")) {
            return;
        }
//=============================================
//Clear Structure HashMap
        Structure.clear();
//--- Create Structure HashMap with All needed info to
//    construct GUI items and Calculate Data
        for (String baseNode : BaseNodes) {
            String BasePath = getPath(baseNode, timeStampBase);
            HashMap<String, HashMap<String, Command>> TargetCommands = new HashMap<String, HashMap<String, Command>>();

            for (String targetNode : TargetNodes) {
                String TargetPath = getPath(targetNode, timeStampTarget);
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
                        Commands.put(command, CommandObject);
                    }
                }
                TargetCommands.put(targetNode, Commands);
            }
            Structure.put(baseNode, TargetCommands);
        }
        //Hash prepared do additional Stuff 
        updateBaseNodesCombo();
        updateTargetNodesTree();
    }

    public void updateBaseNodesCombo() {
        this.BaseNodesCombo.removeAllItems();
        for (String key : Structure.keySet()) {
            this.BaseNodesCombo.addItem(key);
        }
        //this.BaseNodesCombo.setSelectedIndex(0);
    }

    public void updateTargetNodesTree() {
        //Remove all TreeView Items - if any
        this.commsTreeModel.removeAllChildren();

        // Get Selected BaseNodesCombo item
        String selectedNode = (String) this.BaseNodesCombo.getSelectedItem();
        System.out.println("Selected Base Node :" + selectedNode);

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
}
