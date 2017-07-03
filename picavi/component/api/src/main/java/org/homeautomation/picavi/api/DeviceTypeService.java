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

package org.homeautomation.picavi.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.ResponseHeader;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.homeautomation.picavi.api.constants.PicaviConstants;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This is the API which is used to control and manage device type functionality
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "picavi"),
                                @ExtensionProperty(name = "context", value = "/picavi"),
                        })
                }
        ),
        tags = {
                @Tag(name = "picavi", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:picavi:enroll",
                        permissions = {"/device-mgt/devices/enroll/picavi"}
                )
        }
)
@SuppressWarnings("NonJaxWsWebServices")
public interface DeviceTypeService {
    /**
     * End point to send message to Picavi device.
     */
    @POST
    @Path("device/{deviceId}/message")
    @ApiOperation(
            httpMethod = "POST",
            value = "Send message to Picavi device",
            notes = "",
            response = Response.class,
            tags = "picavi",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = PicaviConstants.SCOPE, value = "perm:picavi:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                            + " send threashold",
                    response = Response.class)
    })
    Response sendMessage(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "message",
                    value = "The message to be displayed.",
                    required = true)
            @QueryParam("message") String message);

    /**
     * Enroll devices.
     */
    @POST
    @Path("device/{device_id}/register")
    @ApiOperation(
            httpMethod = "POST",
            value = "Enroll device",
            notes = "",
            response = Response.class,
            tags = "picavi",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = PicaviConstants.SCOPE, value = "perm:picavi:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = "Accepted.",
                    response = Response.class),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable"),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error on retrieving stats",
                    response = Response.class)
    })
    Response register(
            @ApiParam(
                    name = "deviceId",
                    value = "Device identifier id of the device to be added",
                    required = true)
            @PathParam("device_id") String deviceId,
            @ApiParam(
                    name = "deviceName",
                    value = "Device name of the device to be added",
                    required = true)
            @QueryParam("deviceName") String deviceName);

    /**
     * Retrieve Sensor data for the device type
     */
    @Path("device/stats/{deviceId}")
    @GET
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve Sensor data for the device type",
            notes = "",
            response = Response.class,
            tags = "picavi",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = PicaviConstants.SCOPE, value = "perm:picavi:enroll")
                    })
            }
    )
    @Consumes("application/json")
    @Produces("application/json")
    Response getPicaviDeviceStats(@PathParam("deviceId") String deviceId, @QueryParam("from") long from,
                                  @QueryParam("to") long to, @QueryParam("sensorType") String sensorType);


}