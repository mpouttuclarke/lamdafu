package lamdafu.primitives;

import static lamdafu.primitives.Primitives.COUNT;
import static lamdafu.primitives.Primitives.COUNT_DISTINCT;
import static lamdafu.primitives.Primitives.COUNT_NULL;
import static lamdafu.primitives.Primitives.COUNT_NaN;
import static lamdafu.primitives.Primitives.COUNT_USABLE;
import static lamdafu.primitives.Primitives.KURTOSIS;
import static lamdafu.primitives.Primitives.MAX;
import static lamdafu.primitives.Primitives.MEAN;
import static lamdafu.primitives.Primitives.MEAN_ABS_DEVIATION;
import static lamdafu.primitives.Primitives.MEDIAN;
import static lamdafu.primitives.Primitives.MIN;
import static lamdafu.primitives.Primitives.QUADRATIC_MEAN;
import static lamdafu.primitives.Primitives.QUANTILE;
import static lamdafu.primitives.Primitives.SKEWNESS;
import static lamdafu.primitives.Primitives.STANDARD_DEVIATION;
import static lamdafu.primitives.Primitives.SUM;
import static lamdafu.primitives.Primitives.SUM_OF_SQUARES;
import static lamdafu.primitives.Primitives.VARIANCE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.FastMath;

import cern.colt.buffer.DoubleBuffer;
import cern.colt.list.DoubleArrayList;
import cern.jet.random.engine.MersenneTwister;
import hep.aida.bin.QuantileBin1D;
import lamdafu.base.LamdaMap;
import lamdafu.boot.Cast;
import lamdafu.codec.Unibit;

public class StreamCalc extends LamdaMap {

	static final long serialVersionUID = -4240126532387563198L;
	long count, countNull, countNaN, countUsable, countDistinct;
	double madMeanDelta;
	QuantileBin1D qb;
	DoubleBuffer queue;
	DoubleArrayList buff;
	String qFormat;
	DoubleArrayList phis;
	int queueDepth;
	Unibit unibit;

	public StreamCalc() {
		super();
	}

	public StreamCalc(int quantiles, int queueDepth) {
		super();
		qb = new QuantileBin1D(false, Long.MAX_VALUE, 1.0e-5D, 1.0e-5D, quantiles,
				new MersenneTwister(Arrays.hashCode(new Object[] { Thread.currentThread(), System.nanoTime() })), false,
				false, 5);
		buff = new DoubleArrayList(queueDepth);
		queue = qb.buffered(queueDepth);
		qFormat = "%s%0" + (String.valueOf(quantiles).length() + 1) + "d";
		phis = calcPhi(quantiles);
		this.queueDepth = queueDepth;
		unibit = new Unibit();
		unibit.initCache();
	};

	public void add(Object... vals) {
		if (vals == null) {
			count++;
			countNull++;
		}
		for (Object val : vals) {
			if (String.class.isAssignableFrom(val.getClass())) {
				addOne(unibit.encode(String.valueOf(val)));
			} else if (Double.class.isAssignableFrom(val.getClass())) {
				addOne((Double) val);
			}
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

	double totalDelta(DoubleArrayList vals, double centroid) {
		double result = 0;
		for (int x = 0; x < vals.size(); x++) {
			result += FastMath.abs(vals.get(x) - centroid);
		}
		return result;
	}

	public SortedMap<String, Object> snapshot() {
		microBatch();
		PatriciaTrie<Object> p = new PatriciaTrie<>();
		p.put(COUNT.alias, count);
		p.put(COUNT_NULL.alias, countNull);
		p.put(COUNT_NaN.alias, countNaN);
		p.put(COUNT_USABLE.alias, countUsable);
		// TODO: add distinct calc
		p.put(COUNT_DISTINCT.alias, countDistinct);
		p.put(MIN.alias, Unibit.decode(qb.min()));
		p.put(MAX.alias, Unibit.decode(qb.max()));
		p.put(MEDIAN.alias, Unibit.decode(qb.median()));
		p.put(MEAN.alias, qb.mean());
		p.put(QUADRATIC_MEAN.alias, qb.rms());
		p.put(SUM.alias, qb.sum());
		p.put(SUM_OF_SQUARES.alias, qb.sumOfSquares());
		p.put(VARIANCE.alias, qb.variance());
		p.put(STANDARD_DEVIATION.alias, qb.standardDeviation());
		p.put(MEAN_ABS_DEVIATION.alias, madMeanDelta / countUsable);
		p.put(SKEWNESS.alias, qb.skew());
		p.put(KURTOSIS.alias, qb.kurtosis());
		DoubleArrayList vals = qb.quantiles(phis);
		for (int x = 0; x < vals.size(); x++) {
			p.put(String.format(qFormat, QUANTILE.alias, x), Unibit.decode(vals.get(x)));
		}
		// TODO: make NaN, Infinity, -Infinity configurable
		for (Map.Entry<String, Object> entry : p.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Double) {
				Double asDouble = Cast.as(value);
				if (asDouble == null || !Double.isFinite(asDouble)) {
					entry.setValue(String.valueOf(asDouble));
				}
			} else if (!(value instanceof Number)) {
				entry.setValue(String.valueOf(value));
			}
		}
		return Collections.unmodifiableSortedMap(new PatriciaTrie<>(p));
	}

	protected DoubleArrayList calcPhi(int quantiles) {
		DoubleArrayList phis = new DoubleArrayList();
		double incr = 1.0D / quantiles;
		for (int i = 1; i <= quantiles; i++) {
			phis.add(i * incr);
		}
		return phis;
	}

	@Override
	public Object put(String key, Object value) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException("key null or empty");
		} else if (StringUtils.equals("add", key)) {
			if (value == null) {
				add((String) null);
			} else if (value.getClass().isArray()) {
				add(value);
			} else {
				if (value instanceof Double) {
					addOne((Double) value);
				} else if (value instanceof String) {
					add((String) value);
				}
			}
		}
		return Void.TYPE;
	}

	@Override
	public Object get(Object key) {
		if (key != null && StringUtils.equals("snapshot", String.valueOf(key))) {
			return snapshot();
		}
		return null;
	}

}
