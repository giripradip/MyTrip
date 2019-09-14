package com.example.mytrip.interceptor;

import com.example.mytrip.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    private static final int SUCCESS_CODE = 200;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String responseString = getResponseString(request);

        if (BuildConfig.DEBUG) {
            return chain.proceed(request).newBuilder()
                    .code(SUCCESS_CODE)
                    .protocol(Protocol.HTTP_2)
                    .message("Sync successful")
                    .body(ResponseBody.create(responseString, MediaType.parse("application/json")))
                    .addHeader("content-type", "application/json")
                    .build();
        }
        return chain.proceed(request);
    }

    private String getResponseString(Request request) {

        String uri = request.url().uri().toString();
        if (uri.endsWith("all")) {

            return "[ \n" +
                    "   { \n" +
                    "      \"destinationAddress\":\"Kathmandu 44600, Nepal\",\n" +
                    "      \"destinationAddressId\":\"ChIJv6p7MIoZ6zkR6rGN8Rt8E7U\",\n" +
                    "      \"destinationAddressName\":\"Kathmandu\",\n" +
                    "      \"endDateTime\":1569778140,\n" +
                    "      \"id\":8,\n" +
                    "      \"startAddress\":\"Mumbai, Maharashtra, India\",\n" +
                    "      \"startAddressId\":\"ChIJwe1EZjDG5zsRaYxkjY_tpF0\",\n" +
                    "      \"startAddressName\":\"Mumbai\",\n" +
                    "      \"startDateTime\":1569605340\n" +
                    "   },\n" +
                    "   { \n" +
                    "      \"destinationAddress\":\"Murcia, Spain\",\n" +
                    "      \"destinationAddressId\":\"ChIJf4yS1fiBYw0RmqvEOJsSJ9Y\",\n" +
                    "      \"destinationAddressName\":\"Murcia\",\n" +
                    "      \"endDateTime\":1570556340,\n" +
                    "      \"id\":18,\n" +
                    "      \"startAddress\":\"Kathmandu 44600, Nepal\",\n" +
                    "      \"startAddressId\":\"ChIJv6p7MIoZ6zkR6rGN8Rt8E7U\",\n" +
                    "      \"startAddressName\":\"Kathmandu\",\n" +
                    "      \"startDateTime\":1568396340\n" +
                    "   },\n" +
                    "   { \n" +
                    "      \"destinationAddress\":\"Berlin, Germany\",\n" +
                    "      \"destinationAddressId\":\"ChIJAVkDPzdOqEcRcDteW0YgIQQ\",\n" +
                    "      \"destinationAddressName\":\"Berlin\",\n" +
                    "      \"endDateTime\":1569953400,\n" +
                    "      \"id\":26,\n" +
                    "      \"startAddress\":\"Koblenz, Germany\",\n" +
                    "      \"startAddressId\":\"ChIJnX0jdld7vkcRYLPaENXUIgQ\",\n" +
                    "      \"startAddressName\":\"Koblenz\",\n" +
                    "      \"startDateTime\":1568398200\n" +
                    "   },\n" +
                    "   { \n" +
                    "      \"destinationAddress\":\"Kharghar, Navi Mumbai, Maharashtra, India\",\n" +
                    "      \"destinationAddressId\":\"ChIJgwvjUxrC5zsR1wl3aJ-yZqc\",\n" +
                    "      \"destinationAddressName\":\"Kharghar\",\n" +
                    "      \"endDateTime\":1569953400,\n" +
                    "      \"id\":27,\n" +
                    "      \"startAddress\":\"De Haar 9, 9405 TE Assen, Netherlands\",\n" +
                    "      \"startAddressId\":\"ChIJvzUnT64kyEcR4tTUraR01UU\",\n" +
                    "      \"startAddressName\":\"TT Circuit Assen\",\n" +
                    "      \"startDateTime\":1569953400\n" +
                    "   }\n" +
                    "]";
        }


        return "{ \n" +
                "      \"destinationAddress\":\"Kathmandu 44600, Nepal\",\n" +
                "      \"destinationAddressId\":\"ChIJv6p7MIoZ6zkR6rGN8Rt8E7U\",\n" +
                "      \"destinationAddressName\":\"Kathmandu\",\n" +
                "      \"endDateTime\":1569778140,\n" +
                "      \"id\":8,\n" +
                "      \"startAddress\":\"Mumbai, Maharashtra, India\",\n" +
                "      \"startAddressId\":\"ChIJwe1EZjDG5zsRaYxkjY_tpF0\",\n" +
                "      \"startAddressName\":\"Mumbai\",\n" +
                "      \"startDateTime\":1569605340\n" +
                "   }";
    }
}
