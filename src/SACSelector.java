import com.steadystate.css.parser.selectors.*;
import org.w3c.css.sac.Selector;

import java.util.ArrayList;

/**
 * Created by smart on 22/03/2018.
 */
public class SACSelector {
    // list types of selectors
    short SAC_CONDITIONAL_SELECTOR = 0;
    short SAC_ANY_NODE_SELECTOR = 1;
    short SAC_ROOT_NODE_SELECTOR = 2;
    short SAC_NEGATIVE_SELECTOR = 3;
    short SAC_ELEMENT_NODE_SELECTOR = 4;
    short SAC_TEXT_NODE_SELECTOR = 5;
    short SAC_CDATA_SECTION_NODE_SELECTOR = 6;
    short SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR = 7;
    short SAC_COMMENT_NODE_SELECTOR = 8;
    short SAC_PSEUDO_ELEMENT_SELECTOR = 9;
    short SAC_DESCENDANT_SELECTOR = 10;
    short SAC_CHILD_SELECTOR = 11;
    short SAC_DIRECT_ADJACENT_SELECTOR = 12;

    ArrayList<String> selectorList = new ArrayList<String>();

    public ArrayList<String> getSelectorOfOneRule (Selector selector) {

        short selectorType = selector.getSelectorType();

        switch (selectorType) {
            case 0:
                selectorList.add(SacConditionalSelector((ConditionalSelectorImpl) selector));
                break;
            case 11:
                SacChildSelector((ChildSelectorImpl) selector);
                break;
            case 4:
                selectorList.add(SacElementNodeSelector((ElementSelectorImpl) selector));
                break;
        }
        return selectorList;
    }

    // case 0: Conditional Selector
    public String SacConditionalSelector(ConditionalSelectorImpl conditionalSelector)
    {
        return conditionalSelector.toString();
    }

    // case 11: child Selector
    public void SacChildSelector (ChildSelectorImpl childSelector)
    {
        // keep getting till the end of ancestorSelector
        getSelectorOfOneRule(childSelector.getAncestorSelector());
        getSelectorOfOneRule(childSelector.getSimpleSelector());
//        return childSelector.toString();
    }

    public String SacPseudoClassCondition(PseudoClassConditionImpl pseudoClassCondition)
    {
        return pseudoClassCondition.getValue();
    }

    public String SacElementNodeSelector (ElementSelectorImpl elementSelector)
    {
        return elementSelector.toString();
    }

    public String SacClassCondition (ClassConditionImpl classCondition)
    {
        // ConditionType = 9
        return classCondition.getValue();
    }
}
