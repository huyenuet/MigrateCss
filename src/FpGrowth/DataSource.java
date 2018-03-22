package FpGrowth;

public interface DataSource {

	Transaction next();

	boolean hasNext();

	void reset();

}
