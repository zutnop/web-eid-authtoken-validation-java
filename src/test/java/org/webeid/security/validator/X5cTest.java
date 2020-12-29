/*
 * Copyright (c) 2020 The Web eID Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.webeid.security.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.webeid.security.exceptions.TokenParseException;
import org.webeid.security.exceptions.UserCertificateMissingPurposeException;
import org.webeid.security.exceptions.UserCertificateWrongPurposeException;
import org.webeid.security.testutil.AbstractTestWithValidatorAndCorrectNonce;
import org.webeid.security.testutil.Dates;
import org.webeid.security.testutil.Tokens;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class X5cTest extends AbstractTestWithValidatorAndCorrectNonce {

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        try {
            // Ensure that certificate is valid
            Dates.setMockedDate(Dates.create("2020-09-25"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testX5cMissing() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_MISSING))
            .isInstanceOf(TokenParseException.class)
            .hasMessageStartingWith("x5c field must be present");
    }

    @Test
    public void testX5cNotArray() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_NOT_ARRAY))
            .isInstanceOf(TokenParseException.class)
            .hasMessageStartingWith("x5c field in authentication token header must be an array");
    }

    @Test
    public void testX5cEmpty() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_EMPTY))
            .isInstanceOf(TokenParseException.class)
            .hasMessageStartingWith("Certificate from x5c field must not be empty");
    }

    @Test
    public void testX5cNotString() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_NOT_STRING))
            .isInstanceOf(TokenParseException.class)
            .hasMessageStartingWith("x5c field in authentication token header must be an array of strings");
    }

    @Test
    public void testX5cInvalidCertificate() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_INVALID_CERTIFICATE))
            .isInstanceOf(TokenParseException.class)
            .hasMessageStartingWith("x5c field must contain a valid certificate");
    }

    @Test
    public void testX5cMissingPurposeCertificate() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_MISSING_PURPOSE_CERTIFICATE))
            .isInstanceOf(UserCertificateMissingPurposeException.class);
    }

    @Test
    public void testX5cWrongPurposeCertificate() {
        assertThatThrownBy(() -> validator.validate(Tokens.X5C_WRONG_PURPOSE_CERTIFICATE))
            .isInstanceOf(UserCertificateWrongPurposeException.class);
    }

}