import com.taoyuanx.sso.util.PasswordUtil;
import org.junit.Test;

/**
 * @author dushitaoyuan
 * @date 2020/12/3118:51
 */
public class PasswordTest {
    @Test
    public void passwordTest() {
        String hashedPassword = PasswordUtil.md5Hashed("123456");
        System.out.println(hashedPassword);

        System.out.println(PasswordUtil.passwordEncode(hashedPassword));

    }

    @Test
    public void demoTest() {
        System.out.println(addSessioinIdToRedirectUrl("http://localhost:8080/app?", "1"));
        System.out.println(addSessioinIdToRedirectUrl("http://localhost:8080/app?demo=demo", "1"));
        System.out.println(addSessioinIdToRedirectUrl("http://localhost:8080/app?demo=demo&demo2=1", "1"));

        // System.out.println(addSessioinIdToRedirectUrl("http://localhost:8080/app?demo=demo", "1"));

    }

    public String addSessioinIdToRedirectUrl(String redirectUrl, String sessionId) {
        int flagIndex = redirectUrl.indexOf("?");

        if (flagIndex > -1) {
            return redirectUrl.substring(0, flagIndex) + "?" + "sessionId" + "=" + sessionId+"&" + redirectUrl.substring(flagIndex+1);
        }

        return redirectUrl+"?sessionId="+sessionId;
    }
}
