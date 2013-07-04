/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.mongodb.DBObject;

public class GetFileContentTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("getFileContent");
		
		assertEquals("There should be 0 files in total before setting up the test", 0, findFiles());
		
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename2"));
		
		assertEquals("There should be 2 files in total after setting up the test", 2, findFiles());
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
		assertEquals("There should be 0 files in total after tearing down the test", 0, findFiles());
	}

	@Category({ RegressionTests.class })
	@Test
	public void testGetFileContent() {
		try {
			MessageProcessor getFileContentFlow = lookupFlowConstruct("get-file-content");
			
			DBObject queryRef = (DBObject) testObjects.get("queryRef");
			queryRef.put("filename", testObjects.get("filename1"));
			
			MuleEvent response = getFileContentFlow.process(getTestEvent(testObjects));
			
			assertNotNull(response.getMessage());
			assertNotNull(response.getMessage().getPayload());
			assertTrue(response.getMessage().getPayload() instanceof InputStream);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
