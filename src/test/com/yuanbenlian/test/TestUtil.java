/*
 * Copyright 2018 Seven Seals Technology
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

package com.yuanbenlian.test;

import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.http.BlockHashQueryResp;
import com.yuanbenlian.service.NodeProcessor;

public class TestUtil {
    public static void fillBlockHash(Metadata metadata, String URL) throws InvalidException {
        BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL);
        if (resp == null || !Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode())) {
            //use default value
            metadata.setBlockHash("FD6C96C7EE44BE1774843CF6A806A757C3AD7FA1");
            metadata.setBlockHeight("199130");
        } else {
            metadata.setBlockHash(resp.getData().getLatestBlockHash());
            metadata.setBlockHeight(resp.getData().getLatestBlockHeight().toString());
        }
    }
}
