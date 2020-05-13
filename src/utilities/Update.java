package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraftserver.PhilippsPlugin;
import org.bukkit.Bukkit;

public class Update {

    PhilippsPlugin philippsPlugin = null;

    public Update(PhilippsPlugin philippsPlugin) {
        this.philippsPlugin = philippsPlugin;
    }

    private void download(String url) throws IOException {
        String pluginFolder = philippsPlugin.getServer().getWorldContainer().toPath().toAbsolutePath().toString();
        Bukkit.getLogger().log(Level.INFO, pluginFolder);
        pluginFolder += "\\plugins\\update\\";//create folder if not exists
        File updateFolder = new File(pluginFolder);
        if (!updateFolder.exists()) {
            updateFolder.mkdir();
        }
        Bukkit.getLogger().log(Level.INFO, pluginFolder);
        
        String pluginName;
        try {
            pluginName = Paths.get(Bukkit.getPluginManager().getPlugin(philippsPlugin.getName()).getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().getName();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Bukkit.getLogger().log(Level.INFO, (pluginFolder + pluginName));
        
        URL download = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(download.openStream());
        FileOutputStream fileOut = new FileOutputStream(pluginFolder + pluginName);
        fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
        fileOut.flush();
        fileOut.close();
        rbc.close();
        philippsPlugin.getLogger().log(Level.INFO, "Downloaded");
    }

    public void update(String url, Runnable r) {
        Thread t = new Thread(() -> {
            try {
                download(url);
                Thread.sleep(2000);
                r.run();
            } catch (IOException ex) {
                Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }
}
