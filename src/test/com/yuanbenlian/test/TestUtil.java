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
