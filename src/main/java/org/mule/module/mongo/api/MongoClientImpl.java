/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoClientImpl implements MongoClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientImpl.class);

    private final DB db;

    public MongoClientImpl(final DB db)
    {
        Validate.notNull(db);
        this.db = db;
    }

    public void close() throws IOException
    {
        try
        {
            db.cleanCursors(true);
        }
        catch (final Exception e)
        {
            LOGGER.warn("Failed to properly clean cursors of db: " + db, e);
        }

        try
        {
            db.requestDone();
        }
        catch (final Exception e)
        {
            LOGGER.warn("Failed to properly set request done for db: " + db, e);
        }
    }

    public long countObjects(@NotNull final String collection, final DBObject query)
    {
        Validate.notNull(collection);
        if (query == null)
        {
            return openSession().getCollection(collection).count();
        }
        return openSession().getCollection(collection).count(query);
    }

    public void createCollection(@NotNull final String collection,
                                 final boolean capped,
                                 final Integer maxObjects,
                                 final Integer size)
    {
        Validate.notNull(collection);
        final BasicDBObject options = new BasicDBObject("capped", capped);
        if (maxObjects != null)
        {
            options.put("maxObject", maxObjects);
        }
        if (size != null)
        {
            options.put("size", size);
        }
        openSession().createCollection(collection, options);
    }

    public DBCollection getCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        return openSession().getCollection(collection);
    }

    public void addUser(final String username, final String password)
    {
        Validate.notNull(username);
        Validate.notNull(password);
        final WriteResult writeResult = openSession().addUser(username, password.toCharArray());
        if (!writeResult.getLastError().ok())
        {
            throw new MongoException(writeResult.getLastError().getErrorMessage());
        }
    }

    public void dropDatabase()
    {
        openSession().dropDatabase();
    }

    public void dropCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        openSession().getCollection(collection).drop();
    }

    public boolean existsCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        return openSession().collectionExists(collection);
    }

    public Iterable<DBObject> findObjects(@NotNull final String collection,
                                          final DBObject query,
                                          final List<String> fields,
                                          final Integer numToSkip,
                                          final Integer limit)
    {
        Validate.notNull(collection);

        DBCursor dbCursor = openSession().getCollection(collection).find(query, FieldsSet.from(fields));
        if (numToSkip != null)
        {
            dbCursor = dbCursor.skip(numToSkip);
        }
        if (limit != null)
        {
            dbCursor = dbCursor.limit(limit);
        }

        return bug5588Workaround(dbCursor);
    }

    public DBObject findOneObject(@NotNull final String collection,
                                  final DBObject query,
                                  final List<String> fields)
    {
        Validate.notNull(collection);
        final DBObject element = openSession().getCollection(collection).findOne(query,
            FieldsSet.from(fields));
        if (element == null)
        {
            throw new MongoException("No object found for query " + query);
        }
        return element;
    }

    public String insertObject(@NotNull final String collection,
                               @NotNull final DBObject object,
                               @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(object);
        Validate.notNull(writeConcern);
        openSession().getCollection(collection).insert(object,
            writeConcern.toMongoWriteConcern(openSession()));
        final ObjectId id = (ObjectId) object.get("_id");
        if (id == null) return null;

        return id.toStringMongod();
    }

    public Collection<String> listCollections()
    {
        return openSession().getCollectionNames();
    }

    public Iterable<DBObject> mapReduceObjects(@NotNull final String collection,
                                               @NotNull final String mapFunction,
                                               @NotNull final String reduceFunction,
                                               final String outputCollection)
    {
        Validate.notNull(collection);
        Validate.notEmpty(mapFunction);
        Validate.notEmpty(reduceFunction);
        return bug5588Workaround(openSession().getCollection(collection)
            .mapReduce(mapFunction, reduceFunction, outputCollection, outputTypeFor(outputCollection), null)
            .results());
    }

    private OutputType outputTypeFor(final String outputCollection)
    {
        return outputCollection != null ? OutputType.REPLACE : OutputType.INLINE;
    }

    public void removeObjects(@NotNull final String collection,
                              final DBObject query,
                              @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(writeConcern);
        openSession().getCollection(collection).remove(query != null ? query : new BasicDBObject(),
            writeConcern.toMongoWriteConcern(openSession()));
    }

    public void saveObject(@NotNull final String collection,
                           @NotNull final DBObject object,
                           @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(object);
        Validate.notNull(writeConcern);
        openSession().getCollection(collection).save(object, writeConcern.toMongoWriteConcern(openSession()));
    }

    public void updateObjects(@NotNull final String collection,
                              final DBObject query,
                              final DBObject object,
                              final boolean upsert,
                              final boolean multi,
                              final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(writeConcern);
        openSession().getCollection(collection).update(query, object, upsert, multi,
            writeConcern.toMongoWriteConcern(openSession()));

    }

    public void createIndex(final String collection, final String field, final IndexOrder order)
    {
        openSession().getCollection(collection).createIndex(new BasicDBObject(field, order.getValue()));
    }

    public void dropIndex(final String collection, final String name)
    {
        openSession().getCollection(collection).dropIndex(name);
    }

    public Collection<DBObject> listIndices(final String collection)
    {
        return openSession().getCollection(collection).getIndexInfo();
    }

    public DBObject createFile(final InputStream content,
                               final String filename,
                               final String contentType,
                               final DBObject metadata)
    {
        Validate.notNull(filename);
        Validate.notNull(content);
        final GridFSInputFile file = getGridFs().createFile(content);
        file.setFilename(filename);
        file.setContentType(contentType);
        if (metadata != null)
        {
            file.setMetaData(metadata);
        }
        file.save();
        return file;
    }

    public Iterable<DBObject> findFiles(final DBObject query)
    {
        return bug5588Workaround(getGridFs().find(query));
    }

    public DBObject findOneFile(final DBObject query)
    {
        Validate.notNull(query);
        final GridFSDBFile file = getGridFs().findOne(query);
        if (file == null)
        {
            throw new MongoException("No file found for query " + query);
        }
        return file;
    }

    public InputStream getFileContent(final DBObject query)
    {
        Validate.notNull(query);
        return ((GridFSDBFile) findOneFile(query)).getInputStream();
    }

    public Iterable<DBObject> listFiles(final DBObject query)
    {
        return bug5588Workaround(getGridFs().getFileList(query));
    }

    public void removeFiles(final DBObject query)
    {
        getGridFs().remove(query);
    }

    public DBObject executeComamnd(final DBObject command)
    {
        return openSession().command(command);
    }

    protected GridFS getGridFs()
    {
        return new GridFS(openSession());
    }

    /*
     * see http://www.mulesoft.org/jira/browse/MULE-5588
     */
    @SuppressWarnings("unchecked")
    private Iterable<DBObject> bug5588Workaround(final Iterable<? extends DBObject> o)
    {
        if (o instanceof Collection<?>)
        {
            return (Iterable<DBObject>) o;
        }
        return new MongoCollection(o);
    }

    /**
     * Gets the DB objects, ensuring that a consistent request is in progress. Consistent requests
     * are never ended, they end when the current thread finishes
     */
    private DB openSession()
    {
        db.requestStart();
        db.requestEnsureConnection();
        return db;
    }

    public DB getDb()
    {
        return db;
    }

}
