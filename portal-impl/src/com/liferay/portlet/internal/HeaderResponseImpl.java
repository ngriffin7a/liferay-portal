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
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.lang.DoPrivilegedUtil;
import com.liferay.portlet.MimeResponseImpl;
import com.liferay.portlet.PortletURLImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.lang.reflect.Constructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionURL;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderURL;

import org.w3c.dom.Element;

/**
 * @author Neil Griffin
 */
public class HeaderResponseImpl extends MimeResponseImpl
	implements HeaderResponse, LiferayPortletResponse3 {

	@Override
	public void addDependency(String name, String scope, String version) {

		// TODO portlet3
		// The addDependency methods are meant to be a page-level type of
		// solution, so that the portal can prevent duplicate resources.
		// According to the Spec, The the addProperty( String, Element) method
		// should only be used for namespaced resources that only target a
		// single portlet window. So the fact that we're hack calling
		// addProperty doesn't fix that problem exactly. Also, need to look
		// at how Pluto deals with a "registry" of known resources and then
		// come up with a Liferay solution for that.

		Element element = createElement("asdf");

		addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
	}

	@Override
	public void addDependency(
			String name, String scope, String version, String markup) {

		// TODO portlet3
		// The bridge only calls this one at this time. In other words it
		// tries to always supply the markup. The portlet container doesn't
		// have to use it though, if it already has something in the registry.

		addDependency(markup); // New protected method you just added
	}

	@Override
	public ActionURL createActionURL(Copy copy) {
		return (ActionURL) createActionURL(getPortletName(), copy);
	}

	@Override
	public LiferayPortletURL createActionURL(String portletName, Copy copy) {
		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
			String portletName, String lifecycle, Copy copy) {
		return createLiferayPortletURL(getPlid(), portletName, lifecycle, copy);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
			long plid, String portletName, String lifecycle, Copy copy) {
		return createLiferayPortletURL(
			plid, portletName, lifecycle, true, copy);
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
	public RenderURL createRenderURL(Copy copy) {
		return (RenderURL) createRenderURL(getPortletName(), copy);
	}

	@Override
	public LiferayPortletURL createRenderURL(String portletName, Copy copy) {

		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE, copy);
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.HEADER_PHASE;
	}

	public String getTitle() {
		return _title;
	}

	public Boolean getUseDefaultTemplate() {
		return _useDefaultTemplate;
	}

	@Override
	public PrintWriter getWriter() throws IOException {

		// TODO portlet3 -- JAVADOC: Data written to the HeaderResponse writer
		// is added to the aggregated portal document HEAD section. The code
		// below just returns a printwriter that can write to a string for now.
		// But ultimately we need to find a way to write this captured stuff
		// into the <head>...</head> section.

		return new PrintWriter(new StringWriter());
	}

	@Override
	public void setTitle(String title) {
		_title = title;

		// See LEP-2188

		ThemeDisplay themeDisplay = (ThemeDisplay)
			portletRequestImpl.getAttribute(WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setTitle(_title);
	}

	public void setUseDefaultTemplate(Boolean useDefaultTemplate) {
		_useDefaultTemplate = useDefaultTemplate;
	}

	private final Map<String, Constructor<? extends PortletURLImpl>> _constructors =
		new ConcurrentHashMap<>();
	private PortletPreferences _portletSetup;
	private String _title;
	private Boolean _useDefaultTemplate;

}
