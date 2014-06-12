package com.pearson.portello.domain;

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

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.model.Card;
import com.pearson.model.Course;
import com.pearson.model.LinkedCard;
import com.pearson.model.Term;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;


public class LSEnrollmentProcessor implements IPortalEventProcessor {
	
	Map<Integer, String> images = new  HashMap<Integer, String>();

	public LSEnrollmentProcessor() {
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
		
		int courseId = 0;
		int userId = 0;
		String loginName = null;
		String clientid = null;
		
		Random generator = new Random();
		
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new ByteArrayInputStream(payload
					.getBytes()));

			doc.getDocumentElement().normalize();
			System.out.println("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			Node user = doc.getElementsByTagName("user").item(0);
			Node enrollment = doc.getElementsByTagName("enrollment").item(0);

			if (user.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) user;
				userId = Integer.parseInt(element.getAttribute("userid"));
				loginName = getLoginId(userId);
				clientid = element.getAttribute("clientid");				
			}
			if (enrollment.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) enrollment;
				courseId = Integer.parseInt(element.getAttribute("courseid"));
			}

		}

		catch (IOException ex) {

		} catch (SAXException ex) {

		} catch (ParserConfigurationException ex) {

		}

		WSODClient client = new WSODClient();

		String token = client.getToken(loginName
				, null
				, Constants.PROPERTIES.getProperty("client_" + clientid )
				, Constants.PROPERTIES.getProperty("clientName_" + clientid )
				, TokenType.ASSERTION);

		Course[] courseList = client.<Course[], Course[]> CallService(Constants.PROPERTIES.getProperty("mApi")
				+ "/courses/" + courseId, null, token
				, HttpMethod.GET
				, Course[].class
				, WSODClient.RestServiceType.WSOD);

		// get term
		Term[] termList = client.<Term[], Term[]> CallService(courseList[0].links[0].getHref(), null,  token
				, HttpMethod.GET
				, Term[].class
				, WSODClient.RestServiceType.WSOD);

		Card coursecard = new Card();

		Card termcard = new Card();

		// TODO create nodes		
		
		//check if term exists.
		
		LinkedCard existingCard =  client.< LinkedCard, LinkedCard> CallService(Constants.PROPERTIES.getProperty("cardApi") + "/user/" + userId
				+ "/cards?referenceId=" + termList[0].id , null, token,
				HttpMethod.GET, LinkedCard.class,
				WSODClient.RestServiceType.RESTExpress);
		
		if (existingCard.items.length == 0)
		{
		
		// create term
		
		termcard.cardLevel = 0;
		termcard.cardType = "term";
		termcard.referenceId = termList[0].id;
		termcard.title = termList[0].name;
		termcard.description = termList[0].description;
		termcard.children = new String[] { coursecard.id };
		

		termcard = client.<Card, Card> CallService(Constants.PROPERTIES.getProperty("cardApi") + "/user/" + userId
				+ "/cards", termcard, token,
				HttpMethod.POST, Card.class,
				WSODClient.RestServiceType.RESTExpress);
		
		}
		else
			
		{
			termcard = existingCard.items[0];
		}
	

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

		coursecard.cardLevel = 1;
		coursecard.cardType = "course";
		coursecard.linkUri = Constants.PROPERTIES.getProperty("cardApi") + "/user/{0}/course/" + courseId + "/link?linktype=" + AuthProviderType.WSOD.toString();
		coursecard.referenceId = courseList[0].id;
		coursecard.title = courseList[0].title;
		coursecard.externalLinks = links;
		coursecard.parentId = termcard.id;
		coursecard.imageUrl = images.get(generator.nextInt(images.size()-1));

		coursecard = client.<Card, Card> CallService(Constants.PROPERTIES.getProperty("cardApi") + "/user/"
				+ userId + "/cards", coursecard, token, HttpMethod.POST, Card.class,
				WSODClient.RestServiceType.RESTExpress);
		List<Card> savedCards =  new ArrayList<Card>();
		savedCards.add(coursecard);
		return savedCards;
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
