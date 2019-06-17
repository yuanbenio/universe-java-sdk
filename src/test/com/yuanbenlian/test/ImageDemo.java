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
import com.yuanbenlian.model.entity.Image;
import com.yuanbenlian.model.http.BlockHashQueryResp;
import com.yuanbenlian.model.http.MetadataQueryResp;
import com.yuanbenlian.model.http.MetadataSaveResp;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.ECKeyProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.TreeMap;

public class ImageDemo {

    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";


    // *****************  save image demo *****************
    @Test
    public void SaveImage() {
        Metadata metadata = new Metadata();
        String imgPath = "/Users/envin_xie/work/universe/universe-java-sdk/src/test/com/yuanbenlian/test/yuanben.png";
        try {

            File f = new File(imgPath);
            BufferedImage bi = ImageIO.read(f);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, imgPath.substring(imgPath.lastIndexOf(".") + 1), baos);
            byte[] bytes = baos.toByteArray();
            String imageBase64 = new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();

            TestUtil.fillBlockHash(metadata, URL);

            metadata.setContent(imageBase64);
            metadata.setType(Constants.TYPE_IMAGE);
            metadata.setCategory("test,YuanBen chain");

            metadata.setTitle("YuanBen chain test");
            Metadata.License license = new Metadata.License();
            license.setType("one-license");
            TreeMap<String, String> params = new TreeMap<>();
            params.put("sale", "no");
            license.setParameters(params);
            metadata.setLicense(license);

            Image data = new Image();
            data.setExt(imgPath.substring(imgPath.lastIndexOf(".") + 1));
            data.setThumb("https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.setOriginal("https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.setHeight("" + bi.getHeight());
            data.setWidth("" + bi.getWidth());
            data.setSize("" + imageBase64.length());
            metadata.setData(data.toMap());

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


    @Test
    //If you don't need content, just fill in the contentHash, such as a large picture.
    public void SaveImageRmContent() {
        Metadata metadata = new Metadata();
        String imgPath = "yuanbenlian.png";
        try {

            File f = new File(imgPath);
            BufferedImage bi = ImageIO.read(f);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, imgPath.substring(imgPath.lastIndexOf(".") + 1), baos);
            byte[] bytes = baos.toByteArray();
            String imageBase64 = new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();

            TestUtil.fillBlockHash(metadata, URL);

            //fill contentHash
            metadata.setContentHash(DTCPProcessor.GenContentHash(imageBase64));
            metadata.setType(Constants.TYPE_IMAGE);
            metadata.setCategory("test,YuanBen chain");

            metadata.setTitle("YuanBen chain test");
            Metadata.License license = new Metadata.License();
            license.setType("one-license");
            TreeMap<String, String> params = new TreeMap<>();
            params.put("sale", "no");
            license.setParameters(params);
            metadata.setLicense(license);

            Image data = new Image();
            data.setExt(imgPath.substring(imgPath.lastIndexOf(".") + 1));
            data.setThumb("https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.setOriginal("https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.setHeight("" + bi.getHeight());
            data.setWidth("" + bi.getWidth());
            data.setSize("" + imageBase64.length());
            metadata.setData(data.toMap());

            //user identity
            String userID = "100001";

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

    @Test
    public void QueryImageTx() {
//        String dna = "54Q6XUSQNOZ2CSAE5NOKLS09VEKKYPTMLZV71IWOJDPKCTFPZR";
        String dna = "689YBKTOSNZYDML45JG1ZLODEUN0YPWQY0MLIECHG1LKK167PK";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();
            System.out.println("QueryImageTx success:\n" + resp.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void TransferOwnership() {
        try {
            String ownerDNA = "54Q6XUSQNOZ2CSAE5NOKLS09VEKKYPTMLZV71IWOJDPKCTFPZR";
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, ownerDNA);

            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();

            String recipientPubKey = ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey();

            Metadata oldMetadata = resp.getData();
            Metadata newMetadata = new Metadata();

            newMetadata.setCategory(oldMetadata.getCategory());
            newMetadata.setParentDna(oldMetadata.getDna());
            newMetadata.setTitle("[Transfer Ownership] " + oldMetadata.getTitle());

            BlockHashQueryResp blockHashResp = NodeProcessor.QueryLatestBlockHash(URL);
            if (blockHashResp != null && Constants.NODE_SUCCESS.equalsIgnoreCase(blockHashResp.getCode())) {
                newMetadata.setBlockHash(blockHashResp.getData().getLatestBlockHash());
                newMetadata.setBlockHeight(blockHashResp.getData().getLatestBlockHeight().toString());
            } else {
                //use default value
                newMetadata.setBlockHash("FD6C96C7EE44BE1774843CF6A806A757C3AD7FA1");
                newMetadata.setBlockHeight("199130");
            }

            newMetadata.setSource(oldMetadata.getSource());
            newMetadata.setContent(oldMetadata.getContent());
            newMetadata.setContentHash(oldMetadata.getContentHash());
            newMetadata.setLicense(oldMetadata.getLicense());
            newMetadata.setLanguage(oldMetadata.getLanguage());
            newMetadata.setType(oldMetadata.getType());
            newMetadata.setPubKey(oldMetadata.getPubKey());
            newMetadata.setData(oldMetadata.getData());
            newMetadata.setAbstractContent(oldMetadata.getAbstractContent());

            newMetadata.setAbstractContent(oldMetadata.getAbstractContent());
            TreeMap<String, String> extra = oldMetadata.getExtra();
            if (extra == null) {
                extra = new TreeMap<>();
            }
            // transfer ownership to the recipient
            extra.put("owner", recipientPubKey);
            newMetadata.setExtra(extra);


            //sign
            newMetadata = DTCPProcessor.FullMetadata(private_key, newMetadata);

            MetadataSaveResp saveResp = NodeProcessor.SaveMetadata(URL, newMetadata);
            assert (saveResp != null) : "response is empty";
            assert (Constants.NODE_SUCCESS.equalsIgnoreCase(saveResp.getCode())) : saveResp.getMsg();
            System.out.println("success。" + saveResp.getData().getDna());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // *****************  demo completed! *****************
}
