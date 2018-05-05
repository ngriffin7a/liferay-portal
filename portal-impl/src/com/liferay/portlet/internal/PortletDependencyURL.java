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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Neil Griffin
 */
public class PortletDependencyURL {

	public PortletDependencyURL(
		String cdnBaseURL, String cdnCssPath, String cdnJavaScriptPath,
		String name, String scope, String version, String markup) {

		_cdnBaseURL = cdnBaseURL;
		_cdnCssPath = cdnCssPath;
		_cdnJavaScriptPath = cdnJavaScriptPath;
		_name = name;
		_scope = scope;
		_version = version;
		_markup = markup;

		if (_name.endsWith(".css")) {
			_type = Type.CSS;
		}
		else if (_name.endsWith(".js")) {
			_type = Type.JAVASCRIPT;
		}
		else {
			_type = Type.OTHER;
		}
	}

	public Type getType() {
		return _type;
	}

	@Override
	public String toString() {
		if (Validator.isNull(_markup)) {
			StringBundler sb = new StringBundler(8);

			if (_type.equals(Type.CSS)) {
				sb.append("<link ");
				sb.append("href=\"");
				sb.append(_getPortalURL());
				sb.append("\" type=\"text/css\"></link>");
			}
			else if (_type.equals(Type.JAVASCRIPT)) {
				sb.append("<script ");
				sb.append("src=\"");
				sb.append(_getPortalURL());
				sb.append("\" type=\"text/javascript\"></script>");
			}
			else {
				sb.append("<!-- Unknown portlet resource dependency type ");
				sb.append("name=\"");
				sb.append(_name);
				sb.append("\" scope=\"");
				sb.append(_scope);
				sb.append("\" version=\"");
				sb.append(_version);
				sb.append("\" -->");
			}

			return sb.toString();
		}

		return _markup;
	}

	/**
	 * @author Neil Griffin
	 */
	public enum Type {

		CSS, JAVASCRIPT, OTHER

	}

	private String _getPortalURL() {
		StringBundler sb = new StringBundler(9);

		sb.append(_cdnBaseURL);
		sb.append(StringPool.FORWARD_SLASH);

		if (_type.equals(Type.CSS)) {
			sb.append(_cdnCssPath);
		}
		else if (_type.equals(Type.JAVASCRIPT)) {
			sb.append(_cdnJavaScriptPath);
		}

		if (Validator.isNotNull(_scope)) {
			sb.append(StringPool.FORWARD_SLASH);
			sb.append(_scope);
		}

		if (Validator.isNotNull(_version)) {
			sb.append(StringPool.FORWARD_SLASH);
			sb.append(_version);
		}

		sb.append(StringPool.FORWARD_SLASH);
		sb.append(_name);

		return sb.toString();
	}

	private final String _cdnBaseURL;
	private final String _cdnCssPath;
	private final String _cdnJavaScriptPath;
	private final String _markup;
	private final String _name;
	private final String _scope;
	private final Type _type;
	private final String _version;

}