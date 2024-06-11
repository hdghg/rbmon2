package com.github.hdghg.rbmon2.service;

import com.github.hdghg.rbmon2.model.RbEntry;
import com.github.hdghg.rbmon2.model.Transition;
import com.github.hdghg.rbmon2.repository.RbInfoRepository;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledWorker {
    private static final Logger log = LoggerFactory.getLogger(ScheduledWorker.class);

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private RbInfoRepository rbInfoRepository;

    @Autowired
    private JdaService jdaService;

    private final OkHttpClient client;

    public ScheduledWorker(HtmlParser htmlParser, TransitionService transitionService, RestTemplateBuilder restTemplateBuilder) {
        this.htmlParser = htmlParser;
        this.transitionService = transitionService;
        client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(15))
                .readTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Scheduled(fixedDelayString = "${interval.check-rb}", timeUnit = TimeUnit.SECONDS, initialDelay = 15)
    @Transactional
    public void checkRb() {
        log.info("Checking rb status...");

        List<Transition> currentStatus = transitionService.current();
        Map<String, Transition> statusByName = new HashMap<>();
        MapUtils.populateMap(statusByName, currentStatus, Transition::getName);
        Request request = new Request.Builder()
                .url("http://l2c4.ru/index.php?x=boss")
                .build();

        Call call = client.newCall(request);
        byte[] bytes;
        try (Response response = call.execute(); ResponseBody body = response.body()) {
            if (body != null) {
                bytes = body.bytes();
            } else {
                log.warn("Call to web page resulted with empty body");
                return;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        List<RbEntry> newStatus = htmlParser.parse(new ByteArrayInputStream(bytes));
        Map<String, String> correctNames = rbInfoRepository.correctNames();

        for (RbEntry entry : newStatus) {
            Transition previousStatus = statusByName.get(entry.getName());
            if (previousStatus == null) {
                transitionService.toAliveStatus(entry.getName(), entry.isAlive(), Instant.EPOCH);
                continue;
            }
            if (previousStatus.isAlive() && !entry.isAlive()) {
                log.info("[dead] RB ({}) {} died!", entry.getLevel(), correctNames.getOrDefault(entry.getName(), entry.getName()));
                transitionService.toAliveStatus(entry.getName(), entry.isAlive());
                if ("Raid Boss Von Helman".equals(entry.getName())) {
                    continue;
                }
                String disMsg = "\uD83D\uDD34 РБ (" + entry.getLevel() + ") "
                        + correctNames.getOrDefault(entry.getName(), entry.getName()) + " умер!";
                jdaService.sendMessage(disMsg);
            }

            if (!previousStatus.isAlive() && entry.isAlive()) {
                log.info("[live] RB ({}) {} alive!", entry.getLevel(), correctNames.getOrDefault(entry.getName(), entry.getName()));
                transitionService.toAliveStatus(entry.getName(), entry.isAlive());
                if ("Raid Boss Von Helman".equals(entry.getName())) {
                    continue;
                }
                String disMsg = "\uD83D\uDFE2 РБ (" + entry.getLevel() + ") "
                        + correctNames.getOrDefault(entry.getName(), entry.getName()) + " воскрес!";
                jdaService.sendMessage(disMsg);
            }
        }
    }
}