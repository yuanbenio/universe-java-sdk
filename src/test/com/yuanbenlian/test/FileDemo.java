package com.yuanbenlian.test;

import com.sun.javafx.tools.packager.bundlers.IOUtils;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.http.MetadataQueryResp;
import com.yuanbenlian.model.http.MetadataSaveResp;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import java.io.File;
import java.util.TreeMap;

public class FileDemo {

    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";


    @Test
    public void saveFile() {
        Metadata metadata = new Metadata();
        String filePath = "test.txt";
        try {
            File file = new File(filePath);
            byte[] readFully = IOUtils.readFully(file);

            TestUtil.fillBlockHash(metadata, URL);

            metadata.setContentHash(DTCPProcessor.GenContentHash(new String(readFully)));
            metadata.setCategory("test,file,flower");

            metadata.setTitle("YuanBen chain test");
            //no license from outside
            metadata.setLicense(Metadata.License.NoneLicense);

            com.yuanbenlian.model.entity.File data = new com.yuanbenlian.model.entity.File();
            data.setExt(filePath.substring(filePath.lastIndexOf(".") + 1));
            data.setSize("" + file.length()); //bytes
            metadata.setData(data.toMap());

            metadata.setType(data.getType());

            TreeMap<String, String> extra = new TreeMap<>();
            extra.put("author", "Seven Seals Technology");
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
    public void QueryFileTx() {
        String dna = "197AFHUD3ZZH5YVVQISMUZNNU9IGIR9I7NDKTOR41EDYA56EZL";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();
            System.out.println("QueryFileTx success:\n" + resp.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
}
