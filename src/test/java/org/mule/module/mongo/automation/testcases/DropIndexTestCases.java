package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.construct.Flow;
import org.mule.module.mongo.api.IndexOrder;

import com.mongodb.DBObject;

public class DropIndexTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (HashMap<String, Object>) context.getBean("createIndex");
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
			
			String indexName = testObjects.get("index").toString();
			
			MessageProcessor flow = lookupFlowConstruct("drop-index");
			MuleEvent response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			Collection<DBObject> payload = (Collection<DBObject>) response.getMessage().getPayload();
						
			boolean found = false;
			
			for (DBObject obj : payload) {
				String name = obj.get("name").toString();
				if (name.equals(indexName)) {
					found = true;
					break;
				}
			}
			
			assertTrue(!found);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
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
