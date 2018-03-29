/**
 * Created by smart on 21/03/2018.
 */
import FpGrowth.FrequentPattern;
import FpGrowth.Transaction;
import FpGrowth.FPgrowth;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.CSSOMParser;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.*;

import java.io.*;
import java.util.*;

public class MyTest {
    private static MyTest oParser;
    private static ArrayList<String> Selector_List = new ArrayList<>();
    private static ArrayList<String> Declaration_List = new ArrayList<>();
    private static ArrayList<Transaction> transactionList;
    private static Map<String, List<String>> migratedStyleRule = new HashMap<>();
    private static Map<String, List<String>> MixinSet = new HashMap<>();
    private static FPgrowth FPgrowth;
    static Double minSupport = 0.2;

    public static void main(String[] args) throws IOException {
        FPgrowth = new FPgrowth();
        oParser = new MyTest();
        oParser.parseFileCss();
        transactionList = FPgrowth.parseToTransaction(Selector_List,Declaration_List);
        Set<FrequentPattern> frequentPatterns = FPgrowth.findFrequentPattern(
                minSupport, transactionList);
        oParser.migrateCSS(frequentPatterns);
        oParser.writeCSS("migratedCSS");
    }
    public void migrateCSS(Set<FrequentPattern> frequentPatterns) {

        /*create mixin*/
        int mixinIndex = 1;
        for (FrequentPattern fp: frequentPatterns) {
            String mixinName = "mixin" + mixinIndex;
            MixinSet.put(mixinName, fp.getItems());
            mixinIndex++;
        }

        /*modified all old style rules
        * loop through a transaction list
        * check if a transaction has all frequent items -> remove them from transaction, replace by a mixin
        * */
        for (Transaction t: transactionList) {

            ArrayList<String> styleRuleList = new ArrayList<>();
            styleRuleList.addAll(t.getItems());
            migratedStyleRule.put(t.getName(),styleRuleList);

            for (Map.Entry<String,List<String>> entry : MixinSet.entrySet()) {
                int count = 0;
                for (String mixinElement : entry.getValue()) {

                    if (t.getItems().contains(mixinElement)){
                        count++;
                    }
                }
                if (count == entry.getValue().size()) {

                    for (String mixinElement : entry.getValue()) {
                        if(styleRuleList.contains(mixinElement)) {
                            styleRuleList.remove(mixinElement);
                        }
                    }
                    styleRuleList.add("@"+entry.getKey());
                    migratedStyleRule.replace(t.getName(),styleRuleList);
                    break;
                }
            }
        }
    }
    public void writeCSS(String outputName) throws IOException {
        FileWriter fileWriter = new FileWriter("./src/Resources/"+outputName+".css");
        PrintWriter pw = new PrintWriter(fileWriter);

        /*write mixin first*/
        for (Map.Entry<String, List<String>> entry: MixinSet.entrySet()) {
            String selectorName = entry.getKey();
            List<String> declList = entry.getValue();
            pw.append(selectorName);
            pw.append(" {");
            pw.append("\n");
            for (String decl : declList) {
                pw.append("   "+decl+";");
                pw.append("\n");
            }
            pw.append("}\n");
        }

        /*write style rules*/
        for (Map.Entry<String, List<String>> entry: migratedStyleRule.entrySet()) {
            String selectorName = entry.getKey();
            List<String> declList = entry.getValue();
            pw.append(selectorName);
            pw.append(" {");
            pw.append("\n");
            for (String decl : declList) {
                pw.append("    "+decl+";");
                pw.append("\n");
            }
            pw.append("}\n");
        }
        pw.close();
    }

    public void parseFileCss() {

        InputStream stream = oParser.getClass().getResourceAsStream("Resources/css/bootstrap.css");

        try {
            InputSource source = new InputSource(new InputStreamReader(stream));
            CSSOMParser parser = new CSSOMParser();
            // parse and create a stylesheet composition
            CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

            //ANY ERRORS IN THE DOM WILL BE SENT TO STDERR HERE!!
            // now iterate through the dom and inspect.

            CSSRuleList ruleList = stylesheet.getCssRules();

            for (int i = 0; i < ruleList.getLength(); i++) {
                CSSRule rule = ruleList.item(i);
                if (rule instanceof CSSStyleRule) {
                    CSSStyleRule styleRule = (CSSStyleRule) rule;

                    if (styleRule instanceof CSSStyleRuleImpl) {
                        CSSStyleRuleImpl styleRuleImpl = (CSSStyleRuleImpl) styleRule;
                        SelectorList selectors = styleRuleImpl.getSelectors();
                        CSSStyleDeclaration styleDeclaration = styleRuleImpl.getStyle();

                        for (int k = 0; k < selectors.getLength(); k++) {
                            Selector selector = selectors.item(k);
                            Selector_List.add(selector.toString());
                            Declaration_List.add(styleDeclaration.getCssText());
                        }
                    }
                }
            }

            System.out.println("Selector List: \n" + Selector_List);
            System.out.println("Declaration List: \n" + Declaration_List);
            System.out.println("done!");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
