package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableStats represents statistics (e.g., histograms) about base tables in a
 * query. 
 * 
 * This class is not needed in implementing lab1 and lab2.
 */
public class TableStats {

    private static final ConcurrentHashMap<String, TableStats> statsMap = new ConcurrentHashMap<String, TableStats>();

    static final int IOCOSTPERPAGE = 1000;

    public static TableStats getTableStats(String tablename) {
        return statsMap.get(tablename);
    }

    public static void setTableStats(String tablename, TableStats stats) {
        statsMap.put(tablename, stats);
    }
    
    public static void setStatsMap(HashMap<String,TableStats> s)
    {
        try {
            java.lang.reflect.Field statsMapF = TableStats.class.getDeclaredField("statsMap");
            statsMapF.setAccessible(true);
            statsMapF.set(null, s);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, TableStats> getStatsMap() {
        return statsMap;
    }

    public static void computeStatistics() {
        Iterator<Integer> tableIt = Database.getCatalog().tableIdIterator();

        System.out.println("Computing table stats.");
        while (tableIt.hasNext()) {
            int tableid = tableIt.next();
            TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
            setTableStats(Database.getCatalog().getTableName(tableid), s);
        }
        System.out.println("Done.");
    }

    /**
     * Number of bins for the histogram. Feel free to increase this value over
     * 100, though our tests assume that you have at least 100 bins in your
     * histograms.
     */
    static final int NUM_HIST_BINS = 100;

    private HeapFile m_file;
    private int m_io;
    
    private HashMap<String, IntHistogram> m_histos;
    private HashMap<String, StringHistogram> m_string_histos;

    private HashMap<String,Integer> m_min;
    private HashMap<String,Integer> m_max;
    
    private TupleDesc m_tupleDes;
    private int m_tuple_count;
    private void minmaxInitializer()
    {
        
        //get iterator
        DbFileIterator itr = m_file.iterator(null);
        try {
			itr.open();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			while(itr.hasNext())
			{
				m_tuple_count++;
				Tuple tuple = itr.next();
				for(int i = 0; i < m_tupleDes.numFields();i++)
				{
					//do minmax
					if(m_tupleDes.getFieldType(i).equals(Type.INT_TYPE))
					{
						String fieldName= m_tupleDes.getFieldName(i);
						int fieldVal = ((IntField) tuple.getField(i)).getValue();
						if(m_min.containsKey(fieldName))
						{
							m_min.put(fieldName, Math.min(fieldVal, m_min.get(fieldName)));
							m_max.put(fieldName, Math.max(fieldVal, m_max.get(fieldName)));
						}
						else{
							//initialize
							m_min.put(fieldName, fieldVal);
							m_max.put(fieldName, fieldVal);
						}
					}
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void histoInitilizer()
    {
        //get iterator
        DbFileIterator itr = m_file.iterator(null);
        try {
			itr.open();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			while(itr.hasNext())
			{
				Tuple tuple = itr.next();
				for(int i = 0; i < m_tupleDes.numFields();i++)
				{
					//do INTHISTO
					if(m_tupleDes.getFieldType(i).equals(Type.INT_TYPE))
					{
						String fieldName= m_tupleDes.getFieldName(i);
						int fieldVal = ((IntField) tuple.getField(i)).getValue();
						if(m_histos.containsKey(fieldName))
						{
							IntHistogram m_newhisto = m_histos.get(fieldName);
							m_newhisto.addValue(fieldVal);
							m_histos.put(fieldName,m_newhisto);
						}
						else{
							//initialize
							m_histos.put(fieldName, new IntHistogram(NUM_HIST_BINS, m_min.get(fieldName), m_max.get(fieldName)));
							IntHistogram m_newhisto = m_histos.get(fieldName);
							m_newhisto.addValue(fieldVal);
							m_histos.put(fieldName,m_newhisto);
						}
					}
					else{//string type
						String fieldName= m_tupleDes.getFieldName(i);
						String fieldVal = ((StringField) tuple.getField(i)).getValue();
						if(m_histos.containsKey(fieldName))
						{
							StringHistogram m_newhisto = m_string_histos.get(fieldName);
							m_newhisto.addValue(fieldVal);
							m_string_histos.put(fieldName,m_newhisto);
						}
						else{
							//initialize
							m_string_histos.put(fieldName, new StringHistogram(NUM_HIST_BINS));
							StringHistogram m_newhisto = m_string_histos.get(fieldName);
							m_newhisto.addValue(fieldVal);
							m_string_histos.put(fieldName,m_newhisto);
						}
					}
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * Create a new TableStats object, that keeps track of statistics on each
     * column of a table
     * 
     * @param tableid
     *            The table over which to compute statistics
     * @param ioCostPerPage
     *            The cost per page of IO. This doesn't differentiate between
     *            sequential-scan IO and disk seeks.
     */
    public TableStats(int tableid, int ioCostPerPage) {
        // For this function, you'll have to get the
        // DbFile for the table in question,
        // then scan through its tuples and calculate
        // the values that you need.
        // You should try to do this reasonably efficiently, but you don't
        // necessarily have to (for example) do everything
        // in a single scan of the table.
        // some code goes here
    	m_file = (HeapFile) Database.getCatalog().getDatabaseFile(tableid);
    	m_io = ioCostPerPage;
    	m_tuple_count = 0;
    	m_tupleDes = m_file.getTupleDesc();
    	m_histos = new HashMap<String, IntHistogram>();

        m_min = new HashMap<String, Integer>();
        m_max = new HashMap<String, Integer>();;
    	minmaxInitializer();
    	histoInitilizer();
    }

    /**
     * Estimates the cost of sequentially scanning the file, given that the cost
     * to read a page is costPerPageIO. You can assume that there are no seeks
     * and that no pages are in the buffer pool.
     * 
     * Also, assume that your hard drive can only read entire pages at once, so
     * if the last page of the table only has one tuple on it, it's just as
     * expensive to read as a full page. (Most real hard drives can't
     * efficiently address regions smaller than a page at a time.)
     * 
     * @return The estimated cost of scanning the table.
     */
    public double estimateScanCost() {
        // some code goes here
        return m_file.numPages()*m_io;
    }

    /**
     * This method returns the number of tuples in the relation, given that a
     * predicate with selectivity selectivityFactor is applied.
     * 
     * @param selectivityFactor
     *            The selectivity of any predicates over the table
     * @return The estimated cardinality of the scan with the specified
     *         selectivityFactor
     */
    public int estimateTableCardinality(double selectivityFactor) {
        // some code goes here
        return (int) (selectivityFactor*m_tuple_count);
    }

    /**
     * The average selectivity of the field under op.
     * @param field
     *        the index of the field
     * @param op
     *        the operator in the predicate
     * The semantic of the method is that, given the table, and then given a
     * tuple, of which we do not know the value of the field, return the
     * expected selectivity. You may estimate this value from the histograms.
     * */
    public double avgSelectivity(int field, Predicate.Op op) {
        // some code goes here
    	return 1.0;
    }

    /**
     * Estimate the selectivity of predicate <tt>field op constant</tt> on the
     * table.
     * 
     * @param field
     *            The field over which the predicate ranges
     * @param op
     *            The logical operation in the predicate
     * @param constant
     *            The value against which the field is compared
     * @return The estimated selectivity (fraction of tuples that satisfy) the
     *         predicate
     */
    public double estimateSelectivity(int field, Predicate.Op op, Field constant) {
        // some code goes here
    	String fieldName = m_tupleDes.getFieldName(field);
        Type T = constant.getType();
        if(T.equals(Type.INT_TYPE)){
        	return m_histos.get(fieldName).estimateSelectivity(op, ((IntField) constant).getValue());
        }else{
        	//string type
        	return m_string_histos.get(fieldName).estimateSelectivity(op, ((StringField) constant).getValue());
        }
    }

    /**
     * return the total number of tuples in this table
     * */
    public int totalTuples() {
        // some code goes here
        return m_tuple_count;
    }

}
