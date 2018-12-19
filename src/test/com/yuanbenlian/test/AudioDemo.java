package com.yuanbenlian.test;

import com.sun.javafx.tools.packager.bundlers.IOUtils;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.entity.Audio;
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

            TestUtil.fillBlockHash(metadata, URL);

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

            //Because the remaining audio information are required the other jar packets,
            // the analog data is used here.
            Audio data = new Audio();
            data.setExt(audioPath.substring(audioPath.lastIndexOf(".") + 1));
            data.setSize("" + new File(audioPath).length());
            data.setDuration("" + 10 * 60);//10 min
            data.setSimpleRate("44000");  // 44 khz
            data.setBitRate("180000");  //180 kb
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
        String dna = "GAD27EU1E8VFK458NQ54RR76QJHK5VZ5OGDKJGYMTWUOWM85E";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();
            System.out.println("QueryAudioTx success:\n" + resp.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
    // *****************  demo completed! *****************
}
