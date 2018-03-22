/**
 * Created by smart on 21/03/2018.
 */
import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.CSSOMParser;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.*;

import java.io.*;
import java.util.ArrayList;

public class MyTest {
    protected static MyTest oParser;

    public static void main(String[] args) throws FileNotFoundException {

        oParser = new MyTest();

        if (oParser.parse("index.css")) {

            System.out.println("Parsing completed OK");

        } else {

            System.out.println("Unable to parse CSS");

        }
    }
    public boolean parse(String inputPath) throws FileNotFoundException {

        FileOutputStream out = null;
        PrintStream ps = null;
        boolean rtn = false;

        try {
            // cssfile accessed as a resource, so must be in the pkg (in src dir).
            InputStream stream = oParser.getClass().getResourceAsStream(inputPath);

            // overwrites and existing file contents
            out = new FileOutputStream("log-2.txt");

            if (out != null) {
                //log file
                ps = new PrintStream(out);
                System.setErr(ps); //redirects stderr to the log file as well

            } else {

                return rtn;

            }

            InputSource source = new InputSource(new InputStreamReader(stream));
            CSSOMParser parser = new CSSOMParser();
            // parse and create a stylesheet composition
            CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

            //ANY ERRORS IN THE DOM WILL BE SENT TO STDERR HERE!!
            // now iterate through the dom and inspect.

            CSSRuleList ruleList = stylesheet.getCssRules();
            ps.println("Number of rules: " + ruleList.getLength());

            for (int i = 0; i < ruleList.getLength(); i++)
            {
                CSSRule rule = ruleList.item(i);
                if (rule instanceof CSSStyleRule) {
                    CSSStyleRule styleRule = (CSSStyleRule)rule;
                    ps.println("selector:" + i + ": ");

                    if (styleRule instanceof CSSStyleRuleImpl) {
                        CSSStyleRuleImpl styleRuleImpl = (CSSStyleRuleImpl) styleRule;
                        SelectorList selectorList = styleRuleImpl.getSelectors();
                        CSSStyleDeclaration styleDeclaration = styleRuleImpl.getStyle();

                        for (int k =0; k < selectorList.getLength(); k++)
                        {
                            Selector selector = selectorList.item(k);

                            /*SACSelector sacSelector = new SACSelector();
                            ArrayList<String> selList = sacSelector.getSelectorOfOneRule(selector);
                            ps.println(selList.toString());*/
                        }

                        for (int j = 0; j < styleDeclaration.getLength(); j++)
                        {
                            String property = styleDeclaration.item(j);
                            ps.println("property: " + property);
                            ps.println("priority: " + styleDeclaration.getPropertyPriority(property));
                            ps.println("value: " + styleDeclaration.getPropertyCSSValue(property).getCssText());
                        }
                    }

                }
            }

            if (out != null) out.close();
            if (stream != null) stream.close();
            rtn = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) ps.close();
        }
        return rtn;
    }
}
