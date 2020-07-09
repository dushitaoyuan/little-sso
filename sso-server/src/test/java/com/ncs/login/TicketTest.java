package com.ncs.login;

import com.ncs.ticket.AuthManager;
import com.ncs.ticket.Ticket;
import org.junit.Test;

/**
 * @author lianglei
 * @date 2020/4/2 18:21
 **/
public class TicketTest {
    @Test
    public void ticketTest() throws Exception {
        String oldTicket="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiID8+PHRpY2tldD48Y29udGVudD48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1VTRVJJRCIgIHZhbHVlPSI4MzUiICAvPjxwcm9wZXJ0eSAgbmFtZT0iVElDS0VUX0lORk9fQ1JFQVRFVElNRSIgIHZhbHVlPSIyMDIwLTA0LTAyIDE4OjE3OjI4IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0RFRklORV9VU0VSU04iICB2YWx1ZT0iMDAwMzU3MTg1OCIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfSU5GT19USU1FTElORSIgIHZhbHVlPSI4IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0RFRklORV9VU0VSUFJPVklOQ0VJRCIgIHZhbHVlPSIwMCIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfSU5GT19VU0VSVFlQRSIgIHZhbHVlPSIwIiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1VJRCIgIHZhbHVlPSJsbF90ZXN0IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1NFU1NJT05JRCIgIHZhbHVlPSJNVFU0TlRneU1qWTBPRFV3T1E9PSIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfSU5GT19DTElFTlRJUCIgIHZhbHVlPSIxOTIuMTY4LjQwLjE4IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0VORFRJTUUiICB2YWx1ZT0iMjAyMC0wNC0wMyAwMjoxNzoyOCIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfQVJFQV9DT0RFIiAgdmFsdWU9IuaXoCIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfT1JHX0NPREUiICB2YWx1ZT0i5pegIiAgLz48L2NvbnRlbnQ+PHNpZ25hdHVyZXM+PHNpZ25hdHVyZSBhbGc9IjEuMi44NDAuMTEzNTQ5LjEuMS41IiBlbmNvZGluZz0iYmFzZTY0IiBzaWduYnk9IjE3MDUwMDAwMDAwMDAwM2UiID5MTmdXZ2xpZG9tcDQwQnVHM1NJOFlmb0hTNFZyUWgydG5KT3hTMU1LRlBoUm9pV2Yra3dDaVUzYjJTcWRvYmdZNHVnU3hKYkgrMWgyQVUycTFWNDI2c1V2dlRwZzdQVTdmRmZVM0ovZ0YrcGZ3ZmtMenlyUlpQYXYrVGRKb2FvTjROa2thdElhR3hzUXhLWFlyTnNaWXdpWmlJUkxPZGhzQkNzdHlpa1NkRDg9PC9zaWduYXR1cmU+PC9zaWduYXR1cmVzPjwvdGlja2V0Pg==";

        String oldTicket2="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiID8+PHRpY2tldD48Y29udGVudD48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1VTRVJJRCIgIHZhbHVlPSIxMDAiICAvPjxwcm9wZXJ0eSAgbmFtZT0iVElDS0VUX0lORk9fQ1JFQVRFVElNRSIgIHZhbHVlPSIyMDIwLTA0LTAyIDE4OjE5OjA2IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0RFRklORV9VU0VSU04iICB2YWx1ZT0iMDAwMDAwMDAwNiIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfSU5GT19USU1FTElORSIgIHZhbHVlPSI4IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0RFRklORV9VU0VSUFJPVklOQ0VJRCIgIHZhbHVlPSIwIiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1VJRCIgIHZhbHVlPSJsbHRlc3QxIiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX1NFU1NJT05JRCIgIHZhbHVlPSJUVlJCZDB4cVNUQk5kdy4yYTVhZDNjYzYwZjRhMDYyODQ0NzZjMDMwZWQxMzEwNyIgIC8+PHByb3BlcnR5ICBuYW1lPSJUSUNLRVRfSU5GT19DTElFTlRJUCIgIHZhbHVlPSIxOTIuMTY4LjQwLjE4IiAgLz48cHJvcGVydHkgIG5hbWU9IlRJQ0tFVF9JTkZPX0VORFRJTUUiICB2YWx1ZT0iMjAyMC0wNC0wMyAwMjoxOTowNiIgIC8+PC9jb250ZW50PjxzaWduYXR1cmVzPjxzaWduYXR1cmUgYWxnPSIxLjIuODQwLjExMzU0OS4xLjEuNSIgZW5jb2Rpbmc9ImJhc2U2NCIgc2lnbmJ5PSIxNzA1MDAwMDAwMDAwMDNlIiA+TUwyY0Q4QUdkSm9KZlpRNStwRDVsSUorcXhZeXYxcTlRSlhodXFQVTFmT1QySmE4N28rVG1ybmo1aVdLVnM5djdCUlBOL0Q2WnJ3bXlLTldJS2kvY214TWVYTitQV1JIM2FYS3lob2daK0gxd1liR09RbU50YXhBSmNkd213Wk11QWFHOEN5R2wvbG1qTVc1NVZmNXpxcDZxdEpHM3dITnBQSGxMSnZnekp3PTwvc2lnbmF0dXJlPjwvc2lnbmF0dXJlcz48L3RpY2tldD4=";
        Ticket oldTicketObj = AuthManager.parseTicket(oldTicket);
        Ticket oldTicketObj2 = AuthManager.parseTicket(oldTicket2);
        System.out.println(oldTicketObj);
        System.out.println(oldTicketObj2);

    }
}
