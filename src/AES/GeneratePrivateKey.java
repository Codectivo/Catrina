/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AES;
import java.math.BigInteger;
import java.util.Random;
/**
 *
 * @author misa
 */
public class GeneratePrivateKey{
    public BigInteger alpha, p, a, beta, beta_32_digitos, divisor;
    private Random rnd;
    
    public GeneratePrivateKey(){
        p = new BigInteger("525274121540768527094545773858758619837155656011840418759432305591535958921419791860260046000163112882330894486346743939260799249776004504189864684643819573297716630447050882389982838538287374643266587878675720319474308492766437030186926867856769972554044908829796000181858862568937035467384270278403604721802842587994134189593128712286905405253485465556329630904848487640710427473060615445543690638741");
        alpha = new BigInteger("75116781641540323205764318186954619115878174318577340992398423329441828584382998367279418445936207835175953033007592377182506720065645050993161033190143016417754421970154615926124702766846768603420983");
        a = new BigInteger("1000000000");
        rnd = new Random();
        int rnd_uno = rnd.nextInt(20);
        int rnd_dos = rnd.nextInt(30);
        int limit_num = rnd_uno * rnd_dos;
        for(int i=0; i<limit_num; i++){
            a = a.nextProbablePrime();
        }
        beta =  alpha.modPow(a, p);
        divisor = new BigInteger("10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    }
    
//    public static void main(String args[]){
//        GeneratePrivateKey pk = new GeneratePrivateKey();
//        System.out.println("Num p:" + pk.p+"\nNum a: "+pk.a + "\nNum alpha: " + pk.alpha + "\nNum beta: " + pk.beta + "\nPK: "+ pk.beta_32_digitos);
//    }
}
