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
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.PortletResponseImpl;
import com.liferay.portlet.PortletURLImpl;

import java.lang.reflect.Constructor;

import java.security.PrivilegedAction;

import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Neil Griffin
 */
public class LiferayPortletURLPrivilegedAction
	implements PrivilegedAction<LiferayPortletURL> {

	public LiferayPortletURLPrivilegedAction(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, MimeResponse.Copy copy, Layout layout,
		Portlet portlet, PortletPreferences portletPreferences,
		PortletRequest portletRequest, PortletResponseImpl portletResponseImpl,
		long requestPlid,
		Map<String, Constructor<? extends PortletURLImpl>> constructors) {

		_plid = plid;
		_portletName = portletName;
		_lifecycle = lifecycle;
		_includeLinkToLayoutUuid = includeLinkToLayoutUuid;
		_copy = copy;
		_layout = layout;
		_portlet = portlet;
		_portletPreferences = portletPreferences;
		_portletRequest = portletRequest;
		_portletResponseImpl = portletResponseImpl;
		_request = null;
		_requestPlid = requestPlid;
		_constructors = constructors;
	}

	public LiferayPortletURLPrivilegedAction(
		String portletName, String lifecycle, MimeResponse.Copy copy,
		Layout layout, Portlet portlet, HttpServletRequest request) {

		_portletName = portletName;
		_lifecycle = lifecycle;
		_copy = copy;
		_layout = layout;
		_portlet = portlet;
		_request = request;

		_constructors = null;
		_includeLinkToLayoutUuid = false;
		_plid = 0;
		_portletPreferences = null;
		_portletRequest = null;
		_portletResponseImpl = null;
		_requestPlid = 0;
	}

	@Override
	public LiferayPortletURL run() {
		if (_request != null) {
			return PortletURLFactoryUtil.create(
				_request, _portlet, _layout, _lifecycle, _copy);
		}

		return PortletResponseUtil.doCreateLiferayPortletURL(
			_plid, _portletName, _lifecycle, _includeLinkToLayoutUuid, _copy,
			_layout, _portlet, _portletPreferences, _portletRequest,
			_portletResponseImpl, _requestPlid, _constructors);
	}

	private final Map<String, Constructor<? extends PortletURLImpl>>
		_constructors;
	private final MimeResponse.Copy _copy;
	private final boolean _includeLinkToLayoutUuid;
	private final Layout _layout;
	private final String _lifecycle;
	private final long _plid;
	private final Portlet _portlet;
	private final String _portletName;
	private final PortletPreferences _portletPreferences;
	private final PortletRequest _portletRequest;
	private final PortletResponseImpl _portletResponseImpl;
	private final HttpServletRequest _request;
	private final long _requestPlid;

}