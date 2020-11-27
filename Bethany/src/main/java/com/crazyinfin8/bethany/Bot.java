package com.crazyinfin8.bethany;

import java.util.ArrayList;
import java.util.HashMap;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The Bethany Discord bot.
 * 
 * @author CrazyInfin8
 */
public class Bot {

    JDA bot;
    String prefix;
    String admins[];
    HashMap<String, Command> commandList;
    HashMap<String, String> trackEmoji;
    Listener listener;

    public Bot(Config cfg) throws LoginException {
        bot = JDABuilder.createDefault(cfg.token).build();
        prefix = cfg.prefix;
        admins = cfg.admins.clone();
        commandList = new HashMap<String, Command>();
        listener = new Listener(bot);
        bot.addEventListener(listener);
    }

    /**
     * Adds a command to this bot instance.
     * 
     * @param name String to call this command in Discord (Should be lowercase and
     *             should not contain spaces)
     * @param cmd  An instance of a class implementing "Command"
     */
    public void addCommand(String name, Command cmd) {
        commandList.put(name, cmd);
    }

    class Listener extends ListenerAdapter {
        JDA jda;
        Bot bot;

        Listener(JDA bot) {
            this.jda = bot;
        }

        @Override
        public void onMessageReceived(MessageReceivedEvent evt) {
            // do not process any messages that come from this bot
            if (evt.getAuthor() != jda.getSelfUser()) {
                Message msg = evt.getMessage();
                String text = msg.getContentRaw();
                System.out.println(text);
                if (text.startsWith(prefix)) {
                    int start = prefix.length();
                    // trim whitespace before command
                    while (Character.isWhitespace(text.charAt(start))) {
                        ++start;
                        if (start >= text.length()) {
                            return;
                        }
                    }
                    int end = start;
                    // parse command
                    while (true) {
                        ++end;
                        if (end >= text.length()) {
                            break;
                        }
                        char c = text.charAt(end);
                        if (Character.isWhitespace(c)) {
                            break;
                        }
                    }
                    String command = text.substring(start, end);
                    Command cmd = commandList.get(command.toLowerCase());
                    // if we don't have a matching command, then we are done!
                    if (cmd != null) {
                        System.out.printf("Found command: \"%s\"\n", command);
                        ArrayList<String> params = new ArrayList<String>();
                        try {
                            // parsing parameters to make creating commands easier
                            start = end;
                            while (end < text.length()) {
                                while (Character.isWhitespace(text.charAt(start))) {
                                    ++start;
                                    if (start >= text.length()) {
                                        break;
                                    }
                                }
                                char c = text.charAt(start);
                                // parameters are space separated unless they start with a quotation mark
                                if (c == '\'' || c == '"' || c == '“' || c == '‘') {
                                    end = ++start;

                                    char endC;
                                    switch (c) {
                                        case '“':
                                            endC = '”';
                                            break;
                                        case '‘':
                                            endC = '’';
                                            break;
                                        default:
                                            endC = c;
                                    }
                                    while (true) {
                                        ++end;
                                        if (end >= text.length()) {
                                            break;
                                        }
                                        c = text.charAt(end);
                                        if (c == endC)
                                            break;
                                    }
                                } else {
                                    end = start;
                                    while (true) {
                                        ++end;
                                        if (end >= text.length()) {
                                            break;
                                        }
                                        c = text.charAt(end);
                                        if (Character.isWhitespace(c)) {
                                            break;
                                        }
                                    }
                                }
                                params.add(text.substring(start, end));
                                start = ++end;
                                if (start >= text.length()) {
                                    break;
                                }
                            }
                            String arr[] = new String[params.size()];
                            params.toArray(arr);
                            cmd.run(bot, jda, msg, arr);
                        } catch (Exception e) {
                            System.err.print(e);
                        }
                    } else {
                        System.out.printf("No command: \"%s\"\n", command);
                    }

                }
            }
        }

        @Override
        public void onEmoteAdded(EmoteAddedEvent evt) {
            // TODO: process emotes
        }

        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent evt) {
            // TODO: process member joining
        }

        @Override
        public void onReady(ReadyEvent event) {
            System.out.println("Logged in as: " + jda.getSelfUser());
        }
    }
}