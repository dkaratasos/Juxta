/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juxtanetwork;

import java.util.ArrayList;

/**
 *
 * @author dkar
 */
public class CommandUtils {
    
    
    
    /**
     * Method getStartDiff finds the start position of a difference between two
     * texts. StartPos will define the start position for the check
     * @param po1
     * @param po2
     * @param startPos
     * @return the start index of the difference between the two texts or -1 if no
     * difference is found
     */
    public static int getStartDiff(String po1, String po2, int startPos){
        return -1;
    }
    
    /**
     * Method getEndDiff finds the end position of a difference between two
     * texts. StartPos will define the start position for check. Special care should
     * be taken for the kind of difference. Difference may be: 1)a set of characters
     * that do not match 2) a set of characters missing 3) a set of character more
     * @param po1
     * @param po2
     * @param startPos
     * @return the end index of the difference between the two texts
     */
    public static int getEndDiff(String po1, String po2, int startPos){
        return 0;
    }
    
    /**
     * Method getAllDiffs will fetch all differences for all commands in ArrayList comms
     * @param comms 
     */
    public static void getAllDiffs(ArrayList<Command> comms){
        ArrayList<int[]> diffs = new ArrayList<int[]>();
        diffs = comms.get(0).diff(comms.get(1));
    }
    
}
