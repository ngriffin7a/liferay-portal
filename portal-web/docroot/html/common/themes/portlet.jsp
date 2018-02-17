<%--
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
--%>

<%@ include file="/html/common/themes/init.jsp" %>

<portlet:defineObjects />

<%
String tilesPortletContent = GetterUtil.getString(request.getAttribute(WebKeys.PORTLET_CONTENT_JSP));

if (Validator.isBlank(tilesPortletContent)) {
	tilesPortletContent = GetterUtil.getString(TilesAttributeUtil.getTilesAttribute(pageContext, "portlet_content"));
}

TilesAttributeUtil.removeComponentContext(pageContext);

Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

PortletPreferences portletSetup = portletDisplay.getPortletSetup();

String lifecycle = (String)request.getAttribute(PortletRequest.LIFECYCLE_PHASE);

// Portlet title

String responseTitle = null;

if (PortletRequest.HEADER_PHASE.equals(lifecycle)) {

	// System.err.println("!@#$ portlet.jsp: HEADER_PHASE!");
	// TODO portlet3: It PROBABLY makes no sense to get the portlet title here
	// at this point, because we are not really rendering the portlet to the
	// aggregated response in the HEADER_PHASE. Need to keep the following
	// code commented-out because what happens is that the headerResponse
	// can be equal to HeaderRequestBridgeLiferayImpl when the FacesBridge
	// dispatches to a JSP in the HEADER_PHASE because the Liferay Portal
	// PortletRequestDispatcherImpl.dispatch(PortletRequest,PortletResponse)
	// sets the "javax.portlet.request" attribute on the underlying
	// RestrictPortletRequest which carries all the way through to here.
	// Just doesn't make sense to try and unwrap it in order to get the
	// title. You think it was null anyway. Whatever, check the final
	// rendered page to see if the title is right in the RENDER_PHASE.

	/*
	try {
		Object hr = pageContext.getAttribute("headerResponse");
		System.err.println("!@#$ hr=" + hr);
		HeaderResponseImpl defineObjectsHeaderResponse =
			(HeaderResponseImpl)pageContext.getAttribute("headerResponse");

		responseTitle = defineObjectsHeaderResponse.getTitle();
		System.err.println("!@#$ portlet.jsp HeaderResponse title=" + responseTitle);
	}
	catch (Exception e) {
		e.printStackTrace();
		throw e;
	}

	*/

	responseTitle = HtmlUtil.escape(PortalUtil.getPortletTitle((HeaderResponse)pageContext.getAttribute("headerResponse")));

	// System.err.println("!@#$ portlet.jsp HEADER_PHASE responseTitle=" + responseTitle);

}
else {
	/* TODO: portlet3 - after you figure out why "jsp" keeps showing up in the rendered html, then have the code below look more like the headerResponse
	code above except with RenderResponse instead. Note that you would need to import the RenderResponse class at the top of this JSP, just like you
	do for HeaderResponse now.
	 */
	RenderResponseImpl defineObjectsRenderResponse = (RenderResponseImpl)pageContext.getAttribute("renderResponse");

	responseTitle = defineObjectsRenderResponse.getTitle();
}

String portletTitle = PortletConfigurationUtil.getPortletTitle(portletSetup, themeDisplay.getLanguageId());

if (portletDisplay.isAccess() && portletDisplay.isActive() && Validator.isNull(portletTitle)) {
	portletTitle = responseTitle;
}

if (Validator.isNull(portletTitle)) {
	portletTitle = PortalUtil.getPortletTitle(portlet, application, locale);
}

portletDisplay.setTitle(portletTitle);

// Portlet description

if (Validator.isNull(portletDisplay.getDescription())) {
	String portletDescription = PortalUtil.getPortletDescription(portlet, application, locale);

	portletDisplay.setDescription(portletDescription);
}

Group group = layout.getGroup();

boolean wsrp = ParamUtil.getBoolean(PortalUtil.getOriginalServletRequest(request), "wsrp");
%>

<c:choose>
	<c:when test="<%= wsrp %>">
		<liferay-wsrp-portlet>
			<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
		</liferay-wsrp-portlet>
	</c:when>
	<c:when test="<%= themeDisplay.isStateExclusive() %>">
		<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
	</c:when>
	<c:when test="<%= themeDisplay.isStatePopUp() %>">
		<div class="portlet-body">
			<c:if test='<%= !tilesPortletContent.endsWith("/error.jsp") %>'>
				<liferay-theme:portlet-messages
					group="<%= group %>"
					portlet="<%= portlet %>"
				/>
			</c:if>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(tilesPortletContent) %>">
					<liferay-util:include page="<%= StrutsUtil.TEXT_HTML_DIR + tilesPortletContent %>" />
				</c:when>
				<c:otherwise>

					<%
					if (PortletRequest.HEADER_PHASE.equals(lifecycle)) {
						RenderRequest defineObjectsHeaderRequest = (HeaderRequest)pageContext.getAttribute("headerRequest");

						pageContext.getOut().print(defineObjectsHeaderRequest.getAttribute(WebKeys.PORTLET_CONTENT));
					}
					else {
						RenderRequest defineObjectsRenderRequest = (RenderRequest)pageContext.getAttribute("renderRequest");

						pageContext.getOut().print(defineObjectsRenderRequest.getAttribute(WebKeys.PORTLET_CONTENT));
					}
					%>

				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>

		<%
		Boolean renderPortletResource = (Boolean)request.getAttribute(WebKeys.RENDER_PORTLET_RESOURCE);

		boolean runtimePortlet = (renderPortletResource != null) && renderPortletResource.booleanValue();

		boolean freeformPortlet = themeDisplay.isFreeformLayout() && !runtimePortlet && !layoutTypePortlet.hasStateMax();

		String containerStyles = StringPool.BLANK;

		if (freeformPortlet) {
			Properties freeformStyleProps = PropertiesUtil.load(portletSetup.getValue("portlet-freeform-styles", StringPool.BLANK));

			containerStyles = "style=\"height: ".concat(GetterUtil.getString(HtmlUtil.escapeAttribute(freeformStyleProps.getProperty("height")), "300px")).concat("; overflow: auto;\"");
		}
		%>

		<liferay-theme:wrap-portlet
			page="portlet.jsp"
		>
			<div class="<%= portletDisplay.isStateMin() ? "hide" : "" %> portlet-content-container" <%= containerStyles %>>
				<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
			</div>
		</liferay-theme:wrap-portlet>

		<c:if test="<%= freeformPortlet %>">
			<div class="portlet-resize-container">
				<div class="portlet-resize-handle"></div>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>