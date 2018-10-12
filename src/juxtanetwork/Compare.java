//IXGKOAG
//1. Collect Select Commands from Command List  
package juxtanetwork;

import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JList;
import javax.swing.tree.DefaultMutableTreeNode;

public class Compare {

    String fileSeperator = System.getProperty("path.separator");
    private String LogsDirectory = "Data";
    private String timeStampBase = "2018-10-11_16_42_17";  // will be retrieved later
    private String timeStampTarget = "2018-10-11_16_42_18";  // will be retrieved later

    private ArrayList<String> selectedCommands = new ArrayList<String>();
    private ArrayList<String> BaseNodes = new ArrayList<String>();
    private ArrayList<String> TargetNodes = new ArrayList<String>();

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
    }

    //Update BaseNodes ArrayList
    void updateBaseNodes(JList<String> compList1) {
        BaseNodes.clear();
        for (int i = 0; i < compList1.getModel().getSize(); i++) {
            BaseNodes.add(compList1.getModel().getElementAt(i));
        }
        System.out.println("Selected Base Nodes : " + BaseNodes);
    }

    //Update TargetNodes ArrayList
    void updateTargetNodes(JList<String> compList2) {
        TargetNodes.clear();
        for (int i = 0; i < compList2.getModel().getSize(); i++) {
            TargetNodes.add(compList2.getModel().getElementAt(i));
        }
        System.out.println("Selected Target Nodes : " + TargetNodes);
    }

    //Update timeStampBase String
    void updateTimeStampBase(JList<String> compList2) {
        // To be implemented when timestamp selection implemented

    }

    //Update timeStampBase String
    void updateTimeStampTarget(JList<String> compList2) {
        // To be implemented when timestamp selection implemented
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



    }

}
