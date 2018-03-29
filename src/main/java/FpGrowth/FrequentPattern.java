package FpGrowth;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FrequentPattern {

	private List<String> items = new ArrayList<String>();

	private Integer supportCount;
	private Double support;
	private static final String DELIMITER = ",";

	public FrequentPattern(String items, Integer supportCount, Double support) {

	    // 1. get all rgb item
		while (items.contains("rgb")) {
            items = streamItemsContainRgb(items);
        }

        if (items.contains(",")) {
            StringTokenizer tokenizer = new StringTokenizer(items, DELIMITER);
            while (tokenizer.hasMoreTokens())
                this.items.add(tokenizer.nextToken());
        } else {
            this.items.add(items);
        }

		this.supportCount = supportCount;
		this.support = support;
	}

	public String streamItemsContainRgb(String items) {
	    /*a.if items contains rgb, we need to
        *   1. get items before rbg (maybe doesnt exist)
        *   2. get item rgb
        *   3. get item after rbg (maybe doesnt exist)
        *   4. concat items before and after rgb
        *   5. repeat check if new items have rgb
        *       5.1. if yes: back to 1
        *       5.2. if no: end
        * b.else: add items then end.
        * */
        int rgbIndex = -1;
        int delimIndex = -1;
        String beforeRgbItems = "";
        String afterRgbItems = "";
        String rgbItem = "";

        // 1. get items before rbg (maybe doesnt exist)
        rgbIndex = items.indexOf("rgb");
        beforeRgbItems = items.substring(0,rgbIndex);
        if (beforeRgbItems.contains(",")) {
            delimIndex = beforeRgbItems.lastIndexOf(',');
            beforeRgbItems = items.substring(0,delimIndex-1);
            items = items.substring(delimIndex+1,items.length());
        }

        // 2. get item rgb
        int n = 3;
        int count = StringUtils.countMatches(items, ",");
        // if items have only 2 ',' that means items have only one item (rgb)
        if (count == 2) {
            rgbItem = items;
            this.items.add(rgbItem);
        }
        // get rgb item from items
        else {
            for (int i = 0; i < items.length(); i++) {
                if (items.charAt(i) == ',') {
                    n--;
                    if (n == 0) delimIndex = i;
                }
            }
            rgbItem = items.substring(0,delimIndex-1);
            this.items.add(rgbItem);

            // 3. get item after rbg(maybe doesnt exist)
            afterRgbItems = items.substring(delimIndex+1, items.length());
        }

        // 4. concat items before and after rgb
        items = beforeRgbItems.concat("," + afterRgbItems);
        return items;
    }

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public Integer getSupportCount() {
		return supportCount;
	}

	public void setSupportCount(Integer support) {
		this.supportCount = support;
	}

	public Double getSupport() {
		return support;
	}

	public void setSupport(Double support) {
		this.support = support;
	}

	public String toString() {
		return "FrequentPattern[" + items + ":" + supportCount + "]";
	}

}
