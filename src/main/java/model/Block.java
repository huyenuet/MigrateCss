package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smart on 31/03/2018.
 */
public class Block {
    private String selectorName;
    private List<String> properties = new ArrayList<>();
    private List<StyleDeclaration> block = new ArrayList<>();

    public List<StyleDeclaration> getStyleDeclarations() {
        return block;
    }

    public void setBlock(List<StyleDeclaration> block) {
        this.block = block;
    }

    public void add (StyleDeclaration styleDeclaration) {
        this.block.add(styleDeclaration);
        this.properties.add(styleDeclaration.getProperty());
    }

    public String getSelectorName() {
        return selectorName;
    }

    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    public List<String> getProperties() {
        return properties;
    }
}
