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

package com.yuanben.common;

import java.math.BigInteger;
import java.util.HashMap;

public class Constants {
    public static final String STRING_EMPTY = "";

    public static final String Language_ZH = "zh-CN";

    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_PRIVATE = "private";
    public static final String TYPE_CUSTOM = "custom";

    public static final String VERSION = "v1.1";

    public static final String CODE_ERROR = "error";


    private static final BigInteger SECP256K1N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);


    /**
     * Introduced in the Homestead release
     */
    public static BigInteger getSECP256K1N() {
        return SECP256K1N;
    }


    public static final HashMap<String, String> HTML_SAFE_REPLACEMENT_CHARS =
            new HashMap<String, String>() {{
                put(">", "\\u003e");
                put("<", "\\u003c");
                put("&", "\\u0026");
            }};
}
