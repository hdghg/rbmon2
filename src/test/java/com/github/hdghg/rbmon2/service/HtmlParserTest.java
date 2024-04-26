package com.github.hdghg.rbmon2.service;

import com.github.hdghg.rbmon2.model.RbEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlParserTest {

    @Test
    void testParsing() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/testpage.html")) {
            List<RbEntry> parse = new HtmlParser().parse(is);
            assertTrue(parse.get(0).isAlive());
            assertFalse(parse.get(1).isAlive());
        }
    }

}