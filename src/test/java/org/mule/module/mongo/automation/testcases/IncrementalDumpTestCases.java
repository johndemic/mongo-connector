/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.processor.MessageProcessor;

public class IncrementalDumpTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("dump");
		
		File dumpOutputDir = new File("./" + testObjects.get("outputDirectory"));
		
		assertFalse("dump directory should not exist before test runs", dumpOutputDir.exists());
	}

	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(new File("./" + testObjects.get("outputDirectory")));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testIncrementalDump() {
		try {
			MessageProcessor dumpFlow = lookupFlowConstruct("dump");
			
			dumpFlow.process(getTestEvent(testObjects));
			
			File dumpOutputDir = new File("./" + testObjects.get("outputDirectory"));
			assertTrue("dump directory should exist after test runs", dumpOutputDir.exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
