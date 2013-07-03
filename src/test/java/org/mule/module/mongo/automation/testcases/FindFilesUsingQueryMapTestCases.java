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

public class FindFilesUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context
				.getBean("findFilesUsingQueryMap");

		assertEquals(
				"There should be 0 files in total before setting up the test",
				0, findFiles());
		createFileFromPayload(testObjects.get("filename1"));

		// create another file with a different name
		createFileFromPayload(testObjects.get("filename2"));
		assertEquals(
				"There should be 2 files in total after setting up the test",
				2, findFiles());
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@SuppressWarnings("unchecked")
	@Category({ SmokeTests.class, RegressionTests.class })
	@Test
	public void testFindFilesUsingQueryMap() {
		MuleEvent response = null;
		try {
			MessageProcessor findFilesUsingQueryMapFlow = lookupFlowConstruct("find-files-using-query-map");

			// queryAttribKey and queryAttribVal in testObjects are used in
			// findFilesUsingQueryMapFlow to query for a file with filename of
			// 'file2'
			// One such file should be found
			response = findFilesUsingQueryMapFlow
					.process(getTestEvent(testObjects));
			Iterable<DBObject> iterable = (Iterable<DBObject>) response
					.getMessage().getPayload();
			int filesFoundUsingQueryMap = iterableSize(iterable);

			assertEquals(
					"There should be 1 file with the name "
							+ testObjects.get("filename2"), 1,
					filesFoundUsingQueryMap);
			assertEquals("There should be 2 files in total", 2, findFiles());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
