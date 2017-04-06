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

package com.liferay.portlet.tck.bridge.setup;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.osgi.framework.Bundle;

/**
 * @author Vernon Singleton
 */
public class TckSetup {

	public static void run(
		String directory, String servletContextName, String[] excludeWarNames,
		String[] servletContextNames) {

		try {
			_tckDeployFiles = directory;

			System.err.println("run: _tckDeployFiles = " + _tckDeployFiles);
			_log.error("run: _tckDeployFiles = " + _tckDeployFiles);

			for (long companyId : PortalUtil.getCompanyIds()) {
				System.err.println("run: companyId = " + companyId);
				setupPermissionChecker(companyId);

				Company company = CompanyLocalServiceUtil.getCompanyById(
					companyId);

				long userId = company.getDefaultUser().getUserId();

				System.err.println("run: userId = " + userId);
				ServiceUtil.addUser(userId, companyId, "tck", "tck");
				setupSites(
					companyId, userId, servletContextName, excludeWarNames,
					servletContextNames);

				// clearPermissionChecker();

			}

			System.err.println("run: ending.");
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	protected static void addAllUsersToSite(long groupId) throws Exception {
		List<User> users = UserLocalServiceUtil.getUsers(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		ArrayList<Long> userIdList = new ArrayList<>();

		for (User user : users) {
			if (!user.isDefaultUser()) {
				userIdList.add(user.getUserId());
			}
		}

		long[] userIds = new long[userIdList.size()];

		for (int i = 0; i < userIds.length; i++) {
			userIds[i] = userIdList.get(i);
		}

		UserLocalServiceUtil.addGroupUsers(groupId, userIds);
	}

	protected static void addPortlet(Layout portalPageLayout, LayoutTypePortlet
			layoutTypePortlet, long userId, int columnNumber, String portletId)
		throws PortalException {

		String columnNumberLabel = Integer.toString(columnNumber);

		// Liferay 6.2 changed the expected value for the String-based column
		// number. Previous versions didn't require the "column-" prefix.

		columnNumberLabel = "column-" + columnNumber;

		// NOTE: In Liferay 6.1.x the following call was to setPortletIds()
		// but that method was removed in 6.2.x

		layoutTypePortlet.addPortletId(userId, portletId, columnNumberLabel, 1);

		layoutTypePortlet.resetModes();
		layoutTypePortlet.resetStates();
	}

	protected static Layout getPortalPageLayout(
			long userId, long groupId, String portalPageName,
			boolean privateLayout)
		throws Exception {

		Layout portalPageLayout = null;

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			groupId, privateLayout);

		for (Layout layout : layouts) {
			if (layout.getName(Locale.US).equals(portalPageName)) {
				portalPageLayout = layout;
			}
		}

		if (portalPageLayout == null) {
			long parentLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;
			String type = LayoutConstants.TYPE_PORTLET;
			String friendlyURL = "/" + StringUtil.toLowerCase(portalPageName);

			portalPageLayout = ServiceUtil.addLayout(
				userId, groupId, privateLayout, parentLayoutId, portalPageName,
				portalPageName, portalPageName, type, false, friendlyURL);
		}

		return portalPageLayout;
	}

	protected static String getServletContextName(
		String portalPageName, String[] servletContextNames) {

		System.err.println(
			"getServletContextName: portalPageName = " + portalPageName);

		String servletContextName = "";
		int maxLength = 0;

		for (String war : servletContextNames) {
			war = war.replaceFirst("tck-", "");

			if (portalPageName.startsWith(war)) {
				if (maxLength == 0) {
					maxLength = war.length();
					servletContextName = "tck-" + war;
				}
				else {
					if (maxLength < war.length()) {
						maxLength = war.length();
						servletContextName = "tck-" + war;
					}
				}
			}
		}

		System.err.println(
			"getServletContextName: servletContextName = " +
				servletContextName);

		return servletContextName;
	}

	protected static void setupPage(
			long userId, long groupId, PortalPage portalPage,
			boolean privateLayout, Bundle[] bundles, String servletContextName,
			String[] servletContextNames)
		throws Exception {

		String portalPageName = portalPage.getPageName();

		System.err.println("setupPage: portalPageName = " + portalPageName);

		List<Portlet> portlets = portalPage.getPortlets();
		Layout portalPageLayout = getPortalPageLayout(
			userId, groupId, portalPageName, privateLayout);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)portalPageLayout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			userId, portalPage.getLayoutTemplateId(), false);

		int columnNumber = 1;

		List<String> addedPortletIds = new ArrayList<>();

		for (Portlet portlet : portlets) {

			// TODO remove V2.  Later the V2 prefix may go away
			// when we start implementing portlet 3.

			//

			// establish the servletContextName for this page, if it is a glob

			if ("tck-V2*".equals(servletContextName)) {
				servletContextName = getServletContextName(
					portalPageName, servletContextNames);
			}

			System.err.println(
				"setupPage: searching for servetContextName = " +
					servletContextName);

			long bundleId = 0L;
			int bundleState = Bundle.UNINSTALLED;

			for (Bundle bundle : bundles) {
				String symbolicName = bundle.getSymbolicName();

				// TODO remove bundle symbolicName magic

				if (symbolicName.startsWith(servletContextName)) {
					bundleId = bundle.getBundleId();
					bundleState = bundle.getState();
				}
			}

			String portletName = portlet.getPortletName();

			System.err.println("setupPage: final bundleId = " + bundleId);

			if (bundleId > 0) {
				if (bundleState == Bundle.ACTIVE) {
					String portletId;

					if (portlet.getArtifactType() == Portlet.ArtifactType.WAB) {
						portletId = portletName + portlet.getInstanceToken();
					}
					else {

						// pluto 3 has a magic "tck" before the bundleName ...

						String noDashPortletName = portletName.replaceAll(
							"[-]", "");

						String warTckBundleName = "_WAR_" + servletContextName;

						portletId =
							noDashPortletName +
								warTckBundleName.replaceAll("[.]", "") +
									portlet.getInstanceToken();
					}

					System.err.println(
						"adding portletId=[" + portletId + "] portletName=[" +
							portletName + "] ...");
					addPortlet(
						portalPageLayout, layoutTypePortlet, userId,
						columnNumber, portletId);

					// not needed for Liferay 7
					// storePortletPreferences(portalPageLayout, portletId);

					addedPortletIds.add(portletId);
				}
				else {
					System.err.println(
						"Unable to add portletName=[" + portletName +
							"] since bundle =[" + servletContextName +
								"] is not active.");
				}
			}
			else {
				System.err.println(
					"Unable to add portletName=[" + portletName +
						"] since bundleName=[" + servletContextName +
							"] is not deployed.");
			}

			// columnNumber++;

		}

		LayoutLocalServiceUtil.updateLayout(portalPageLayout);
		System.err.println("setupPage: groupId = " + groupId);
		System.err.println("setupPage: privateLayout = " + privateLayout);
		System.err.println(
			"setupPage: portalPageLayout.getLayoutId() = " +
				portalPageLayout.getLayoutId());
		System.err.println(
			"setupPage: themeId = portlettck_WAR_potlettcktheme");
		System.err.println(
			"setupPage: portalPageLayout.getColorSchemetId() = " +
				portalPageLayout.getColorSchemeId());
		System.err.println("setupPage: css = ");
		System.err.println(
			"setupPage: LayoutLocalServiceUtil.updateLookAndFeel ...");
		LayoutLocalServiceUtil.updateLookAndFeel(
			groupId, privateLayout, portalPageLayout.getLayoutId(),
			"portlettck_WAR_portlettcktheme",
			portalPageLayout.getColorSchemeId(), "");

		int totalAddedPortlets = addedPortletIds.size();

		if (totalAddedPortlets == 0) {
			System.err.println(
				"Setting up page name=[" + portalPageName +
					"] without any portlets");
		}
		else if (totalAddedPortlets == 1) {
			System.err.println(
				"Setting up page name=[" + portalPageName + "] with portlet=" +
					addedPortletIds.toString());
		}
		else {
			System.err.println(
				"Setting up page name=[" + portalPageName + "] with portlets=" +
					addedPortletIds.toString());
		}
	}

