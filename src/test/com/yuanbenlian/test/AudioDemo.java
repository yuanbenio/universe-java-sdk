package com.yuanbenlian.test;

import com.sun.javafx.tools.packager.bundlers.IOUtils;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.entity.Audio;
import com.yuanbenlian.model.http.BlockHashQueryResp;
import com.yuanbenlian.model.http.MetadataQueryResp;
import com.yuanbenlian.model.http.MetadataSaveResp;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import java.io.File;
import java.util.TreeMap;

public class AudioDemo {

    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";


    // *****************  save Audio demo *****************
    @Test
    public void SaveAudioTest() {
        Metadata metadata = new Metadata();
        String audioPath = "test.mp3";
        try {
            byte[] readFully = IOUtils.readFully(new File(audioPath));

            BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL);
            if (resp == null || !Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode())) {
                //use default value
                metadata.setBlockHash("FD6C96C7EE44BE1774843CF6A806A757C3AD7FA1");
                metadata.setBlockHeight("199130");
            } else {
                metadata.setBlockHash(resp.getData().getLatestBlockHash());
                metadata.setBlockHeight(resp.getData().getLatestBlockHeight().toString());
            }


            metadata.setContentHash(DTCPProcessor.GenContentHash(new String(readFully)));
            metadata.setType(Constants.TYPE_AUDIO);
            metadata.setCategory("test,YuanBen chain");

            metadata.setTitle("YuanBen chain test");
            Metadata.License license = new Metadata.License();
            license.setType("test-license");
            TreeMap<String, String> params = new TreeMap<>();
            params.put("sale", "no");
            license.setParameters(params);
            metadata.setLicense(license);

            Audio data = new Audio();
            data.setExt(audioPath.substring(audioPath.lastIndexOf(".") + 1));
            data.setSize("" + new File(audioPath).length());
            data.setDuration(""+10*60);//10 min
            data.setThumb("https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            metadata.setData(data.toMap());

            TreeMap<String, String> extra = new TreeMap<>();
            extra.put("author", "Seven Seals Technology");
            metadata.setExtra(extra);

            metadata = DTCPProcessor.FullMetadata(private_key, metadata);

            MetadataSaveResp saveResp = NodeProcessor.SaveMetadata(URL, metadata);
            if (saveResp == null) {
                System.out.println("received empty result");
            } else if (!Constants.NODE_SUCCESS.equals(saveResp.getCode())) {
                System.out.println("save fail:" + saveResp.getMsg());
            } else {
                System.out.println("success。" + saveResp.getData().getDna());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void QueryAudioTx() {
        String dna = "3KJ9NVUE9I79Y3JX65642JO16Z02IZ3VTE4M9GGPILG2F3M56K";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            if (resp != null && Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode())) {
                System.out.println("success:\n" + resp.toJson());
            } else {
                if (resp == null) {
                    System.out.println("failure:result is empty");
                } else {
                    System.out.println("failure:" + resp.getMsg());
                }
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
    // *****************  demo completed! *****************
}
