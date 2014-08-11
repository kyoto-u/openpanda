/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.hybrid.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copied from <a href=
 * "http://github.com/sakaiproject/nakamura/blob/master/libraries/utils/src/main/java/org/sakaiproject/nakamura/util/Signature.java"
 * >nakamura/libraries/utils/src/main/java/org/sakaiproject/nakamura/util/
 * Signature.java@70078352b144921ee03a4f2e1d17a3a9b9a2b231</a>
 * <p>
 * Utility to calculate signatures for information.
 */
@SuppressWarnings("PMD.LongVariable")
public class Signature {
  private static final Log LOG = LogFactory.getLog(Signature.class);
  protected transient String hmacSha1Algorithm = "HmacSHA1";
	private transient Mac mac;

	/**
	 * @throws IllegalStateException
	 *             If there are any run time problems getting an instance.
	 */
	public Signature() {
		instatiateMac();
	}

	private void instatiateMac() {
		try {
			// Get an hmac_sha1 Mac instance
			mac = Mac.getInstance(hmacSha1Algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Intended to be used for unit testing only.
	 * 
	 * @param algorithm
	 */
	protected Signature(final String algorithm) {
		this.hmacSha1Algorithm = algorithm;
		instatiateMac();
	}

	/**
	 * Calculate an RFC2104 compliant HMAC (Hash-based Message Authentication
	 * Code)
	 * 
	 * @param data
	 *            The data to be signed. This data is pushed through a hex
	 *            converter in this method, so there is no need to do this
	 *            before generating the HMAC.
	 * @param key
	 *            The signing key.
	 * @return The Base64-encoded RFC 2104-compliant HMAC signature. By default
	 *         it is not URL safe.
	 * @throws InvalidKeyException
	 *             This is the exception for invalid Keys (invalid encoding,
	 *             wrong length, uninitialized, etc).
	 * @see #calculateRFC2104HMACWithEncoding(String, String, boolean)
	 */
	public String calculateRFC2104HMAC(final String data, final String key)
			throws InvalidKeyException {
		return calculateRFC2104HMACWithEncoding(data, key, false);
	}

	/**
	 * Calculate an RFC2104 compliant HMAC (Hash-based Message Authentication
	 * Code)
	 * 
	 * @param data
	 *            The data to be signed. This data is pushed through a hex
	 *            converter in this method, so there is no need to do this
	 *            before generating the HMAC.
	 * @param key
	 *            The signing key.
	 * @param urlSafe
	 *            true if the token needs to be URL safe.
	 * @return The Base64-encoded RFC 2104-compliant HMAC signature.
	 * @throws InvalidKeyException
	 *             This is the exception for invalid Keys (invalid encoding,
	 *             wrong length, uninitialized, etc).
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public String calculateRFC2104HMACWithEncoding(final String data,
			final String key, final boolean urlSafe) throws InvalidKeyException {
		if (data == null) {
			throw new IllegalArgumentException("String data == null");
		}
		if (key == null) {
			throw new IllegalArgumentException("String key == null");
		}
		String result = null;
    try {
      // Get an hmac_sha1 key from the raw key bytes
      final byte[] keyBytes = key.getBytes("UTF-8");
      final SecretKeySpec signingKey = new SecretKeySpec(keyBytes,
      		hmacSha1Algorithm);

      // initialize with the signing key
      mac.init(signingKey);

      // Compute the hmac on input data bytes
      final byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

      // Convert raw bytes to encoding
      final byte[] base64Bytes = Base64.encodeBase64(rawHmac, false, urlSafe);
      result = new String(base64Bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage(), e);
    }
		return result;
	}
}