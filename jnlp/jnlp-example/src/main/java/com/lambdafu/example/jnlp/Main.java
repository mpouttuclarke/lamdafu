/**
 * 
 */
package com.lambdafu.example.jnlp;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;

/**
 * 
 * 
 * @author mpouttuc
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final BasicService sm = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
		System.out.println("Hello World from JNLP program loaded from: " + sm.getCodeBase());
	}

}
