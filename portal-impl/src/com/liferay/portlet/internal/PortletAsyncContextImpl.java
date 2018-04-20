/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.internal;

import javax.portlet.PortletAsyncContext;
import javax.portlet.PortletAsyncListener;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author Neil Griffin
 */
public class PortletAsyncContextImpl implements PortletAsyncContext {

	@Override
	public void addListener(PortletAsyncListener portletAsyncListener)
		throws IllegalStateException {
	}

	@Override
	public void addListener(
			PortletAsyncListener portletAsyncListener,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {
	}

	@Override
	public void complete() throws IllegalStateException {
	}

	@Override
	public <T extends PortletAsyncListener> T createPortletAsyncListener(
		Class<T> listenerClass) throws PortletException {

		try {

			// TODO: Needs to support CDI. Would need to delegate to the
			// BeanPortletInvoker in order to create the managed bean via the
			// help of the CDI BeanManager.

			return listenerClass.newInstance();
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	@Override
	public void dispatch() throws IllegalStateException {
	}

	@Override
	public void dispatch(String s) throws IllegalStateException {
	}

	@Override
	public ResourceRequest getResourceRequest() throws IllegalStateException {
		return null;
	}

	@Override
	public ResourceResponse getResourceResponse() throws IllegalStateException {
		return null;
	}

	@Override
	public long getTimeout() {
		return 0;
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return false;
	}

	@Override
	public void setTimeout(long timeout) {
	}

	@Override
	public void start(Runnable runnable) throws IllegalStateException {
	}

}