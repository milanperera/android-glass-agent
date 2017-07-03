/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.homeautomation.picavi.api.util;

import org.json.simple.JSONObject;

/**
 * This holds the required cdmf.unit.device.type.android_tv.platform.configuration for agent to connect to the server.
 */
public class AndroidConfiguration {
	public String tenantDomain;
	public String mqttEndpoint;

	public String getTenantDomain() {
		return tenantDomain;
	}

	public void setTenantDomain(String tenantDomain) {
		this.tenantDomain = tenantDomain;
	}

	public String getMqttEndpoint() {
		return mqttEndpoint;
	}

	public void setMqttEndpoint(String mqttEndpoint) {
		this.mqttEndpoint = mqttEndpoint;
	}

	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("tenantDomain", tenantDomain);
		obj.put("mqttEndpoint", mqttEndpoint);
		return obj.toString();
	}
}
