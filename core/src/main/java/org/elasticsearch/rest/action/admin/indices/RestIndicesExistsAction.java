/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.indices;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.action.RestResponseListener;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.HEAD;
import static org.elasticsearch.rest.RestStatus.NOT_FOUND;
import static org.elasticsearch.rest.RestStatus.OK;

public class RestIndicesExistsAction extends BaseRestHandler {
    public RestIndicesExistsAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(HEAD, "/{index}", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(Strings.splitStringByCommaToArray(request.param("index")));
        IndicesOptions indicesOptions = IndicesOptions.fromRequest(request, indicesExistsRequest.indicesOptions());
        indicesExistsRequest.expandWilcardsOpen(indicesOptions.expandWildcardsOpen());
        indicesExistsRequest.expandWilcardsClosed(indicesOptions.expandWildcardsClosed());
        indicesExistsRequest.local(request.paramAsBoolean("local", indicesExistsRequest.local()));
        return channel -> client.admin().indices().exists(indicesExistsRequest, new RestResponseListener<IndicesExistsResponse>(channel) {
            @Override
            public RestResponse buildResponse(IndicesExistsResponse response) {
                if (response.isExists()) {
                    return new BytesRestResponse(OK, BytesRestResponse.TEXT_CONTENT_TYPE, BytesArray.EMPTY);
                } else {
                    return new BytesRestResponse(NOT_FOUND, BytesRestResponse.TEXT_CONTENT_TYPE, BytesArray.EMPTY);
                }
            }

        });
    }
}
