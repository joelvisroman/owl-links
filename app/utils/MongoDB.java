package utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Link;

import org.bson.types.ObjectId;

import org.apache.commons.collections.IteratorUtils;
import org.jongo.Jongo;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import play.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDB {

	private static MongoCollection factoryCollectionLinks() {
		
		try {
		
			DB db = new MongoClient( new MongoClientURI("mongodb://owl:owl@ds051851.mongolab.com:51851/owl-links")).getDB("owl-links");
								
			Jongo jongo = new Jongo(db);
			return jongo.getCollection("links");
		
		} catch (UnknownHostException e1) {			
			Logger.error("Falha ao conectar com o MongoDB " + e1.getMessage());
			return null;
		}		
			
	}
	
	
	public static MongoCursor<Link> allLinks() {
		
		MongoCollection links = MongoDB.factoryCollectionLinks();
		
		return links.find().as(Link.class);	
		 
	}
	
	public static List<Link> links() {	
		
		//return  IteratorUtils.toList(allLinks());
		List<Link> list = new ArrayList<Link>();
		
		allLinks().forEachRemaining(list::add);		
				
		return list;
		 
	}
	
	public static List<Link> subscribe() {
				
		MongoCursor<Link> subscribe = MongoDB.factoryCollectionLinks().
				find("{ $or: [ { notifynews: { $exists: false } }, { notifynews: { $eq: 'N' } } ] }").limit(3).as(Link.class);
		
		List<Link> list = new ArrayList<Link>();
			
		subscribe.forEachRemaining(list::add);		
			
		return list;
		
	}
	
	public static void notifySendNews(List<Link> links) {
		
		MongoCollection collection = MongoDB.factoryCollectionLinks();
		
		links.forEach( (l) -> { 
			l.notifynews = "S";
			collection.update("{_id: #}", new ObjectId(l._id)).with("{$set: {notifynews: 'S'}}");							
		} );				   
		
		
			
	}
	
}
