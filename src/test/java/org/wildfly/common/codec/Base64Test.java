/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package org.wildfly.common.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.wildfly.common.bytes.ByteStringBuilder;
import org.wildfly.common.iteration.ByteIterator;
import org.wildfly.common.iteration.CodePointIterator;

/**
 * Tests of encoding/decoding Base64 B (standard alphabet)
 * implemented in org.wildfly.security.util.Base64
 *
 * @author <a href="mailto:jkalina@redhat.com">Jan Kalina</a>
 */
@SuppressWarnings("SpellCheckingInspection")
public class Base64Test {
    public Base64Test() {
    }

    /*
     * Standard Base64 alphabet encoding
     * (Expected values by http://www.freeformatter.com/base64-encoder.html)
     */

    @Test
    public void testEncodeBlank() {
        assertEquals("", ByteIterator.EMPTY.base64Encode().drainToString());
    }

    @Test
    public void testEncodeWithoutPadding() {
        assertEquals("YWJj", CodePointIterator.ofString("abc").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeWith1Padding() {
        assertEquals("YWI=", CodePointIterator.ofString("ab").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeWith2Padding() {
        assertEquals("YWJjZA==", CodePointIterator.ofString("abcd").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeWithTurnedOffPadding() {
        assertEquals("YWJjZA", CodePointIterator.ofString("abcd").asLatin1().base64Encode(Base64Alphabet.STANDARD, false).drainToString());
    }

    @Test
    public void testEncodeBinary() {
        assertEquals("AAEjRWeJq83v", ByteIterator.ofBytes((byte)0x00,(byte)0x01,(byte)0x23,(byte)0x45,(byte)0x67,(byte)0x89,(byte)0xAB,(byte)0xCD,(byte)0xEF).base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc1() {
        assertEquals("Zg==", CodePointIterator.ofString("f").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc2() {
        assertEquals("Zm8=", CodePointIterator.ofString("fo").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc3() {
        assertEquals("Zm9v", CodePointIterator.ofString("foo").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc4() {
        assertEquals("Zm9vYg==", CodePointIterator.ofString("foob").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc5() {
        assertEquals("Zm9vYmE=", CodePointIterator.ofString("fooba").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeRfc6() {
        assertEquals("Zm9vYmFy", CodePointIterator.ofString("foobar").asLatin1().base64Encode().drainToString());
    }

    @Test
    public void testEncodeAgainstPrecomputedValue() throws Exception {
        final byte[] input = "Testing input of base64 function".getBytes("UTF-8");
        final String output = CodePointIterator.ofString("Testing input of base64 function").asLatin1().base64Encode().drainToString();

        Assert.assertEquals("VGVzdGluZyBpbnB1dCBvZiBiYXNlNjQgZnVuY3Rpb24=", output);
        Assert.assertArrayEquals(input, CodePointIterator.ofString(output).base64Decode().drain());
    }

    @Test
    public void testEncodeByteStartingWithOne() {
        ByteStringBuilder bsb = new ByteStringBuilder();
        bsb.append((byte)0x00);
        bsb.append((byte)0xB8);
        assertEquals("ALg=", bsb.iterate().base64Encode().drainToString());
    }

    @Test
    public void testEncodeMoreBinaryBytes() {
        ByteStringBuilder bsb = new ByteStringBuilder();
        bsb.append((byte)0xD0);
        bsb.append((byte)0xB8);
        bsb.append((byte)0xE4);
        bsb.append((byte)0xBD);
        bsb.append((byte)0xA0);
        bsb.append((byte)0xF0);
        bsb.append((byte)0x9F);
        bsb.append((byte)0x82);
        bsb.append((byte)0xA1);
        bsb.append((byte)0x31);
        bsb.append((byte)0xE2);
        bsb.append((byte)0x81);
        bsb.append((byte)0x84);
        bsb.append((byte)0x32);
        bsb.append((byte)0x20);
        bsb.append((byte)0xCC);
        bsb.append((byte)0x81);
        assertEquals("0LjkvaDwn4KhMeKBhDIgzIE=", bsb.iterate().base64Encode().drainToString());
    }


    /*
     * Standard Base64 alphabet decoding
     * (Expected values by http://www.freeformatter.com/base64-encoder.html)
     */

    @Test
    public void testDecodeBlank() throws Exception {
        Assert.assertArrayEquals(new byte[]{}, CodePointIterator.EMPTY.base64Decode(Base64Alphabet.STANDARD, false).drain());
    }

    @Test
    public void testDecodeWithoutPadding() throws Exception {
        assertEquals("abc", CodePointIterator.ofString("YWJj").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeWith1Padding() throws Exception {
        assertEquals("ab", CodePointIterator.ofString("YWI=").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeWith2Padding() throws Exception {
        assertEquals("abcd", CodePointIterator.ofString("YWJjZA==").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeBinary() throws Exception {
        byte[] out = CodePointIterator.ofString("AAEjRWeJq83v").base64Decode(Base64Alphabet.STANDARD, false).drain();
        Assert.assertArrayEquals(new byte[]{(byte)0x00,(byte)0x01,(byte)0x23,(byte)0x45,(byte)0x67,(byte)0x89,(byte)0xAB,(byte)0xCD,(byte)0xEF}, out);
    }

    @Test
    public void testDecodeRfc1() throws Exception {
        assertEquals("f", CodePointIterator.ofString("Zg==").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeRfc2() throws Exception {
        assertEquals("fo", CodePointIterator.ofString("Zm8=").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeRfc3() throws Exception {
        assertEquals("foo", CodePointIterator.ofString("Zm9v").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeRfc4() throws Exception {
        assertEquals("foob", CodePointIterator.ofString("Zm9vYg==").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeRfc5() throws Exception {
        assertEquals("fooba", CodePointIterator.ofString("Zm9vYmE=").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }

    @Test
    public void testDecodeRfc6() throws Exception {
        assertEquals("foobar", CodePointIterator.ofString("Zm9vYmFy").base64Decode(Base64Alphabet.STANDARD, false).asUtf8String().drainToString());
    }


    @Test
    public void testUtf8Transcode() {
        final String str = "abc123xyz987?????????????????????";
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(bytes, CodePointIterator.ofString(str).asUtf8().drain());
        assertEquals(str, ByteIterator.ofBytes(bytes).asUtf8String().drainToString());
        assertEquals(str, CodePointIterator.ofString(str).asUtf8().asUtf8String().drainToString());
    }

    /*
     * Decoding of invalid input
     */

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidInputDecodePadding1() throws Exception {
        CodePointIterator.ofString("=").base64Decode(Base64Alphabet.STANDARD, false).drain();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidInputDecodePadding2() throws Exception {
        CodePointIterator.ofString("==").base64Decode(Base64Alphabet.STANDARD, false).drain();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidInputDecodePadding3() throws Exception {
        CodePointIterator.ofString("===").base64Decode(Base64Alphabet.STANDARD, false).drain();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidInputDecodeNonAlphabeticChar() throws Exception {
        CodePointIterator.ofString("????????????????????????").base64Decode(Base64Alphabet.STANDARD, false).drain();
    }

    public void testInvalidInputDecodeTooMuchPadding() throws Exception {
        final CodePointIterator r = CodePointIterator.ofString("YWI==");
        r.base64Decode(Base64Alphabet.STANDARD, false).drain();
        assertTrue(r.hasNext());
        assertEquals('=', r.next());
        assertFalse(r.hasNext());
    }


    /*
     * General Base64 tests
     */

    /**
     * Tests if encoding/decoding works properly.
     * (data length) % 3 == 0
     */
    @Test
    public void testEncodeDecodeToByteStringBuilderMod0() throws Exception {
        doEncodeDecodeTest(generateSequence(255));
    }

    /**
     * Tests if encoding/decoding works properly.
     * (data length) % 3 == 1
     */
    @Test
    public void testEncodeDecodeToByteStringBuilderMod1() throws Exception {
        doEncodeDecodeTest(generateSequence(256));
    }

    /**
     * Tests if encoding/decoding works properly.
     * (data length) % 3 == 2
     */
    @Test
    public void testEncodeDecodeToByteStringBuilderMod2() throws Exception {
        doEncodeDecodeTest(generateSequence(257));
    }

    private void doEncodeDecodeTest(byte[] inputData) throws Exception {
        byte[] outputData = ByteIterator.ofBytes(inputData).base64Encode().base64Decode().drain();
        assertArrayEquals("Encode-Decode test failed, results are not the same.", inputData, outputData);
    }

    private byte[] generateSequence(final int len) {
        byte[] data = new byte[len];
        for (int i = 0; i < len ; i++) {
            data[i] = (byte)i;
        }
        return data;
    }

}
