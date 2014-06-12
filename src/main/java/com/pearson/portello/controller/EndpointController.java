package com.pearson.portello.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.pearson.model.Card;
import com.pearson.model.Course;
import com.pearson.model.LinkedCard;
import com.pearson.model.Term;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;
import com.pearson.portello.config.Configuration;
import com.pearson.portello.domain.EventProcessorFactory;
import com.pearson.portello.domain.ILoginProvider;
import com.pearson.portello.domain.IPortalEventProcessor;
import com.pearson.portello.domain.LoginProviderFactory;
import com.pearson.portello.domain.EventProcessorFactory.EventType;
import com.strategicgains.hyperexpress.domain.LinkableCollection;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.microsoft.sqlserver.*;
import com.microsoft.sqlserver.jdbc.*;

public class EndpointController {


	public EndpointController() {
		super();
	}


	public String CourseEnrollment(Request request, Response response) {

		
		ChannelBuffer content = request.getBody();
		
		String payload = content.toString("UTF-8");
		
		EventType messageType = null;
			
		QueryStringDecoder decoder = new QueryStringDecoder("?" + payload);		
		
		payload = decoder.getParameters().get("PAYLOAD").get(0);
		messageType = EventType.valueOf( decoder.getParameters().get("MESSAGE-TYPE").get(0));
		System.out.println(decoder.getParameters().get("PAYLOAD").get(0));
		
		IPortalEventProcessor loginProvider = EventProcessorFactory.instance().Get(
				messageType);
			
		loginProvider.ProcessEvent(payload);		
		response.setResponseCode(201);
		return "success";	
		
	}

}
