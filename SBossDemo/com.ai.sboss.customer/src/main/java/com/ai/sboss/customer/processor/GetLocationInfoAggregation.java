/**
 * 
 */
package com.ai.sboss.customer.processor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
/**
 * @author monica
 *
 */
public class GetLocationInfoAggregation implements AggregationStrategy{

	Logger logger = Logger.getLogger(GetLocationInfoAggregation.class);

	
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		String newBody = newIn.getBody(String.class);
		JSONObject json = null;
		String retbody = null;
		if (oldExchange == null) {
			//logger.info("newBody===>" + newBody);
			JSONArray ret = new JSONArray();
			ret.add(newBody);
			newIn.setBody(ret.toString());
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			String olddata = in.getBody(String.class);
			logger.info("newBody===>" + newBody);
			logger.info("olddata===>" + olddata);
			json = JSONObject.fromObject(newBody);
			JSONArray oldjson = JSONArray.fromObject(olddata);
			oldjson.add(json);
			retbody = oldjson.toString();
			logger.info("aggregated===>" + retbody);
			oldExchange.getIn().setBody(retbody);
			return oldExchange;
		}
	}

}
