import java.util.Scanner;

public class Application {
    public void run() {
        try {
            System.out.println("Videobase Test App\n");

            VideoPlatform database = new VideoPlatform();
            database.connect();

            Command command = new Command(database);
            String input;
            Scanner scan = new Scanner(System.in);

            do {
                input = scan.nextLine();
                command.run(input);
                System.out.println();
            } while (!input.equalsIgnoreCase("exit"));

            database.disconnect();

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }
    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}
