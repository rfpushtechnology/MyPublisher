package com.pushtechnology.publisher;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.TimeoutException;
import com.pushtechnology.diffusion.api.data.TopicDataFactory;
import com.pushtechnology.diffusion.api.data.metadata.MDataType;
import com.pushtechnology.diffusion.api.data.remote.RemoteControlTopicDataEdge;
import com.pushtechnology.diffusion.api.data.single.SingleValueTopicData;
import com.pushtechnology.diffusion.api.data.record.*;
import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.publisher.Client;
import com.pushtechnology.diffusion.api.publisher.Publisher;
import com.pushtechnology.diffusion.api.topic.Topic;

public class MyPublisher extends Publisher {
	   private Topic  TimeTopic= null;
	   private Topic  EchoTopic= null;
	   private static SingleValueTopicData TimeTopicData=null;

	   //Runnable task to update Time Topic
		static class ScheduledTask extends TimerTask {
			public void run() {
				Date date = new Date();

				try {
					//Simply update the data and tell diffusion to publish to all subscribers
					TimeTopicData.updateAndPublish(date.toString());
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (APIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	   @Override
	    protected void initialLoad() throws APIException {
			Date date = new Date();
			System.out.println(date.toString());
			
			//Create Single Value Topic Data as a string
			TimeTopicData = TopicDataFactory.newSingleValueData(MDataType.STRING);
			
			//Set Initial Value of Topic Data
			TimeTopicData.initialise(date.toString());
			
			//Add Topic and Data to diffusion 
			TimeTopic=addTopic("TIME",TimeTopicData);
			
			//Add additional Topic to support ECHO functionality
			EchoTopic=addTopic("ECHO");

			Timer time = new Timer();
			ScheduledTask tt = new ScheduledTask();
			time.schedule(tt,0,1000);

			logWarning("initialLoad called");

	   }
	   @Override	   
	   protected void publisherStarted() throws APIException {
		   logWarning("publisherStarted called");

	   }
	   
	   protected void subscription(Client client, Topic topic, boolean loaded) throws APIException {
	       logWarning("Subscription called...Topic = "+ topic.toString());

	   }
	   
	   protected void messageFromClient(TopicMessage message, Client client) {
	       logWarning("Message Received...Topic="+ message.getTopicName());
		   logWarning("Sending Message to "+client.getClientID());

	    	try {
				client.send(message);
			} catch (APIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	   }
	   
	  
}