	/**
	 * This method sets up the {@link PermissionChecker} {@link ThreadLocal}
	 * prior to performing additional test setup.
	 *
	 * @throws SystemException
	 * @throws  PortalException
	 */
	protected static void setupPermissionChecker(long companyId)
		throws PortalException {

		_log.error("setupPermissionChecker: companyId = " + companyId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			Role administratorRole = RoleLocalServiceUtil.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			User administratorUser = UserLocalServiceUtil.getRoleUsers(
				administratorRole.getRoleId()).get(0);

			try {
				permissionChecker = PermissionCheckerFactoryUtil.create(
					administratorUser);

				PermissionThreadLocal.setPermissionChecker(permissionChecker);

				_fixedPermissionChecker = true;
			}
			catch (Exception e) {
				throw new SystemException(e);
			}
		}
	}

	protected static void setupPortletTCKSite(
			long companyId, long userId, Bundle[] bundles,
			String servletContextName, String[] excludeWarNames,
			String[] servletContextNames)
		throws Exception {

		Group site = _getSiteForSetup(companyId, userId, "Portlet TCK");

		long groupId = site.getGroupId();

		addAllUsersToSite(groupId);

		URL configFileURL = new File(
			_tckDeployFiles +
				"/pluto-portal-driver-config.xml").toURI().toURL();

		System.err.println(
			"setupPortletTCKSite: configFileURL = " + configFileURL.toString());

		Document document = SAXReaderUtil.read(configFileURL);

		Element rootElement = document.getRootElement();

		Element renderConfigElement = rootElement.element("render-config");

		Iterator<Element> pageElementIterator =
			renderConfigElement.elementIterator("page");

		while (pageElementIterator.hasNext()) {
			Element pageElement = pageElementIterator.next();

			Attribute nameAttribute = pageElement.attribute("name");

			String pageName = nameAttribute.getValue();

			// TODO remove pageName magic ... choke included tck pages

			HashMap excludedName = new HashMap();

			for (String warName : excludeWarNames) {
				excludedName.put(warName, "1");
			}

			String prefix = servletContextName.replaceFirst("tck-", "");

			// TODO remove V2.  Later the V2 prefix may go away
			// when we start implementing portlet 3.

			if ("tck-V2*".equals(servletContextName)) {
				prefix = "V2";
			}

			if (pageName.startsWith(prefix)) {
				if (excludedName.get(pageName) == null) {
					List<Element> portletElements = pageElement.elements(
						"portlet");
					List<Portlet> portlets = new LinkedList<>();

					for (Element elem : portletElements) {
						nameAttribute = elem.attribute("name");

						String portletName = nameAttribute.getValue();

						Portlet portlet = new Portlet(
							portletName, pageName, false);

						portlets.add(portlet);
					}

					PortalPage portalPage = new PortalPage(pageName, portlets);

					setupPrivatePage(
						userId, groupId, portalPage, bundles,
						servletContextName, servletContextNames);
				}
			}
		}
	}

