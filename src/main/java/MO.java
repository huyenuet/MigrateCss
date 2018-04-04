
import FpGrowth.FrequentPattern;
import FpGrowth.Transaction;
import FpGrowth.oFpGrowth;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.CSSOMParser;
import model.Block;
import model.StyleDeclaration;
import model.StyleRuleList;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.*;

import java.io.*;
import java.util.*;

/**
 * Created by smart on 31/03/2018.
 */
/*MO = Mixin Opportunity*/

public class MO {
    
    private static MO parser;
    private static StyleRuleList styleRuleList = new StyleRuleList();
    private static ArrayList<String> propertyList = new ArrayList<>();
    private static List<StyleRuleList> S = new ArrayList<>();
    private static List<String> P = new ArrayList<>();
    private static Map<String, List<String>> migratedStyleRule = new HashMap<>();
    private static Map<String, List<String>> MixinSet = new HashMap<>();
    private static oFpGrowth fpGrowth;
    static Double minSupport = 0.05;

    public static void main(String[] args) throws IOException {
        fpGrowth = new oFpGrowth();
        parser = new MO();
        parser.parseFileCss();
//        transactionList = FPgrowth.parseToTransaction(styleRuleList,propertyList);
        Set<FrequentPattern> frequentPatterns = fpGrowth.findFrequentPattern(
                minSupport, styleRuleList);
        parser.generateMO(frequentPatterns);
        parser.writeCSS("migratedCSS");
    }

    public void migrationOp (StyleRuleList styleRuleList, Set<FrequentPattern> fps) {
        int count = 0;
        StyleRuleList styleRules = new StyleRuleList();
        for (FrequentPattern fp : fps) {
            for (Block block : styleRuleList.getBlocks()) {
                for (String item: fp.getItems()) {
                    if (block.getProperties().contains(item)) count++;
                }
                if (count == fp.getItems().size()) {
                    styleRules.add(block);
                }
            }
            S.add(styleRules);
        }
    }

    public void generateMO(Set<FrequentPattern> frequentPatterns) {

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
        for (Block block : styleRuleList.getBlocks()) {

            for (Map.Entry<String,List<String>> entry : MixinSet.entrySet()) {
                int count = 0;
                for (String mixinElement : entry.getValue()) {
                    if (block.getProperties().contains(mixinElement)){
                        count++;
                    }
                }
                if (count == entry.getValue().size()) {

                    for (String mixinElement : entry.getValue()) {
                        if(block.getProperties().contains(mixinElement)) {
                            styleRuleList.remove(styleRuleList.getBlockByName(mixinElement));
                        }
                    }
//                    styleRuleList.add("@"+entry.getKey());
//                    migratedStyleRule.replace(t.getName(),styleRuleList);
                    break;
                }
            }
        }
    }
    public void writeCSS(String outputName) throws IOException {
        System.out.println("Starting write css ...");
        FileWriter fileWriter = new FileWriter("./src/main/resources/"+outputName+".css");
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
        System.out.println("finish writing css ...");
    }

    public void parseFileCss() {

        System.out.println("parsing ... Css!");
        try {
            InputStream stream = parser.getClass().getResourceAsStream("index.css");
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
                            Block block = new Block();
                            Selector selector = selectors.item(k);

                            for (int m = 0; m < styleDeclaration.getLength(); m++) {
                                StyleDeclaration styleDecl = new StyleDeclaration();
                                String property = styleDeclaration.item(m);
                                styleDecl.setProperty(property);
                                styleDecl.setPropertyValue(styleDeclaration.getPropertyCSSValue(property).getCssText());
                                block.add(styleDecl);
                                if (!propertyList.contains(property)) {
                                    propertyList.add(property);
                                }
                            }
                            block.setSelectorName(selector.toString());
                            styleRuleList.add(block);
                        }

                    }
                }
            }

            System.out.println("done!");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
