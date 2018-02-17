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

import javax.portlet.MutablePortletParameters;

/**
 * @author Neil Griffin
 */
public interface LiferayMutablePortletParameters
	extends MutablePortletParameters {

	/**
	 * Returns a flag indicating whether or not the state of the portlet
	 * parameters have changed.
	 * @return changed a value of <code>true</code> indicates that the state has
	 *         changed, otherwise <code>false</code> indicates that the state
	 *         has not changed.
	 */
	public boolean isChanged();

	/**
	 * Sets a flag indicating whether or not the state of the portlet parameters
	 * have changed.
	 *
	 * @param changed a value of <code>true</code> indicates that the state has
	 *        changed, otherwise <code>false</code> indicates that the state has
	 *        not changed.
	 */
	public void setChanged(boolean changed);

	/**
	 * Sets the URL parameter to the value.
	 *
	 * @param name the name of the URL parameter
	 * @param value the value of the URL parameter. If <code>null</code> then
	 *        the URL parameter is removed.
	 * @param append whether the new value should be appended to any existing
	 *        values for the parameter. If <code>append</code> is
	 *        <code>false</code> any existing values will be overwritten with
	 *        the new value.
	 * @return the value prior to setting
	 * @throws IllegalArgumentException if the name is <code>null</code>
	 */
	public String setValue(String name, String value, boolean append);

	/**
	 * Sets the URL parameter the values.
	 *
	 * @param name the name of the URL parameter
	 * @param values the values of the URL parameter. If <code>null</code> then
	 *        the URL parameter is removed.
	 * @param append whether the new values should be appended to any existing
	 *        values for the parameter. If <code>append</code> is
	 *        <code>false</code> any existing values will be overwritten with
	 *        the new values.
	 * @return the value prior to setting
	 * @throws IllegalArgumentException if the name is <code>null</code>
	 */
	public String[] setValues(String name, String[] values, boolean append);

}