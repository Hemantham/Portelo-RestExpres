package com.pearson.portello.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.portello.Constants;
import com.pearson.portello.domain.Card;

import com.strategicgains.hyperexpress.RelTypes;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.LinkableCollection;
import com.strategicgains.hyperexpress.util.LinkUtils;
import com.strategicgains.repoexpress.mongodb.MongodbEntityRepository;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.common.query.FilterComponent;
import com.strategicgains.restexpress.common.query.FilterOperator;
import com.strategicgains.restexpress.common.query.QueryFilter;
import com.strategicgains.restexpress.common.query.QueryOrder;
import com.strategicgains.restexpress.common.query.QueryRange;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.query.QueryOrders;
import com.strategicgains.restexpress.query.QueryRanges;
import com.strategicgains.syntaxe.ValidationEngine;

public class CardController {
	private MongodbEntityRepository<Card> cards;

	public CardController(MongodbEntityRepository<Card> cardRepository) {
		super();
		this.cards = cardRepository;
	}

	public Card create(Request request, Response response) {
		/*String referenceId = request.getHeader(
				Constants.Card.CARD_REFERANCE_ID, "No user ID supplied");*/
		
		Card card = request.getBodyAs(Card.class,	"card details not provided");		
		
		String userId = request.getHeader(Constants.User.USER_ID, "User Id not provided");
		List<FilterComponent> filters = new ArrayList<FilterComponent>();
		filters.add(new FilterComponent(Constants.User.USER_ID,
				FilterOperator.EQUALS, userId));

		filters.add(new FilterComponent(Constants.Card.CARD_REFERANCE_ID,
					FilterOperator.EQUALS, Integer.valueOf(card.referenceId)));

		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);

		List<Card> results = cards.readAll(new QueryFilter(filters), range,	order);
		Card saved = null;
		if (results != null && !results.isEmpty()) {
			
			card.setId(results.get(0).getId());
			
			ValidationEngine.validateAndThrow(card);
			
			saved = cards.update(card);
			
		} else {			
			card.setUserId(userId);
			ValidationEngine.validateAndThrow(card);
			saved = cards.create(card);
		}
		// Construct the response for create...
		response.setResponseCreated();

