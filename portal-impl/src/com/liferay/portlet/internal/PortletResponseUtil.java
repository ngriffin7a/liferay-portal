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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.StrutsActionPortletURL;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletResponseImpl;
import com.liferay.portlet.PortletURLImpl;

import java.lang.reflect.Constructor;

import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.WindowStateException;

/**
 * @author Neil Griffin
 */
public class PortletResponseUtil {

	public static LiferayPortletURL doCreateLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid, MimeResponse.Copy copy, Layout layout,
		Portlet portlet, PortletPreferences portletPreferences,
		PortletRequest portletRequest, PortletResponseImpl portletResponseImpl,
		long requestPlid,
		Map<String, Constructor<? extends PortletURLImpl>> constructors) {

		try {
			String linkToLayoutUuid = GetterUtil.getString(
				portletPreferences.getValue(
					"portletSetupLinkToLayoutUuid", null));

			if (PropsValues.PORTLET_CROSS_LAYOUT_INVOCATION_MODE.equals(
					"render") &&
				!PortletRequest.RENDER_PHASE.equals(lifecycle)) {

				includeLinkToLayoutUuid = false;
			}

			if (Validator.isNotNull(linkToLayoutUuid) &&
				includeLinkToLayoutUuid) {

				try {
					Layout linkedLayout =
						LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(
							linkToLayoutUuid, layout.getGroupId(),
							layout.isPrivateLayout());

					plid = linkedLayout.getPlid();
				}
				catch (PortalException pe) {

					// LPS-52675

					if (_log.isDebugEnabled()) {
						_log.debug(pe, pe);
					}
				}
			}
		}
		catch (SystemException se) {
			if (_log.isWarnEnabled()) {
				_log.warn(se);
			}
		}

		if (plid == LayoutConstants.DEFAULT_PLID) {
			plid = requestPlid;
		}

		LiferayPortletURL portletURL = null;

		String portletURLClass = portlet.getPortletURLClass();

		String portletId = portlet.getPortletId();

		if (portletId.equals(portletName) &&
			Validator.isNotNull(portletURLClass)) {

			if (portletURLClass.equals(
					StrutsActionPortletURL.class.getName())) {

				portletURL = new StrutsActionPortletURL(
					portletResponseImpl, plid, lifecycle);
			}
			else {
				try {
					Constructor<? extends PortletURLImpl> constructor =
						constructors.get(portletURLClass);

					if (constructor == null) {
						Class<?> portletURLClassObj = Class.forName(
							portletURLClass);

						constructor = (Constructor<? extends PortletURLImpl>)
							portletURLClassObj.getConstructor(
								new Class<?>[] {
									PortletResponseImpl.class, long.class,
									String.class
								});

						constructors.put(portletURLClass, constructor);
					}

					portletURL = constructor.newInstance(
						new Object[] {portletResponseImpl, plid, lifecycle});
				}
				catch (Exception e) {
					_log.error("Unable to create portlet URL", e);
				}
			}
		}

		if (portletURL == null) {
			if (portletName.equals(portletId)) {
				portletURL = PortletURLFactoryUtil.create(
					portletRequest, portlet, plid, lifecycle, copy);
			}
			else {
				portletURL = PortletURLFactoryUtil.create(
					portletRequest, portletName, plid, lifecycle, copy);
			}
		}

		try {
			if (portlet.hasWindowState(
					portletRequest.getResponseContentType(),
					portletRequest.getWindowState())) {

				portletURL.setWindowState(portletRequest.getWindowState());
			}
		}
		catch (WindowStateException wse) {
			_log.error(wse.getMessage());
		}

		try {
			if (portlet.hasPortletMode(
					portletRequest.getResponseContentType(),
					portletRequest.getPortletMode())) {

				portletURL.setPortletMode(portletRequest.getPortletMode());
			}
		}
		catch (PortletModeException pme) {
			_log.error(pme.getMessage());
		}

		return portletURL;
	}

	public static Layout getLayout(
		PortletRequest portletRequest, ThemeDisplay themeDisplay) {

		Layout layout = (Layout)portletRequest.getAttribute(WebKeys.LAYOUT);

		if ((layout == null) && (themeDisplay != null)) {
			layout = themeDisplay.getLayout();
		}

		return layout;
	}

	public static PortletPreferences getPortletSetup(
		ThemeDisplay themeDisplay, Layout layout, String portletName) {

		if (themeDisplay == null) {
			return PortletPreferencesFactoryUtil.getStrictLayoutPortletSetup(
				layout, portletName);
		}
		else {
			return themeDisplay.getStrictLayoutPortletSetup(
				layout, portletName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletResponseUtil.class);

}