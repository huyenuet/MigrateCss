package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smart on 31/03/2018.
 */
public class StyleRuleList {
    List<Block> ruleList = new ArrayList<>();

    public List<Block> getBlocks() {
        return this.ruleList;
    }

    public void setRuleList(List<Block> ruleList) {
        this.ruleList = ruleList;
    }

    public void add (Block block) {
        this.ruleList.add(block);
    }

    public Integer size(){
        return this.ruleList.size();
    }

    public void remove(Block block) {
        this.ruleList.remove(block);
    }

    public Block getBlockByName (String selectorName) {
        Block block = new Block();
        for (Block block_: this.ruleList) {
            if (block_.getSelectorName().equals(selectorName)) {
                block = block_;
                break;
            }
        }
        return block;
    }
}