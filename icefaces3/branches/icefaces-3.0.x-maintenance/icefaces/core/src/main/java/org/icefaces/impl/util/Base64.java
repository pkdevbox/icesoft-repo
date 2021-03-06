/*
 * Copyright 2004-2012 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.impl.util;

/**
 * This <code>Base64</code> is a utility class for encoding using the Base64
 * encoding. </p>
 * <p/>
 * The Base64 encoding is designed to represent arbitrary sequences of octets in
 * a form that need to be humanly readable. The encoding and decoding algorithms
 * are simple, but the encoded data are consitently only about 33% larger than
 * the unencoded data. </p>
 * <p/>
 * For more complete information about the Base64 encoding, please read <a
 * href="http://www.ietf.org/rfc/rfc1521.txt" target="_top">MIME (Multipurpose
 * Internet Mail Extensions) Part One</a> (Section 5.2). </p>
 */
public class Base64 {
    private static final byte[] BASE64_ALPHABET_ARRAY = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '+', '/'
    };
    private static final byte[] BASE64_FOR_URL_ALPHABET_ARRAY = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '-', '_'
    };
    private static final byte PAD = '=';

    /**
     * Encodes the specified <code>bytes</code> using the Base64 encoding. </p>
     *
     * @param bytes the bytes to be encoded.
     * @return the Base64-encoded bytes.
     * @see #encode(String)
     */
    public static byte[] encode(byte[] bytes) {
        return encode(bytes, BASE64_ALPHABET_ARRAY, true);
    }

    /**
     * Encodes the specified <code>string</code> using the Base64 encoding. </p>
     *
     * @param string the string to be encoded.
     * @return the Base64-encoded string.
     * @see #encode(byte[])
     */
    public static String encode(String string) {
        if (string == null) {
            return null;
        }
        return new String(encode(string.getBytes()));
    }

    /**
     * Encodes the specified <code>bytes</code> using the Base64 encoding for
     * URL usage. </p>
     *
     * @param bytes the bytes to be encoded.
     * @return the Base64-encoded bytes for URL usage.
     */
    public static byte[] encodeForURL(byte[] bytes) {
        return encode(bytes, BASE64_FOR_URL_ALPHABET_ARRAY, false);
    }

    private static byte[] encode(
            byte[] bytes, byte[] alphabetArray, boolean usePadding) {

        if (bytes == null) {
            return null;
        } else if (bytes.length == 0) {
            return bytes;
        }
        int _length = bytes.length;
        int _remainder = _length % 3;
        byte[] _bytes;
        if (usePadding) {
            _bytes = new byte[(_length + 2) / 3 * 4];
        } else {
            _bytes =
                    new byte[
                            ((_length + 2) / 3 * 4) -
                                    (_remainder != 0 ? 3 - _remainder : 0)];
        }
        _length -= _remainder;
        int _group;
        int _i;
        int _index = 0;
        for (_i = 0; _i < _length;) {
            _group =
                    (bytes[_i++] & 0xFF) << 16 |
                            (bytes[_i++] & 0xFF) << 8 |
                            bytes[_i++] & 0xFF;
            _bytes[_index++] = alphabetArray[_group >>> 18];
            _bytes[_index++] = alphabetArray[_group >>> 12 & 0x3F];
            _bytes[_index++] = alphabetArray[_group >>> 6 & 0x3F];
            _bytes[_index++] = alphabetArray[_group & 0x3F];
        }
        switch (_remainder) {
            case 0:
                break;
            case 1:
                _group = (bytes[_i] & 0xFF) << 4;
                _bytes[_index++] = alphabetArray[_group >>> 6];
                if (usePadding) {
                    _bytes[_index++] = alphabetArray[_group & 0x3F];
                    _bytes[_index++] = PAD;
                    _bytes[_index] = PAD;
                } else {
                    _bytes[_index] = alphabetArray[_group & 0x3F];
                }
                break;
            case 2:
                _group = ((bytes[_i++] & 0xFF) << 8 | (bytes[_i] & 0xFF)) << 2;
                _bytes[_index++] = alphabetArray[_group >>> 12];
                _bytes[_index++] = alphabetArray[_group >>> 6 & 0x3F];
                if (usePadding) {
                    _bytes[_index++] = alphabetArray[_group & 0x3F];
                    _bytes[_index] = PAD;
                } else {
                    _bytes[_index] = alphabetArray[_group & 0x3F];
                }
                break;
            default:
                // this should never happen.
        }
        return _bytes;
    }
}
