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

package com.yuanben.util;

import java.math.BigInteger;
import java.util.Arrays;

public class Base36 {
    private static byte[] base36 = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static byte[] EncodeByteAsBytes(byte[] bytes) {
        BigInteger x = new BigInteger(bytes);
        byte[] answer = new byte[bytes.length*136/100];
        BigInteger bigRadix = BigInteger.valueOf(36);

        int i = 0;
        while (x.compareTo(BigInteger.ZERO) > 0 ) {
            BigInteger[] remainder = x.divideAndRemainder(bigRadix);
            x  = remainder[0];
            BigInteger mod = remainder[1];
            if (i == answer.length) {
                answer = Arrays.copyOf(answer,i+1);
            }
            answer[i++] = base36[mod.intValue()];
        }

        for (byte b:bytes){
            if (0 != b) {
                break;
            }
            if (i == answer.length) {
                answer = Arrays.copyOf(answer,i+1);
            }
            answer[i++] = base36[0];
        }
        //reverse
        int alen = answer.length;
        for (int j=0;j<alen/2;j++) {
            byte swap = answer[alen-1-j];
            answer[alen-1-j] = answer[j];
            answer[j] = swap;
        }
        return answer;
    }
    public static String EncodeBytes(byte[] bytes) {
        return new String(EncodeByteAsBytes(bytes));
    }
}
