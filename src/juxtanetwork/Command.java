/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juxtanetwork;

/**
 *
 * @author dkar
 */
public class Command {
    public enum nodeType { MSC, HLR }

    //    CONSTRUCTORS
    
    /**
     * Command Constructor
     * @param name 
     */
    public Command(String name) {
        this.name = name;
        this.type = nodeType.MSC;
    }
    
    /**
     * Command Constructor
     * @param name
     * @param type 
     */
    public Command(String name,nodeType type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * Command Constructor
     * @param name
     * @param type
     * @param nodeName
     * @param bc 
     */
    public Command(String name,nodeType type,String nodeName,Byte bc) {
        this.name = name;
        this.type = type;
        this.nodeName = nodeName;
        this.bc = bc;
    }
    
    /**
     * Command Constructor
     * @param name
     * @param type
     * @param nodeName
     * @param bc
     * @param printOut 
     */
    public Command(String name,nodeType type,String nodeName,Byte bc,String printOut) {
        this.name = name;
        this.type = type;
        this.nodeName = nodeName;
        this.bc = bc;
        this.printOut = printOut;
    }
    
    /**
     * Command Constructor
     * @param name
     * @param type
     * @param nodeName
     * @param bc
     * @param printOut
     * @param startSorting
     * @param endSorting 
     */
    public Command(String name,nodeType type,String nodeName,Byte bc,String printOut,String startSorting,String endSorting) {
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
     * Method equals returns true if printout is equal to printout of command given as parameter
     * @param comm
     * @return  boolean true if printouts are equal
     */
    public boolean equals(Command comm){
        return this.printOut.equals(comm.printOut);
    }
    
    /**
     * Method equalsSorted returns true if sorted printout is equal to sorted printout of command 
     * given as parameter
     * @param comm
     * @return boolean true if sorted printouts are equal
     */
    public boolean equalsSorted(Command comm){
        return this.sortedPO.equals(comm.sortedPO);
    }
    
    /**
     * Method sort will short command printout from a startSorting string to the endSorting string
     * @return 
     */
    public String sort(){
        String start = this.startSorting;
        String end = this.endSorting;
        StringBuilder sorted = new StringBuilder();
        sorted.append(this.printOut);
        
        sortedPO = sorted.toString();
        return sorted.toString();
    }
    
    //   SETTERS & GETTERS
    
    /**
     * set the Name
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * set the Type of Node (MSC or HLR)
     * @param type 
     */
    public void setType(nodeType type) {
        this.type = type;
    }
    
    /**
     * set the PrintOut
     * @param printOut 
     */
    public void setPrintOut(String printOut) {
        this.printOut = printOut;
    }
    
    /**
     * set the Sorting
     * @param startSorting
     * @param endSorting 
     */
    public void setSorting(String startSorting,String endSorting) {
        this.startSorting = startSorting;
        this.endSorting = endSorting;
    }
    
    /**
     * set the NodeName
     * @param nodeName 
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    /**
     * set the Blade
     * @param bc 
     */
    public void setBc(Byte bc) {
        this.bc = bc;
    }
    
    /**
     * get the Name of command
     * @return String
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * get the Type of Node (MSC or HLR)
     * @return nodeType
     */
    public nodeType getType() {
        return this.type;
    }
    
    /**
     * get the PrintOut of command
     * @return String
     */
    public String getPrintOut() {
        return this.printOut;
    }
    
    /**
     * get the Start of Sorting point
     * @return String
     */
    public String getStartSorting() {
        return this.startSorting;
    }
    
    /**
     * get the End of Sorting point
     * @return String
     */
    public String getEndSorting() {
        return this.endSorting;
    }
    
    /**
     * get the NodeName
     * @return String
     */
    public String getNodeName() {
        return this.nodeName;
    }
    
    /**
     * get the Blade
     * @return Byte
     */
    public Byte getBc() {
        return this.bc;
    }
    
    /**
     * get the Sorted PrintOut
     * @return 
     */
    public String getSortedPO() {
        return this.sortedPO;
    }
    
    //     VARIABLES
    
    private String name;
    private String nodeName;
    private Byte bc;
    private String printOut;
    private String startSorting;
    private String endSorting;
    private nodeType type;
    private String sortedPO = "";
}
