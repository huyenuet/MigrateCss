package FpGrowth;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
	private String name;
	private int Id;

	private List<String> items = new ArrayList<>();

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}
	public String getName() {
		return this.name;
	}
	public int getId() {
	    return this.Id;
    }
    public void setName(String name) {
	    this.name = name;
    }
    public void setId(int Id) {
	    this.Id = Id;
    }
}