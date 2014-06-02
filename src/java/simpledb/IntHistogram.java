package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
	private static final Object EQUALS = null;
	/**
	 * buckets
	 */
	private int[] m_buckets;
	private int m_bucketCount;
	private int m_interval;
	private int m_min;
	private int m_max;
	private int m_tupCount;
	

	private double getSelWithinInterval(int index, int offset, boolean left)
	{
		if(left){
			if(offset < 0)
				return 0;
			return offset/m_interval*m_buckets[index]/m_interval;
		}else{
			if(offset > m_interval)
				return 0;
			return (1-offset/m_interval)*m_buckets[index]/m_interval;
		}
	}
	
	private double getSelBetweenBuckets(int left,int right){
		if (left>right)
			return 0;
		int sum = 0;
		for (int i = left; i<=right; i++)
		{
			sum += m_buckets[i];
		}
		return sum/(right-left+1)/m_interval;
	}
    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here
    	double range = max - min;
    	m_min = min;
    	m_max = max;
    	m_tupCount = 0;
    	m_interval = (int) Math.ceil(range/buckets);
    	m_buckets = new int[buckets];
    	m_bucketCount = buckets;
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here
    	m_tupCount++;
    	int index = getIndex(v);
    	m_buckets[index]++;
    }

    public double getEql(int v)
    {
    	int index = (int) Math.floor((v-m_min)/m_interval);
    	int height = m_buckets[index];
    	double eqls =  (double)height/m_interval/m_tupCount;
    	return eqls;
    }
    public double forGetEqls(int a, int b)
    {
    	double sum = 0;
    	for (int i=a;i<=b;i++)
    	{
    		sum += getEql(i);
    	}
    	return sum;
    }
    
    /**
     * get index for int v
     * 
     */
    private int getIndex(int v){
		int index = (int) Math.floor((v-m_min)/(double)m_interval);
		if (index == m_bucketCount)
			index--;
		return index;
    }    
    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {

    	// some code goes here
    	int index = getIndex(v);
    	//int offset = (v-(m_min+index*m_interval));
    	boolean minflag=false;
    	boolean maxflag=false;
    	if(v<m_min)
    		minflag = true;
    	if(v>m_max)
    		maxflag = true;
    	
    	switch(op){
    	case EQUALS:
    		if(minflag || maxflag)
    			return 0;
    		int height = m_buckets[index];
        	double eqls =  ((double)height)/m_interval/m_tupCount;
        	return eqls;
		case GREATER_THAN:
			if(minflag)
				return 1;
			if(maxflag)
				return 0;
			if(v==m_max)
				return 0;
    		return forGetEqls(v+1,m_max);
		case LESS_THAN:
			if(minflag)
				return 0;
			if(maxflag)
				return 1;
			if(v==m_min)
				return 0;
    		return forGetEqls(m_min, v-1);
		case GREATER_THAN_OR_EQ:
			if(minflag)
				return 1;
			if(maxflag)
				return 0;
    		return forGetEqls(v,m_max);
		case LESS_THAN_OR_EQ:
			if(minflag)
				return 0;
			if(maxflag)
				return 1;
    		return forGetEqls(m_min,v);
		case NOT_EQUALS: 
    		if(minflag || maxflag)
    			return 1;
    		height = m_buckets[index];
        	eqls =  ((double)height)/m_interval/m_tupCount;
        	return 1-eqls;
		default:
			return 0;
    	}
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {

        // some code goes here
        return null;
    }
}
