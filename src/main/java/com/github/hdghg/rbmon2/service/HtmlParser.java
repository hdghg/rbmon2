package com.github.hdghg.rbmon2.service;

import com.github.hdghg.rbmon2.model.RbEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HtmlParser {
    public List<RbEntry> parse(InputStream is) {
        try {
            var doc = Jsoup.parse(is, "ISO-8859-1", "http://example.com");
            Elements select = doc.getElementsByClass("content").select("tr");
            List<RbEntry> result = new ArrayList<>();
            for (Element element : select.subList(2, select.size())) {
                RbEntry rbEntry = new RbEntry();
                rbEntry.setLevel(Integer.parseInt(element.child(0).ownText()));
                rbEntry.setName(element.child(1).ownText());
                rbEntry.setAlive(5 == element.child(2).child(0).ownText().length());
                result.add(rbEntry);
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
