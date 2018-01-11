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

package com.liferay.portlet;

import com.liferay.portlet.internal.ResourceResponse3Impl;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class ResourceResponseFactory {

	public static ResourceResponseImpl create(
		ResourceRequestImpl resourceRequestImpl, HttpServletResponse response) {

		// TODO: Portlet 3.0 -- maybe return new ResourceResponseImpl() for Portlet 2.0.
		ResourceResponseImpl resourceResponseImpl = new ResourceResponse3Impl();

		resourceResponseImpl.init(resourceRequestImpl, response);

		return resourceResponseImpl;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #create(ResourceRequestImpl,
	 *             HttpServletResponse)}
	 */
	@Deprecated
	public static ResourceResponseImpl create(
			ResourceRequestImpl resourceRequestImpl,
			HttpServletResponse response, String portletName, long companyId)
		throws Exception {

		return create(resourceRequestImpl, response);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #create(ResourceRequestImpl,
	 *             HttpServletResponse)}
	 */
	@Deprecated
	public static ResourceResponseImpl create(
			ResourceRequestImpl resourceRequestImpl,
			HttpServletResponse response, String portletName, long companyId,
			long plid)
		throws Exception {

		return create(resourceRequestImpl, response);
	}

}