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

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.AUIUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.StateAwareResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class DefineObjectsTag extends TagSupport {

	@Override
	public int doStartTag() {
		HttpServletRequest request =
			(HttpServletRequest)pageContext.getRequest();

		String lifecycle = (String)request.getAttribute(
			PortletRequest.LIFECYCLE_PHASE);

		PortletConfig portletConfig = (PortletConfig)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		if (portletConfig != null) {
			pageContext.setAttribute("portletConfig", portletConfig);
			pageContext.setAttribute(
				"portletContext", portletConfig.getPortletContext());
			pageContext.setAttribute(
				"portletName", portletConfig.getPortletName());
		}

		PortletRequest portletRequest = (PortletRequest)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			if (lifecycle.equals(PortletRequest.ACTION_PHASE) ||
				lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

				pageContext.setAttribute("clientDataRequest", portletRequest);
			}

			pageContext.setAttribute(
				"contextPath", portletRequest.getContextPath());
			pageContext.setAttribute("cookies", portletRequest.getCookies());

			pageContext.setAttribute(
				"liferayPortletRequest",
				PortalUtil.getLiferayPortletRequest(portletRequest));

			pageContext.setAttribute("locale", portletRequest.getLocale());

			ArrayList<Locale> locales = Collections.list(
				portletRequest.getLocales());
			pageContext.setAttribute(
				"locales", locales.toArray(new Locale[locales.size()]));

			String portletRequestAttrName = null;

			if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
				portletRequestAttrName = "actionRequest";
				ActionRequest actionRequest = (ActionRequest)portletRequest;

				pageContext.setAttribute(
					"actionParams", actionRequest.getActionParameters());
			}
			else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
				portletRequestAttrName = "eventRequest";
			}
			else if (lifecycle.equals(PortletRequest.HEADER_PHASE)) {
				portletRequestAttrName = "headerRequest";
			}
			else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
				portletRequestAttrName = "renderRequest";
			}
			else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
				portletRequestAttrName = "resourceRequest";
				ResourceRequest resourceRequest =
					(ResourceRequest)portletRequest;

				pageContext.setAttribute(
					"resourceParams", resourceRequest.getResourceParameters());
			}

			pageContext.setAttribute(
				"portletMode", portletRequest.getPortletMode());
			pageContext.setAttribute("portletRequest", portletRequest);
			pageContext.setAttribute(portletRequestAttrName, portletRequest);

			PortletPreferences portletPreferences =
				portletRequest.getPreferences();

			pageContext.setAttribute("portletPreferences", portletPreferences);
			pageContext.setAttribute(
				"portletPreferencesValues",
				ProxyUtil.newProxyInstance(
					ClassLoader.getSystemClassLoader(),
					new Class<?>[] {Map.class},
					new PortletPreferencesValuesInvocationHandler(
						portletPreferences)));

			PortletSession portletSession = portletRequest.getPortletSession();

			pageContext.setAttribute("portletSession", portletSession);

			try {
				pageContext.setAttribute(
					"portletSessionScope", portletSession.getAttributeMap());
			}
			catch (IllegalStateException ise) {
			}

			pageContext.setAttribute("windowId", portletRequest.getWindowID());
			pageContext.setAttribute(
				"windowState", portletRequest.getWindowState());
		}

		PortletResponse portletResponse = (PortletResponse)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return SKIP_BODY;
		}

		pageContext.setAttribute(
			"liferayPortletResponse",
			PortalUtil.getLiferayPortletResponse(portletResponse));

		String namespace = AUIUtil.getNamespace(portletRequest, portletResponse);

		if (Validator.isNull(namespace)) {
			namespace = AUIUtil.getNamespace(request);
		}

		pageContext.setAttribute("namespace", namespace);

		String portletResponseAttrName = null;

		if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			portletResponseAttrName = "actionResponse";
		}
		else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
			portletResponseAttrName = "eventResponse";
		}
		else if (lifecycle.equals(PortletRequest.HEADER_PHASE)) {
			portletResponseAttrName = "headerResponse";
		}
		else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			portletResponseAttrName = "renderResponse";
		}
		else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			portletResponseAttrName = "resourceResponse";
		}

		pageContext.setAttribute("portletResponse", portletResponse);
		pageContext.setAttribute(portletResponseAttrName, portletResponse);

		if (lifecycle.equals(PortletRequest.ACTION_PHASE) ||
			lifecycle.equals(PortletRequest.EVENT_PHASE)) {

			StateAwareResponse stateAwareResponse =
				(StateAwareResponse)portletResponse;

			pageContext.setAttribute(
				"mutableRenderParams",
				stateAwareResponse.getRenderParameters());
			pageContext.setAttribute("stateAwareResponse", stateAwareResponse);
		}

		return SKIP_BODY;
	}

	private static class PortletPreferencesValuesInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			if (_map == null) {
				_map = _portletPreferences.getMap();
			}

			return method.invoke(_map, args);
		}

		private PortletPreferencesValuesInvocationHandler(
			PortletPreferences portletPreferences) {

			_portletPreferences = portletPreferences;
		}

		private Map<String, String[]> _map;
		private final PortletPreferences _portletPreferences;

	}

}