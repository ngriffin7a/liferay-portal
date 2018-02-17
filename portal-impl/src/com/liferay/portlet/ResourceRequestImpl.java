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

import aQute.bnd.annotation.ProviderType;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.internal.ResourceParametersImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletAsyncContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderParameters;
import javax.portlet.ResourceParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
@ProviderType
public class ResourceRequestImpl
	extends ClientDataRequestImpl implements ResourceRequest {

	@Override
	public String getCacheability() {
		return _cacheablity;
	}

	@Override
	public DispatcherType getDispatcherType() {

		// TODO: portlet3

		return null;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.RESOURCE_PHASE;
	}

	@Override
	public PortletAsyncContext getPortletAsyncContext() {

		// TODO: portlet3

		return null;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		Map<String, String[]> renderParameters = RenderParametersPool.get(
			getOriginalHttpServletRequest(), getPlid(), getPortletName());

		if ((renderParameters == null) || renderParameters.isEmpty()) {
			return Collections.emptyMap();
		}

		Portlet portlet = getPortlet();

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		if (publicRenderParameters.isEmpty()) {
			return Collections.unmodifiableMap(renderParameters);
		}

		Map<String, String[]> privateRenderParameters = new HashMap<>();

		for (Map.Entry<String, String[]> entry : renderParameters.entrySet()) {
			if (portlet.getPublicRenderParameter(entry.getKey()) != null) {
				continue;
			}

			privateRenderParameters.put(entry.getKey(), entry.getValue());
		}

		if (privateRenderParameters.isEmpty()) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(privateRenderParameters);
	}

	@Override
	public String getResourceID() {
		return _resourceID;
	}

	@Override
	public ResourceParameters getResourceParameters() {
		return _resourceParameters;
	}

	@Override
	public boolean isAsyncStarted() {

		// TODO: portlet3

		return false;
	}

	@Override
	public boolean isAsyncSupported() {

		// TODO: portlet3

		return false;
	}

	@Override
	public PortletAsyncContext startPortletAsync()
		throws IllegalStateException {

		// TODO: portlet3

		return null;
	}

	@Override
	public PortletAsyncContext startPortletAsync(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {

		// TODO: portlet3

		return null;
	}

	@Override
	protected void init(
		HttpServletRequest request, Portlet portlet,
		InvokerPortlet invokerPortlet, PortletContext portletContext,
		WindowState windowState, PortletMode portletMode,
		PortletPreferences preferences, long plid) {

		if (Validator.isNull(windowState.toString())) {
			windowState = WindowState.NORMAL;
		}

		if (Validator.isNull(portletMode.toString())) {
			portletMode = PortletMode.VIEW;
		}

		super.init(
			request, portlet, invokerPortlet, portletContext, windowState,
			portletMode, preferences, plid);

		_cacheablity = ParamUtil.getString(
			request, "p_p_cacheability", ResourceURL.PAGE);

		_resourceID = request.getParameter("p_p_resource_id");

		if (!PortalUtil.isValidResourceId(_resourceID)) {
			_resourceID = StringPool.BLANK;
		}

		Map<String, String[]> resourceParameterMap = new LinkedHashMap<>();
		RenderParameters renderParameters = getRenderParameters();

		Set<String> renderParameterNames = renderParameters.getNames();

		Map<String, String[]> parameterMap = getParameterMap();
		String portletNamespace = PortalUtil.getPortletNamespace(
			getPortletName());
		Map<String, String[]> servletRequestParameterMap =
			request.getParameterMap();

		for (Map.Entry<String, String[]> mapEntry : parameterMap.entrySet()) {
			String name = mapEntry.getKey();

			if (!renderParameterNames.contains(name)) {
				resourceParameterMap.put(name, mapEntry.getValue());
			}
			else {

				// If the parameter name is not a public/private render
				// parameter, then it is an resource parameter. Also, if the
				// parameter name is prefixed with the portlet namespace in the
				// original request, then it is to be regarded as an resource
				// parameter (even if it has the/ same name as a public render
				// parameter).
				// See: TCK V3PortletParametersTests_SPEC11_4_getNames

				String namespacedParameter = portletNamespace + name;

				if (renderParameterNames.contains(name) &&
					servletRequestParameterMap.containsKey(
						namespacedParameter)) {

					String[] resourceParameterArray =
						servletRequestParameterMap.get(namespacedParameter);

					if (resourceParameterArray == null) {
						resourceParameterArray = new String[0];
					}

					String[] renderParameterArray = renderParameters.getValues(
						name);

					if (renderParameterArray == null) {
						renderParameterArray = new String[0];
					}

					if (!Arrays.equals(
							resourceParameterArray, renderParameterArray)) {

						List<String> renderParameterValues = Arrays.asList(
							renderParameterArray);
						List<String> resourceParameterValues =
							new ArrayList<>();

						for (String resourceParameterValue :
								resourceParameterArray) {

							if (!renderParameterValues.contains(
									resourceParameterValue)) {

								resourceParameterValues.add(
									resourceParameterValue);
							}
						}

						resourceParameterMap.put(
							name,
							resourceParameterValues.toArray(new String[0]));
					}
					else {
						System.err.println(
							"!@#$ NOT COUNTING AS RESOURCE PARAM name=" + name);
					}
				}
			}
		}

		_resourceParameters = new ResourceParametersImpl(
			resourceParameterMap, portletNamespace);
	}

	private String _cacheablity;
	private String _resourceID;
	private ResourceParameters _resourceParameters;

}