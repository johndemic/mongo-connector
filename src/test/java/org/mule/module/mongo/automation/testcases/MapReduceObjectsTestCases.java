package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.MongoCollection;
import org.mvel2.ast.AssertNode;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MapReduceObjectsTestCases extends MongoTestParent {


	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			// Create the collection
			testObjects = (Map<String, Object>) context.getBean("mapReduceObjects");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));

			int numApples = Integer.parseInt(testObjects.get("numApples").toString());
			int numOranges = Integer.parseInt(testObjects.get("numOranges").toString());
			
			// Create sample objects with which we can map reduce
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numApples; i++) {
				DBObject obj = new BasicDBObject("item", "apple");
				objects.add(obj);
			}
			
			for (int i = 0; i < numOranges; i++) {
				DBObject obj = new BasicDBObject("item", "orange");
				objects.add(obj);
			}
			
			// Insert the objects into the collection
			insertObjects(objects);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testMapReduceObjects() {
		try {

			int numApples = Integer.parseInt(testObjects.get("numApples").toString());
			int numOranges = Integer.parseInt(testObjects.get("numOranges").toString());
						
			MessageProcessor flow = lookupFlowConstruct("map-reduce-objects");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			MongoCollection resultCollection = (MongoCollection) response.getMessage().getPayload();
			assertTrue(resultCollection != null);
			assertTrue(resultCollection.size() == 2); // We only have apples and oranges
								
			for (DBObject obj : resultCollection) {
				DBObject valueObject = (DBObject) obj.get("value");
				assertNotNull(valueObject);
				if (obj.get("_id").equals("apple")) {
					assertTrue(valueObject.containsField("count"));
					assertTrue((Double)valueObject.get("count") == numApples); // map reduce returns doubles, typecast to Double and compare
				}
				else {
					if (obj.get("_id").equals("orange")) {
						assertTrue(valueObject.containsField("count"));
						assertTrue((Double)valueObject.get("count") == numOranges); // map reduce returns doubles, typecast to Double and compare
					}
					else fail();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			
			String collection = testObjects.get("collection").toString();
			String outputCollection = testObjects.get("outputCollection").toString();
			
			// drop the collection
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
			
			// drop the output collection
			// replace the "collection" entry so that the drop-collection flow drops the correct collection
			testObjects.put("collection", outputCollection);
			flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	
}
