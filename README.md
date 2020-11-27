# Bethany
A Java Discord bot for a school project

## How to get started
1. Clone this director.y
2. Add your Discord login token to the file: `Bethany\src\main\java\com\crazyinfin8\bethany\Main.java` where it says `<INSERT_TOKEN_HERE>`.
3. You should be able to build the project and run after that!

## How to add commands
1. Create a class that implements the interface `Command`
2. Add the command to the bot using the `addCommand` function of class `Bot`