package lamdafu.dsciprim;

import static lamdafu.dsciprim.Primitives.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.math3.util.FastMath;

import cern.colt.buffer.DoubleBuffer;
import cern.colt.list.DoubleArrayList;
import cern.jet.random.engine.MersenneTwister;
import hep.aida.bin.QuantileBin1D;
import lamdafu.common.codec.PhoneticDouble;

public class StreamingCalc {

	private long count, countNull, countNaN, countUsable, countDistinct;
	private double madMeanDelta;
	private QuantileBin1D qb;
	private DoubleBuffer queue;
	private DoubleArrayList buff;
	private PhoneticDouble pd = new PhoneticDouble();
	private transient final PatriciaTrie<Object> p = new PatriciaTrie<>();
	private int quantiles;
	private int queueDepth;

	public StreamingCalc() {
		super();
	}

	public StreamingCalc(int quantiles, int queueDepth) {
		super();
		qb = new QuantileBin1D(false, Long.MAX_VALUE, 1.0e-5D, 1.0e-5D, quantiles,
				new MersenneTwister(Arrays.hashCode(new Object[] { Thread.currentThread(), System.nanoTime() })), false,
				false, 5);
		queue = qb.buffered(queueDepth);
		buff = new DoubleArrayList(queueDepth);
		this.quantiles = quantiles;
		this.queueDepth = queueDepth;
	};

	public void add(String... vals) {
		if (vals == null) {
			count++;
			countNull++;
		}
		for (String val : vals) {
			addOne(pd.encode(val));
		}
	}

	public void add(double... vals) {
		if (vals == null) {
			count++;
			countNull++;
		}
		for (double val : vals) {
			addOne(val);
		}
	}

	public void add(Double... vals) {
		if (vals == null) {
			count++;
			countNull++;
		}
		for (Double val : vals) {
			addOne(val);
		}
	}

	public void addOne(Double val) {
		count++;
		if (val == null) {
			count++;
			countNull++;
			return;
		} else if (Double.isNaN(val)) {
			countNaN++;
			return;
		}
		buff.add(val);
		if (++countUsable % queueDepth == 0) {
			microBatch();
		}
	}

	public int microBatch() {
		int size = buff.size();
		queue.addAllOf(buff);
		queue.flush();
		madMeanDelta += totalDelta(buff, qb.mean());
		buff.clear();
		return size;
	}

	private double totalDelta(DoubleArrayList vals, double centroid) {
		double result = 0;
		for (int x = 0; x < vals.size(); x++) {
			result += FastMath.abs(vals.get(x) - centroid);
		}
		return result;
	}

	public SortedMap<String, Object> snapshotPrimitives() {
		microBatch();
		p.put(COUNT.alias, count);
		p.put(COUNT_NULL.alias, countNull);
		p.put(COUNT_NaN.alias, countNaN);
		p.put(COUNT_USABLE.alias, countUsable);
		p.put(COUNT_DISTINCT.alias, countDistinct);
		p.put(MIN.alias, qb.min());
		p.put(MAX.alias, qb.max());
		p.put(MEDIAN.alias, qb.median());
		p.put(MEAN.alias, qb.mean());
		p.put(QUADRATIC_MEAN.alias, qb.rms());
		p.put(SUM.alias, qb.sum());
		p.put(SUM_OF_SQUARES.alias, qb.sumOfSquares());
		p.put(VARIANCE.alias, qb.variance());
		p.put(STANDARD_DEVIATION.alias, qb.standardDeviation());
		p.put(MEAN_ABS_DEVIATION.alias, madMeanDelta / countUsable);
		p.put(SKEWNESS.alias, qb.skew());
		p.put(KURTOSIS.alias, qb.kurtosis());
		String format = "%s%0" + (String.valueOf(quantiles).length() + 1) + "d";
		double quantile = 1D/quantiles;
		DoubleArrayList phis = new DoubleArrayList();
		for(double d = quantile; d <= 1D; d += quantile) {
			phis.add(d);
		}
		DoubleArrayList vals = qb.quantiles(phis);
		for(int x = 0; x < vals.size(); x++) {
			p.put(String.format(format, QUANTILE.alias, x + 1), vals.get(x));
		}
		return Collections.unmodifiableSortedMap(new PatriciaTrie<>(p));
	}

}
