package com.github.hdghg.rbmon2.service;

import com.github.hdghg.rbmon2.service.jdalistener.BonusCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

@Service
public class JdaService {

    private final JDA jda;
    private final long channelId;

    public JdaService(
            @Value("${discord.bot.token}") String botToken,
            @Value("${discord.channel.url}") String channelUrl,
            BonusCommandListener bonusCommandListener) throws LoginException {
        this.jda = JDABuilder.createDefault(botToken)
                .enableIntents(Arrays.asList(
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING
                ))
                .addEventListeners(bonusCommandListener)
                .build();
        jda.updateCommands()
                .addCommands(
                        new CommandData("reg-bonus", "Добавить персонажа в бонусы")
                                .addOptions(new OptionData(OptionType.STRING, "character", "Имя персонажа", true),
                                        new OptionData(OptionType.STRING, "party", "Имя пати")),
                        new CommandData("dereg-bonus", "Удалить персонажа из мониторинга бонусов")
                                .addOptions(new OptionData(OptionType.STRING, "character", "Имя персонажа", true)),
                        new CommandData("bonus", "Показать состояние бонусов")
                                .addOptions(new OptionData(OptionType.STRING, "party", "Имя пати"))
                )
                .queue();
        this.channelId = Long.parseLong(StringUtils.substringAfterLast(channelUrl, "/"));
    }

    public void sendMessage(String text) {
        jda.getTextChannelById(channelId).sendMessage(text).queue();
    }

}
