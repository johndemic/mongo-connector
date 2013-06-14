/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class FindFilesTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("findFiles");
		
		assertEquals("There should be 0 files in total before setting up the test", 0, findFiles());
		
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename1"));
	}
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testFindFiles() {
		assertEquals("There should be 2 files found", 2, findFiles());
	}
	
}
