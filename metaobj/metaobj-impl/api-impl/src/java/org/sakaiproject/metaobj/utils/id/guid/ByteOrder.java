/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/utils/id/guid/ByteOrder.java $
 * $Id: ByteOrder.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

/**
 * Various static routines for solving endian problems.
 */
public class ByteOrder {
   private static final int UNSIGNED_BYTE_MASK = 0x000000FF;

   /**
    * Returns the reverse of x.
    */
   public static byte[] reverse(byte[] x) {
      int n = x.length;
      byte[] ret = new byte[n];
      for (int i = 0; i < n; i++) {
         ret[i] = x[n - i - 1];
      }
      return ret;
   }

   /**
    * Little-endian bytes to short
    *
    * @requires x.length-offset>=2
    * @effects returns the value of x[offset..offset+2] as a short,
    * assuming x is interpreted as a signed little endian number (i.e.,
    * x[offset] is LSB).  If you want to interpret it as an unsigned number,
    * call ubytes2int on the result.
    */
   public static short leb2short(byte[] x, int offset) {
      int x0 = (x[offset]) & UNSIGNED_BYTE_MASK;
      int x1 = (x[offset + 1] << 8);
      return (short) (x1 | x0);
   }

   /**
    * Little-endian bytes to int
    *
    * @requires x.length-offset>=4
    * @effects returns the value of x[offset..offset+4] as an int,
    * assuming x is interpreted as a signed little endian number (i.e.,
    * x[offset] is LSB) If you want to interpret it as an unsigned number,
    * call ubytes2int on the result.
    */
   public static int leb2int(byte[] x, int offset) {
      //Must mask value after left-shifting, since case from byte
      //to int copies most significant bit to the left!
      int x0 = x[offset] & UNSIGNED_BYTE_MASK;
      int x1 = (x[offset + 1] << 8) & 0x0000FF00;
      int x2 = (x[offset + 2] << 16) & 0x00FF0000;
      int x3 = (x[offset + 3] << 24);
      return x3 | x2 | x1 | x0;
   }

   /**
    * Short to little-endian bytes: writes x to buf[offset...]
    */
   public static void short2leb(short x, byte[] buf, int offset) {
      buf[offset] = (byte) (x & (short) 0x00FF);
      buf[offset + 1] = (byte) ((short) (x >> 8) & (short) 0x00FF);
   }

   /**
    * Int to little-endian bytes: writes x to buf[offset..]
    */
   public static void int2leb(int x, byte[] buf, int offset) {
      buf[offset] = (byte) (x & UNSIGNED_BYTE_MASK);
      buf[offset + 1] = (byte) ((x >> 8) & UNSIGNED_BYTE_MASK);
      buf[offset + 2] = (byte) ((x >> 16) & UNSIGNED_BYTE_MASK);
      buf[offset + 3] = (byte) ((x >> 24) & UNSIGNED_BYTE_MASK);
   }

   /**
    * Interprets the value of x as an unsigned byte, and returns
    * it as integer.  For example, ubyte2int(0xFF)==255, not -1.
    */
   public static int ubyte2int(byte x) {
      return ((int) x) & UNSIGNED_BYTE_MASK;
   }

   /**
    * Interprets the value of x as am unsigned two-byte number
    */
   public static int ubytes2int(short x) {
      return ((int) x) & 0x0000FFFF;
   }


   /**
    * Interprets the value of x as an unsigned two-byte number
    */
   public static long ubytes2long(int x) {
      return ((long) x) & 0x00000000FFFFFFFFl;
   }

   /**
    * Returns the int value that is closest to l.  That is, if l can fit into a
    * 32-bit unsigned number, returns (int)l.  Otherwise, returns either
    * Integer.MAX_VALUE or Integer.MIN_VALUE as appropriate.
    */
   public static int long2int(long l) {
      if (l >= Integer.MAX_VALUE) {
         return Integer.MAX_VALUE;
      }
      else if (l <= Integer.MIN_VALUE) {
         return Integer.MIN_VALUE;
      }
      else {
         return (int) l;
      }
   }

   /** Unit test */
   /*
   public static void main(String args[]) {
       byte[] x1={(byte)0x2, (byte)0x1};  //{x1[0], x1[1]}
       short result1=leb2short(x1,0);
       Assert.that(result1==(short)258, "result1="+result1 );  //256+2;
       byte[] x1b=new byte[2];
       short2leb(result1, x1b, 0);
       for (int i=0; i<2; i++)
           Assert.that(x1b[i]==x1[i]);

       byte[] x2={(byte)0x2, (byte)0, (byte)0, (byte)0x1};
       //2^24+2 = 16777216+2 = 16777218
       int result2=leb2int(x2,0);
       Assert.that(result2==16777218, "result2="+result2);

       byte[] x2b=new byte[4];
       int2leb(result2, x2b, 0);
       for (int i=0; i<4; i++)
           Assert.that(x2b[i]==x2[i]);

       byte[] x3={(byte)0x00, (byte)0xF3, (byte)0, (byte)0xFF};
       int result3=leb2int(x3,0);
       Assert.that(result3==0xFF00F300, Integer.toHexString(result3));

       byte[] x4={(byte)0xFF, (byte)0xF3};
       short result4=leb2short(x4,0);
       Assert.that(result4==(short)0xF3FF, Integer.toHexString(result4));

       byte in=(byte)0xFF; //255 if unsigned, -1 if signed.
       int out=(int)in;
       Assert.that(out==-1, out+"");
       out=ubyte2int(in);
       Assert.that(out==255, out+"");
       out=ubyte2int((byte)1);
       Assert.that(out==1, out+"");

       short in2=(short)0xFFFF;
       Assert.that(in2<0,"L122");
       Assert.that(ubytes2int(in2)==0x0000FFFF, "L123");
       Assert.that(ubytes2int(in2)>0, "L124");

       int in3=(int)0xFFFFFFFF;
       Assert.that(in3<0, "L127");
       Assert.that(ubytes2long(in3)==0x00000000FFFFFFFFl, "L128");
       Assert.that(ubytes2long(in3)>0, "L129");

       byte[] buf={(byte)0xFF, (byte)0xFF};
       in2=leb2short(buf,0);
       Assert.that(in2==-1, "L133: "+Integer.toHexString(in2));
       int out2=ubytes2int(in2);
       Assert.that(out2==0x0000FFFF, "L134: "+out);

       byte[] buf2={(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
       in3=leb2int(buf2,0);
       Assert.that(in3==-1, "L139");
       long out4=ubytes2long(in3);
       Assert.that(out4==0x00000000FFFFFFFFl, "L141");

       Assert.that(long2int(5l)==5);
       Assert.that(long2int(-10l)==-10);
       Assert.that(long2int(0l)==0);
       Assert.that(long2int(0xABFFFFFFFFl)==0x7FFFFFFF);  //Integer.MAX_VALUE
       Assert.that(long2int(-0xABFFFFFFFFl)==0x80000000); //Integer.MIN_VALUE
   }
   */
}
