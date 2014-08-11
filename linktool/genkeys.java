// from http://www.cs.ru.nl/~martijno/
// Martijn Oostdijk. They appear to be samples from a course.

// This functionality is built into linktool. The first time it is
// deployed keys are put in the sakai configuration directory.
// This program was used during development. It should still work,
// and create keys equivalent to those created by the tool.

import java.io.*;
import java.math.BigInteger;

import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;

/**
 * Generate an RSA public/private keypair.
 *
 * @version $Revision: 1.1 $
 */
public class genkeys
{
    /**
     * Generates an RSA public/private key pair.
     */
    public genkeys() {
	try {
	    /* Generate keypair. */
	    System.out.println("Generating keys...");
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(1024);
	    KeyPair keypair = generator.generateKeyPair();
	    RSAPublicKey publickey = (RSAPublicKey)keypair.getPublic();
	    RSAPrivateKey privatekey = (RSAPrivateKey)keypair.getPrivate();

	    /* Write public key to file. */
	    writeKey(publickey, "publickey");

	    /* Write private key to file. */
	    writeKey(privatekey, "privatekey");

	    System.out.println("modulus = " + publickey.getModulus());
	    System.out.println("pubexpint = " + publickey.getPublicExponent());
	    System.out.println("privexpint = " + privatekey.getPrivateExponent());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Writes <code>key</code> to file with name <code>filename</code> in
     * standard encoding (X.509 for RSA public key, PKCS#8 for RSA private key).
     *
     * @param key the key to write.
     * @param filename the name of the file.
     *
     * @throws IOException if something goes wrong.
     */
    public static void writeKey(Key key, String filename) throws IOException {
	FileOutputStream file = new FileOutputStream(filename);
	file.write(key.getEncoded());
	file.close();
    }

    /**
     * The main method just calls the constructor.
     *
     * @param arg The command line arguments.
     */
    public static void main(String[] arg) {
	new genkeys();
    }
}

