package model;

/**
 * Created by smart on 31/03/2018.
 */
public class StyleDeclaration {
    private String property;
    private String propertyValue;

    public StyleDeclaration(){}
    public StyleDeclaration(String property, String propertyValue) {
        this.property = property;
        this.propertyValue = propertyValue;
    }

    public String getProperty() {
        return this.property;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getCssText() {
        return property + ": " + propertyValue;
    }
}
