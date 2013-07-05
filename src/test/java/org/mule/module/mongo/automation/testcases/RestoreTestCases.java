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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;

import com.mongodb.DBObject;

public class RestoreTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("restore");
		try {
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder =(IndexOrder) testObjects.get("order");
			
			String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
			File dumpOutputDir = new File("./" + testObjects.get("outputDirectory"));
			
			MessageProcessor restoreTestCaseSetupFlow = lookupFlowConstruct("createIndex_Dump");
			restoreTestCaseSetupFlow.process(getTestEvent(testObjects));
			
			assertTrue("dump directory should exist after creating it", dumpOutputDir.exists());
			
			MessageProcessor listIndicesFlow = lookupFlowConstruct("list-indices-for-drop-restore");
			MuleEvent responseEvent = listIndicesFlow.process(getTestEvent(testObjects));
			
			List<DBObject> payload = (List<DBObject>) responseEvent.getMessage().getPayload();
			assertTrue("After creating the index with index name = " + indexName + " it should exist", MongoHelper.indexExistsInList(payload, indexName));
			
			// drop index
			testObjects.put("index", indexName);			
			MessageProcessor dropIndexFlow = lookupFlowConstruct("drop-index-for-drop-restore");
			try {
				dropIndexFlow.process(getTestEvent(testObjects));
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			
			MuleEvent listIndicesResponse = listIndicesFlow.process(getTestEvent(testObjects));
			
			List<DBObject> listIndicesResponsePayload = (List<DBObject>) listIndicesResponse.getMessage().getPayload();
			assertFalse("After dropping the index with index name = " + indexName + " it should not exist", MongoHelper.indexExistsInList(listIndicesResponsePayload, indexName));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@After
	public void tearDown() {
		try {
			File dumpOutputDir = new File("./" + testObjects.get("outputDirectory"));
			FileUtils.deleteDirectory(dumpOutputDir);
			assertFalse("dump directory should not exist after test runs", dumpOutputDir.exists());
			
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = (IndexOrder) testObjects.get("order");
			
			String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
			
			// drop index
			testObjects.put("index", indexName);			
			MessageProcessor dropIndexFlow = lookupFlowConstruct("drop-index-for-drop-restore");
			try {
				dropIndexFlow.process(getTestEvent(testObjects));
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
						
			MessageProcessor flow = lookupFlowConstruct("list-indices-for-drop-restore");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			List<DBObject> payload = (List<DBObject>) response.getMessage().getPayload();
			
			assertFalse("After deleting the index with index name = " + indexName + " it should not exist", MongoHelper.indexExistsInList(payload, indexName));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Category({RegressionTests.class})
	@Test
	@Ignore
	public void testRestore() {
		try {
			MessageProcessor restoreFlow = lookupFlowConstruct("restore");
			
			restoreFlow.process(getTestEvent(testObjects));
			
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = (IndexOrder) testObjects.get("order");
			
			String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
			
			MessageProcessor listIndicesFlow = lookupFlowConstruct("list-indices-for-drop-restore");
			MuleEvent responseEvent = listIndicesFlow.process(getTestEvent(testObjects));
			
			List<DBObject> payload = (List<DBObject>) responseEvent.getMessage().getPayload();
			
			assertTrue("After restoring the database, the index with index name = " + indexName + " should exist", MongoHelper.indexExistsInList(payload, indexName));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
