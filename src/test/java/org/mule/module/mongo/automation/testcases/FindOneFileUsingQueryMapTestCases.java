/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class FindOneFileUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("findOneFileUsingQueryMap");

		assertEquals(
				"There should be 0 files in total before setting up the test",
				0, findFiles());

		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename2"));
		
		assertEquals(
				"There should be 3 files in total after setting up the test",
				3, findFiles());
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ SmokeTests.class, SanityTests.class })
	@Test
	public void testFindOneFileUsingQueryMap() {
		try {
			MessageProcessor findOneUsingQueryMapFile = lookupFlowConstruct("find-one-file-using-query-map");
			
			MuleEvent response = findOneUsingQueryMapFile.process(getTestEvent(testObjects));
			
			DBObject dbObj = (DBObject) response.getMessage().getPayload();
			
			assertEquals("The file found should have the name " + testObjects.get("filename1"), testObjects.get("filename1"), dbObj.get("filename"));
			assertEquals("There should be 3 files in total", 3, findFiles());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
