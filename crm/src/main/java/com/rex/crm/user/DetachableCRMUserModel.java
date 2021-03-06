/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rex.crm.user;

import org.apache.wicket.model.LoadableDetachableModel;

import com.rex.crm.beans.Account;
import com.rex.crm.beans.CRMUser;
import com.rex.crm.db.DAOImpl;

/**
 * detachable model for an instance of contact
 * 
 * @author igor
 * 
 */
public class DetachableCRMUserModel extends LoadableDetachableModel<CRMUser>
{
	private final int id;


	/**
	 * @param c
	 */
	public DetachableCRMUserModel(CRMUser u)
	{
		this(u.getId());
	}

	/**
	 * @param id
	 */
	public DetachableCRMUserModel(int id)
	{
		if (id == 0)
		{
			throw new IllegalArgumentException();
		}
		this.id = id;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return Long.valueOf(id).hashCode();
	}

	/**
	 * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
	 * 
	 * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj == null)
		{
			return false;
		}
		else if (obj instanceof DetachableCRMUserModel)
		{
		    DetachableCRMUserModel other = (DetachableCRMUserModel)obj;
			return other.id == id;
		}
		return false;
	}

	/**
	 * @see org.apache.wicket.model.LoadableDetachableModel#load()
	 */
	@Override
	protected CRMUser load()
	{
		return DAOImpl.getCRMUserInfoById(id);
	}
}
