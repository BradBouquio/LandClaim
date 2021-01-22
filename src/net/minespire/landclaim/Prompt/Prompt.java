package net.minespire.landclaim.Prompt;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minespire.landclaim.LandClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Prompt {

    private Player player;
    private int serviceTaskID;
    private String answer;
    private String promptMessage;
    private int ticksPassed = 1;
    private String promptType; //ADDMEMBER, ADDOWNER
    private ProtectedRegion region;
    private static Set<String> playersWithPrompts = new HashSet<>();
    private static Map<String,Prompt> playerPrompts = new HashMap<>();

    public Prompt(String message, Player player, String promptType, ProtectedRegion region){
        this.player = player;
        this.promptMessage = message;
        this.promptType = promptType;
        this.region = region;
    }

    private void awaitResponse(){

        answer = null;
        playersWithPrompts.add(player.getDisplayName());
        serviceTaskID = Bukkit.getScheduler().runTaskTimer(LandClaim.plugin, () -> {
            ticksPassed += 5;
            if(answer != null || ticksPassed > 100) {
                Bukkit.getScheduler().cancelTask(serviceTaskID);
            }

        }, 1L, 5L).getTaskId();
    }

    public static boolean hasActivePrompt(Player player){
        return playersWithPrompts.contains(player.getDisplayName());
    }

    public boolean sendPrompt(){
        if(hasActivePrompt(player)) return false;
        else {
            player.sendMessage(promptMessage);
            awaitResponse();
            savePrompt(this);
            return true;
        }
    }

    public void setAnswer(String answer){
        this.answer = answer;
    }

    public void savePrompt(Prompt prompt){
        playerPrompts.put(player.getDisplayName(), this);
    }

    public static Prompt getPrompt(String playerName){
        return playerPrompts.get(playerName);
    }

    public String getAnswer(){
        return answer;
    }

    public String getPromptType(){
        return promptType;
    }
    public ProtectedRegion getRegion(){
        return region;
    }
}