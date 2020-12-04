package com.crazyinfin8.bethany;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.security.auth.login.LoginException;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.ParseException;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author CrazyInfin8
 */
public class Main {
    static final int COLOR = 0xABCDEF;

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
        bot.addCommand("raffle", new Raffle());
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

class StringTools {
    public static String matchLength(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length - 3) + "...";
        } else {
            return StringTools.lpad(text, ' ', length);
        }
    }

    public static String lpad(String text, char pad, int length) {
        StringBuilder sb = new StringBuilder(length);
        sb.append(text);
        length -= text.length();
        if (length > 0)
            sb.append(StringTools.times(pad, length));
        return sb.toString();
    }

    public static String times(char c, int length) {
        StringBuilder sb = new StringBuilder(length);
        while (--length >= 0)
            sb.append(c);
        return sb.toString();
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
        EmbedBuilder emb = new EmbedBuilder().setColor(Main.COLOR).setAuthor(msg.getAuthor().getAsTag(), null,
                msg.getAuthor().getAvatarUrl());
        if (params.length < 3) {
            msg.getChannel()
                    .sendMessage(
                            "\"Poll\" command requires at least 3 parameters! (title/options and at least 2 options)")
                    .queue();
            return;
        }
        int count = 1;
        String tempTitle = params[0];
        long time = 5000;
        {
            try {
                JsonObject obj;
                obj = JsonValue.readHjson(params[0]).asObject();
                tempTitle = obj.getString("title", params[0]);
                time = Math.abs(obj.getLong("time", 5000));
            } catch (ParseException e) {
                System.out.println("Parameter is not hjson");
            }
        }
        String title = tempTitle;
        emb.appendDescription("**New poll:**\n" + title + "\n");
        int tempMaxOptLen = 0;
        for (; count < params.length && count < EMOTES.length; ++count) {
            emb.appendDescription(EMOTES[count - 1] + " ` " + params[count] + " `\n");
            tempMaxOptLen = params[count].length() > tempMaxOptLen ? params[count].length() : tempMaxOptLen;
        }
        int maxOptLen = tempMaxOptLen > 20 ? 20 : tempMaxOptLen;
        emb.setFooter("React with the according letter to vote");
        final int optCount = count - 1;
        Message newMsg = msg.getChannel().sendMessage(emb.build()).complete();
        for (int i = 0; i < optCount; ++i) {
            newMsg.addReaction(EMOTE_CODES[i]).complete();
        }
        new Timer().schedule(new Task(() -> {
            EmbedBuilder results = new EmbedBuilder().setColor(Main.COLOR)
                    .setAuthor(msg.getAuthor().getAsTag(), null, msg.getAuthor().getAvatarUrl())
                    .appendDescription("**Pole results:**\n```markdown\n# " + title + "\n");
            double tally[] = new double[optCount];
            double sum = 0;
            for (int i = 0; i < optCount; i++) {
                tally[i] = newMsg.retrieveReactionUsers(EMOTE_CODES[i]).complete().size() - 1;
                sum += tally[i];
            }
            if (sum == 0)
                sum = 1;
            for (int i = 0; i < tally.length; i++) {

                int tallyCount = (int) (tally[i] / sum * 20);

                System.out.println(tally[i] / sum);
                results.appendDescription(String.format("[%s][%s%s]%3.0f%% %.0f\n",
                        StringTools.matchLength(params[i + 1], maxOptLen), StringTools.times('#', tallyCount),
                        StringTools.times(' ', 20 - tallyCount), tally[i] / sum * 100, tally[i]));
            }
            results.appendDescription("```");
            newMsg.clearReactions().complete();
            newMsg.editMessage(results.build()).complete();
        }), time);
    }
}

class Raffle implements Command {
    public void run(Bot bot, JDA jda, Message msg, String... params) {
        if (params.length < 1) {
            msg.getChannel().sendMessage("\"Raffle\" command requires 1 parameters! (title/options)").complete();
            return;
        }
        long time = 5000;
        String tempTitle = params[0];
        int count = 1;
        try {
            JsonObject obj;
            obj = JsonValue.readHjson(params[0]).asObject();
            tempTitle = obj.getString("title", params[0]);
            time = Math.abs(obj.getLong("time", 5000));
            count = Math.abs(obj.getInt("count", 1));
        } catch (Exception e) {
            System.out.println("Parameter is not hjson");
        }
        if (count < 1) {
            msg.getChannel().sendMessage("\"count\" cannot be less than 1").complete();
        }
        String title = tempTitle;
        EmbedBuilder emb = new EmbedBuilder().setColor(Main.COLOR)
                .setAuthor(msg.getAuthor().getAsTag(), null, msg.getAuthor().getAvatarUrl())
                .appendDescription("**New raffle:**\n```markdown\n# " + title + "```")
                .setFooter("react with \u2705 to participate");
        Message newMsg = msg.getChannel().sendMessage(emb.build()).complete();
        newMsg.addReaction("\u2705").complete();
        int winners = count;
        new Timer().schedule(new Task(() -> {
            EmbedBuilder results = new EmbedBuilder().setColor(Main.COLOR)
                    .setAuthor(msg.getAuthor().getAsTag(), null, msg.getAuthor().getAvatarUrl())
                    .appendDescription("**Raffle results:**```markdown\n# " + title + "```");
            List<User> tally = newMsg.retrieveReactionUsers("\u2705").complete();
            newMsg.clearReactions().complete();
            tally.removeIf((user) -> {
                return user.getId().equals(jda.getSelfUser().getId());
            });
            for (int i = 0; i < winners; i++) {
                if (tally.isEmpty()) {
                    results.setFooter("Thats all the people!!");
                    break;
                }
                int j = (int) (Math.random() * tally.size());
                User user = tally.remove(j);
                results.appendDescription("**" + (i + 1) + ":** " + user.getAsMention() + "\n");
            }
            System.out.println(tally.size());
            System.out.println(Arrays.toString(tally.toArray()));
            newMsg.editMessage(results.build()).complete();
        }), time);
    }
}
