/*
 * Copyright 2019 Seven Seals Technology
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
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.entity.Article;
import com.yuanbenlian.model.http.MetadataSaveResp;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.ECKeyProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import java.util.TreeMap;

public class ArticleDemo {
    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String content = "The Yuanben Chain is a distributed underlying data network\n" +
            "The Chain is efficient, secure, easy to use, scalable, global, enterprise-level trusted alliance chain\n" +
            "Yuanben Chain supports sustainable on-chain data interaction and secures data which transfers through an intelligent contract and digital encryption algorithm\n" +
            "The Chain provides a worlds first unique ‘Yuanben Chain DNA’ which makes sure the data on the chain cannot be tampered with\n" +
            "The Chain has increased block-writing speeds to millisecond-levels by optimising the consensus mechanism and the original ‘Lightning DNA’ algorithm";


    // *****************  save article demo *****************
    @Test
    public void SaveArticle() {
        Metadata metadata = new Metadata();
        try {
            TestUtil.fillBlockHash(metadata, URL);
            metadata.setContent(content);
            metadata.setCategory("Test,YuanBen chain");

            metadata.setTitle("YuanBen chain test");
            //Not authorized
            metadata.setLicense(Metadata.License.NoneLicense);

            Article article = new Article();
            //company
            article.put("unit", "Seven Seals Technology");
            metadata.setData(article.toMap());

            metadata.setType(article.getType());

            //user identity
            String userID = "001";
            TreeMap<String, String> extra = new TreeMap<>();
            //Determination of ownership
            extra.put("owner", ECKeyProcessor.GetPubKeyFromPri(private_key));
            //Associate secondary user identity
            extra.put("sub_account", userID);

            metadata.setExtra(extra);

            metadata = DTCPProcessor.FullMetadata(private_key, metadata);

            MetadataSaveResp saveResp = NodeProcessor.SaveMetadata(URL, metadata);
            assert (saveResp != null) : "response is empty";
            assert (Constants.NODE_SUCCESS.equalsIgnoreCase(saveResp.getCode())) : saveResp.getMsg();
            System.out.println("success。" + saveResp.getData().getDna());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
