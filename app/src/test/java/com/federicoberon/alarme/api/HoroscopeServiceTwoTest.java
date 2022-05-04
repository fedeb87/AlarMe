package com.federicoberon.alarme.api;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import com.google.common.base.Charsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@RunWith(RobolectricTestRunner.class)
public class HoroscopeServiceTwoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HoroscopeServiceTwo horoscopeServiceTwo;

    private MockWebServer mockWebServer;

    private Context application;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        application = ApplicationProvider.getApplicationContext();
        mockWebServer = new MockWebServer();
        horoscopeServiceTwo = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(HoroscopeServiceTwo.class);
    }

    @After
    public void stopService() {
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getHoroscopeTwo() {
        MockResponse mockResponse = new MockResponse();
        try {
            InputStream inputStream = application.getClassLoader().getResourceAsStream("api-response/horoscopeResponseTwo.xml");
            BufferedSource source = Okio.buffer(Okio.source(inputStream));
            mockResponse.setBody(source.readString(Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockWebServer.enqueue(mockResponse);

        TestObserver<HoroscopeTwo> testObserver = horoscopeServiceTwo.getHoroscope("aries").test();

        testObserver.assertComplete();
        testObserver.assertValue(horoscope ->
            horoscope.title.equals("Aries Horoscope May 4 2022") &&
            horoscope.link.equals("https://www.clickastro.com/aries-horoscope-daily")
        );
    }
}
