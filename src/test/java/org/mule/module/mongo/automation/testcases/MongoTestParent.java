/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoTestParent extends FunctionalTestCase {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// Set global timeout of tests to 10minutes
    @Rule
    public Timeout globalTimeout = new Timeout(600000);
    
	protected static final String[] SPRING_CONFIG_FILES = new String[] { "AutomationSpringBeans.xml" };
	protected static ApplicationContext context;
	protected Map<String, Object> testObjects;

	@Override
	protected String getConfigResources() {
		return "automation-test-flows.xml";
	}

	protected MessageProcessor lookupFlowConstruct(String name) {
		return (MessageProcessor) muleContext.getRegistry()
				.lookupFlowConstruct(name);
	}

	@BeforeClass
	public static void beforeClass() {
		context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
	}

	protected List<DBObject> getEmptyDBObjects(int num) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (int i = 0; i < num; i++) {
			list.add(new BasicDBObject());
		}
		return list;
	}
	
	protected void setTestObjects(Map<String, Object> testObjects) {
		this.testObjects = testObjects;
	}

	protected void insertObjects(List<DBObject> objs) {
		if(testObjects == null) {
			setTestObjects(new HashMap<String, Object>());
		}
		
		try {
			MessageProcessor insertFlow = lookupFlowConstruct("insert-object");

			for (DBObject obj : objs) {
				testObjects.put("dbObjectRef", obj);
				insertFlow.process(getTestEvent(testObjects));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	// Returns all number of all files in database as per find-files operation
	protected int findFiles() {
		Iterable<DBObject> iterable = null;
		MuleEvent response = null;
		try {
			MessageProcessor findFilesFlow = lookupFlowConstruct("find-files");
			MuleEvent event = getTestEvent(new BasicDBObject());
			response = findFilesFlow.process(event);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		iterable = (Iterable<DBObject>) response.getMessage().getPayload();
		return iterableSize(iterable);
	}
	
	protected int iterableSize(Iterable<?> iterable) {
		if (iterable instanceof Collection<?>) {
			return ((Collection<?>) iterable).size();
		} else {
			Iterator<?> it = iterable.iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
			}
			return i;
		}
	}

	protected GridFSInputFile createFileFromPayload(DBObject dbObj, String filename) {
		if(testObjects == null) {
			setTestObjects(new HashMap<String, Object>());
		}
		
		GridFSInputFile res = null;
		try {
			File file = folder.newFile(filename);

			MessageProcessor createFileFromPayloadFlow = lookupFlowConstruct("create-file-from-payload");
			testObjects.put("filename", filename);
			MuleEvent event = getTestEvent(file);
			event.setSessionVariable("filename", filename);
			event.setSessionVariable("metaDataRef", dbObj);

			res = (GridFSInputFile) createFileFromPayloadFlow.process(event)
					.getMessage().getPayload();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		return res;
	}
	
	protected GridFSInputFile createFileFromPayload(String filename) {
		return createFileFromPayload(new BasicDBObject(), filename);
	}
	
	protected GridFSInputFile createFileFromPayload(Object filename) {
		return createFileFromPayload(filename.toString());
	}
	
	protected void deleteFilesCreatedByCreateFileFromPayload() {
		if(testObjects == null) {
			setTestObjects(new HashMap<String, Object>());
		}
		try {
			MessageProcessor dropCollectionFlow = lookupFlowConstruct("drop-collection");
			
			testObjects.put("collection", "fs.chunks");
			dropCollectionFlow.process(getTestEvent(testObjects));
			
			testObjects.put("collection", "fs.files");
			lookupFlowConstruct("drop-collection").process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	protected boolean existsInList(List<DBObject> objects, String indexName) {
		for (DBObject obj : objects) {
			if (obj.get("name").equals(indexName)) {
				return true;
			}
		}
		return false;
	}
	
	protected String getIndexName() {
		String indexKey = (String) testObjects.get("field");
		IndexOrder indexOrder = (IndexOrder) testObjects.get("order");
		
		return indexKey + "_" + indexOrder.getValue();
	}
	
	protected void dropIndex(String indexName) {
		testObjects.put("index", indexName);			
		MessageProcessor dropIndexFlow = lookupFlowConstruct("drop-index");
		try {
			dropIndexFlow.process(getTestEvent(testObjects));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
