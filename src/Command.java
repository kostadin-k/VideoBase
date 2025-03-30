import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;

public class Command {
    String[] arguments;
    VideoPlatform database;
    String[] help = {
            "Arguments: <> - required, [] - optional",
            "get <user|video> <ID>",
            "show <users|cameras|videos|deleted_users|user_overview|camera_usage>",
            "user <username|overview|videos|subscriptions> <user_ID>",
            "delete <user|video> <ID>",
            "upload <user_ID> <title> <length> [cam_manufacturer,cam_mod]",
            "register <username> <password>",
            "ban <user_ID> [reason]",
            "subscribe <user_ID> <creator_ID>",
            "unsubscribe <user_ID> <creator_ID>",
            "rename <user|video> <ID> <new_name>",
            "exit"
    };
    Command(VideoPlatform database) {
        this.database = database;
    }

    private void split(String command) {
        String[] buffer = command.trim().replaceAll("\\s+", " ").split("\"");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < buffer.length; i++) {
            String argument = buffer[i].trim();
            if (i % 2 == 1) {
                list.add(argument);
            } else {
                String[] unquoted = argument.split(" ");
                Collections.addAll(list, unquoted);
            }
        }
        arguments = list.toArray(new String[0]);
    }
    public void run(String command) {
        split(command);
        try {
            if(arguments.length == 0) throw new InputMismatchException("Invalid command");
            switch (arguments[0].toLowerCase()) {
                case "get": {
                    if(arguments.length >= 3) {
                        database.get(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[1] + "\n");
                }

                case "show": {
                    if(arguments.length >= 2) {
                        database.show(arguments[1]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[2] + "\n");
                }

                case "user": {
                    if(arguments.length >= 3) {
                        database.user(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[3] + "\n");
                }

                case "delete": {
                    if(arguments.length >= 3) {
                        database.delete(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[4] + "\n");
                }

                case "upload": {
                    if(arguments.length == 4) {
                        database.upload(arguments[1],arguments[2],arguments[3]);
                        break;
                    } else if (arguments.length >= 6) {
                        database.upload(arguments[1],arguments[2],arguments[3],arguments[4],arguments[5]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[5] + "\n");
                }

                case "register": {
                    if(arguments.length >= 3) {
                        database.register(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[6] + "\n");
                }

                case "ban": {
                    if(arguments.length == 2) {
                        database.ban(arguments[1]);
                        break;
                    } else if(arguments.length >= 3) {
                        database.ban(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[7] + "\n");
                }

                case "subscribe": {
                    if(arguments.length >= 3) {
                        database.subscribe(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[8] + "\n");
                }

                case "unsubscribe": {
                    if(arguments.length >= 3) {
                        database.unsubscribe(arguments[1],arguments[2]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[9] + "\n");
                }

                case "rename": {
                    if(arguments.length >= 4) {
                        database.rename(arguments[1],arguments[2],arguments[3]);
                        break;
                    }
                    throw new InputMismatchException("Invalid arguments\n" +
                            "Usage: " + help[10] + "\n");
                }

                case "help": {
                    for (String row: help) {
                        System.out.println(row);
                    }
                    break;
                }

                case "exit": {
                    System.out.println("Exiting program...");
                    break;
                }

                default:
                    throw new InputMismatchException("Unknown command\n");
            }
        } catch (InputMismatchException | SQLException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
