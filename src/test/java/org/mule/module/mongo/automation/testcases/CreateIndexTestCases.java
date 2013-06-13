package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.DBObject;

public class CreateIndexTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createIndex");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testCreateIndex() {
		try {

			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			String indexName = indexKey + "_" + indexOrder.getValue();
	
			MessageProcessor flow = lookupFlowConstruct("create-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			List<DBObject> payload = (ArrayList<DBObject>) response.getMessage().getPayload();
								
			assertTrue(existsInList(payload, indexName));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private boolean existsInList(List<DBObject> objects, String indexName) {
		for (DBObject obj : objects) {
			if (obj.get("name").equals(indexName)) {
				return true;
			}
		}
		return false;
	}
	
	@After
	public void tearDown() {
		try {
			// Drop the created index
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			testObjects.put("index", indexName);
			
			MessageProcessor flow = lookupFlowConstruct("drop-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Drop the collection
			flow = lookupFlowConstruct("drop-collection");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