		// Include the Location header...
		String locationPattern = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SINGLE_SAMPLE);
		response.addLocationHeader(LinkUtils.formatUrl(locationPattern,
				Constants.Url.SAMPLE_ID, saved.getId()));

		// Return the newly-created ID...
		return saved;
	}

	public Card read(Request request, Response response) {
		String id = request.getHeader(Constants.User.USER_ID,
				"No Sample ID supplied");

		Card card = cards.read(id);

		// Add 'self' link
		String selfPattern = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SINGLE_SAMPLE);
		String selfUrl = LinkUtils.formatUrl(selfPattern,
				Constants.Url.SAMPLE_ID, card.getId());
		card.addLink(new Link(RelTypes.SELF, selfUrl));

		return card;
	}

	public LinkableCollection<Card> rootFilter(Request request,
			Response response) {
		String userId = request.getHeader(Constants.User.USER_ID,
				"No user ID supplied");
		String referenceId = null;

		if (request.getQueryStringMap() != null
				&& request.getQueryStringMap().containsKey("referenceId")) {
			referenceId = request.getQueryStringMap().get("referenceId");
		}

		List<FilterComponent> filters = new ArrayList<FilterComponent>();
		filters.add(new FilterComponent(Constants.Card.CARD_LEVEL,
				FilterOperator.EQUALS, 0));
		filters.add(new FilterComponent(Constants.User.USER_ID,
				FilterOperator.EQUALS, userId));

		if (referenceId != null) {
			filters.add(new FilterComponent(Constants.Card.CARD_REFERANCE_ID,
					FilterOperator.EQUALS, Integer.valueOf(referenceId)));
		}

		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);

		List<Card> results = cards.readAll(new QueryFilter(filters), range,
				order);
		// long count = cards.count(queryFilter);
		// response.setCollectionResponse(range, results.size(), count);

		// Add 'self' links
		String orderSelfPattern = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SINGLE_SAMPLE);

		for (Card result : results) {
			String selfUrl = LinkUtils.formatUrl(orderSelfPattern,
					Constants.Url.SAMPLE_ID, result.getId());
			result.addLink(new Link(RelTypes.SELF, selfUrl));
		}

		String selfUrl = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SAMPLE_COLLECTION);
		LinkableCollection<Card> wrapper = new LinkableCollection<Card>(results);
		wrapper.addLink(new Link(RelTypes.SELF, selfUrl));
		return wrapper;
	}

	public LinkableCollection<Card> chiledFilter(Request request,
			Response response) {
		String parentCardId = request.getHeader(Constants.Card.CARD_ID,
				"No Card ID supplied");
		String userId = request.getHeader(Constants.User.USER_ID,
				"No User ID supplied");
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.addCriteria(Constants.Card.PARENT_CARD_ID,
				FilterOperator.EQUALS, parentCardId);
		// QueryFilter filter = QueryFilters.parseFrom(request);
		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);

		List<Card> results = cards.readAll(queryFilter, range, order);
		long count = cards.count(queryFilter);
		response.setCollectionResponse(range, results.size(), count);
		// Add 'self' links
		String orderSelfPattern = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SINGLE_SAMPLE);

		for (Card result : results) {
			setUserId(result, userId);
			String selfUrl = LinkUtils.formatUrl(orderSelfPattern,
					Constants.Url.SAMPLE_ID, result.getId());
			result.addLink(new Link(RelTypes.SELF, selfUrl));
		}

		String selfUrl = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SAMPLE_COLLECTION);
		LinkableCollection<Card> wrapper = new LinkableCollection<Card>(results);
		wrapper.addLink(new Link(RelTypes.SELF, selfUrl));
		return wrapper;
	}

	private void setUserId(Card result, String userId) {
		com.pearson.model.Link[] links = result.getExternalLinks();
		String linkUri = result.getLinkUri();
		if (linkUri != null) {
			linkUri = linkUri.replace("{0}", userId);
			result.setLinkUri(linkUri);
		}
		for (com.pearson.model.Link link : links) {
			if (link.getHref() != null) {
				link.setHref(link.getHref().replace("{0}", userId));
			}
		}
	}

	public LinkableCollection<Card> readAll(Request request, Response response) {

		String userId = request.getHeader(Constants.User.USER_ID,
				"User Id not provided");
		String cardId = request.getHeader(Constants.Card.CARD_ID,
				"Card Id not provided");

		QueryFilter queryFilter = new QueryFilter();
		queryFilter.addCriteria(Constants.User.USER_ID, FilterOperator.EQUALS,
				userId);

		QueryFilter queryFilter2 = new QueryFilter();
		queryFilter.addCriteria(Constants.Card.CARD_ID, FilterOperator.EQUALS,
				cardId);

		List<FilterComponent> filters = new ArrayList<FilterComponent>();
		filters.add(new FilterComponent(Constants.User.USER_ID,
				FilterOperator.EQUALS, userId));
		filters.add(new FilterComponent(Constants.Card.CARD_ID,
				FilterOperator.EQUALS, cardId));

		// QueryFilter filter = QueryFilters.parseFrom(request);
		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);

		List<Card> results = cards.readAll(new QueryFilter(filters), range,
				order);
		long count = cards.count(queryFilter);
		response.setCollectionResponse(range, results.size(), count);

		// Add 'self' links
		String orderSelfPattern = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SINGLE_SAMPLE);

		for (Card result : results) {
			String selfUrl = LinkUtils.formatUrl(orderSelfPattern,
					Constants.Url.SAMPLE_ID, result.getId());
			result.addLink(new Link(RelTypes.SELF, selfUrl));
		}

		String selfUrl = request.getNamedUrl(HttpMethod.GET,
				Constants.Routes.SAMPLE_COLLECTION);
		LinkableCollection<Card> wrapper = new LinkableCollection<Card>(results);
		wrapper.addLink(new Link(RelTypes.SELF, selfUrl));
		return wrapper;
	}

	public void update(Request request, Response response) {
		String id = request.getHeader(Constants.Url.SAMPLE_ID);
		Card card = request
				.getBodyAs(Card.class, "Sample details not provided");

		if (!id.equals(card.getId())) {
			throw new BadRequestException(
					"ID in URL and ID in Sample must match");
		}

		// ValidationEngine.validateAndThrow(sample);
		cards.update(card);
		response.setResponseNoContent();
	}

	public void delete(Request request, Response response) {
		String id = request.getHeader(Constants.Card.CARD_ID,
				"No Sample ID supplied");
		cards.delete(id);
		response.setResponseNoContent();
	}
	
	public String status(Request request, Response response) {
	
			response.setResponseCode(200);
			return "Ok";
	}
	
	
}
