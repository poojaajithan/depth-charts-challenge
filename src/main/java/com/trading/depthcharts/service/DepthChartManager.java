package com.trading.depthcharts.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.trading.depthcharts.model.Player;

public class DepthChartManager {
    private final Map<String, List<Player>> depthChart;

    public DepthChartManager() {
        this.depthChart = new LinkedHashMap<>();
    }
    
    /**
     * Adds a player to the depth chart at a given position.
     * @param position      The position (e.g., "QB", "LWR")
     * @param player        The Player object
     * @param positionDepth The 0-indexed depth. If null, appends to the end of the chart.
     */

    public void addPlayerToDepthChart(String position, Player player, Integer positionDepth) {
        List<Player> playersList = depthChart.computeIfAbsent(position, k -> new ArrayList<>());
        
        // if player is already present, remove them first to prevent a duplicate entry
        playersList.remove(player);

        if (positionDepth == null || positionDepth < 0 || playersList.size()<positionDepth){
            // if depth is invalid, rule is to append player at end
            playersList.add(player);
        }
        else{
            playersList.add(positionDepth, player);
        }
    }

    /**
     * Removes a specified player from the depth chart for a given position.
     * @param position The position from which the player should be removed (e.g., "LWR", "QB").
     * @param player   The Player object to be removed. Equality is determined by the player's unique number.
     * @return A List containing the removed Player instance if successful. 
     * Returns an empty List if the position does not exist, or if the player is not found at that position.
     */
    public List<Player> removePlayerFromDepthChart(String position, Player player){
        List<Player> playersList = depthChart.get(position);

        if (playersList == null || playersList.isEmpty()){
            return Collections.emptyList();
        }
        
        int removePlayerIndex = playersList.indexOf(player);
        if (removePlayerIndex >= 0){
            return List.of(playersList.remove(removePlayerIndex));
        }
        
        return Collections.emptyList();
    }

    public List<Player> getBackups(String position, Player player){
        List<Player> playersList = depthChart.get(position);

        if (playersList == null || playersList.isEmpty()){
            return Collections.emptyList();
        }

        int playerIndex = playersList.indexOf(player);
        if (playerIndex >= 0 && playersList.size()>1){
            return new ArrayList<>(playersList.subList(playerIndex+1, playersList.size()));
        }

        return Collections.emptyList();
    }

    public void getFullDepthChart(){
        for(Map.Entry<String, List<Player>> entry : depthChart.entrySet()){
            String position = entry.getKey();
            List<Player> players = entry.getValue();

            //if position is empty, skip printing it
            if (players.isEmpty()){
                continue;
            }

            String formattedPlayersOutput = players.stream()
                                                    .map(Player::toString)
                                                    .collect(Collectors.joining(", "));
                                                    
            System.out.println(position + " - " + formattedPlayersOutput);
        }
    }
}
