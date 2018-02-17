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

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse3;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.lang.DoPrivilegedUtil;
import com.liferay.portlet.ActionResponseImpl;
import com.liferay.portlet.PortletURLImpl;

import java.lang.reflect.Constructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderURL;

/**
 * @author Neil Griffin
 */
public class ActionResponse3Impl
	extends ActionResponseImpl implements LiferayPortletResponse3 {

	@Override
	public <T extends PortletURL & ActionURL> T createActionURL(
		MimeResponse.Copy copy) {

		return (T)createActionURL(portletName, copy);
	}

	@Override
	public LiferayPortletURL createActionURL(String portletName) {

		// TODO: portlet3 - Might need to have a Portlet 2.0 compatibility mode
		// if/then check that passes MimeResponse.Copy.NONE. Need to try a
		// Pluto 2.0 test portlet.

		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE, MimeResponse.Copy.PUBLIC);
	}

	@Override
	public LiferayPortletURL createActionURL(
		String portletName, MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid) {

		return createLiferayPortletURL(
			plid, portletName, lifecycle, includeLinkToLayoutUuid, null);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, MimeResponse.Copy copy) {

		ThemeDisplay themeDisplay = (ThemeDisplay)
			portletRequestImpl.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = PortletResponseUtil.getLayout(
			portletRequestImpl, themeDisplay);

		// LOH -- Almost done it seems. There is this method for sure, but there
		// is at least one more that needs to be overridden from the parent
		// class in order to handle the MimeResponse.Copy parameter overloads

		if (_portletSetup == null) {
			_portletSetup = PortletResponseUtil.getPortletSetup(
				themeDisplay, layout, portletName);
		}

		return DoPrivilegedUtil.wrap(
			new LiferayPortletURLPrivilegedAction(
				plid, portletName, lifecycle, includeLinkToLayoutUuid, copy,
				layout, getPortlet(), _portletSetup, portletRequestImpl, this,
				getPlid(), _constructors));
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			plid, portletName, lifecycle, true, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, MimeResponse.Copy copy) {

		return createLiferayPortletURL(getPlid(), portletName, lifecycle, copy);
	}

	@Override
	public RenderURL createRedirectURL(MimeResponse.Copy copy)
		throws IllegalStateException {

		// TODO: portlet3

		return null;
	}

	@Override
	public <T extends PortletURL & RenderURL> T createRenderURL(
		MimeResponse.Copy copy) {

		return (T)createRenderURL(portletName, copy);
	}

	@Override
	public LiferayPortletURL createRenderURL(String portletName) {

		// TODO: portlet3 - Might need to have a Portlet 2.0 compatibility mode
		// if/then check that passes MimeResponse.Copy.NONE. Need to try a
		// Pluto 2.0 test portlet.

		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE, MimeResponse.Copy.PUBLIC);
	}

	@Override
	public LiferayPortletURL createRenderURL(
		String portletName, MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE, copy);
	}

	@Override
	public LiferayPortletURL createResourceURL(String portletName) {

		// TODO: portlet3 - Might need to have a Portlet 2.0 compatibility mode
		// if/then check that passes MimeResponse.Copy.NONE. Need to try a
		// Pluto 2.0 test portlet.

		return createLiferayPortletURL(
			portletName, PortletRequest.RESOURCE_PHASE, MimeResponse.Copy.ALL);
	}

	private final Map<String, Constructor<? extends PortletURLImpl>>
		_constructors = new ConcurrentHashMap<>();
	private PortletPreferences _portletSetup;

}