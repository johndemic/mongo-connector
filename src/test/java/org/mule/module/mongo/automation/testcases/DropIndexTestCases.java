package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.construct.Flow;
import org.mule.module.mongo.api.IndexOrder;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DropIndexTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("dropIndex");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Create the index
			flow = lookupFlowConstruct("create-index");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testDropIndexByName() {
		try {

			testObjects = (HashMap<String, Object>) context.getBean("dropIndex");
			
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			
			testObjects.put("index", indexName);
			
			MessageProcessor flow = lookupFlowConstruct("drop-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			List<DBObject> payload = (List<DBObject>) response.getMessage().getPayload();
						
			
			assertFalse(existsInList(payload, indexName));
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
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
