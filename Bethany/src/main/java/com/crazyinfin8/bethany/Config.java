package com.crazyinfin8.bethany;

/**
 * Configurations for the Bethany Discord bot.
 * 
 * @author CrazyInfin8
 */
public class Config {
    public String token;
    public String prefix;
    public String admins[];

    /**
     * Creates the default config.
     * 
     * @param token Your Discord login token.
     */
    public Config(String token) {
        this.token = token;
        this.prefix = "$";
        admins = new String[] {};
    }

    /**
     * Creates configuration with other settings
     * 
     * @param token  Your Discord login token.
     * @param prefix String of characters that must precede a command (for example,
     *               in "$ping", the character "$" is the prefix)
     * @param admins (Not currently implemented yet) ID's of Discord users who
     *               should have access to commands with elevated permissions.
     */
    public Config(String token, String prefix, String... admins) {
        this.token = token;
        this.prefix = prefix;
        this.admins = admins.clone();
    }
}