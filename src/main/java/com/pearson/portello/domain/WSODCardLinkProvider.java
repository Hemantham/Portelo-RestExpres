package com.pearson.portello.domain;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.model.AuthenticateResponse;
import com.pearson.model.WebEntry;
import com.pearson.model.lsEntry;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.strategicgains.restexpress.Request;

public class WSODCardLinkProvider implements ICardLinkProvider {

	@Override
	public String GetLink(Request request) {
		
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
