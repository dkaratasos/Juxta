/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juxtanetwork;

import java.awt.Color;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;

/**
 *
 * @author dkar
 */
public class Highlight {

    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    int savedHilitePos1 = 0;
    int savedHilitePos2 = 0;

//    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
//    Highlighter.HighlightPainter myHighlightPainterFound = new MyHighlightPainter(Color.CYAN);
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
            hilite.addHighlight(start, end, new MyHighlightPainter(color));
            textComp.select(start, end);
//            textComp.setSelectionColor(Color.CYAN);
//            textComp.setSelectedTextColor(Color.BLUE);

        } catch (Exception e) {
            System.out.println("ERROR: Problem with highlighter");
        }
    }

    public void highlightText1(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return;
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
                if (currSearchPos > savedHilitePos1) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(colorFound));
                        savedHilitePos1 = currSearchPos + 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(color));
                pos += pattern.length();

            }
            if (currSearchPos < savedHilitePos1) {
                savedHilitePos1 = 0;
            }

        } catch (Exception e) {
        }
    }

    public void highlightText2(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return;
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
                if (currSearchPos > savedHilitePos2) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(colorFound));
                        savedHilitePos2 = currSearchPos + 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(color));
                pos += pattern.length();

            }
            if (currSearchPos < savedHilitePos2) {
                savedHilitePos2 = 0;
            }

        } catch (Exception e) {
        }
    }

    public void backhighlightText1(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return;
        }
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = text.length() - 1;
            int currSearchPos = text.length() - 1;
            boolean stopSearchSelect = false;
            if (savedHilitePos1 == 0) {
                savedHilitePos1 = text.length() - 1;
            }

            while ((pos = text.toUpperCase().lastIndexOf(pattern.toUpperCase(), pos)) >= 0) {

                currSearchPos = pos;
                if (currSearchPos < savedHilitePos1) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(colorFound));
                        savedHilitePos1 = currSearchPos - 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(color));
                pos -= pattern.length();

            }
            if (currSearchPos > savedHilitePos1) {
                savedHilitePos1 = text.length() - 1;
            }

        } catch (Exception e) {
        }
    }

    public void backhighlightText2(JTextComponent textComp, String pattern, Color color, Color colorFound) {
        if (pattern.length() == 0) {
            highlightremove(textComp);
            return;
        }
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = text.length() - 1;
            int currSearchPos = text.length() - 1;
            boolean stopSearchSelect = false;
            if (savedHilitePos2 == 0) {
                savedHilitePos2 = text.length() - 1;
            }

            while ((pos = text.toUpperCase().lastIndexOf(pattern.toUpperCase(), pos)) >= 0) {

                currSearchPos = pos;
                if (currSearchPos < savedHilitePos2) {
                    if (stopSearchSelect == false) {
                        textComp.select(pos, pattern.length());
                        hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(colorFound));
                        savedHilitePos2 = currSearchPos - 1;
                        stopSearchSelect = true;
                    }
                }
                hilite.addHighlight(pos, pos + pattern.length(), new MyHighlightPainter(color));
                pos -= pattern.length();

            }
            if (currSearchPos > savedHilitePos2) {
                savedHilitePos2 = text.length() - 1;
            }

        } catch (Exception e) {
        }
    }
}
