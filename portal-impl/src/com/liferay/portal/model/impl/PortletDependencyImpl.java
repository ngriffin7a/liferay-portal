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

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.model.PortletDependency;

/**
 * @author Neil Griffin
 */
public class PortletDependencyImpl implements PortletDependency {

	public PortletDependencyImpl(String name, String scope, String version) {
		_name = name;
		_scope = scope;
		_version = version;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getScope() {
		return _scope;
	}

	@Override
	public String getVersion() {
		return _version;
	}

	private final String _name;
	private final String _scope;
	private final String _version;

}