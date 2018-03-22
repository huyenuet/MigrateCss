
import org.w3c.dom.css.CSSRuleList;

/**
 * Created by smart on 22/03/2018.
 */
public class StyleDeclaration {
    protected static StyleDeclaration styleDecl;
    public void groupDelc (CSSRuleList ruleList) {
        styleDecl = new StyleDeclaration();
    }

}
