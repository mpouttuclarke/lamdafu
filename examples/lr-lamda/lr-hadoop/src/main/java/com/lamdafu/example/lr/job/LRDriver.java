/**
 * 
 */
package com.lamdafu.example.lr.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Calculates linear regression. The input file(s) shall contain tab delimited
 * group\tmeasure and the output file{s} will contain tab delimited
 * group\tslope\tintercept
 * 
 * @author mpouttuc
 *
 */
public class LRDriver extends Configured implements Tool {

	private static final String CONF_LR_URL = LRDriver.class.getName() + ".impl.jar";

	public static class LRReducer extends Reducer<Text, Text, Text, Text> {

		private static final String LOADER_ERROR = CONF_LR_URL + " needs to be set to a valid uber jar URL";
		private Map<Object, Object> lr;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce
		 * .Reducer.Context)
		 */
		@Override
		protected void setup(Reducer<Text, Text, Text, Text>.Context ctx) throws IOException, InterruptedException {
			lr = initLamda(ctx.getConfiguration());
		}

		/**
		 * Loads the Lamda in is's own private class loader, using a
		 * java.util.Map for interaction with the underlying implementation.
		 * This prevents transitive dependency collisions, because Lamda
		 * dependencies are loaded only from the uber jar and the default JDK
		 * class loader.
		 * 
		 * @param conf
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Map<Object, Object> initLamda(final Configuration conf) {
			ServiceLoader<Map> loader;
			try {
				final URL[] urls = new URL[] { new URL(conf.get(CONF_LR_URL,
						new File("../lr-acm-impl/target/lr-impl-uber.jar").toURI().toURL().toString())) };
				loader = ServiceLoader.load(Map.class, new URLClassLoader(urls, Map.class.getClassLoader()));
			} catch (Exception e) {
				throw new IllegalStateException(LOADER_ERROR, e);
			}
			Iterator<Map> i = loader.iterator();
			if (!i.hasNext()) {
				throw new IllegalStateException(LOADER_ERROR);
			}
			return i.next();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object,
		 * java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		@Override
		protected void reduce(Text key, Iterable<Text> vals, Reducer<Text, Text, Text, Text>.Context ctx)
				throws IOException, InterruptedException {
			for (Text val : vals) {
				String[] splits = val.toString().split("\\t");
				if (splits == null || splits.length < 2) {
					continue;
				}
				double x = NumberUtils.toDouble(splits[0], Double.NaN);
				double y = NumberUtils.toDouble(splits[1], Double.NaN);
				if (!Double.isNaN(x) && !Double.isNaN(y)) {
					lr.put("\u03BBlr\u0394add", new double[] { x, y });
				}
			}
			ctx.write(key, new Text(lr.get("\u03BBlr\u0398slope") + "\t" + lr.get("\u03BBlr\u0398intercept")));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
	 */
	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: LRDriver <input path> <output path> [<LR uber jar URL>]");
			return -1;
		}

		final Configuration conf = getConf();
		Job job = Job.getInstance(conf, "LRDriver");
		job.setJarByClass(LRDriver.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		if (args.length > 2) {
			conf.set(CONF_LR_URL, args[2]);
		}

		job.setReducerClass(LRReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new LRDriver(), args));
	}

}
