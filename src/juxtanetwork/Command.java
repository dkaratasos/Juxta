/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juxtanetwork;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author dkar
 */
public class Command {

    public enum nodeType {
        MSC, HLR
    }

    //    CONSTRUCTORS
    /**
     * Command Constructor
     *
     * @param name
     */
    public Command(String name) {
        this.name = name;
        this.type = nodeType.MSC; // Default value set to MSC 
    }

    /**
     * IXGKOAG Command Constructor
     *
     * @param name
     * @param BaseFile
     * @param TargetFile
     */
    public Command(String name, File BaseFile, File TargetFile) {
        this.name = name;
        this.BaseFile = BaseFile;           // The BaseCommand file  
        this.TargetFile = TargetFile;       // The TargetCommand file 
    }

    /**
     * Command Constructor
     *
     * @param name
     * @param type
     */
    public Command(String name, nodeType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Command Constructor
     *
     * @param name
     * @param type
     * @param nodeName
     * @param bc
     */
    public Command(String name, nodeType type, String nodeName, Byte bc) {
        this.name = name;
        this.type = type;
        this.nodeName = nodeName;
        this.bc = bc;
    }

    /**
     * Command Constructor
     *
     * @param name
     * @param type
     * @param nodeName
     * @param bc
     * @param printOut
     */
    public Command(String name, nodeType type, String nodeName, Byte bc, String printOut) {
        this.name = name;
        this.type = type;
        this.nodeName = nodeName;
        this.bc = bc;
        this.printOut = printOut;
    }

    /**
     * Command Constructor
     *
     * @param name
     * @param type
     * @param nodeName
     * @param bc
     * @param printOut
     * @param startSorting
     * @param endSorting
     */
    public Command(String name, nodeType type, String nodeName, Byte bc, String printOut, String startSorting, String endSorting) {
        this.name = name;
        this.type = type;
        this.nodeName = nodeName;
        this.bc = bc;
        this.printOut = printOut;
        this.startSorting = startSorting;
        this.endSorting = endSorting;
    }

    //    METHODS
    /**
     * Method equals returns true if printout is equal to printout of command
     * given as parameter
     *
     * @param comm
     * @return boolean true if printouts are equal
     */
    public boolean equals(Command comm) {
        return this.printOut.equals(comm.printOut);
    }

    /**
     * Method equalsSorted returns true if sorted printout is equal to sorted
     * printout of command given as parameter
     *
     * @param comm
     * @return boolean true if sorted printouts are equal
     */
    public boolean equalsSorted(Command comm) {
        return this.sortedPO.equals(comm.sortedPO);
    }

    /**
     * Method sort will sort command printout from a startSorting string to the
     * endSorting string
     *
     * @return
     */
    public String sort() {
        String start = this.startSorting;
        String end = this.endSorting;
        StringBuilder sorted = new StringBuilder();
        sorted.append(this.printOut);

        sortedPO = sorted.toString();
        return sorted.toString();
    }

    /**
     * Method getPOtext gets the final printout to be checked for a command.
     *
     * @param comm
     * @return The pure final printout of the command, sorted if necessary
     */
    public String getPOtext(Command comm) {
        return "";
    }

    /**
     * Method diff finds the differences between the current command and the
     * input command comm
     *
     * @param comm
     * @return An ArrayList of the differences. Each difference is defined as
     * from a start index in the printout string of the current command to an
     * end index.
     */
    public ArrayList<int[]> diff(Command comm) {
        ArrayList<int[]> differences = new ArrayList<int[]>();

        String po1 = getPOtext(comm);
        String po2 = getPOtext(this);

        int startDiff = CommandUtils.getStartDiff(po1, po2, 0);
        int endDiff = CommandUtils.getEndDiff(po1, po2, 0);

        return differences;
    }

    //   SETTERS & GETTERS
    /**
     * set the Name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * set the Type of Node (MSC or HLR)
     *
     * @param type
     */
    public void setType(nodeType type) {
        this.type = type;
    }

    /**
     * set the PrintOut
     *
     * @param printOut
     */
    public void setPrintOut(String printOut) {
        this.printOut = printOut;
    }

    /**
     * set the Sorting
     *
     * @param startSorting
     * @param endSorting
     */
    public void setSorting(String startSorting, String endSorting) {
        this.startSorting = startSorting;
        this.endSorting = endSorting;
    }

    /**
     * set the NodeName
     *
     * @param nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * set the Blade
     *
     * @param bc
     */
    public void setBc(Byte bc) {
        this.bc = bc;
    }

    /**
     * set the Failed status in command if a comparison has failed reset to
     * false if the difference is accepted by the user
     *
     * @param failed
     */
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /**
     * get the Name of command
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * get the Type of Node (MSC or HLR)
     *
     * @return nodeType
     */
    public nodeType getType() {
        return this.type;
    }

    /**
     * get the PrintOut of command
     *
     * @return String
     */
    public String getPrintOut() {
        return this.printOut;
    }

    /**
     * get the Start of Sorting point
     *
     * @return String
     */
    public String getStartSorting() {
        return this.startSorting;
    }

    /**
     * get the End of Sorting point
     *
     * @return String
     */
    public String getEndSorting() {
        return this.endSorting;
    }

    /**
     * get the NodeName
     *
     * @return String
     */
    public String getNodeName() {
        return this.nodeName;
    }

    /**
     * get the Blade
     *
     * @return Byte
     */
    public Byte getBc() {
        return this.bc;
    }

    /**
     * get the Sorted PrintOut
     *
     * @return String the sorted Printout
     */
    public String getSortedPO() {
        return this.sortedPO;
    }

    /**
     * get the status of this Printout, if a check has failed
     *
     * @return boolean failed
     */
    public boolean getFailed() {
        return this.failed;
    }

    //     VARIABLES
    //IXGKOAG 
    private File BaseFile;           // The BaseCommand file  
    private File TargetFile;         // The TargetCommand file 
    //
    private String name;             // The name of the command
    private String nodeName;         // The node the command is given
    private Byte bc;                 // The blade number the command is given
    private String printOut;         // The printout of command as read from the input file
    private String startSorting;     // The start string for sorting the printout of command
    private String endSorting;       // The end string for sorting the printout of command
    private nodeType type;           // The type of the node the command is given (MSC or HLR)
    private String sortedPO = "";    // The sorted printout of the command 
    private boolean failed = false;  // Whether the command has failed in a comparison. Default false
}
