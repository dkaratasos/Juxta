//IXGKOAG
//1. Collect Select Commands from Command List
package juxtanetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.JList;
import javax.swing.tree.DefaultMutableTreeNode;

public class Compare {

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
    private HashMap<String,HashMap<String, HashMap<String, Command>>> Structure = new HashMap<String,HashMap<String, HashMap<String, Command>>>() ;

    //Base Files Path's
    // Empty yet Constructor
    public Compare() {
    }

    //Update Selected Commands ArrayList
    void updateSelectedCommands(JList<String> commList) {
        selectedCommands.clear();
        int[] selectedIndex = commList.getSelectedIndices();
        for (int i = 0; i < selectedIndex.length; i++) {
            selectedCommands.add(commList.getModel().getElementAt(selectedIndex[i]));
        }
        System.out.println("Selected Commads : " + selectedCommands);
        getPOintoHash();
    }

    //Update BaseNodes ArrayList
    void updateBaseNodes(JList<String> compList1) {
        BaseNodes.clear();
        for (int i = 0; i < compList1.getModel().getSize(); i++) {
            BaseNodes.add(compList1.getModel().getElementAt(i));
        }
        System.out.println("Selected Base Nodes : " + BaseNodes);
        getPOintoHash();
    }

    //Update TargetNodes ArrayList
    void updateTargetNodes(JList<String> compList2) {
        TargetNodes.clear();
        for (int i = 0; i < compList2.getModel().getSize(); i++) {
            TargetNodes.add(compList2.getModel().getElementAt(i));
        }
        System.out.println("Selected Target Nodes : " + TargetNodes);
        getPOintoHash();
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

    void getPOintoHash() {
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
//--- Create Structure HashMap with All needed info to
//    construct GUI items and Calculate Data

        for (String baseNode : BaseNodes) {
            String BasePath = getPath(baseNode, timeStampBase);
            HashMap<String, HashMap<String, Command>> TargetCommands = new HashMap<String, HashMap<String, Command>>();

            for (String targetNode : TargetNodes) {
                String TargetPath = getPath(targetNode, timeStampTarget);
                HashMap<String, Command> Commands = new HashMap<String, Command>();

                for (String command : selectedCommands) {
                    String pathTarget = TargetPath + fileSeperator + command;
                    String pathBase = BasePath + fileSeperator + command;

                    //=== If both Files exist
                    File f1 = new File(pathBase);
                    File f2 = new File(pathTarget);

                    if (f1.exists() & f1.exists()) {
                        //create CommandObject and appendit to the targetNode Commands
                        Command CommandObject = new Command(command, f1, f2);
                        Commands.put(command, CommandObject);
                    }
                }
                TargetCommands.put(targetNode, Commands);
            }
            Structure.put(baseNode, TargetCommands);
        } 
        // Do the GUI Stuff
        
        
        
        
        
        
        
        }
}