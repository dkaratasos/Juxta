/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juxtanetwork;

import java.awt.Color;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;

/**
 *
 * @author dkar
 */
public class Highlight {
    int savedHilitePos1 = 0;
    int savedHilitePos2 = 0;

    public void highlighBookmark(JTextComponent textComp, String line) {
        Highlighter hilite = textComp.getHighlighter();
        hilite.removeAllHighlights();
    }

    public void highlightremove(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        hilite.removeAllHighlights();
    }

    public void highlightremoveOne(JTextComponent textComp, int num) {
        Highlighter hilite = textComp.getHighlighter();
        hilite.removeHighlight(hilite.getHighlights()[num]);
    }

    public void highlight(JTextComponent textComp, int start, int end, Color color) {
        try {
            Highlighter hilite = textComp.getHighlighter();
            hilite.addHighlight(start, end, new DefaultHighlightPainter(color));
            textComp.select(start, end);
        } catch (Exception e) {
            System.out.println("ERROR: Problem with highlighter");
        }
    }

    public int highlightText(JTextComponent textComp, String pattern, Color color, Color colorFound,int savedHilitePos) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return savedHilitePos;
        }
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            int currSearchPos = 0;
            boolean stopSearchSelect = false;

            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {

                currSearchPos = pos;
                if (currSearchPos > savedHilitePos) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new DefaultHighlightPainter(colorFound));
                        savedHilitePos = currSearchPos + 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new DefaultHighlightPainter(color));
                pos += pattern.length();

            }
            if (currSearchPos < savedHilitePos) {
                savedHilitePos = 0;
            }

        } catch (Exception e) {
        }
        return savedHilitePos;
    }
    
    public int backhighlightText(JTextComponent textComp, String pattern, Color color, Color colorFound,int savedHilitePos) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return savedHilitePos;
        }
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = text.length() - 1;
            int currSearchPos = text.length() - 1;
            boolean stopSearchSelect = false;
            if (savedHilitePos == 0) {
                savedHilitePos = text.length() - 1;
            }

            while ((pos = text.toUpperCase().lastIndexOf(pattern.toUpperCase(), pos)) >= 0) {

                currSearchPos = pos;
                if (currSearchPos < savedHilitePos) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new DefaultHighlightPainter(colorFound));
                        savedHilitePos = currSearchPos - 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new DefaultHighlightPainter(color));
                pos -= pattern.length();

            }
            if (currSearchPos > savedHilitePos) {
                savedHilitePos = text.length() - 1;
            }

        } catch (Exception e) {
        }
        return savedHilitePos;
    }
    
    public void highlightText1(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        savedHilitePos1 = highlightText(textComp, pattern, color, colorFound,savedHilitePos1);
    }

    public void highlightText2(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        savedHilitePos2 = highlightText(textComp, pattern, color, colorFound,savedHilitePos2);
    }

    public void backhighlightText1(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        savedHilitePos1 = backhighlightText(textComp, pattern, color, colorFound,savedHilitePos1);
    }

    public void backhighlightText2(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        savedHilitePos2 = backhighlightText(textComp, pattern, color, colorFound,savedHilitePos2);
    }
}
