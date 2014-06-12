package com.pearson.portello.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pearson.model.Card;
import com.pearson.model.Course;
import com.pearson.model.EnrollmentMessage;
import com.pearson.model.LinkedCard;
import com.pearson.model.Term;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;


public class SMSEnrollmentProcessor implements IPortalEventProcessor {
	
	Map<Integer, String> images = new  HashMap<Integer, String>();

	public SMSEnrollmentProcessor() {
		super();	
		images.put(0, "https://www.edx.org/sites/default/files/course/image/tile/cs50x-course-listing-banner_0.jpg");
		images.put(1, "https://www.edx.org/sites/default/files/course/image/tile/paul_262x136_0.jpg");
		images.put(2, "https://www.edx.org/sites/default/files/course/image/tile/publicspeaking_262x136.jpg");
		images.put(3, "https://www.edx.org/sites/default/files/course/image/tile/ec1011x_262x136_0.jpg");
		images.put(4, "https://www.edx.org/sites/default/files/course/image/tile/ut.5.01x_262x136.jpg");
		images.put(5, "https://www.edx.org/sites/default/files/course/image/tile/immunityyochange_262x136.jpg");
		images.put(6, "https://www.edx.org/sites/default/files/course/image/tile/jazz_262x136.jpg");
	}

	@Override
	public List<Card> ProcessEvent(String payload) {	
		
		ObjectMapper mapper = new ObjectMapper();
		
		int courseId = 0;
		
		Random generator = new Random();
		
		List<EnrollmentMessage> enrollmentMessages = null;
		List<Card> coursecards = new ArrayList<Card>();
		
		try {
			
			 enrollmentMessages = new ArrayList<EnrollmentMessage>(Arrays.asList( mapper.readValue(payload, EnrollmentMessage[].class)));
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

		WSODClient client = new WSODClient();
		
		for (EnrollmentMessage enrollmentMessage : enrollmentMessages)
		{
		Card coursecard = new Card();	

		// create class
		com.pearson.model.Link[] links = new com.pearson.model.Link[2];

		links[0] = new com.pearson.model.Link();
		links[0].setHref(Constants.PROPERTIES.getProperty("mApi") + "/users/{0}/courses/" + courseId
				+ "/announcements");
		links[0].setTitle("announcements");

		links[1] = new com.pearson.model.Link();
		links[1].setHref(Constants.PROPERTIES.getProperty("mApi")
				+ "/users/{0}/whatshappeningfeed?courseIdQueryString="
				+ courseId);
		links[0].setTitle("whatshappeningfeed");

		coursecard.cardLevel = 0;
		coursecard.cardType = "course";
		coursecard.linkUri = Constants.PROPERTIES.getProperty("cardApi") + "/user/{0}/course/" + courseId + "/link?linktype=" + AuthProviderType.SMS_SSO.toString();
		coursecard.referenceId = enrollmentMessage.CourseID;
		coursecard.title = enrollmentMessage.CourseTitle;
		coursecard.externalLinks = links;
		coursecard.parentId = "0";
		coursecard.imageUrl = images.get(generator.nextInt(images.size()-1));

		coursecard = client.<Card, Card> CallService(Constants.PROPERTIES.getProperty("cardApi") + "/user/"
				+ enrollmentMessage.UserID + "/cards", coursecard, "" , HttpMethod.POST, Card.class,
				WSODClient.RestServiceType.RESTExpress);
		coursecards.add(coursecard);
		}
		return coursecards;
	}

	private String getLoginId(int userId)
	{
		String loginid = "";
		
		String realdbserver =Constants.PROPERTIES.getProperty("realdbserver" );
				String		 user = Constants.PROPERTIES.getProperty("user"  );
						String		 password = Constants.PROPERTIES.getProperty("password"  );
		
		
		
		try
		{		
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://" + realdbserver + ";databaseName=RealDB;user="+user+";password="+password+";";
				Connection con = DriverManager.getConnection(connectionUrl);
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT  cn FROM RealDB.dbo.Users where UserID =" +  Integer.toString(userId) );
				 while ( rs.next() ) {
					 loginid = rs.getString("cn");// your sql record saved as string
				              //  System.out.println(l);//writes your sql record
				            }
				con.close();
		}
		catch(SQLServerException e)
		{
			e.printStackTrace();	
		}
		catch(SQLException e)
		{
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loginid;
		
	}

}
