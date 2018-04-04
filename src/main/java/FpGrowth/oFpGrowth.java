package FpGrowth;

import model.Block;
import model.StyleDeclaration;
import model.StyleRuleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.Style;
import java.util.*;

/**
 * Created by smart on 23/03/2018.
 */
public class oFpGrowth {
    private static final String PATTERN_DELIMITER = ";;";
    private Map<String, Integer> itemFrequencies = new HashMap<>();
    private FPtree fpTree;
    private Map<String, FPtree> headerTable = new HashMap<>();
    private Set<FrequentPattern> frequentPatterns = new HashSet<>();
    private Double minSupport;
    private Integer minSupportCount;
    private Integer transactionCount;
    private StyleRuleList S;
    private ArrayList<String> P;

    private static final Logger log = LoggerFactory.getLogger(oFpGrowth.class);

    /*method: findFrequentPattern
    * @param : minSupport
    * @param : each selector (in selector list) is considered as a transaction name
    * @param : declarations (in declaration list) is considered as an items set
    * */

    public Set<FrequentPattern> findFrequentPattern(Double minSupport, StyleRuleList S)
    {
        this.minSupport = minSupport;
        this.S = S;
        countFrequencyByWord();
        buildFPTree();
        findFrequentPatterns();
        filterFrequentPattern();
        log.info("{} Frequent Item Sets found", this.frequentPatterns.size());
        return this.frequentPatterns;
    }

    private void countFrequencyByWord() {
        this.transactionCount = S.size();

        for (Block block: S.getBlocks()) {
            for (StyleDeclaration styleDecl : block.getStyleDeclarations())
            {
                String item = styleDecl.getProperty();
                if(itemFrequencies.containsKey(item)) {
                    int oldFrequency = itemFrequencies.get(item);
                    itemFrequencies.replace(item,oldFrequency+1);
                }
                else {
                    itemFrequencies.put(item,1);
                }
            }
        }

        this.minSupportCount = (int) Math.ceil(minSupport * transactionCount);

    }

    private void buildFPTree() {

        // Add root to FPTree
        this.fpTree = new FPtree("null", null);
        this.fpTree.setRoot(Boolean.TRUE);

        // Create Header Table
        Map<String, FPtree> headerTable = new HashMap<>();

        // Iterate over transactions but order items by frequency
        int i=0;
        for (Block block : S.getBlocks()) {

            List<String> orderedList = orderItemsByFrequency(block.getProperties(),
                    this.itemFrequencies);
            log.debug("Processing Transaction {}", orderedList);

            List<Integer> orderedItemsValues = new ArrayList<>();
            for (int j = 0; j < block.getProperties().size(); j++) {
                orderedItemsValues.add(1);
            }

            insertFPTree(this.fpTree, orderedList, orderedItemsValues,
                    headerTable);

            this.headerTable = headerTable;

            i++;
        }
    }
    
    /*order the items in List decrease by their frequencies
    * param items: items need to be ordered
    * param frequencies: a map represent name of item and respective frequency */

    private List<String> orderItemsByFrequency(List<String> items,
                                               Map<String, Integer> frequencies) {
        List<String> orderedList = new LinkedList<>();
        for (String item : items) {
            if (orderedList.size() == 0) {
                orderedList.add(item);
            } else {
                int frequency = frequencies.get(item);
                int i = 0;
                for (String word : orderedList) {
                    if (word.length() > 0 && frequencies.get(word) < frequency) {
                        break;
                    }
                    i++;
                }
                orderedList.add(i, item);
            }
        }
        return orderedList;
    }

    private void insertFPTree(FPtree tree, List<String> words,
                              List<Integer> wordValues, Map<String, FPtree> headerTable) {
        if (tree.getChildren().size() == 0) {
            if (words.size() > 0) {
                FPtree subTree = new FPtree(words.get(0), tree);
                subTree.setParent(tree);
                subTree.setCount(wordValues.get(0));
                if (headerTable.containsKey(words.get(0))) {
                    subTree.setNext(headerTable.get(words.get(0)));
                    headerTable.replace(words.get(0), subTree);
                } else {
                    headerTable.put(words.get(0), subTree);
                }
                if (words.size() > 1)
                    insertFPTree(subTree, words.subList(1, words.size()),
                            wordValues.subList(1, wordValues.size()),
                            headerTable);
                tree.addChild(subTree);
            }
        } else {
            for (FPtree child : tree.getChildren()) {
                if (child.getItem().equals(words.get(0))) {
                    child.incrementCount();
                    if (words.size() > 1)
                        insertFPTree(child, words.subList(1, words.size()),
                                wordValues.subList(1, wordValues.size()),
                                headerTable);
                    return;
                }
            }
            FPtree newChild = new FPtree(words.get(0), tree);
            newChild.setParent(tree);
            newChild.setCount(wordValues.get(0));
            if (headerTable.containsKey(words.get(0))) {
                newChild.setNext(headerTable.get(words.get(0)));
                headerTable.replace(words.get(0), newChild);
            } else {
                headerTable.put(words.get(0), newChild);
            }
            if (words.size() > 1)
                insertFPTree(newChild, words.subList(1, words.size()),
                        wordValues.subList(1, wordValues.size()), headerTable);
            tree.addChild(newChild);
        }

    }

