package org.mule.module.mongo.api;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.mongo.automation.testcases.AddUserTestCases;
import org.mule.module.mongo.automation.testcases.CreateCollectionTestCases;
import org.mule.module.mongo.automation.testcases.CreateIndexTestCases;
import org.mule.module.mongo.automation.testcases.DropIndexTestCases;
import org.mule.module.mongo.automation.testcases.ExistsCollectionTestCases;
import org.mule.module.mongo.automation.testcases.FindObjectsTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectTestCases;
import org.mule.module.mongo.automation.testcases.ListCollectionTestCases;
import org.mule.module.mongo.automation.testcases.RemoveObjectsTestCases;
import org.mule.module.mongo.automation.testcases.SanityTests;

@RunWith(Categories.class)
@IncludeCategory(SanityTests.class)
@SuiteClasses({ AddUserTestCases.class, CreateCollectionTestCases.class,
		CreateIndexTestCases.class, DropIndexTestCases.class,
		ExistsCollectionTestCases.class, FindObjectsTestCases.class,
		InsertObjectTestCases.class, ListCollectionTestCases.class,
		RemoveObjectsTestCases.class })
public class SanityTestSuite {

}
