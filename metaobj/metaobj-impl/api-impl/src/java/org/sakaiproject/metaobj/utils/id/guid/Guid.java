/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/id/guid/Guid.java $
 * $Id: Guid.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.metaobj.utils.id.guid;


public class Guid implements java.io.Serializable {


   private static final int HEX_RADIX = 16;
   /**
    * length of a GUID, in bytes (16)
    */
   private static final short GUID_LEN = HEX_RADIX;
   private byte guid[] = new byte[GUID_LEN];

   public static final byte AUTOGEN_BY_DB = 1;
   public static final byte NO_AUTOGEN_BY_DB = 2;

   /**
    * holds the string representation of this GUID, and is computed during construction,
    * and when setGuid() is called.  Used to optimize toString performance.
    */
   private String guidStr;

   /**
    * indicates whether to let the database auto-generate the value or not
    */
   private boolean dbAutoGen = true;

   /**
    * Description: Allocate a new Guid object
    */

   public Guid() {
      boolean bSecureGuid = true;
      RandomGUID tmpGuid = new RandomGUID(bSecureGuid);
      guid = fromHexString(tmpGuid.valueAfterMD5);
      setGuid(guid);
      tmpGuid = null;
   }

   /**
    * from effective java by joshua bloch
    * step 1: perform == test
    * step 2: instanceof test
    * step 3: cast parameter to type
    * step 4: check primitives with ==, objects with equals()
    *
    * @param o
    * @return
    */
   public boolean equals(Object o) {
      //1.
      if (this == o) {
         return true;
      }
      //2.
      if (!(o instanceof Guid)) {
         // if o is null, we return here
         return false;
      }
      //3.
      Guid guid = (Guid) o;
      //4.
      if (this.toString().equals(guid.toString())) {
         return true;
      }

      return false;
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   /**
    * Description: Allocates a new Guid object from the passed in byte array.
    *
    * @param inGuid 16 byte array for this GUID
    * @throws IllegalArgumentException if byte array is not GUID_LEN bytes
    */
   public Guid(byte[] inGuid) {
      setGuid(inGuid);
   }

   public Guid(String sGuid) {
      if ((sGuid != null) && (sGuid.length()) == 32) {
         guid = fromHexString(sGuid);
         guidStr = internalToString();
      }
      else {
         throw new IllegalArgumentException("sGuid is either null or the wrong length");
      }
   }

   public boolean isValidGuid() {
      return this.guid != null && this.guid.length == GUID_LEN;
   }

   /**
    * Sets GUID to passed in value
    *
    * @param inGuid 16-byte array containing raw GUID value
    * @throws IllegalArgumentException if byte array is not GUID_LEN bytes
    */
   public void setGuid(byte[] inGuid) throws IllegalArgumentException {

      if (inGuid.length == GUID_LEN) {
         for (int i = 0; i < GUID_LEN; i++) {
            guid[i] = inGuid[i];
         }
         guidStr = internalToString();
      }
      else {
         throw new IllegalArgumentException("GUID Passed in is not " + GUID_LEN +
               " bytes - it is " + inGuid.length + " bytes");
      }
   }

   /**
    * Get the raw bytes for this GUID
    *
    * @return the raw GUID in a byte array
    */
   public byte[] getGuid() {
      return (byte[]) guid.clone();
   }

   public String getString() {
      return guidStr;
   }

   /**
    * Return string representation of the GUID as a hex value
    * <p>Example: returns the string 0x8f6eff8344a8e03b125590af6d21e9b2
    * <p> This can be then passed to a database as a numerical value
    *
    * @see internalToString()
    */
   public String toString() {
      return guidStr;
   }

   /**
    * Converts byte array to hex string representation
    *
    * @return a new string
    */
   // convert byte array to hex string representation
   private String internalToString() {

      StringBuilder buf = new StringBuilder();
      String hexStr;
      int val;
      for (int i = 0; i < GUID_LEN; i++) {
         //Treating each byte as an unsigned value ensures
         //that we don't str doesn't equal things like 0xFFFF...
         val = ByteOrder.ubyte2int(guid[i]);
         hexStr = Integer.toHexString(val);
         while (hexStr.length() < 2) {
            hexStr = "0" + hexStr;
         }
         buf.append(hexStr);
      }
      return buf.toString().toUpperCase();

/*
    Integer tmpInt;
    String hexChar = "";
    String hexStr2 = "";

    for(int i=0; i<guid.length; i++) {
      tmpInt = new Integer( ((int) guid[i]) & 0x000000FF);
      hexChar = tmpInt.toHexString(tmpInt.intValue());
      tmpInt = null;
      // toHexString strips leading zeroes, so add back in if necessary
      if (hexChar.length() == 1) {
        hexChar = "0" + hexChar;
      }
      hexStr2 += hexChar;
    }
    hexStr2 = "0x" + hexStr2;
    return hexStr2.toUpperCase();
*/
   }


   /**
    * Create a GUID bytes from a hex string version.
    * Throws IllegalArgumentException if sguid is
    * not of the proper format.
    */
   public static byte[] fromHexString(String sguid)
         throws IllegalArgumentException {
      byte bytes[] = new byte[GUID_LEN];
      try {
         for (int i = 0; i < GUID_LEN; i++) {
            bytes[i] =
                  (byte) Integer.parseInt(sguid.substring(i * 2, (i * 2) + 2), HEX_RADIX);
         }
         return bytes;
      }
      catch (NumberFormatException e) {
         throw new IllegalArgumentException();
      }
      catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   public static boolean isValidGuidString(String sGuid) {
      return (sGuid != null && sGuid.length() == GUID_LEN * 2);
   }

   public boolean isDbAutoGen() {
      return dbAutoGen;
   }

   public boolean getDbAutoGen() {
      return isDbAutoGen();
   }

   public void setAutoGen(boolean dbAutoGen) {
      this.dbAutoGen = dbAutoGen;
   }

}