package minecraftserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.bukkit.command.CommandSender;

public class Cmd {

    private String basedir;

    public Cmd(String basedir) {
        this.basedir = basedir;
    }

    public void exec(String command, CommandSender sender) throws InterruptedException, IOException {
        System.out.println("executing command: " + command);
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(
                    "cmd /c "
                    + command, null, new File(basedir));
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sender.sendMessage("OUTPUT:");
        printStream(p.getInputStream(), sender);
        sender.sendMessage("ERROR-OUTPUT:");
        printStream(p.getErrorStream(), sender);
    }

    public void printStream(InputStream stream, CommandSender sender) {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(stream));
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                sender.sendMessage(line);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
