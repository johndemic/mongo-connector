package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleMessage;
import org.mule.module.mongo.api.MongoCollection;

import com.mongodb.DBObject;

public class AddUserTestCases extends MongoTestParent {
	
	@SuppressWarnings("unchecked")
	@Category({SanityTests.class, SmokeTests.class})
	@Test
	public void testAddUser() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("addUser");

			String newUsername = testObjects.get("newUsername").toString();
			
			flow = lookupFlowConstruct("add-user");
			response = flow.process(getTestEvent(testObjects));
			// Nothing is returned - http://www.mulesoft.org/jira/browse/CLDCONNECT-915
			// Check that user was added
			
			testObjects = (HashMap<String, Object>) context.getBean("getUsers");
			flow = lookupFlowConstruct("find-objects");
			response = flow.process(getTestEvent(testObjects));
			
			MuleMessage message = response.getMessage();
			MongoCollection payload =(MongoCollection) message.getPayload();
			
			assertNotNull(payload);
			
			boolean found = false;
			
			Iterator<DBObject> iterator = payload.iterator();
			while (iterator.hasNext()) {
				DBObject obj = iterator.next();
				String username = obj.get("user").toString();
				if (username.equals(newUsername)) {
					found = true;
					break;
				}
			}
			
			assertTrue (found);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