    private void findFrequentPatterns() {
        oFpGrowthStep(this.headerTable, this.frequentPatterns, "");
    }

    private void oFpGrowthStep(Map<String, FPtree> headerTable,
                              Set<FrequentPattern> frequentPatterns, String base) {

        for (String item : headerTable.keySet()) {
            FPtree treeNode = headerTable.get(item);

            String currentPattern = item + PATTERN_DELIMITER + base;
            if (currentPattern.endsWith(PATTERN_DELIMITER))
                currentPattern = currentPattern.substring(0,
                        currentPattern.length() - PATTERN_DELIMITER.length());

            // 1. Step: Conditional Patter n Base
            Map<String, Integer> conditionalPatternBase = new HashMap<>();

            // Is the item frequent? (count >= minSupport)
            Integer frequentItemsetCount = 0;

            // Jump from leaf to leaf
            while (treeNode != null) {

                String conditionalPattern = "";
                frequentItemsetCount += treeNode.getCount();
                Integer supportConditionalPattern = treeNode.getCount();

                FPtree parentNode = treeNode.getParent();

                // Work yourself up to the root
                while (!parentNode.isRoot()) {
                    conditionalPattern = parentNode.getItem().concat(
                            PATTERN_DELIMITER + conditionalPattern);
                    parentNode = parentNode.getParent();
                }
                if (conditionalPattern.endsWith(PATTERN_DELIMITER))
                    conditionalPattern = conditionalPattern.substring(0,
                            conditionalPattern.length() - PATTERN_DELIMITER.length());

                treeNode = treeNode.getNext();

                if (!conditionalPattern.equals(""))
                    conditionalPatternBase.put(conditionalPattern,
                            supportConditionalPattern);

            }

            // Is the item frequent? (count >= minSupport)
            if (frequentItemsetCount < minSupportCount) {
                // Skip the current item
                continue;
            } else {
                frequentPatterns.add(new FrequentPattern(currentPattern,
                        frequentItemsetCount, (double) frequentItemsetCount
                        / transactionCount));
            }

            // 2. Step: Conditional FP-Tree
            Map<String, Integer> conditionalItemFrequencies = new HashMap<>();
            FPtree conditionalTree = new FPtree("null", null);
            conditionalTree.setRoot(Boolean.TRUE);

            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);

                while (tokenizer.hasMoreTokens()) {
                    String conditionalToken = tokenizer.nextToken();
                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        int count = conditionalItemFrequencies
                                .get(conditionalToken);
                        count += conditionalPatternBase.get(conditionalPattern);
                        conditionalItemFrequencies.put(conditionalToken, count);
                    } else {
                        conditionalItemFrequencies.put(conditionalToken,
                                conditionalPatternBase.get(conditionalPattern));
                    }
                }
            }

            // Remove not frequent nodes
            Map<String, Integer> tmp = new HashMap<>(conditionalItemFrequencies);
            for (String s : tmp.keySet())
                if (conditionalItemFrequencies.get(s) < minSupportCount)
                    conditionalItemFrequencies.remove(s);

            // Construct Conditional FPTree
            HashMap<String, FPtree> conditionalHeaderTable = new HashMap<>();
            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);
                List<String> path = new ArrayList<>();
                List<Integer> pathValues = new ArrayList<>();

                while (tokenizer.hasMoreTokens()) {
                    String conditionalToken = tokenizer.nextToken();
                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        path.add(conditionalToken);
                        pathValues.add(conditionalPatternBase
                                .get(conditionalPattern));

                    }
                }
                if (path.size() > 0) {
                    insertFPTree(conditionalTree, path, pathValues,
                            conditionalHeaderTable);
                }

            }

            if (!conditionalTree.getChildren().isEmpty())
                oFpGrowthStep(conditionalHeaderTable,
                        frequentPatterns, currentPattern);
        }
    }
    /*method: filter the Frequent Pattern Set by Frequency(min Frequency = 2)
    * param: a set of Frequent Pattern
    * */

    public void filterFrequentPattern()
    {
        Set <FrequentPattern> filteredFPs = new HashSet<>();
        for (FrequentPattern fp : this.frequentPatterns) {
            if (fp.getSupportCount() >= 2 && fp.getItems().size() >= 0) {
                filteredFPs.add(fp);
            }
        }
        this.frequentPatterns = filteredFPs;
    }
}