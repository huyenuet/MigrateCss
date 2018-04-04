/**
 * Created by smart on 21/03/2018.
 */

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFactory;
import org.fit.cssbox.layout.ElementBox;

import java.awt.Color;

public class CSSParserTest {
    protected static CSSParserTest oParser;
    private static TermFactory tf = CSSFactory.getTermFactory();

    public static void main(String[] args) {

    }
    private Color getBorderColor(ElementBox elem, String side) {

        Color clr = null;
        // gets the color value from CSS property
        CSSProperty.BorderColor bclr = elem.getStyle().getProperty("border-"+side+"-color");
        TermColor tclr = elem.getStyle().getValue(TermColor.class, "border-"+side+"-color");
        CSSProperty.BorderStyle bst = elem.getStyle().getProperty("border-"+side+"-style");

//        if (bst != CSSProperty.BorderStyle.HIDDEN && bclr != CSSProperty.BorderColor.TRANSPARENT) {
//            if (tclr != null) clr = tclr.getValue();
//
//            if (clr == null) {
//                clr = null;
//                clr = elem.getVisualContext().getColor();
//                if (clr == null) clr = Color.BLACK;
//            }
//        }
//        else { clr = elem.getBgcolor(); }

        return clr;
    }
}