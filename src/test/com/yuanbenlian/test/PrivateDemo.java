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
import com.yuanbenlian.model.http.MetadataQueryResp;
import com.yuanbenlian.model.http.MetadataSaveResp;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class PrivateDemo {
    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";

    @Test
    public void savePrivateData() throws InvalidException, UnsupportedEncodingException {
        Metadata metadata = new Metadata();
        TestUtil.fillBlockHash(metadata, URL);
        metadata.setType(Constants.TYPE_PRIVATE);

        metadata.setContentHash(DTCPProcessor.GenContentHash("This is a private data"));

        Metadata.License license = new Metadata.License();
        license.setType("none");
        metadata.setLicense(license);

        TreeMap<String, String> extra = new TreeMap<>();
        extra.put("function", "test");

        metadata.setExtra(extra);

        metadata = DTCPProcessor.FullMetadata(private_key, metadata);

        MetadataSaveResp saveResp = NodeProcessor.SaveMetadata(URL, metadata);
        assert (saveResp != null) : "response is empty";
        assert (Constants.NODE_SUCCESS.equalsIgnoreCase(saveResp.getCode())) : saveResp.getMsg();
        System.out.println("success。" + saveResp.getData().getDna());
    }

    @Test
    public void QueryPrivateDataTx() {
        String dna = "FMTECG958T16MUVJ1YH2E1RCKEHF5PDKV9ALMO5KPCONPZPQ6";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();
            System.out.println("QueryPrivateDataTx success:\n" + resp.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

}
