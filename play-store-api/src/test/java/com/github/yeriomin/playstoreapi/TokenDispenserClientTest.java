package com.github.yeriomin.playstoreapi;

import okhttp3.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TokenDispenserClientTest {

    static private final String DISPENSER_URL = "http://tokendispenser-yeriomin.rhcloud.com";
    static private final String EMAIL = "yalp.store.user.one@gmail.com";
    static private final String TOKEN = "2ATCN-yk96j5X34If6J3hKYZYeSvWkmRiKMPKXpgoJbpg0wECRJ23aADRR3tDHIJ56inlA.";
    static private final String TOKEN_AC2DM = "2ATCN3JzOxZm-ymh7Ueb6jSKv4DcZEAeKpKKK1CuFpg2Q5Fpp5_-nLn-taMoIHDjY0Qcrw.";

    private MockOkHttpClientAdapter httpClientAdapter;
    private TokenDispenserClient tokenDispenserClient;

    @Before
    public void setUp() throws Exception {
        httpClientAdapter = new MockOkHttpClientAdapter();
        tokenDispenserClient = new TokenDispenserClient(DISPENSER_URL, httpClientAdapter);
    }

    @Test
    public void getRandomEmail() throws Exception {
        String email = tokenDispenserClient.getRandomEmail();
        Assert.assertEquals(EMAIL, email);

        List<Request> requests = httpClientAdapter.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(1, request.url().pathSegments().size());
        Assert.assertEquals("email", request.url().pathSegments().get(0));
    }

    @Test
    public void getToken() throws Exception {
        String token = tokenDispenserClient.getToken(EMAIL);
        Assert.assertEquals(TOKEN, token);

        List<Request> requests = httpClientAdapter.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(3, request.url().pathSegments().size());
        Assert.assertEquals("token", request.url().pathSegments().get(0));
        Assert.assertEquals("email", request.url().pathSegments().get(1));
        Assert.assertEquals(EMAIL, request.url().pathSegments().get(2));
    }

    @Test
    public void getTokenAc2dm() throws Exception {

        String token = tokenDispenserClient.getTokenAc2dm(EMAIL);
        Assert.assertEquals(TOKEN_AC2DM, token);

        List<Request> requests = httpClientAdapter.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(3, request.url().pathSegments().size());
        Assert.assertEquals("token-ac2dm", request.url().pathSegments().get(0));
        Assert.assertEquals("email", request.url().pathSegments().get(1));
        Assert.assertEquals(EMAIL, request.url().pathSegments().get(2));
    }
}