package com.thedevd.springboot.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class ZuulRequestLoggingPreFilter extends ZuulFilter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean shouldFilter() {
		return true; // true means should be applied for each request.
	}

	@Override
	public Object run() throws ZuulException {
		// run method is the place where our filter logic is placed
		RequestContext reqCtx = RequestContext.getCurrentContext();
		HttpServletRequest httpRequest = reqCtx.getRequest();

		logger.info("Request method: {}, Request Url: {}", httpRequest.getMethod(),
				httpRequest.getRequestURL().toString());

		return null;
	}

	@Override
	public String filterType() {
		// Zuul supports 4 types of filters - pre,post,route and error
		return "pre";
	}

	@Override
	public int filterOrder() {
		// decide in which order filter to be applied
		return 1;
	}

}
