package com.crazyinfin8.bethany;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

/**
 * Commands extend the functionality of the Bethany bot. Create classes that
 * implement Command and add it to the bot using `addCommand` function of the
 * bot.
 * 
 * @author CrazyInfin8
 */
public interface Command {
    /**
     * This function is called when the command is being run.
     * 
     * @param bot    The Bethany bot instance.
     * @param jda    The underlying JDA user instance
     * @param msg    The original message received
     * @param params The parsed parameters extracted from the message
     */
    void run(Bot bot, JDA jda, Message msg, String... params);
}