package com.crazyinfin8.bethany;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

/**
 *
 * @author CrazyInfin8
 */
public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOKEN");
        if (token == null) {
            System.out.println("No token specified in \".env\" file.");
            return;
        }
        Bot bot = new Bot(new Config(token));
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
    private final String EMOTE_CODES[] = new String[] { "\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9",
            "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF",
            "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5",
            "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB",
            "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF", };

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
        emb.appendDescription("**New poll:**\n" + params[0] + "\n");
        for (; count < params.length && count < EMOTES.length; ++count) {
            emb.appendDescription(EMOTES[count - 1] + " ` " + params[count] + " `\n");
        }
        final int optCount = count - 1;
        Message newMsg = msg.getChannel().sendMessage(emb.build()).complete();
        for (int i = 0; i < optCount; ++i) {
            newMsg.addReaction(EMOTE_CODES[i]).complete();
        }
        new Timer().schedule(new Task(() -> {
            EmbedBuilder results = new EmbedBuilder();
            results.appendDescription("**Pole results:**\n");
            for (int i = 0; i < optCount; i++) {
                // TODO: Make this output look a bit more pretty!
                results.appendDescription(params[i + 1] + ": `"
                        + (newMsg.retrieveReactionUsers(EMOTE_CODES[i]).complete().size() - 1) + "`\n");
            }
            newMsg.clearReactions().complete();
            newMsg.editMessage(results.build()).complete();
        }), 5000);
    }
}

class Task extends TimerTask {
    Runnable fn;

    public Task(Runnable fn) {
        this.fn = fn;
    }

    public void run() {
        fn.run();
    }
}