	protected static void setupPrivatePage(
			long userId, long groupId, PortalPage portalPage, Bundle[] bundles,
			String servletContextName, String[] servletContextNames)
		throws Exception {

		setupPage(
			userId, groupId, portalPage, true, bundles, servletContextName,
			servletContextNames);
	}

	protected static void setupSites(
			long companyId, long userId, String servletContextName,
			String[] excludeWarNames, String[] servletContextNames)
		throws Exception {

		Bundle[] bundles = BundleUtil.getBundles();

		setupPortletTCKSite(
			companyId, userId, bundles, servletContextName, excludeWarNames,
			servletContextNames);
	}

	private static Group _getSiteForSetup(
			long companyId, long userId, String name)
		throws Exception {

		Group site;

		try {
			site = GroupLocalServiceUtil.getGroup(companyId, name);
		}
		catch (NoSuchGroupException nsge) {
			site = ServiceUtil.addActiveOpenGroup(userId, name);
		}

		System.err.println(
			"Setting up site name=[" + site.getName() + "] publicLayouts=[" +
				site.hasPublicLayouts() + "]");

		return site;
	}

	private static final Log _log = LogFactoryUtil.getLog(TckSetup.class);

	private static boolean _fixedPermissionChecker;
	private static String _tckDeployFiles = "";

}