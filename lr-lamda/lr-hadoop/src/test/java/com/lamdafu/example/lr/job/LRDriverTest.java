/**
 * 
 */
package com.lamdafu.example.lr.job;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Before;
import org.junit.Test;

import com.lamdafu.example.lr.job.LRDriver;

/**
 * @author mpouttuc
 *
 */
public class LRDriverTest {

	private static final String TARGET_TEST = "target/test/";
	private static final File OUTPUT_DIR = new File(TARGET_TEST + LRDriverTest.class.getSimpleName());

	private Configuration conf = new Configuration(false);

	@Before
	public void before() {
		try {
			FileUtils.forceDelete(OUTPUT_DIR);
			FileUtils.forceMkdir(OUTPUT_DIR);
		} catch (IOException e1) {
			// Ignore
		}
		conf.set("fs.defaultFS", OUTPUT_DIR.toURI().toString());
		conf.set("hadoop.tmp.dir", OUTPUT_DIR.toURI().toString() + "/tmp");
		conf.setBoolean("io.native.lib.available", false);
	}

	@Test
	public void test() {
		try {
			assertEquals(0, ToolRunner.run(conf, new LRDriver(), new String[] {
					new File("src/test/resources/data/lr").toURI().toString(), OUTPUT_DIR + "/output" }));
		} catch (Exception e) {
			e.printStackTrace();
			fail(String.valueOf(e));
		}
	}

}
