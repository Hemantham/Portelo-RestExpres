package com.pearson.portello.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.model.AuthenticateResponse;
import com.pearson.model.Card;
import com.pearson.model.Course;
import com.pearson.model.Term;
import com.pearson.model.TokenRequest;
import com.pearson.model.WebEntry;
import com.pearson.model.lsEntry;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;
import com.pearson.portello.config.Configuration;
import com.pearson.portello.domain.Comment;
import com.pearson.portello.domain.ILoginProvider;
import com.pearson.portello.domain.LoginProviderFactory;
import com.pearson.portello.domain.WSODLoginProvider;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

public class AuthenticateController {

	// Configuration configuration = new Configuration();

	public AuthenticateController() {
		super();

	}

	public AuthenticateResponse Authenticate(Request request, Response response) {

		try {

			AuthProviderType providerType = AuthProviderType.WSOD; // defaulted
																	// to wsod

			if (request.getQueryStringMap() != null
					&& request.getQueryStringMap().containsKey("authprovider")) {
				providerType = AuthProviderType.valueOf(request
						.getQueryStringMap().get("authprovider"));
			}

			ILoginProvider loginProvider = LoginProviderFactory.instance().Get(
					providerType);

			TokenRequest token = request.getBodyAs(TokenRequest.class,
					"authentication parameters not given");

			AuthenticateResponse authenticateRequest = loginProvider.login(
					token.userid, token.password);

			if (authenticateRequest.authValue == null ||authenticateRequest.authValue.length() == 0) {			
				response.setResponseCode(401);
			}
			return authenticateRequest;
		} catch (Exception ex) {
			response.setResponseCode(401);
			return null;
		}
	}

	public String LinkUri(Request request, Response response) {

		WebEntry webEntry = new WebEntry();

		webEntry.courseId = request.getUrlDecodedHeader(
				"courseId", "No courseId supplied");
		webEntry.userId = request.getUrlDecodedHeader(
				"userid", "No userId supplied");
		webEntry.exitUrl = Constants.PROPERTIES.getProperty("exitUrl");
		webEntry.logoutUrl = Constants.PROPERTIES.getProperty("logoutUrl");

		String token = request.getHeader("X-Authorization");

		WSODClient client = new WSODClient();

		lsEntry lsentry = client.<lsEntry, WebEntry> CallService(
				Constants.PROPERTIES.getProperty("mApi") + "/lsEntry/course",
				webEntry, token, HttpMethod.POST, lsEntry.class,
				WSODClient.RestServiceType.WSOD);

		return lsentry.entryUrl;

	}
}
