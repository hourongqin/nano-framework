/**
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nanoframework.extension.shiro.realm;

import org.nanoframework.commons.entity.BaseEntity;

/**
 * @author yanghe
 * @date 2015年12月9日 上午8:42:08
 */
public class RealmQuery extends BaseEntity {
	private static final long serialVersionUID = 7418773011557990196L;
	
	protected String authenticationQuery;
	protected String userRolesQuery;
	protected String permissionsQuery;

	public String getAuthenticationQuery() {
		return authenticationQuery;
	}

	public void setAuthenticationQuery(String authenticationQuery) {
		this.authenticationQuery = authenticationQuery;
	}

	public String getUserRolesQuery() {
		return userRolesQuery;
	}

	public void setUserRolesQuery(String userRolesQuery) {
		this.userRolesQuery = userRolesQuery;
	}

	public String getPermissionsQuery() {
		return permissionsQuery;
	}

	public void setPermissionsQuery(String permissionsQuery) {
		this.permissionsQuery = permissionsQuery;
	}

}
