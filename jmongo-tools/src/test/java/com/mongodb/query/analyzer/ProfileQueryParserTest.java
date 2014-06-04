package com.mongodb.query.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;

public class ProfileQueryParserTest {

    @Test
    public void testIn() throws JsonParseException, IOException {
        ProfileQueryParser p = new ProfileQueryParser();
        String[] keys = p.parse("{ \"op\" : \"command\", \"ns\" : \"hmda.$cmd\", \"command\" : { \"count\" : \"hmda_lar\", \"query\" : { \"$and\" : [ { \"da\" : 2012 }, { \"eb\" : { \"$in\" : [ 1, 2 ] } }, { \"a14\" : 1 }, { \"fb\" : 1 }, { \"25\" : 1 } ] } }, \"ntoreturn\" : 1, \"keyUpdates\" : 0, \"numYield\" : 316, \"lockStats\" : { \"timeLockedMicros\" : { \"r\" : 31757054, \"w\" : 0 }, \"timeAcquiringMicros\" : { \"r\" : 88281, \"w\" : 212 } }, \"responseLength\" : 108, \"millis\" : 16441, \"execStats\" : {  }, \"ts\" : \"2014-05-16T13:48:14.572Z\", \"client\" : \"10.153.91.183\", \"allUsers\" : [ { \"user\" : \"__system\", \"db\" : \"local\" } ], \"user\" : \"__system@local\" }");
        System.out.println(Arrays.asList(keys));
    }

}
