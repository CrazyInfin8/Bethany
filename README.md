> Bethany is not maintained. There are currently security issues with it's hjson dependancy, however this project was simply a class project and served its purpose.
# Bethany
A Java Discord bot for a school project

## How to get started
1. Clone this directory
2. Create a file called `.env` at the root of this directory
3. In the `.env` file, add `TOKEN=<YOUR_TOKEN_HERE>` replacing the `<YOUR_TOKEN_HERE>` with your actual token.
4. You should be able to build the project and run after that!

## How to add commands
1. Create a class that implements the interface `Command`
2. Add the command to the bot using the `addCommand` function of class `Bot`
