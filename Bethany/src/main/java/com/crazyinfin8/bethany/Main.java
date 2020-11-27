package com.crazyinfin8.bethany;

import java.io.IOException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

/**
 *
 * @author CrazyInfin8
 */
public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        Bot bot = new Bot(new Config("<INSERT_TOKEN_HERE>"));
        bot.addCommand("ping", new Ping());
        bot.addCommand("poll", new Poll());
    }
}

class Ping implements Command {
    public void run(Bot bot, JDA jda, Message msg, String... params) {
        msg.getChannel().sendMessageFormat("pong!").queue();
    }
}

class Poll implements Command {
    private final String EMOTES[] = new String[] { ":regional_indicator_a:", ":regional_indicator_b:",
            ":regional_indicator_c:", ":regional_indicator_d:", ":regional_indicator_e:", ":regional_indicator_f:",
            ":regional_indicator_g:", ":regional_indicator_h:", ":regional_indicator_i:", ":regional_indicator_j:",
            ":regional_indicator_k:", ":regional_indicator_l:", ":regional_indicator_m:", ":regional_indicator_n:",
            ":regional_indicator_o:", ":regional_indicator_p:", ":regional_indicator_q:", ":regional_indicator_r:",
            ":regional_indicator_s:", ":regional_indicator_t:", ":regional_indicator_u:", ":regional_indicator_v:",
            ":regional_indicator_w:", ":regional_indicator_x:", ":regional_indicator_y:", ":regional_indicator_z:", };
    private final String EMOTE_CODES[] = new String[] { "U+1F1E6", "U+1F1E7", "U+1F1E8", "U+1F1E9", "U+1F1EA",
            "U+1F1EB", "U+1F1EC", "U+1F1ED", "U+1F1EE", "U+1F1EF", "U+1F1F0", "U+1F1F1", "U+1F1F2", "U+1F1F3",
            "U+1F1F4", "U+1F1F5", "U+1F1F6", "U+1F1F7", "U+1F1F8", "U+1F1F9", "U+1F1FA", "U+1F1FB", "U+1F1FC",
            "U+1F1FD", "U+1F1FE", "U+1F1FF", };

    public void run(Bot bot, JDA jda, Message msg, String... params) {
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(0xFF0000);
        emb.setAuthor(msg.getAuthor().getAsTag(), null, msg.getAuthor().getAvatarUrl());
        if (params.length < 3) {
            msg.getChannel()
                    .sendMessage("\"Poll\" command requires at least 3 parameters! (title and at least 2 options)")
                    .queue();
            return;
        }
        int count = 1;
        emb.appendDescription("**" + params[0] + "**\n");
        for (; count < params.length && count < EMOTES.length; ++count) {
            emb.appendDescription(EMOTES[count - 1] + " ` " + params[count] + " `\n");
        }
        final int optCount = count - 1;
        msg.getChannel().sendMessage(emb.build()).queue(newMsg -> {
            for (int i = 0; i < optCount; ++i) {
                newMsg.addReaction(EMOTE_CODES[i]).queue();
            }
            // TODO: Tally commands after a certain amount of time and update message to
            // display results.
        });
    }
}