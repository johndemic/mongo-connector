package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.construct.Flow;

import com.mongodb.DBObject;

public class CreateIndexTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createIndex");
			flow = (Flow) lookupFlowConstruct("create-collection");
			response = flow.process(getTestEvent(testObjects));
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

			String indexName = testObjects.get("field").toString();
			
			flow = lookupFlowConstruct("create-index");
			response = flow.process(getTestEvent(testObjects));
						
			flow = lookupFlowConstruct("list-indices");
			response = flow.process(getTestEvent(testObjects));
			Collection<DBObject> payload = (Collection<DBObject>) response.getMessage().getPayload();
						
			boolean found = false;
			
			for (DBObject obj : payload) {
				DBObject key = (DBObject) obj.get("key");
				if (key.containsField(indexName)) {
					found = true;
					break;
				}
			}
			
			assertTrue(found);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
//			flow = (Flow) lookupFlowConstruct("drop-index");
//			response = flow.process(getTestEvent(testObjects));
			flow = (Flow) lookupFlowConstruct("drop-collection");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
