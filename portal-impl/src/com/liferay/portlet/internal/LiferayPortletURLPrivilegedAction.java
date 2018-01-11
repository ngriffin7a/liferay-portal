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
import com.liferay.portlet.PortletResponseImpl;
import com.liferay.portlet.PortletURLImpl;

import javax.portlet.MimeResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;
import java.util.Map;

public class LiferayPortletURLPrivilegedAction implements
	PrivilegedAction<LiferayPortletURL> {

	public LiferayPortletURLPrivilegedAction(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, MimeResponse.Copy copy,
		Layout layout, Portlet portlet, PortletPreferences portletPreferences,
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
		_requestPlid = requestPlid;
		_constructors = constructors;
	}

	@Override
	public LiferayPortletURL run() {
		return PortletResponseUtil.doCreateLiferayPortletURL(
			_plid, _portletName, _lifecycle, _includeLinkToLayoutUuid,
			_copy, _layout, _portlet, _portletPreferences, _portletRequest,
			_portletResponseImpl, _requestPlid, _constructors);
	}

	private final Map<String, Constructor<? extends PortletURLImpl>>
		_constructors;
	private final MimeResponse.Copy _copy;
	private final boolean _includeLinkToLayoutUuid;
	private final Layout _layout;
	private final String _lifecycle;
	private long _plid;
	private final Portlet _portlet;
	private final String _portletName;
	private PortletPreferences _portletPreferences;
	private final PortletRequest _portletRequest;
	private final PortletResponseImpl _portletResponseImpl;
	private final long _requestPlid;

}
