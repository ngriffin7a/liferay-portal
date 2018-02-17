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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.MutablePortletParameters;
import javax.portlet.PortletParameters;

/**
 * @author Neil Griffin
 */
public abstract class PortletParametersBase implements PortletParameters {

	public PortletParametersBase(Map<String, String[]> parameterMap) {
		this(parameterMap, null);
	}

	public PortletParametersBase(
		Map<String, String[]> parameterMap, String namespace) {

		_parameterMap = parameterMap;
		_namespace = namespace;
	}

	@Override
	public abstract MutablePortletParameters clone();

	@Override
	public Set<String> getNames() {
		return _parameterMap.keySet();
	}

	@Override
	public String getValue(String name) {
		String[] values = getValues(name);

		if ((values == null) || (values.length < 1)) {
			return null;
		}

		return values[0];
	}

	@Override
	public String[] getValues(String name) {
		String[] values = _parameterMap.get(name);

		if ((values == null) && (_namespace != null)) {
			values = _parameterMap.get(_namespace + name);
		}

		return values;
	}

	@Override
	public boolean isEmpty() {
		return _parameterMap.isEmpty();
	}

	@Override
	public int size() {
		return _parameterMap.size();
	}

	protected Map<String, String[]> deepCopyMap(Map<String, String[]> map) {
		Map<String, String[]> copiedMap = new HashMap<>(map.size());
		Set<String> keySet = map.keySet();

		for (String key : keySet) {
			String[] values = map.get(key);

			if (values != null) {
				String[] copiedParameterValues = values.clone();

				copiedMap.put(key, copiedParameterValues);
			}
			else {
				copiedMap.put(key, null);
			}
		}

		return copiedMap;
	}

	protected Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	private final String _namespace;
	private final Map<String, String[]> _parameterMap;

}