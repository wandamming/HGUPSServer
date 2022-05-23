package com.hgups.express.util;

/**
 * @author fanc
 * 2020/6/12 0012-19:32
 */
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Test {
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();




    public static void main(String[] args) {
        String USERNAME = "707HGUPS0501";
        String PASSWORD = "100GO58OY992";
//        String USERNAME = "hgups2019";
//        String PASSWORD = "WG.U4V4nyJ8c2MW";
        String CREDENTIALS = USERNAME + ":"+ PASSWORD;
        String AUTHORIZATION = "Basic "+ Base64.encodeBase64String(CREDENTIALS.getBytes());
        System.out.println(" AUTHORIZATION: " + AUTHORIZATION);
//        String auth1 = "Basic aGd1cHMyMDE5OldHLlU0VjRueUo4YzJNVw==";
//        String auth2 = "Basic NzA3SEdVUFMwNTAxOjEwMEdPNThPWTk5Mg==";
//        Test t = new Test();
//        t.outToImage();
        String base64Str = "JVBERi0xLjINCjUgMCBvYmoNCjw8DQovVHlwZSAvWE9iamVjdA0KL1N1YnR5cGUgL0ltYWdlDQov\n" +
                "TmFtZSAvU25vd2JvdW5kMA0KL1dpZHRoIDgwMA0KL0hlaWdodCAxMjAwDQovQml0c1BlckNvbXBv\n" +
                "bmVudCAxDQovQ29sb3JTcGFjZSAvRGV2aWNlR3JheQ0KL0ZpbHRlciAvQ0NJVFRGYXhEZWNvZGUN\n" +
                "Ci9EZWNvZGVQYXJtcyA8PA0KL0sgLTENCi9Db2x1bW5zIDgwMCAvUm93cyAxMjAwDQovRW5kT2ZC\n" +
                "bG9jayBmYWxzZQ0KL0VuZE9mTGluZSBmYWxzZQ0KL0VuY29kZWRCeXRlQWxpZ24gZmFsc2UNCj4+\n" +
                "DQovTGVuZ3RoIDYgMCBSDQo+Pg0Kc3RyZWFtDQr/k1AmA1fzL8doT/////////////////8mYHhm\n" +
                "f///////////////////////////////////IZWwX///////+VxUDML///Obf///////////////\n" +
                "////8oz+UGU4pmRoZsObigz4Q0FPx/Ni//+g7TQfhB9/f//o1ufGjW0a2e00bHPWfGnz4///p90E\n" +
                "G6fgiPoJ/eCI/7///j/4/7//7f//36Td6TaQ/pN///+wevsH/yIev///+GHFeGH/nB8V////g3Xg\n" +
                "3pN/PWkm////Dc5+G+PJ/nOP///zP+0jH6bf6f//9rfv38//+f/pf/t02uwwk2v7DC//r//Gb4o3\n" +
                "7FRWk2xUhKUm+K//DCwwvaDC/DC/+v/xERERERERERH//////////////+QPBhY//kYMpQIQUHIy\n" +
                "//CDh3//p3f/+ieOamY7H/9BBvb2//0n33yfM8zI/mecEMz/+r//0H9hB//tE5GkYZw/PiFBebi7\n" +
                "LxoRQX6Nj+fmjY//UQg8INB6SDCD/XCDCD/QT/vT//Sca6qn8Vpp/7/+P/6J25OxPbikX7RfPSgt\n" +
                "E7onj/SH/v/+EHpp+np9a4TcJ+P/VklyX/63Vdj4xVauk3+cH8UGH//T/zHYWHX0P88//RIv///3\n" +
                "MwrIlAv4/8n/nLv///9sKwcwz/+3/lH//Z0//9ER7f///a9//vf/+ifygX/mUfYYVtLbX/9L//hZ\n" +
                "1f+aX+xTFbFf+2tr/+puf++/wwmthf/avX+l9/7pOaWIiIj/7DShhf9bCtr/DWGEv/2KYr+KY4r9\n" +
                "imP/9pr+pCD2v2mF/+GEGF/CDCbDC/DQa//EREREREREf////+Qytgv////////////JfNM8f/9q\n" +
                "EH/+9I2P//6f/8hu1x//CDer//nv7JN0n/0m7UMP/+N6Id//+G//5yGpz//96v//bN3tr//FRsV/\n" +
                "+GmmF/+Ij///////////////////8f//4///////////////////////////////////////////\n" +
                "/+P/////////////////xHzM0n//////////zsUCHdhDIgIdqwh2WhCBBDKwvkFFIwUEDNQKAQMj\n" +
                "YZp8UzDKBAzoDUCBmoUlIgIGCBkbBQahSnCmsCP8IODCD+DCDwg9MIMIOD0gg/9O0/hp4T9NO9JB\n" +
                "/6cNP7T0Hpppw9IJ/6en9p6fpp+kn/ra/rr6rekn/yE93IT39yE96If3pyE9yE93pIgk//CDyKRh\n" +
                "B5FMZFqwg5ECoEG/hB4QeRSOlBBv/Sbw6Tf4bSb6f0m0m8PpJP//t///pN6f/t6VN/+/7/vv0/Io\n" +
                "/ff9JJ//9///1YWt/++lUL/sfsf+x6nD/Y2P6Ss6r/h/D/4fj08OH+lQ/7D9h/qw/1thsP9L/4PW\n" +
                "D/Sg9Lp4OD10l/5DOM8hnFPhZDOBf+QziCGcU+tf7B6TByGUfFMH+tsGwelr/7DwrD/Yf9Ow2HhY\n" +
                "v/2HFMP6Yf62w2HFXkMCP9vb+m//bb//8N0G/hBv+nDYbq//w3Qb+CDf62Gw3Uhgd//g3QN/QN/0\n" +
                "4Ng3VP/+VAYhEDDEimMi+yJBjNYL/5AgwVAYhU3/+DwQP+Gwf/QsHB4Kr//BuRSR/tg37ZELwbBu\n" +
                "RRpO2QYv++/7b/wem79N//k7fIk3/bOrf9A/Ibc0b9X/+eF8gQv/lAv935oLnBfp7/+/v/bv9107\n" +
                "7/bf9tf//b/G6//+7/6bXbX921215Frtq2vu2v2t13X93XYS1u6uvbtftbXtf3tdgwvT2tr9thfY\n" +
                "MJMMF2GC/tsMF2KkGi9sMFYYL7wYL8bFbFf2xW1+xTFe2x+1XX71tdPVfbXtPv/b7Wtu/u17TW1+\n" +
                "7W107TX2GF7QYWGF+2GFhhfhhBhe2wvDCDCwwv7DCyqia2GEGF7gwXlbDgwQgwQiIgwQiIiDBAwQ\n" +
                "iIMhEriIiIj///////////HzM0n//////////zVnowFI+aZjMZnmmfj7M8tDiEQyGEPF+l2vf/2u\n" +
                "E/0p0a720ER9AiP8/OCnQXb/S////3+E3+l2vCI///+fYQZf1f9Levb73X9Qg/6H/kJf/3Suvip7\n" +
                "hhDIkfj7X2//+km//giPTh69v//ntR85/03//tpNpf3//t9r7f//vOj2Z/7717H/2vf7pf3///tp\n" +
                "bbZu+Gv7chcH7sUxTFbxWb9iv29r39rsNfhrxERERERERERH///8szCJyJyJz865nmZH8zzghmaa\n" +
                "YQYQf+g/sINNNNP/Rsfz80bGmnd/6Cf96eXeXdE7onf/3/8f+np/9If+0m0m/7+P/Vkmgs/j//nB\n" +
                "/FBh/9Wvzz/9Ei/1Y//J/5y7/0v/b/yj/9J//7Xv/0gyY//YYVtLbX/SH/2KYrYr/S/8MJrYVpWk\n" +
                "kaNLxEREX30rX440QSB+0117TSte01sLxER/zWMnRPmecz5H8zzAc8RmIUZpmf+v6D/Tvv9H0/o2\n" +
                "PzPRrc/Pv/X9BP+gg3v/+gRH/9/1v+ER//I7/9If6f/b/QtLj+P/X/+fGl/nB/8V7f4ulzz/zz/1\n" +
                "7f/EP/J//50////b/7X9v86+Z///vX7H9pf7DC/sMJf/sf+xX7FciPf2F/hhftfv4iIiIiIiIj//\n" +
                "//5L5sKYEP58j2Z5mRDzPMRnEjJ84ENjOGa84EP5m/+8J+ne9f6f4T/nIPz0Xb9Gt3tpBQpI0eho\n" +
                "YUkYWe3/78Jv0EG/tVVLp1SVJv//V/W4RH9tJHejv8dHfo7//9eh+n296HHSyO0h0h//kJYr4//Y\n" +
                "r9Cwl///X/7f+lPjh6Xa/zkHtfPP+3tfi4Nkcv2P/7////S39LZb/Ov+/Zn/a7f/+P4//97pe9bH\n" +
                "/n/L/+c0vtfb4a+wwl/6VpWlpd/sVIWL7FexW/+xsf7Fe194a9rf/YTC/a8RERERERERERERH///\n" +
                "///////////////////////////////////////////////y0CQHg2///NsU///////////////+\n" +
                "RgzoZqZ0f+EGEH/+g01X/VNV/0SxycVX/QQbpwoX/rSbVf/YXyY+TH/6Q/C4X//wuF//44//////\n" +
                "/////kj//+4f//uv//2k2l/+w1tf/2Kj//pr/8NNf/hhBhf/iIj///////////////8f//j/////\n" +
                "//////////////////+ZDxIGazJEUREDKIojwYPxRfggfeEH/f+n3oP+/9E/aBEe9E+/MM/6CDf3\n" +
                "oJv+/9Jv/Sb//+nSbgiPdP/BEe///b1H/b/0m0m/6////9v45B52///////9Jvb/9v//9v5kZhlv\n" +
                "/+P////9Pb//b/20v9s6X//nazfoyHt+v+3/9r+x3X+x/7aX+2v//aTDCSTe7DCX+/sbFf7FfIL9\n" +
                "/aa/dr/f2gYJJvcML/fxEREREREf//////87W4oiQKURRHgQkD/wQfwwg/9P7T/0T9/NTL5/6CDf\n" +
                "7aTf+k/70//Tf///j/4/6X+38f+sjZUv/+kyEZVn+aBPjDD+ZH/o6D//+EDf/mq+Yaf+3/muf///\n" +
                "/++17/9tXrdf9gwWGF4aX+xCYrYr/DTW1/hggwsGF4iIiP//////+ZD5IIkEaskEU4YPghOGCcZJ\n" +
                "maR1jYY9QgwmoQfDwg+1/VNBqn3p71+qJ3RfVRfZhhqOYYXzQIj3zG5bwsIN02Fpvw+k37aX6LH+\n" +
                "k6LH/99P//paV9pf+/6Tf/wvf4X/34+2l+P/jtpe+2k3///7FSDz8g8yMn7WQc3Ev/a7X+yEB96/\n" +
                "/bXCD/ww0m///Y9F85hnmJv20pjct/7Ig/p/8G4///YXpN/4bT//8f/1zXPtpf/pf/X6Mh//tpGL\n" +
                "/9e/7X/+17X63X3r/hhW0ttLwvDSSb//YqK2KkF+FSC/CvjkFzca+01tdbXtf4YQYWGFwWDCSb/x\n" +
                "ERERERER///////zI1GazJEUM3EQZIiiPAhUDNRlQRT5QRQRIIhCJBEgiQXggfpffDCDwmoQYQYQ\n" +
                "YQYQYQYQfp+l99oPQappppppp+iftAiPaWgRHvmo0T6gRHkX1UXlE7ovKLyid0Tuid/QQb/S/vw6\n" +
                "Cb+m9JuEG0g2kG4QbhBuEG/Sb/S//tJv0nWn+uv//6dJvS0m4Ij3vTpN9ow/FKxx0rStK//9L/b+\n" +
                "1H/XS3Vd99/SbSb0tJv/ekm/rzBekl////pf7f//peE/MxkGP////X///7VLi/////Sbi9Jvb/0m\n" +
                "21+Sx/d////yQ7/2//2NLhN+Q2Vb///46b4//42RB6XT+2XEb///069Pb/02Fpf++///tpdN//q2\n" +
                "dLHiD/////9GQ9vRkPb+tUZDS//H//+1/b/2PW68h3t7aRiyHbaTaTaXtpfv//Ta/akO0vtK0v//\n" +
                "YYSSb29JvfCsMJJNtpbDWGFbVtYYWGFhhfYr+5B3/xTFfFcUxTFMUxTFMV7X7f+9Ne1hpppppprw\n" +
                "YJJvb0m94IMJJsMLYQYQYQYQYQYQYXiIiIiIiIiIiIiIiP//////////////////////////////\n" +
                "//////////H////8zNJ/////////////////zIyMoMoFJAYOopEBsNgwaxkuMkGUEcM4RQzoEJNm\n" +
                "2RszqZmL/gg4YIPh4IPS7wg/bSBB/6DtB9poPS/CDVfQf+nafenpd6dV6f/Jdk55LpCHJnyx6Jc9\n" +
                "L9EXn7aUl3/QTe2gm/bW0n0u9Ag/+gm//fff39JvS5Y/pv/3////0m6f/vSyQGg1jbS//sfsf/4W\n" +
                "l7+wsIP7H/h/D/9IdL71HT+H/sH7B+qTfr79abaTB/8g6KyDpOl/r9+RYzBolj+QdeP7D0mHx03x\n" +
                "fv5oHqqd6Yf+GHGGH0v/3/6TdqGH/hug3wv5EHfv//+G/86hKNYTgkm+m/v/43opwn+DcEDfJ4/6\n" +
                "f381GSA0dqDf+HkxP7ceQ1V/e3/4f/PT5ET+2m3B0/v/yYPRPP/hfC/t+n399/2oX++/v9tO62/f\n" +
                "fDJwTqf3+6Ta7a+8mHbXf44YWqcznptfhrYS7CX37YS792GF+Gg47CXsUxWxXtrce/7H7FNMV7vv\n" +
                "704a792F7W/sJhbC+/YW3+GEkk1sLwwgYWDC9imwwt/cGC8MJoGF4iIiIiIiIiIiIj//////////\n" +
                "///////+dmpGI8IeDmIwIYjBGBDQj0ejSMCGhGI9HiNA5iMCGIxGA5oRgjxHiPRgQ8R4jxGCPRiJ\n" +
                "BGA5gjBGBDSMEYI8R4jBHiPEejAh4Q0IxGIwHMRoRoIYjBf/////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////////////////////////////////////////////////////\n" +
                "////////////////////////////+IiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiP//////\n" +
                "////////////mRaZ1IkEQyKtkgzoR0I1AuaGQzOhEuFJyJBEMiDIhkamSDIMKaH4QYQYQYQahBhB\n" +
                "hB4QYQYQeEGEGEGEGEGoQeEH6aaaemmnpppqmmmmnpqn6a+FT11T9f8Knp/Jw5OGiTtEnaycNEnc\n" +
                "nD0ThycNEnfJw0Sdok7RJ2iTtZOHycP0nSenpwqT06TkGe6T08mQnp6enpwqTyaE/7/T0670/1Xv\n" +
                "T/09PT067+/9+trcmP7W/SUe1tL1tbW1uTH9pd/4//C/8df/yOx///hf5Hf/6/8L/1rLn/xa///C\n" +
                "/i/++v8e/6WLv+6//8e+7/dV/919at19Nf//3VN1+NL/4/SW4/kopf//xyUY/dL/39KpK7+1aX//\n" +
                "7tW/ul/7+ktW/i6X//7i7+yb0v/ZN/pe7Jv/S///ZN+yb/HS/8f0q8f9L//8fH/S//9Jf/Ib0v//\n" +
                "8hv/Mik6p6eszOnqt82dPS9PT09PWZnS5s6/VK1tfW1S79bW0krW1tbX1tLX70QxbW177VEMaq0r\n" +
                "7VtUQxbW1tbXvbW/2K2KYrYpivY2KYqK2KYpimK2KimK+v+v2q3//p69pWmtppdppppWmmmtpprw\n" +
                "wsMIMLDCDC8MIMIMIMLDCDCDCDCwwgwgwvERERERERERERERH/////////////////////////4/\n" +
                "////mZpP////////////////////////////////////////////////////H+WaNhv/wAQAQA0K\n" +
                "ZW5kc3RyZWFtDQplbmRvYmoNCjYgMCBvYmoNCjcwOTYNCmVuZG9iag0KMSAwIG9iag0KPDwNCi9U\n" +
                "eXBlIC9DYXRhbG9nDQovUGFnZXMgMyAwIFINCj4+DQplbmRvYmoNCjIgMCBvYmoNClsgL1BERiAv\n" +
                "SW1hZ2VCICBdDQplbmRvYmoNCjcgMCBvYmoNCjw8IC9MZW5ndGggNTIgPj4NCnN0cmVhbQ0KMDAw\n" +
                "Mjg4IDAgMCAwMDA0MzIgICAgICAwICAgICAgMCBjbQ0KL1Nub3dib3VuZDAgRG8NCmVuZHN0cmVh\n" +
                "bQ0KZW5kb2JqDQo0IDAgb2JqDQo8PA0KL1R5cGUgL1BhZ2UNCi9QYXJlbnQgMyAwIFINCi9SZXNv\n" +
                "dXJjZXMgPDwNCi9YT2JqZWN0IDw8DQovU25vd2JvdW5kMCA1IDAgUg0KPj4NCi9Qcm9jU2V0IDIg\n" +
                "MCBSDQo+Pg0KL0NvbnRlbnRzIDcgMCBSDQo+Pg0KZW5kb2JqDQozIDAgb2JqDQo8PA0KL1R5cGUg\n" +
                "L1BhZ2VzDQovS2lkcyBbNCAwIFIgDQpdDQovQ291bnQgMQ0KL01lZGlhQm94IFsgMCAwIDI4OCA0\n" +
                "MzIgXQ0KPj4NCmVuZG9iag0KeHJlZg0KMCA4DQowMDAwMDAwMDAwIDY1NTM1IGYNCjAwMDAwMDc0\n" +
                "NTIgMDAwMDAgbg0KMDAwMDAwNzUwNyAwMDAwMCBuDQowMDAwMDA3NzkwIDAwMDAwIG4NCjAwMDAw\n" +
                "MDc2NDkgMDAwMDAgbg0KMDAwMDAwMDAxMCAwMDAwMCBuDQowMDAwMDA3NDI5IDAwMDAwIG4NCjAw\n" +
                "MDAwMDc1NDMgMDAwMDAgbg0KdHJhaWxlcg0KPDwNCi9TaXplIDgNCi9Sb290IDEgMCBSDQo+Pg0K\n" +
                "c3RhcnR4cmVmDQo3ODg0DQolJUVPRg0K";
        String dest = "/Users/Bingo/Downloads/hgups2.pdf";
        String dest3 = "/Users/Bingo/Downloads/hgups3.pdf";
        PDFUtils.base64StringToPDF(base64Str, dest);

        try {

            PdfReader reader = new PdfReader(dest);
            //或者下面的也可以
//           PdfReader reader = new PdfReader(new FileInputStream(filename));
            Rectangle pageSize = reader.getPageSize(1);
            float height = pageSize.getHeight();
            float width = pageSize.getWidth();
            System.out.println(" pdf.with=" + width + ", pdf.height=" + height);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dest3)));
//            PDFUtils.setWaterPrint(bos, dest, "JFK Entry 1");

            String dest4src = PDFUtils.getPDFBinary(new File(dest3));
            System.out.println("dest4src: " + dest4src);
            String dest4 = "/Users/Bingo/Downloads/hgups4.pdf";
            PDFUtils.base64StringToPDF(dest4src, dest4);
        } catch (FileNotFoundException e) {
            System.out.println(" test error: " + String.valueOf(e));
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" test1 error: " + String.valueOf(e));
            e.printStackTrace();
        }


    }

}
