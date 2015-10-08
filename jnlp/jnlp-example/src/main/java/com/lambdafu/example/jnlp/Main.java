/**
 * 
 */
package com.lambdafu.example.jnlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;

import javax.jnlp.BasicService;
import javax.jnlp.DownloadService;
import javax.jnlp.ServiceManager;

import org.apache.commons.io.IOUtils;

/**
 * 
 * 
 * @author mpouttuc
 *
 */
public class Main {

	private static final String FILE_NAME = "file.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final BasicService sm = (BasicService)ServiceManager.lookup(BasicService.class.getName());
		final URL codeBaseURL = sm.getCodeBase();
		System.out.println("Hello World2 from JNLP program loaded from: " + codeBaseURL);
		final URL urlFrom = new URL(codeBaseURL, FILE_NAME);
		final URLConnection urlConn = urlFrom.openConnection();
		final long srcModified = urlConn.getLastModified();
		System.out.println(FILE_NAME + " last modified " + new Date(srcModified));
		final File outFile = new File(FILE_NAME);
		if(!outFile.exists() && outFile.lastModified() < srcModified) {
			System.out.println("Creating " + outFile.getAbsolutePath());
			final InputStream in = urlConn.getInputStream();
			final OutputStream out = new FileOutputStream(outFile);
			IOUtils.copyLarge(in, out, new byte[64 * 1024]);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} else {
			System.out.println("Skipping");
		}
	}

}
