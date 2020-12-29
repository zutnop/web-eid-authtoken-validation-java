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

import org.webeid.security.validator.validators.OriginValidator;

import javax.cache.Cache;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.webeid.security.nonce.NonceGeneratorBuilder.requirePositiveDuration;

/**
 * Stores configuration parameters for {@link AuthTokenValidatorImpl}.
 */
final class AuthTokenValidationConfiguration implements Cloneable {

    private URI siteOrigin;
    private Cache<String, LocalDateTime> nonceCache;
    private Collection<X509Certificate> trustedCACertificates = new ArrayList<>();
    private boolean isUserCertificateRevocationCheckWithOcspEnabled = true;
    private Duration ocspRequestTimeout = Duration.ofSeconds(5);
    private Duration allowedClientClockSkew = Duration.ofMinutes(3);
    private boolean isSiteCertificateFingerprintValidationEnabled = false;
    private String siteCertificateSha256Fingerprint;

    void setSiteOrigin(URI siteOrigin) {
        this.siteOrigin = siteOrigin;
    }

    URI getSiteOrigin() {
        return siteOrigin;
    }

    void setNonceCache(Cache<String, LocalDateTime> nonceCache) {
        this.nonceCache = nonceCache;
    }

    Cache<String, LocalDateTime> getNonceCache() {
        return nonceCache;
    }

    Collection<X509Certificate> getTrustedCACertificates() {
        return trustedCACertificates;
    }

    boolean isUserCertificateRevocationCheckWithOcspEnabled() {
        return isUserCertificateRevocationCheckWithOcspEnabled;
    }

    void setUserCertificateRevocationCheckWithOcspDisabled() {
        isUserCertificateRevocationCheckWithOcspEnabled = false;
    }

    public Duration getOcspRequestTimeout() {
        return ocspRequestTimeout;
    }

    void setOcspRequestTimeout(Duration ocspRequestTimeout) {
        this.ocspRequestTimeout = ocspRequestTimeout;
    }

    void setAllowedClientClockSkew(Duration allowedClientClockSkew) {
        this.allowedClientClockSkew = allowedClientClockSkew;
    }

    Duration getAllowedClientClockSkew() {
        return allowedClientClockSkew;
    }

    boolean isSiteCertificateFingerprintValidationEnabled() {
        return isSiteCertificateFingerprintValidationEnabled;
    }

    public void setSiteCertificateSha256Fingerprint(String siteCertificateSha256Fingerprint) {
        isSiteCertificateFingerprintValidationEnabled = true;
        this.siteCertificateSha256Fingerprint = siteCertificateSha256Fingerprint;
    }

    public String getSiteCertificateSha256Fingerprint() {
        return siteCertificateSha256Fingerprint;
    }

    /**
     * Checks that the configuration parameters are valid.
     *
     * @throws NullPointerException     when required parameters are null
     * @throws IllegalArgumentException when any parameter is invalid
     */
    void validate() {
        Objects.requireNonNull(siteOrigin, "Origin URI must not be null");
        OriginValidator.validateIsOriginURL(siteOrigin);
        Objects.requireNonNull(nonceCache, "Nonce cache must not be null");
        if (trustedCACertificates.isEmpty()) {
            throw new IllegalArgumentException("At least one trusted certificate authority must be provided");
        }
        requirePositiveDuration(ocspRequestTimeout, "OCSP request timeout");
        requirePositiveDuration(allowedClientClockSkew, "Allowed client clock skew");
        if (isSiteCertificateFingerprintValidationEnabled) {
            Objects.requireNonNull(siteCertificateSha256Fingerprint, "Certificate fingerprint must not be null "
                + "when site certificate fingerprint validation is enabled");
        }
    }

    @Override
    protected AuthTokenValidationConfiguration clone() {
        try {
            final AuthTokenValidationConfiguration clone = (AuthTokenValidationConfiguration) super.clone();
            clone.trustedCACertificates = new ArrayList<>(trustedCACertificates);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }
}