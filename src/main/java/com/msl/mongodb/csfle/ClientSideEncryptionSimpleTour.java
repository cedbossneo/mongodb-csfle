package com.msl.mongodb.csfle;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

public class ClientSideEncryptionSimpleTour {

    public static void main(final String[] args) {
    	
    	ConnectionString connectionString = new ConnectionString("mongodb+srv://m2m:XBLA92iGDv7NMqCq@bau-cluster-zjisk.mongodb.net/test?retryWrites=true&w=majority");

        // This would have to be the same master key as was used to create the encryption key
        final byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<String, Map<String, Object>>() {{
           put("local", new HashMap<String, Object>() {{
               put("key", localMasterKey);
           }});
        }};

        String keyVaultNamespace = "admin.datakeys";

//        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
//                .keyVaultNamespace(keyVaultNamespace)
//                .kmsProviders(kmsProviders)
//                .build();

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
//                .autoEncryptionSettings(autoEncryptionSettings)
                .build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoCollection<Document> collection = mongoClient.getDatabase("test").getCollection("coll");
        collection.drop(); // Clear old data

        collection.insertOne(new Document("encryptedField", "123456789"));

        System.out.println(collection.find().first().toJson());
    }
}