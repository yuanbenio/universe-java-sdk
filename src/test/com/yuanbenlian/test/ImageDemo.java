package com.yuanbenlian.test;

import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
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
        String imgPath = "yuanbenlian.png";
        try {

            File f = new File(imgPath);
            BufferedImage bi = ImageIO.read(f);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, imgPath.substring(imgPath.lastIndexOf(".") + 1), baos);
            byte[] bytes = baos.toByteArray();
            String imageBase64 = new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();

            BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL);
            if (resp == null || !Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode())) {
                //use default value
                metadata.setBlockHash("FD6C96C7EE44BE1774843CF6A806A757C3AD7FA1");
                metadata.setBlockHeight("199130");
            } else {
                metadata.setBlockHash(resp.getData().getLatestBlockHash());
                metadata.setBlockHeight(resp.getData().getLatestBlockHeight().toString());
            }


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

            TreeMap<String, String> data = new TreeMap<>();
            data.put("ext", imgPath.substring(imgPath.lastIndexOf(".") + 1));
            data.put("thumb", "https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.put("original", "https://github.com/yuanbenio/universe-java-sdk/yuanbenlian.png");
            data.put("height", "" + bi.getHeight());
            data.put("width", "" + bi.getWidth());
            data.put("size", "" + imageBase64.length());
            metadata.setData(data);

            //Determination of ownership
            TreeMap<String, String> extra = new TreeMap<>();
            extra.put("owner", ECKeyProcessor.GetPubKeyFromPri(private_key));
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
        String dna = "1OD37XO69A298CYTTF3YPC9EW6SF2V1VU4AAEUZ0WT4LJ2N965";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();
            System.out.println("success:\n" + resp.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void TransferOwnerShip () {
        try {
            String sourceDNA = "54Q6XUSQNOZ2CSAE5NOKLS09VEKKYPTMLZV71IWOJDPKCTFPZR";
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, sourceDNA);

            assert resp != null : "response  is empty";
            assert Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode()) : resp.getMsg();

            //link source data
            Metadata metadata = resp.getData();
            metadata.setSignature(null);
            metadata.setParentDna(metadata.getDna());
            metadata.setDna(null);
            metadata.setTitle("[Transfer Ownership] "+metadata.getTitle());

            //transfer ownership
            TreeMap<String, String> extra = new TreeMap<>();
            extra.put("owner", ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey());
            metadata.setExtra(extra);

            //sign
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);

            MetadataSaveResp saveResp = NodeProcessor.SaveMetadata(URL, metadata);
            assert (saveResp != null) : "response is empty";
            assert (Constants.NODE_SUCCESS.equalsIgnoreCase(saveResp.getCode())) : saveResp.getMsg();
            System.out.println("success。" + saveResp.getData().getDna());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // *****************  demo completed! *****************
}
