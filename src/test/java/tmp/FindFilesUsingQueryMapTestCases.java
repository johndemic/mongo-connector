package tmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.ConnectionException;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.module.mongo.automation.testcases.MongoTestParent;
import org.mule.module.mongo.automation.testcases.SanityTests;
import org.mule.module.mongo.automation.testcases.SmokeTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class FindFilesUsingQueryMapTestCases extends MongoTestParent {
	
//	@After
//	public void tearDown() {
//		deleteFilesCreatedByCreateFileFromPayload();
//	}

	@Test
	public void testShit() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException, ConnectionException, IOException {
//		String newFileName = "mkyong-java-image";
//		File imageFile = new File("c:\\JavaWebHosting.png");
		File file = null;
		try {
			file = folder.newFile(FILENAME_FOR_TEST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Object o = lookupFlowConstruct("Mongo_DB");
//		MongoCloudConnectorConnectionManager mongoConn = (MongoCloudConnectorConnectionManager) muleContext.getRegistry().lookupObject("Mongo_DB");
		
		MongoCloudConnector connector;	
		connector = new MongoCloudConnector();
        connector.setHost("127.0.0.1");
        connector.setPort(27017);
//        connector.connect("newUsername", "1c63885aed467831206d02dd5d1857c5", "testdb");
        connector.connect("newUsername", null, "testdb");

//mongo.host=127.0.0.1
//mongo.port=27017
//mongo.database=testdb
//mongo.username=mule
//mongo.password=mule
		
		
		Field f = connector.getClass().getDeclaredField("client"); //NoSuchFieldException
		f.setAccessible(true);
		MongoClientImpl mongoClient = (MongoClientImpl) f.get(connector); //IllegalAccessException
		DB db = mongoClient.getDb();
		
		System.out.println();
		
//		eventContext.
//		context.getReg
//		Connector quartzConnector = (Connector) eventContext.getMuleContext().getRegistry().lookupConnector("myquartz");
		
		GridFS gfsPhoto = new GridFS(db, "photo");
		GridFSInputFile gfsFile = gfsPhoto.createFile(file);
		gfsFile.setFilename(FILENAME_FOR_TEST);
		gfsFile.save();
	}
	
	@Category({ SmokeTests.class, SanityTests.class })
//	@Test
	public void testFindFilesUsingQueryMap() {
		try {
			assertEquals("There should be 0 files found before create-file-from-payload", 0, findFiles());
			
			createFileFromPayload(FILENAME_FOR_TEST);
		
			// create another file with query map
			
			MuleEvent response = null;
			try {
				MessageProcessor findFilesFlow = lookupFlowConstruct("find-files");
				MuleEvent event = getTestEvent(new BasicDBObject());
				response = findFilesFlow.process(event);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}

			
			assertEquals("There should be 2 files found", 2, findFiles());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
