package com.trading.depthcharts.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.trading.depthcharts.model.Player;

/**
 * A service class responsible for managing the depth chart of a sports team.
 * * <p>This manager provides functionality to add players to specific positions,
 * gracefully handle depth reassignments, remove active players, and query backups.
 * * <p><b>Architecture Note:</b> 
 * State is maintained in-memory using a {@link java.util.LinkedHashMap} to preserve 
 * the insertion order of positions for deterministic console output. 
 * * <p><b>Thread Safety:</b>
 * This current implementation relies on standard collections and is not thread-safe. 
 * If accessed concurrently by multiple threads, external synchronization is required.
 *
 * @author pajithan
 */

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
        Objects.requireNonNull(position, "Position cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");
        
        List<Player> playersList = depthChart.computeIfAbsent(position, k -> new ArrayList<>());
        
        // if player is already present, remove them first to prevent a duplicate entry
        playersList.remove(player);

        if (positionDepth == null || positionDepth < 0 || positionDepth > playersList.size()){
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
        Objects.requireNonNull(position, "Position cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");

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

    /**
     * Retrieves a list of backup players for a specific player at a given position.
     * A backup is defined as any player with a lower position depth (i.e., listed after 
     * the specified player in the depth chart).
     * @param position The position to query (e.g., "QB", "LWR").
     * @param player   The Player object for whom backups are being requested.
     * @return A safely encapsulated List of backup Players. 
     * Returns an empty List if the position does not exist, the player is not 
     * listed at that position, or the player has no backups.
     */
    public List<Player> getBackups(String position, Player player){
        Objects.requireNonNull(position, "Position cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");

        List<Player> playersList = depthChart.get(position);
        if (playersList == null || playersList.isEmpty()){
            return Collections.emptyList();
        }

        int playerIndex = playersList.indexOf(player);
        if (playerIndex >= 0){
            return new ArrayList<>(playersList.subList(playerIndex+1, playersList.size()));
        }

        return Collections.emptyList();
    }

    /**
     * Prints the full depth chart to the console.
     * Iterates through all tracked positions and prints the active players 
     * in their correct depth order. Positions are printed in the order 
     * they were initially added to the system.
     * Positions with no active players are skipped to maintain clean output.
     */
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
