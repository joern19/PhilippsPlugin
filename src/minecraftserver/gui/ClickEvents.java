package minecraftserver.gui;

import org.bukkit.entity.Player;

public abstract class ClickEvents {    
    /**
     * gets called if it is a Rightclick or a Leftclick.
     * The Functions rightClick and leftClick are getting called too if it is a right or left click.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     * @param infos for example a Player who was selected by the Inventory before.
     */
    void click(Player p, Boolean shift, Object[] infos) {};
    /**
     * gets called if it is a RightClick.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     * @param infos for example a Player who was selected by the Inventory before.
     */
    void rightClick(Player p, Boolean shift, Object[] infos) {};
    /**
     * gets called if it is a Leftclick.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     * @param infos for example a Player who was selected by the Inventory before.
     */
    void leftClick(Player p, Boolean shift, Object[] infos) {};
    /**
     * It is also called if there is no info avaible..
     * gets called wen the Item is added to the Inventory.
     * Here you should set Information up to Date...
     * It is very Helpful if you dont want to delete and the create a Page again and again.
     */
    void onLoad(Player p, Object[] infos) {};
    
    /**
     * gets called if it is a Rightclick or a Leftclick.
     * The Functions rightClick and leftClick are getting called too if it is a right or left click.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     * @param infos for example a Player who was selected by the Inventory before.
     */
    void click(Player p, Boolean shift) {};
    /**
     * gets called if it is a RightClick.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     */
    void rightClick(Player p, Boolean shift) {};
    /**
     * gets called if it is a Leftclick.
     * @param p the Player who clicked.
     * @param shift if the Player pressed shift.
     */
    void leftClick(Player p, Boolean shift) {};
    /**
     * gets called wen the Item is added to the Inventory.
     * Here you should set Information up to Date...
     * It is very Helpful if you dont want to delete and than create a Page again and again.
     */
    void onLoad(Player p) {};
}
