package com.pearson.portello.config;

import java.util.Properties;

import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.Constants;
import com.pearson.portello.controller.AuthenticateController;
import com.pearson.portello.controller.BlogController;
import com.pearson.portello.controller.BlogEntryController;
import com.pearson.portello.controller.CommentController;
import com.pearson.portello.controller.EndpointController;
import com.pearson.portello.persistence.BlogEntryRepository;
import com.pearson.portello.persistence.BlogRepository;
import com.pearson.portello.persistence.CommentRepository;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.util.Environment;
import com.pearson.portello.controller.CardController;
import com.pearson.portello.persistence.CardRepository;



public class Configuration
extends Environment
{
	private static final String DEFAULT_EXECUTOR_THREAD_POOL_SIZE = "20";

	private static final String PORT_PROPERTY = "port";
	private static final String DEFAULT_FORMAT_PROPERTY = "default.Format";
	private static final String BASE_URL_PROPERTY = "base.url";
	private static final String EXECUTOR_THREAD_POOL_SIZE = "executor.threadPool.size";

	private int port;
	private String defaultFormat;
	private String baseUrl;
	private int executorThreadPoolSize;
	private MetricsConfig metricsConfig;
	

	public String applicationID = "30e9193b-682f-48e6-9457-868d922e40dd";
	public String authenticateurl = "http://m-api.ecollege-labs.com/token";
	public String cardApi = "http://localhost:8125";
	public String mApi = "http://m-api.ecollege-labs.com";
	public String KeyMoniker = "336f3e3c-ebd1-4dac-9f0e-dc13f6175018";
	public String Secret = "5e8a5b182fbd42c992daff4b07a164b3";
	public String assertionType = "urn:ecollege:names:moauth:1.0:assertion";	 

	private BlogRepository blogRepository;
	private BlogEntryRepository blogEntryRepository;
	private CommentRepository commentRepository;
	private CardRepository cardRepository;


	private BlogController blogController;
	private BlogEntryController blogEntryController;
	private CommentController commentController;
	private EndpointController endpointController;
	private AuthenticateController authenticateController;
	private CardController cardController;

	
	private SubPubMessageBus subPubMessageBus = null;
	private CorsConfig corsConfig = null;
	
	

	@Override
	protected void fillValues(Properties p)
	{
		Constants.PROPERTIES = p; 
		this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
		
		this.applicationID = p.getProperty("applicationID");
		this.authenticateurl = p.getProperty("authenticateurl");
		this.cardApi = p.getProperty("cardApi");
		this.mApi = p.getProperty("mApi");
		this.KeyMoniker = p.getProperty("KeyMoniker");
		this.Secret = p.getProperty("Secret");
		this.assertionType = p.getProperty("assertionType");
		
		this.defaultFormat = p.getProperty(DEFAULT_FORMAT_PROPERTY, Format.JSON);
		this.baseUrl = p.getProperty(BASE_URL_PROPERTY, "http://localhost:" + String.valueOf(port));
		this.executorThreadPoolSize = Integer.parseInt(p.getProperty(EXECUTOR_THREAD_POOL_SIZE, DEFAULT_EXECUTOR_THREAD_POOL_SIZE));
		this.metricsConfig = new MetricsConfig(p);
		this.corsConfig = new CorsConfig(p);
		subPubMessageBus = new SubPubConfig(p).getBus();
		MongoConfig mongoSettings = new MongoConfig(p);
		configureRepositories(mongoSettings);
		configureControllers();
	}

	private void configureRepositories(MongoConfig mongo)
	{
		blogRepository = new BlogRepository(mongo.getClient(), mongo.getDbName(), getSubPubMessageBus());
		blogEntryRepository = new BlogEntryRepository(mongo.getClient(), mongo.getDbName(), getSubPubMessageBus());
		commentRepository = new CommentRepository(mongo.getClient(), mongo.getDbName(), getSubPubMessageBus());
		cardRepository = new CardRepository(mongo.getClient(), mongo.getDbName());
		
	}

	private void configureControllers()
	{
		blogController = new BlogController(blogRepository);
		blogEntryController = new BlogEntryController(blogEntryRepository);
		commentController = new CommentController(commentRepository);
		endpointController = new EndpointController();
		authenticateController = new AuthenticateController();
		cardController = new CardController(cardRepository);

		
	}
	
	
	// SECTION: ACCESSORS - PUBLIC

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public String getDefaultFormat()
	{
		return defaultFormat;
	}

	public int getPort()
	{
		return port;
	}
	
	public BlogController getBlogController()
	{
		return blogController;
	}
	
	public EndpointController getEndpointController()
	{
		return endpointController;
	}
	
	public AuthenticateController getAuthenticateController()
	{
		return authenticateController;
	}
	
	public BlogEntryController getBlogEntryController()
	{
		return blogEntryController;
	}
	
	public CommentController getCommentController()
	{
		return commentController;
	}
	/**
	 * @return the cardController
	 */
	public CardController getCardController() {
		return cardController;
	}


	public BlogRepository getBlogRepository()
	{
		return blogRepository;
	}

	public BlogEntryRepository getBlogEntryRepository()
	{
		return blogEntryRepository;
	}

	public CommentRepository getCommentRepository()
	{
		return commentRepository;
	}
	
	public int getExecutorThreadPoolSize()
	{
		return executorThreadPoolSize;
	}
	
	public SubPubMessageBus getSubPubMessageBus()
	{
		return subPubMessageBus;
	}
	
	public MetricsConfig getMetricsConfig()
	{
		return metricsConfig;
	}
	
	public CorsConfig getCorsConfig()
	{
		return corsConfig;
	}
}