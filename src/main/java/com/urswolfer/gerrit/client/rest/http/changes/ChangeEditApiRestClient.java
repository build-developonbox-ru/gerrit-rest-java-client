/*
 * Copyright (C) 2018 DOB, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.gerrit.client.rest.http.changes;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gerrit.extensions.api.changes.ChangeEditApi;
import com.google.gerrit.extensions.common.ChangeEditMessageInput;
import com.google.gerrit.extensions.common.EditInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.JsonElement;
import com.urswolfer.gerrit.client.rest.http.GerritRestClient;

/**
 * @author Aleksey Svistunov
 */
public class ChangeEditApiRestClient extends ChangeEditApi.NotImplemented {
    private final GerritRestClient gerritRestClient;
    private final EditInfoParser editInfoParser;
    private final String id;

    public ChangeEditApiRestClient(GerritRestClient gerritRestClient,
                                   EditInfoParser editInfoParser,
                                   String id) {
        this.gerritRestClient = gerritRestClient;
        this.editInfoParser = editInfoParser;
        this.id = id;

    }

    @Override
    public Optional<EditInfo> get() throws RestApiException {
        String request = getRequestPath() + "/edit";
        JsonElement jsonElement = gerritRestClient.getRequest(request);
        if (null == jsonElement) return Optional.absent();
        return Iterables.tryFind(editInfoParser.parseEditInfos(jsonElement), new Predicate<EditInfo>() {
            @Override
            public boolean apply(EditInfo editInfo) {
                return true;
            }
        });
    }

    @Override
    public void delete() throws RestApiException {
        String request = getRequestPath() + "/edit";
        gerritRestClient.deleteRequest(request);
    }

    @Override
    public void modifyCommitMessage(ChangeEditMessageInput changeEditMessageInput) throws RestApiException {
        String json = gerritRestClient.getGson().toJson(changeEditMessageInput);
        String request = getRequestPath() + "/edit:message";
        gerritRestClient.putRequest(request, json);
    }

    @Override
    public void publish() throws RestApiException {
        String json = gerritRestClient.getGson().toJson(null);
        String request = getRequestPath() + "/edit:publish";
        gerritRestClient.postRequest(request, json);
    }

    protected String getRequestPath() {
        return "/changes/" + id;
    }
}
