package com.dyszlewskiR.edu.scientling.services.net;

import com.dyszlewskiR.edu.scientling.service.net.UriBuilder;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Razjelll on 19.04.2017.
 */

public class UriBuilderTest {

    private final String ROOT_REQUEST = "localhost:8080/Server/";

    @Test
    public void emptyRequestTest() {
        UriBuilder builder = new UriBuilder();
        String request = builder.getUri();
        Assert.assertEquals("", request);
    }

    @Test
    public void rootRequestTest(){
        UriBuilder builder = new UriBuilder(ROOT_REQUEST);
        String request = builder.getUri();
        Assert.assertEquals(ROOT_REQUEST, request);
    }

    @Test
    public void addSegmentRequestTest(){
        UriBuilder builder = new UriBuilder(ROOT_REQUEST);
        builder.addSegment("rest").addSegment("tests");
        String request = builder.getUri();
        String correctRequest = ROOT_REQUEST+"/rest/tests";
        Assert.assertEquals(correctRequest, request);
    }

    @Test
    public void addParamsRequestTest(){
        UriBuilder builder = new UriBuilder(ROOT_REQUEST);
        builder.addParam("name", "jasiek").tryAddParam("book","");
        String request = builder.getUri();
        String correctRequest = ROOT_REQUEST + "?name=jasiek";
        Assert.assertEquals(correctRequest, request);
    }

    @Test
    public void addEmptyParamsTest(){
        UriBuilder builder = new UriBuilder(ROOT_REQUEST);
        builder.tryAddParam("name", null).tryAddParam("book", null);
        String request = builder.getUri();
        String correctRequest = ROOT_REQUEST;
        Assert.assertEquals(correctRequest, request);
    }
}
