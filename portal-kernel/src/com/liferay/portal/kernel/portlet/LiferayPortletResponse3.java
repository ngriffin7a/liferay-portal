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

package com.liferay.portal.kernel.portlet;

import aQute.bnd.annotation.ProviderType;

import javax.portlet.ActionURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderURL;

/**
 * @author Neil Griffin
 */
@ProviderType
public interface LiferayPortletResponse3 extends LiferayPortletResponse {

	public <T extends PortletURL & ActionURL> T createActionURL(
		MimeResponse.Copy copy);

	public LiferayPortletURL createActionURL(
		String portletName, MimeResponse.Copy copy);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, MimeResponse.Copy copy);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		MimeResponse.Copy copy);

	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, MimeResponse.Copy copy);

	public <T extends PortletURL & RenderURL> T createRenderURL(
		MimeResponse.Copy copy);

	public LiferayPortletURL createRenderURL(
		String portletName, MimeResponse.Copy copy);

}