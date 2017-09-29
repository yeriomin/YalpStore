package com.github.yeriomin.playstoreapi;

import okhttp3.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class GooglePlayAPITest {

//    private static final String EMAIL = "yalp.store.user.one@gmail.com";
    private static final String EMAIL = "konstantin.razdolbaev@gmail.com";
    private static final String PASSWORD = "TemporaryPassword!";
    private static final String GSFID = "3f1abe856b0fa7fd";
//    private static final String TOKEN = "JAXCN65HwzmJg1fKq4Q3k6qY3LXu0yn7__9OOxEI_FRW4Ch90QMHiyTtCoLNZG1UMP_xlA.";
    private static final String TOKEN = "jwSyrOU2RHDv2d82095MoHKOUHhO9KxBbkAoLCMkCKWqB9RUHbvq8VIWufBJcxwRn3_DGQ.";

    private GooglePlayAPI api;

    @Before
    public void setUp() throws Exception {
        api = initApi();
    }

    @Test
    public void getGsfId() throws Exception {
        GooglePlayAPI api = initApi();
        api.setGsfId(null);
        api.setToken(null);
        String ac2dmToken = api.generateAC2DMToken(EMAIL, PASSWORD);
        Assert.assertEquals("TgSyrINgeerWByF9lukvliiumvlSapg-Gl2d7KbpL7esPQzdbcZ0BK2ktdohPRc2RZHRXw.", ac2dmToken);
        String gsfId = api.generateGsfId(EMAIL, ac2dmToken);
        Assert.assertEquals("307edaee584cc716", gsfId);

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(3, requests.size());

        Request requestAuthAc2dm = requests.get(0);
        Assert.assertEquals(1, requestAuthAc2dm.url().pathSegments().size());
        Assert.assertEquals("auth", requestAuthAc2dm.url().pathSegments().get(0));
        Map<String, String> vars = MockOkHttpClientAdapter.parseQueryString(MockOkHttpClientAdapter.getBodyBytes(requestAuthAc2dm));
        Assert.assertEquals(12, vars.size());
        Assert.assertEquals("konstantin.razdolbaev@gmail.com", vars.get("Email"));
        Assert.assertEquals("TemporaryPassword!", vars.get("Passwd"));
        Assert.assertEquals("us", vars.get("device_country"));
        Assert.assertEquals("en", vars.get("lang"));
        Assert.assertEquals("22", vars.get("sdk_version"));
        Assert.assertEquals("ac2dm", vars.get("service"));
        Assert.assertEquals("1", vars.get("add_account"));
        Assert.assertEquals("com.google.android.gsf", vars.get("app"));

        Request requestCheckin1 = requests.get(1);
        Assert.assertEquals(1, requestCheckin1.url().pathSegments().size());
        Assert.assertEquals("checkin", requestCheckin1.url().pathSegments().get(0));
        Assert.assertNull(requestCheckin1.header("Authorization"));
        Assert.assertNull(requestCheckin1.header("X-DFE-Device-Id"));
        Assert.assertEquals("Android-Finsky/7.1.15 (api=3,versionCode=80798000,sdk=22,device=C6902,hardware=qcom,product=C6902)", requestCheckin1.header("User-Agent"));
        Assert.assertEquals("en-US", requestCheckin1.header("Accept-Language"));
        AndroidCheckinRequest requestCheckinProto1 = AndroidCheckinRequest.parseFrom(MockOkHttpClientAdapter.getBodyBytes(requestCheckin1));
        Assert.assertEquals("C6902", requestCheckinProto1.getCheckin().getBuild().getDevice());

        Request requestCheckin2 = requests.get(2);
        Assert.assertEquals(1, requestCheckin2.url().pathSegments().size());
        Assert.assertEquals("checkin", requestCheckin2.url().pathSegments().get(0));
        Assert.assertNull(requestCheckin2.header("Authorization"));
        Assert.assertEquals("Android-Finsky/7.1.15 (api=3,versionCode=80798000,sdk=22,device=C6902,hardware=qcom,product=C6902)", requestCheckin2.header("User-Agent"));
        Assert.assertEquals("en-US", requestCheckin2.header("Accept-Language"));
        AndroidCheckinRequest requestCheckinProto2 = AndroidCheckinRequest.parseFrom(MockOkHttpClientAdapter.getBodyBytes(requestCheckin2));
        Assert.assertEquals("C6902", requestCheckinProto2.getCheckin().getBuild().getDevice());
        Assert.assertEquals(3494471078104581910L, requestCheckinProto2.getId());
        Assert.assertEquals(6680108819399858497L, requestCheckinProto2.getSecurityToken());
        Assert.assertEquals("[konstantin.razdolbaev@gmail.com]", requestCheckinProto2.getAccountCookie(0));
        Assert.assertEquals("TgSyrINgeerWByF9lukvliiumvlSapg-Gl2d7KbpL7esPQzdbcZ0BK2ktdohPRc2RZHRXw.", requestCheckinProto2.getAccountCookie(1));
    }

    @Test
    public void getToken() throws Exception {
        GooglePlayAPI api = initApi();
        api.setToken(null);
        String token = api.generateToken(EMAIL, PASSWORD);
        Assert.assertEquals("TgSyrOQWSodzTLUtSuMebIW5k1XUvhsDE3gVcf-vnL8q9qT_oofA6ygYpE4m6sSi1UrCSQ.", token);

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());

        Request request = requests.get(0);
        Assert.assertEquals(1, request.url().pathSegments().size());
        Assert.assertEquals("auth", request.url().pathSegments().get(0));
        Map<String, String> vars = MockOkHttpClientAdapter.parseQueryString(MockOkHttpClientAdapter.getBodyBytes(request));
        Assert.assertEquals(11, vars.size());
        Assert.assertEquals("konstantin.razdolbaev@gmail.com", vars.get("Email"));
        Assert.assertEquals("TemporaryPassword!", vars.get("Passwd"));
        Assert.assertEquals("us", vars.get("device_country"));
        Assert.assertEquals("en", vars.get("lang"));
        Assert.assertEquals("22", vars.get("sdk_version"));
        Assert.assertEquals("androidmarket", vars.get("service"));
        Assert.assertEquals("com.android.vending", vars.get("app"));
    }

    @Test
    public void searchSuggest() throws Exception {
        SearchSuggestResponse response = api.searchSuggest("cp", new GooglePlayAPI.SEARCH_SUGGESTION_TYPE[] { GooglePlayAPI.SEARCH_SUGGESTION_TYPE.SEARCH_STRING });

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("searchSuggest", request.url().pathSegments().get(1));
        Assert.assertEquals(4, request.url().queryParameterNames().size());
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("cp", request.url().queryParameter("q"));
        Assert.assertEquals("2", request.url().queryParameter("sst"));
        Assert.assertEquals("120", request.url().queryParameter("ssis"));

        Assert.assertTrue(response.getEntryCount() > 0);
        SearchSuggestEntry appEntry = response.getEntry(0);
        Assert.assertEquals(2, appEntry.getType());
        SearchSuggestEntry suggestionEntry = response.getEntry(1);
        Assert.assertTrue(suggestionEntry.hasSuggestedQuery());
        Assert.assertEquals("cpu cooler", suggestionEntry.getSuggestedQuery());
    }

    @Test
    public void searchSuggestBothTypes() throws Exception {
        SearchSuggestResponse response = api.searchSuggest("fir");

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("searchSuggest", request.url().pathSegments().get(1));
        Assert.assertEquals(4, request.url().queryParameterNames().size());
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("fir", request.url().queryParameter("q"));
        List<String> ssts = request.url().queryParameterValues("sst");
        Assert.assertEquals(2, ssts.size());
        Assert.assertTrue(ssts.contains("2"));
        Assert.assertTrue(ssts.contains("3"));
        Assert.assertEquals("120", request.url().queryParameter("ssis"));

        Assert.assertTrue(response.getEntryCount() == 6);
        SearchSuggestEntry appEntry = response.getEntry(0);
        Assert.assertEquals(3, appEntry.getType());
        Assert.assertEquals("Firefox Browser fast & private", appEntry.getTitle());
        Assert.assertTrue(appEntry.hasImageContainer());
        Assert.assertEquals("https://lh5.ggpht.com/8PODwBXKk4L201m4IO1wifRDfbn4Q1JxNxOzj-5TXPJ85_S-vOqntLi7TsVyeFQM0w4", appEntry.getImageContainer().getImageUrl());
        Assert.assertTrue(appEntry.hasPackageNameContainer());
        Assert.assertEquals("org.mozilla.firefox", appEntry.getPackageNameContainer().getPackageName());
        Assert.assertFalse(appEntry.hasSuggestedQuery());
        SearchSuggestEntry suggestionEntry = response.getEntry(1);
        Assert.assertEquals(2, suggestionEntry.getType());
        Assert.assertTrue(suggestionEntry.hasSuggestedQuery());
        Assert.assertEquals("fire and safety book free", suggestionEntry.getSuggestedQuery());
    }

    @Test
    public void searchIteratorType1() throws Exception {
        SearchIterator i = new SearchIterator(api, "cpu");
        Assert.assertEquals("cpu", i.getQuery());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response = i.next();
        Assert.assertEquals(20, response.size());
        Assert.assertEquals("CPU-Z", response.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(20, response.size());
        Assert.assertEquals("AnTuTu Benchmark", response1.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
    }

    @Test
    public void searchIteratorType2() throws Exception {
        SearchIterator i = new SearchIterator(api, "english");
        Assert.assertEquals("english", i.getQuery());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response = i.next();
        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(2, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("search", request.url().pathSegments().get(1));
        Assert.assertEquals(2, request.url().queryParameterNames().size());
        Assert.assertEquals("english", request.url().queryParameter("q"));
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals(10, response.size());
        Assert.assertEquals("Duolingo: Learn Languages Free", response.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(20, response1.size());
        Assert.assertEquals("Learn English - 5000 Phrases", response1.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
    }

    @Test
    public void searchIteratorType3() throws Exception {
        SearchIterator i = new SearchIterator(api, "protonmail");
        Assert.assertEquals("protonmail", i.getQuery());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response = i.next();
        Assert.assertEquals(19, response.size());
        Assert.assertEquals("ProtonMail - Encrypted Email", response.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(3, response1.size());
        Assert.assertEquals("All Emails Solution in One App", response1.get(0).getTitle());

        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void searchIteratorType4() throws Exception {
        SearchIterator i = new SearchIterator(api, "firefox");
        Assert.assertEquals("firefox", i.getQuery());
        Assert.assertTrue(i.hasNext());

        List<DocV2> response = i.next();
        Assert.assertEquals(11, response.size());
        Assert.assertEquals("Firefox Browser fast & private", response.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(20, response1.size());
        Assert.assertEquals("Ghostery Privacy Browser", response1.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
    }

    @Test
    public void searchIteratorType5() throws Exception {
        SearchIterator i = new SearchIterator(api, "tiny archers");
        Assert.assertEquals("tiny archers", i.getQuery());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response = i.next();
        Assert.assertEquals(8, response.size());
        Assert.assertEquals("Tiny Archers", response.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(20, response1.size());
        Assert.assertEquals("Tiny Troopers 2: Special Ops", response1.get(0).getTitle());

        Assert.assertTrue(i.hasNext());
    }

    @Test
    public void similarAppsIterator() throws Exception {
        UrlIterator i = new UrlIterator(api, "cluster?ecp=qgEYChYKEG9yZy5xcHl0aG9uLnFweTMQARgD&ds=1&n=21&c=3");
        Assert.assertTrue(i.hasNext());
        List<DocV2> response = i.next();
        Assert.assertEquals(21, response.size());
        DocV2 details = response.get(0);
        Assert.assertEquals("QPython - Python for Android", details.getTitle());

        Assert.assertTrue(i.hasNext());
    }

    @Test
    public void categoryAppIterator() throws Exception {
        CategoryAppsIterator i = new CategoryAppsIterator(api, "FINANCE", GooglePlayAPI.SUBCATEGORY.TOP_FREE);
        Assert.assertEquals("FINANCE", i.getCategoryId());
        Assert.assertTrue(i.hasNext());

        List<DocV2> response = i.next();
        Assert.assertEquals(20, response.size());
        DocV2 details = response.get(0);
        Assert.assertEquals("Сбербанк Онлайн", details.getTitle());

        Assert.assertTrue(i.hasNext());
        List<DocV2> response1 = i.next();
        Assert.assertEquals(20, response1.size());
        DocV2 details1 = response1.get(0);
        Assert.assertEquals("Денежные Переводы", details1.getTitle());
    }

    @Test
    public void emptyList() {
        UrlIterator i = new UrlIterator(api, "cluster?ecp=igMzChkKEzUzODM5MTMwMDQzMDM5MzUxNjIQCBgDEhQKDmNvbS50cnVlY2FsbGVyEAEYAxgB&ds=1");
        Assert.assertTrue(i.hasNext());
        List<DocV2> docs = i.next();
        Assert.assertTrue(docs.isEmpty());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void details() throws Exception {
        DetailsResponse response = api.details("com.cpuid.cpu_z");

        DocV2 details = response.getDocV2();
        Assert.assertEquals("CPU-Z", details.getTitle());
        Assert.assertEquals(1, details.getOffer(0).getOfferType());
        Assert.assertEquals(21, details.getDetails().getAppDetails().getVersionCode());
        Assert.assertEquals(3, details.getChildCount());
        Assert.assertTrue(details.getChild(0).getBackendDocid().startsWith("similar_apps"));
        Assert.assertEquals(21, details.getChild(0).getChildCount());
        Assert.assertTrue(details.getChild(1).getBackendDocid().startsWith("users_also_installed"));
        Assert.assertEquals(20, details.getChild(1).getChildCount());
        Assert.assertTrue(details.getChild(2).getBackendDocid().startsWith("users_also_installed"));
        Assert.assertEquals(20, details.getChild(2).getChildCount());
        Assert.assertEquals("AIDA64", details.getChild(2).getChild(0).getTitle());
        Assert.assertTrue(details.getChild(2).getChild(0).hasRelatedLinks());
        Assert.assertTrue(details.getChild(2).getChild(0).getRelatedLinks().hasCategoryInfo());
        Assert.assertTrue(details.getChild(2).getChild(0).getRelatedLinks().getCategoryInfo().hasAppCategory());
        Assert.assertEquals("TOOLS", details.getChild(2).getChild(0).getRelatedLinks().getCategoryInfo().getAppCategory());
        Assert.assertTrue(details.hasRelatedLinks());
        Assert.assertTrue(details.getRelatedLinks().hasCategoryInfo());
        Assert.assertTrue(details.getRelatedLinks().getCategoryInfo().hasAppCategory());
        Assert.assertEquals("TOOLS", details.getRelatedLinks().getCategoryInfo().getAppCategory());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("details", request.url().pathSegments().get(1));
        Assert.assertEquals(1, request.url().queryParameterNames().size());
        Assert.assertEquals("com.cpuid.cpu_z", request.url().queryParameter("doc"));
    }

    @Test
    public void bulkDetails() throws Exception {
        BulkDetailsResponse response = api.bulkDetails(Arrays.asList("com.cpuid.cpu_z", "org.torproject.android"));

        DocV2 details1 = response.getEntryList().get(0).getDoc();
        Assert.assertEquals("CPU-Z", details1.getTitle());
        DocV2 details2 = response.getEntryList().get(1).getDoc();
        Assert.assertEquals("Orbot: Proxy with Tor", details2.getTitle());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("bulkDetails", request.url().pathSegments().get(1));
        BulkDetailsRequest protoRequest = BulkDetailsRequest.parseFrom(MockOkHttpClientAdapter.getBodyBytes(request));
        Assert.assertEquals("com.cpuid.cpu_z", protoRequest.getDocid(0));
        Assert.assertEquals("org.torproject.android", protoRequest.getDocid(1));
    }

    @Test
    public void browse() throws Exception {
        BrowseResponse response = api.browse();
        BrowseLink link = response.getCategoryList().get(0);
        Assert.assertEquals("Android Wear", link.getName());
        Assert.assertEquals("homeV2?cat=ANDROID_WEAR&c=3", link.getDataUrl());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("browse", request.url().pathSegments().get(1));
        Assert.assertEquals(1, request.url().queryParameterNames().size());
        Assert.assertEquals("3", request.url().queryParameter("c"));
    }

    @Test
    public void purchase() throws Exception {
        BuyResponse response = api.purchase("com.cpuid.cpu_z", 21, 1);

        Assert.assertTrue(response.getPurchaseStatusResponse().hasAppDeliveryData());
        Assert.assertEquals("08242190784708202273", response.getPurchaseStatusResponse().getAppDeliveryData().getDownloadAuthCookie(0).getValue());
        Assert.assertEquals("https://android.clients.google.com/market/download/Download?packageName=com.cpuid.cpu_z&versionCode=21&ssl=1&token=AOTCm0Qk6Xp0vLAIMTlHfvRcskbmSGugu7zaMbPzul5Ieu2bHAxD5QqKW6whNXp6dO9cid8x171TOLlpMXxAuJZYKl6JbeMUCphFLIBprvX4MX8CV79EOzVAeNiyT0qUOOKulE-5O-YFOM4jd8qkAR5ratStfwTar_eDlbyLQRZObbxVVGUdICUBftnAk-E-gj-mfuClbF-i5dkO_H22KvMrzOLkbsGlCjkmfOxWZvzvtw_LTW-pxD4WWE5_UxrxD6ktnnYnppzWmri687Ju3zkNXLpljvE7Nvr3KQuoW9eye93Wl2hSJSHIghoHQI7H3ImrCk8-gA2Cb-MGGapRne35_SZmjwAS&cpn=tQUzGf5FX6Y4LL14", response.getPurchaseStatusResponse().getAppDeliveryData().getDownloadUrl());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("purchase", request.url().pathSegments().get(1));
        Assert.assertEquals(0, request.url().queryParameterNames().size());

        Map<String, String> vars = MockOkHttpClientAdapter.parseQueryString(MockOkHttpClientAdapter.getBodyBytes(request));
        Assert.assertEquals(3, vars.size());
        Assert.assertEquals("com.cpuid.cpu_z", vars.get("doc"));
        Assert.assertEquals("1", vars.get("ot"));
        Assert.assertEquals("21", vars.get("vc"));
    }

    @Test
    public void delivery() throws Exception {
        DeliveryResponse responseFailure = api.delivery("com.mojang.minecraftpe", 871000016, 1);
        Assert.assertFalse(responseFailure.hasAppDeliveryData());

        DeliveryResponse response = api.delivery("com.cpuid.cpu_z", 21, 1);

        Assert.assertTrue(response.hasAppDeliveryData());
        Assert.assertEquals("08242190784708202273", response.getAppDeliveryData().getDownloadAuthCookie(0).getValue());
        Assert.assertEquals("https://android.clients.google.com/market/download/Download?packageName=com.cpuid.cpu_z&versionCode=21&ssl=1&token=AOTCm0RyRkIlbN-jMwP_CI5aTdDMK3wRaOXhz21bIvkVFDm90TyXrbt1YatiLK2LY1zoEU5iJ_u-AYBLnuAr69TiPAbBDfB6JtJGOVhrc1E2r9UDf70WnmKA_8s024v6g2ZJHCDi485u00NyosPHnCsnIfLI1hZSwqoAQ05bURLy72vFLBznWk_mTnjTy1HybRbxvWV_F4_Lw89Jmdr_RGKISl4bGpE943hE23T3Dy_unQKlZ81Q4i8CIfLEg0Dpl0T4KPXg0t8fiA1k4d0Xo3Mqudcyi3BeBBHS-lq4lS-Nx8RF09O92YsOuuFAHhSrGG0_NWNjd9N-4XuzRRJWjVvL1RtjyxUZ&cpn=wiHoRz1NXwwvhNPR", response.getAppDeliveryData().getDownloadUrl());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(2, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("delivery", request.url().pathSegments().get(1));
        Assert.assertEquals(3, request.url().queryParameterNames().size());
        Assert.assertEquals("com.mojang.minecraftpe", request.url().queryParameterValues("doc").get(0));
        Assert.assertEquals("1", request.url().queryParameterValues("ot").get(0));
        Assert.assertEquals("871000016", request.url().queryParameterValues("vc").get(0));
    }

    @Test
    public void deliveryWithPatchInfo() throws Exception {
        DeliveryResponse response = api.delivery("com.dukei.android.apps.anybalance", 804, 805, 1, GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF);

        Assert.assertTrue(response.hasAppDeliveryData());
        Assert.assertEquals("42", response.getAppDeliveryData().getDownloadAuthCookie(0).getValue());
        Assert.assertEquals("https://android.clients.google.com/market/download/Download?packageName=com.dukei.android.apps.anybalance&versionCode=805&ssl=1&token=AOTCm0QUVF4TgFWbAaUj9aO2fERzAnyoM4wPM4a_zeRi5TLZ2Ysn0fCSAcp-zKLm_GcGL-E4ETR0XY0-68EtprtPgGbbE-x6kQpP-ZiGU0bkLx9MPF02DvieK9sKWW69Ng-iQRB-aBN_Rtd4k3Y2BP9wEn5gXecEWceT-yr947Mpjk_tmuAym7Gxi_JfNbRp2ZbjdWXd8zPnn_repPTbSbrkhLXjeby4AQ5HRpY3Nw3UYIcnEt_dyanunKxAT5dmRhI0J6ROrdzKIO78hLBg12UvS8RpAi3I8PcLwHz2ntAbEq5vp5K1DtiBUospxuAR8GMz9I-OIV-IcQFLj9n-KdqsmRh7lHqB6Tl8Y8UmGSvuTGf9OD18ao9PrJOJIHM&cpn=0U70ZSphBpghV6t_", response.getAppDeliveryData().getDownloadUrl());
        Assert.assertTrue(response.getAppDeliveryData().hasPatchData());
        Assert.assertEquals("https://android.clients.google.com/market/download/Download?packageName=com.dukei.android.apps.anybalance&versionCode=805&ssl=1&token=AOTCm0RIVVa_w16cspCodcRpxEK-UuwlLmYRILrc-R86Xsmr_T7YkDdavGBV29qYY-kq6BRS8Vx9LT7HmpDJbIu2P4DtIBwF4cI_6R-Cj2_g3oxP8QtmpeJZhu3-FbZohrOsHExwM8aGHpdK7OCyA67njkiFGOm_l06vmcOrIlb6PdzVjTa2tzZS4wWMlWS6LBXThNl4hcgmr8wZPjBHNVaQ1M6SXERrXHPUPAyrkScW0UWvf66_E--sA_cedXAfyDZlDS3uW85wHiFpCcM3rmyUHlpCvr39kd5kGTNNmk5OmTdiKQkY9xSQu3Qo8FEhi-y02nXCwZeG_Vu3MsaBbyiR7NHNv-2-5NEQJ4pHuZNfv4BdoCfY2pOIV1m4Vi2fH9v4&baseVersion=804&pf=2&cpn=oT-7iAHlEqbxJzyk", response.getAppDeliveryData().getPatchData().getDownloadUrl());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("delivery", request.url().pathSegments().get(1));
        Assert.assertEquals(5, request.url().queryParameterNames().size());
        Assert.assertEquals("com.dukei.android.apps.anybalance", request.url().queryParameterValues("doc").get(0));
        Assert.assertEquals("1", request.url().queryParameterValues("ot").get(0));
        Assert.assertEquals("805", request.url().queryParameterValues("vc").get(0));
        Assert.assertEquals("804", request.url().queryParameterValues("bvc").get(0));
        Assert.assertEquals("2", request.url().queryParameterValues("pf").get(0));
    }

    @Test
    public void reviews() throws Exception {
        ReviewResponse response = api.reviews("com.cpuid.cpu_z", GooglePlayAPI.REVIEW_SORT.HIGHRATING, 0, 20);

        Assert.assertTrue(response.getGetResponse().getReviewCount() > 0);
        Assert.assertEquals(5, response.getGetResponse().getReview(0).getStarRating());
        Assert.assertEquals("It is awesome app I install and it work success fully", response.getGetResponse().getReview(1).getComment());
        Assert.assertEquals("Nitesh Kumar", response.getGetResponse().getReview(0).getAuthor2().getName());
        Assert.assertEquals("https://plus.google.com/+NiteshKumar", response.getGetResponse().getReview(0).getAuthor2().getGooglePlusUrl());
        Assert.assertEquals("https://lh3.googleusercontent.com/-t-T8LKa60Fc/AAAAAAAAAAI/AAAAAAAAStk/wS6mDBhiWQA/photo.jpg", response.getGetResponse().getReview(0).getAuthor2().getUrls().getUrl());
        Assert.assertEquals("104245217570938637686", response.getGetResponse().getReview(0).getAuthor2().getPersonId());
        Assert.assertEquals("person-104245217570938637686", response.getGetResponse().getReview(0).getAuthor2().getPersonIdString());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("rev", request.url().pathSegments().get(1));
        Assert.assertEquals(5, request.url().queryParameterNames().size());
        Assert.assertEquals("com.cpuid.cpu_z", request.url().queryParameter("doc"));
        Assert.assertEquals("1", request.url().queryParameter("sort"));
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("0", request.url().queryParameter("o"));
        Assert.assertEquals("20", request.url().queryParameter("n"));
    }

    @Test
    public void addReview() throws Exception {
        ReviewResponse response = api.addOrEditReview("com.cpuid.cpu_z", "Работает!", "", 5);

        Assert.assertTrue(response.hasUserReview());
        Assert.assertEquals(5, response.getUserReview().getStarRating());
        Assert.assertEquals("Работает!", response.getUserReview().getComment());
        Assert.assertEquals("konstantin razdolbaev", response.getUserReview().getAuthor2().getName());
        Assert.assertEquals("", response.getUserReview().getAuthor2().getGooglePlusUrl());
        Assert.assertEquals("https://lh3.googleusercontent.com/-PkFVwXLKCKk/AAAAAAAAAAI/AAAAAAAAAAA/AKB_U8valX_uc0SKPSZEhVtxDUqYtRwIgQ/photo.jpg", response.getUserReview().getAuthor2().getUrls().getUrl());
        Assert.assertEquals("100687909122075437983", response.getUserReview().getAuthor2().getPersonId());
        Assert.assertEquals("person-100687909122075437983", response.getUserReview().getAuthor2().getPersonIdString());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("addReview", request.url().pathSegments().get(1));
        Assert.assertEquals(4, request.url().queryParameterNames().size());
        Assert.assertEquals("com.cpuid.cpu_z", request.url().queryParameter("doc"));
        Assert.assertEquals("Работает!", request.url().queryParameter("content"));
        Assert.assertEquals("", request.url().queryParameter("title"));
        Assert.assertEquals("5", request.url().queryParameter("rating"));
    }

    @Test
    public void recommendations() throws Exception {
        ListResponse response = api.recommendations("com.cpuid.cpu_z", GooglePlayAPI.RECOMMENDATION_TYPE.ALSO_VIEWED, 0, 20);

        Assert.assertTrue(response.getDocCount() > 0);
        Assert.assertEquals(20, response.getDoc(0).getChildCount());
        Assert.assertEquals("com.abs.cpu_z_advance", response.getDoc(0).getChild(0).getDetails().getAppDetails().getPackageName());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("rec", request.url().pathSegments().get(1));
        Assert.assertEquals(5, request.url().queryParameterNames().size());
        Assert.assertEquals("com.cpuid.cpu_z", request.url().queryParameter("doc"));
        Assert.assertEquals("1", request.url().queryParameter("rt"));
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("0", request.url().queryParameter("o"));
        Assert.assertEquals("20", request.url().queryParameter("n"));
    }

    @Test
    public void categoriesTop() throws Exception {
        BrowseResponse response = api.categories();

        Assert.assertTrue(response.hasCategoryContainer());
        Assert.assertEquals(35, response.getCategoryContainer().getCategoryCount());
        Assert.assertEquals("Games", response.getCategoryContainer().getCategory(14).getName());
        Assert.assertEquals("homeV2?cat=GAME&c=3", response.getCategoryContainer().getCategory(14).getDataUrl());
        Assert.assertTrue(response.getCategoryContainer().getCategory(14).hasUnknownCategoryContainer());
        Assert.assertTrue(response.getCategoryContainer().getCategory(14).getUnknownCategoryContainer().hasCategoryIdContainer());
        Assert.assertTrue(response.getCategoryContainer().getCategory(14).getUnknownCategoryContainer().getCategoryIdContainer().hasCategoryId());
        Assert.assertEquals("GAME", response.getCategoryContainer().getCategory(14).getUnknownCategoryContainer().getCategoryIdContainer().getCategoryId());
        Assert.assertTrue(response.getCategoryContainer().getCategory(14).hasIcon());
        Assert.assertEquals(5, response.getCategoryContainer().getCategory(14).getIcon().getImageType());
        Assert.assertEquals("http://lh3.ggpht.com/9B4h3oV3V976QI22pHX5CAZmpOjhtjxmJ85x234iVasadqm_lQjL4rebkIoHpDvv_qM09sXlH9UVyHvhmQ", response.getCategoryContainer().getCategory(14).getIcon().getImageUrl());
        Assert.assertEquals("#FF0F9D58", response.getCategoryContainer().getCategory(14).getIcon().getColor());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("categories", request.url().pathSegments().get(1));
        Assert.assertEquals(1, request.url().queryParameterNames().size());
        Assert.assertEquals("3", request.url().queryParameter("c"));
    }

    @Test
    public void categoriesSub() throws Exception {
        BrowseResponse response = api.categories("GAME");

        Assert.assertTrue(response.hasCategoryContainer());
        Assert.assertEquals(18, response.getCategoryContainer().getCategoryCount());
        Assert.assertEquals("Action", response.getCategoryContainer().getCategory(0).getName());
        Assert.assertEquals("homeV2?cat=GAME_ACTION&c=3", response.getCategoryContainer().getCategory(0).getDataUrl());
        Assert.assertTrue(response.getCategoryContainer().getCategory(0).hasUnknownCategoryContainer());
        Assert.assertTrue(response.getCategoryContainer().getCategory(0).getUnknownCategoryContainer().hasCategoryIdContainer());
        Assert.assertTrue(response.getCategoryContainer().getCategory(0).getUnknownCategoryContainer().getCategoryIdContainer().hasCategoryId());
        Assert.assertEquals("GAME_ACTION", response.getCategoryContainer().getCategory(0).getUnknownCategoryContainer().getCategoryIdContainer().getCategoryId());
        Assert.assertTrue(response.getCategoryContainer().getCategory(0).hasIcon());
        Assert.assertEquals(5, response.getCategoryContainer().getCategory(14).getIcon().getImageType());
        Assert.assertEquals("http://lh3.ggpht.com/k4nI1STlxTBgEuwyMNapY9U_vBh38h_nIvYtLVVIaT9PtgOIXfRZKccsGa2wK8gVebKX8S7xG72Bu-8kmmY", response.getCategoryContainer().getCategory(14).getIcon().getImageUrl());
        Assert.assertEquals("#FF0F9D58", response.getCategoryContainer().getCategory(14).getIcon().getColor());

        List<Request> requests = ((MockOkHttpClientAdapter) api.getClient()).getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("categories", request.url().pathSegments().get(1));
        Assert.assertEquals(2, request.url().queryParameterNames().size());
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("GAME", request.url().queryParameter("cat"));
    }

    @Test
    public void testingProgram() throws Exception {
        TestingProgramResponse response = api.testingProgram("com.paget96.lspeed", true);
        Assert.assertTrue(response.hasResult());
    }

    @Test
    public void purchaseWithLogging() throws Exception {
        String packageName = "dev.ukanth.ufirewall";
        int versionCode = 15972;

        BuyResponse buyResponse = api.purchase(packageName, versionCode, 1);
        Assert.assertTrue(buyResponse.getPurchaseStatusResponse().hasAppDeliveryData());
        Assert.assertFalse(buyResponse.getPurchaseStatusResponse().getAppDeliveryData().hasDownloadUrl());
        Assert.assertTrue(buyResponse.hasDownloadToken());
        String downloadToken = buyResponse.getDownloadToken();
        Assert.assertEquals("AB-xQnrt6aUnfcYyHBJkWrMV9AjydxLSMR7AV_sX0BITOa9F6gGnW5H6m0mw77fZENd8lYEYngefgjH1TZGujTCWD3v1WvOqW4k87_G9sFLXo1JJVp6r5gA", downloadToken);

        String response = api.log(packageName, 1504885461243000L);
        Assert.assertEquals(0, response.length());

        DeliveryResponse deliveryResponse = api.delivery(packageName, 0, versionCode, 1, GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF, downloadToken);
        Assert.assertTrue(deliveryResponse.hasAppDeliveryData());
        Assert.assertEquals("https://android.clients.google.com/market/download/Download?packageName=dev.ukanth.ufirewall&versionCode=15972&ssl=1&token=AOTCm0QpUo_yqGEHkwwiujJnCib9Z1GgZMJBYmIA2dWkEc_Gon0U6rKBXcyA_T-Z4DeKUwudpUxerc-t3M-8NxjB9OCajVOI6ipQH9E9yzmbbrk9_iDZ_ACl_dPU9YoD7-wO0dRHTltFojyDPZxeAvQusrS9ssRDhgtUBk3EPkGCW_W7O0-Xth_jG74OC-BPfc9UXhmTMBWgjv0qnTXhSvzDmweiZMLPb_4YfkYZzJJFEGTr4_txY3ExX3COpB5bwXcdSkBXQH2xILkrWcIquPg5KlPNmQwlaVNoseDez8YgujQ63AcImU3eRkwAvdsZiBANPv4BhzEMYVlQ6VYDCV3vTH7W5AGO2GpaIlIcmsiODbvVPg&did=0&cpn=Uno4ICxUZcNY314t", deliveryResponse.getAppDeliveryData().getDownloadUrl());
    }

    private GooglePlayAPI initApi() {
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("device-honami.properties"));
        } catch (IOException e) {
            System.out.println("device-honami.properties not found");
            return null;
        }

        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());
        deviceInfoProvider.setTimeToReport(1482626488L);

        GooglePlayAPI api = new MockGooglePlayAPI();
        api.setClient(new MockOkHttpClientAdapter());
        api.setLocale(Locale.US);
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setGsfId(GSFID);
        api.setToken(TOKEN);
        return api;
    }
}
