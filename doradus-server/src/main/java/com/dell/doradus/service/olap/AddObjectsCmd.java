/*
 * Copyright (C) 2014 Dell, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dell.doradus.service.olap;

import java.io.Reader;
import java.util.Map;

import com.dell.doradus.common.ApplicationDefinition;
import com.dell.doradus.common.BatchResult;
import com.dell.doradus.common.HttpCode;
import com.dell.doradus.common.RESTResponse;
import com.dell.doradus.common.UNode;
import com.dell.doradus.common.Utils;
import com.dell.doradus.olap.OlapBatch;
import com.dell.doradus.service.rest.ReaderCallback;

/**
 * Implements the REST command: POST /{application}/{shard}[?{params}].
 */
// TODO: Switch back to ReaderCallback when OlapBatch.parseJSON is fixed
public class AddObjectsCmd extends ReaderCallback {

    public RESTResponse invokeStreamIn(Reader reader) {
        Utils.require(reader != null, "This command requires an input entity");
        String shard = m_request.getVariableDecoded("shard");
        ApplicationDefinition appDef = m_request.getAppDef();
        
        OlapBatch batch = null;
        if (m_request.getInputContentType().isJSON()) {
            batch = OlapBatch.parseJSON(reader);
        } else {
            UNode rootNode = UNode.parse(reader, m_request.getInputContentType());
            batch = OlapBatch.fromUNode(rootNode);
        }

        Map<String, String> paramMap = Utils.parseURIQuery(m_request.getVariable("params"));
        BatchResult batchResult = OLAPService.instance().addBatch(appDef, shard, batch, paramMap);
        String body = batchResult.toDoc().toString(m_request.getOutputContentType());
        return new RESTResponse(HttpCode.CREATED, body, m_request.getOutputContentType());
    }   // invokeStreamIn

}   // class AddObjectsCmd