/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api.automation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.module.mongo.api.IndexOrder;

import com.mongodb.DBObject;

public class MongoHelper {

	public static String getIndexName(String indexKey, IndexOrder order) {
		String indexName = indexKey + "_" + order.getValue();
		return indexName;
	}
	
	public static int getIterableSize(Iterable<?> iterable) {
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
	
	public static boolean indexExistsInList(List<DBObject> objects, String indexName) {
		for (DBObject obj : objects) {
			if (obj.get("name").equals(indexName)) {
				return true;
			}
		}
		return false;
	}

	
}
