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
import com.liferay.portlet.PortletURLImpl;
import com.liferay.portlet.ResourceResponseImpl;

import java.lang.reflect.Constructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionURL;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderURL;

/**
 * @author Neil Griffin
 */
public class ResourceResponse3Impl
	extends ResourceResponseImpl implements LiferayPortletResponse3 {

	@Override
	public ActionURL createActionURL(Copy copy) {
		return (ActionURL)createActionURL(getPortletName(), copy);
	}

	@Override
	public LiferayPortletURL createActionURL(String portletName, Copy copy) {
		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, Copy copy) {

		ThemeDisplay themeDisplay = (ThemeDisplay)
			portletRequestImpl.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = PortletResponseUtil.getLayout(
			portletRequestImpl, themeDisplay);

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
		long plid, String portletName, String lifecycle, Copy copy) {

		return createLiferayPortletURL(
			plid, portletName, lifecycle, true, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, Copy copy) {

		return createLiferayPortletURL(getPlid(), portletName, lifecycle, copy);
	}

	@Override
	public RenderURL createRenderURL(Copy copy) {
		return (RenderURL)createRenderURL(getPortletName(), copy);
	}

	@Override
	public LiferayPortletURL createRenderURL(String portletName, Copy copy) {
		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE, copy);
	}

	@Override
	public int getStatus() {

		// TODO: portlet3

		return 0;
	}

	@Override
	public void setContentLengthLong(long length) {

		// TODO: portlet3

	}

	@Override
	public void setStatus(int statusCode) {

		// TODO: portlet3

	}

	private final Map<String, Constructor<? extends PortletURLImpl>>
		_constructors = new ConcurrentHashMap<>();
	private PortletPreferences _portletSetup;

}