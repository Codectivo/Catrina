/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AES;
import Rijndael.Rijndael_Algorithm;
import java.io.*;

public class GenEncription {
    
	private static GenEncription INSTANCE  = null;
	//private static final IntranetLogger log = (IntranetLogger) IntranetLogger.getLogger(GenEncription.class);
	private synchronized static void createInstace(){
        if(INSTANCE == null) INSTANCE = new GenEncription();
    }
	
	public static GenEncription getInstance(){
        if(INSTANCE == null) createInstace();
        return INSTANCE;
    }
	
    public GenEncription()
    {
        setKeysize(0);
        BLOCK_SIZE = 0;
        setAlgorithm(null);
        try
        {
            setKeysize(16);
            BLOCK_SIZE = 16;
            setAlgorithm(new Rijndael_Algorithm());
        }
        catch(Exception exception)
        {
        	exception.printStackTrace();
            //log.debug(exception);
        }
    }

    public String encriptaByFile(String file, String s1)
        throws EncriptionException
    {
    	byte abyte0[] = new byte[32];
        try
        {
            FileInputStream fileinputstream = new FileInputStream(file);
            BufferedInputStream bufferedinputstream;
            for(bufferedinputstream = new BufferedInputStream(fileinputstream); bufferedinputstream.available() != 0; bufferedinputstream.read(abyte0));
            bufferedinputstream.close();                        
            return encripta(new String(abyte0),s1);
        }
        catch(Exception exception)
        {
            //log.error("Caught exception " + exception.toString());
            throw new EncriptionException("Encripta: " + exception);
        }
    }
    
    public String encripta(String key, String entrada)
    throws EncriptionException
{
    
    String s4 = "";
    int i = 0;
           
    try
    {
        byte abyte3[] = getHexKey("0x" + key.trim());
        Object obj2 = Rijndael_Algorithm.makeKey(abyte3);
        int j = entrada.length() / 16 + 1;
        int k = entrada.length();
        for(int l = k; l < 16 * j; l++)
        	entrada = entrada + " ";

        j = entrada.length() / 16;
        for(int i1 = 0; i1 < j; i1++)
        {
            String s3 = entrada.substring(i, i + 16);
            byte abyte1[] = s3.getBytes();            
            byte abyte2[] = Rijndael_Algorithm.blockEncrypt(abyte1, 0, obj2, BLOCK_SIZE);
            s4 = s4 + toString(abyte2);
            i += 16;
        }

        return s4;
    }
    catch(Exception exception)
    {
    	//log.error("Caught exception " + exception.toString());
        throw new EncriptionException("Encripta: " + exception);
    }
}

    public String desencriptaByFile(String s, String s1)
        throws EncriptionException
    {
       
        byte abyte0[] = new byte[32];
        try
        {
            FileInputStream fileinputstream = new FileInputStream(s);
            BufferedInputStream bufferedinputstream;
            for(bufferedinputstream = new BufferedInputStream(fileinputstream); bufferedinputstream.available() != 0; bufferedinputstream.read(abyte0));
            bufferedinputstream.close();            
            return desencripta(new String(abyte0),s1);
        }
        catch(Exception exception)
        {
        	//log.error("Caught exception " + exception.toString());
            throw new EncriptionException("Desencripta: " + exception);
        }
    }
    
    public String desencripta(String s, String s1)
    throws EncriptionException
{    
    String s4 = "";
    int i = 0;
       
    try
    {        
        byte abyte3[] = getHexKey("0x" + s.trim());        
        Object obj3 = Rijndael_Algorithm.makeKey(abyte3);
        int j = s1.length() / 32;
        for(int k = 0; k < j; k++)
        {
            String s3 = s1.substring(i, i + 32);
            byte abyte1[] = getHexKey("0x" + s3);            
            byte abyte2[] = Rijndael_Algorithm.blockDecrypt(abyte1, 0, obj3, BLOCK_SIZE);
            String s5 = new String(abyte2);
            s4 = s4 + s5;
            i += 32;
        }

        return s4;
    }
    catch(Exception exception)
    {
    	//log.error("Caught exception " + exception.toString());
        throw new EncriptionException("Desencripta: " + exception);
    }
}

    private static String toString(byte abyte0[])
    {
        int i = abyte0.length;
        char ac[] = new char[i * 2];
        int j = 0;
        int k = 0;
        while(j < i) 
        {
            byte byte0 = abyte0[j++];
            ac[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            ac[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(ac);
    }

    private static byte[] getHexKey(String s)
    {
        byte abyte0[] = new byte[48];               
        int j = 0;
        int k = s.length();        
        try
        {            
            if(s.charAt(0) == '0' && s.charAt(1) == 'x')
                for(j = 2; j < k; j += 2)
                {
                    String s1 = "" + s.charAt(j) + s.charAt(j + 1);
                    int i = Integer.parseInt(s1, 16);
                    abyte0[j - 2 >>> 1] = (byte)i;
                }

        }
        catch(Exception exception)
        {
          //  log.debug("Problems parsing hex key...");
        }
        j = j - 2 >>> 1;
        if(j == 16 || j == 24 || j == 32)
        {
            int l = j;
            byte abyte2[] = new byte[l];
            System.arraycopy(abyte0, 0, abyte2, 0, l);
            return abyte2;
        } else
        {
            return null;
        }
    }

    private int keysize;
    private int BLOCK_SIZE;
    private Rijndael_Algorithm algorithm;
    private static final char HEX_DIGITS[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };
    
    /*public static void main(String args[]){
	          GenEncription gen = new GenEncription();
	        try {
	            //log.debug(gen.encripta("81824548","cb/nsccr"));
	            //System.out.println(gen.encripta("01020304050607080900111213141516","texto clRO"));
	            //log.debug(gen.desencriptaByFile("c:\\enckey","AD45397A012B0D63258C9036390C0128"));
                            String encripta = gen.encripta("01020304050607080900111213141516","hola mundo");
                            String desencripta = gen.desencripta("01020304050607080900111213141516", encripta);
                            System.out.println("Cadena encriptada: "+ encripta);
                            System.out.println("Cadena DESencriptada: "+ desencripta);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	
      }*/

	public void setKeysize(int keysize) {
		this.keysize = keysize;
	}

	public int getKeysize() {
		return keysize;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(Rijndael_Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the algorithm
	 */
	public Rijndael_Algorithm getAlgorithm() {
		return algorithm;
	}    
}
