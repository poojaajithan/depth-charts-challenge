package com.trading.depthcharts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trading.depthcharts.model.Player;
import com.trading.depthcharts.service.DepthChartManager;

public class DepthChartManagerTest {
    
    private DepthChartManager manager;
    private Player tomBrady;
    private Player blaineGabbert;
    private Player kyleTrask;
    private Player mikeEvans;

    @BeforeEach
    void setUp() {
        manager = new DepthChartManager();
        tomBrady = new Player(12, "Tom Brady");
        blaineGabbert = new Player(11, "Blaine Gabbert");
        kyleTrask = new Player(2, "Kyle Trask");
        mikeEvans = new Player(13, "Mike Evans");
    }

    @Test
    void testAddPlayer_ShiftExistingPlayersDown() {
        manager.addPlayerToDepthChart("QB", tomBrady, 0);
        manager.addPlayerToDepthChart("QB", kyleTrask, 1); 

        //insert Blaine Gabbert at index 1 (this should push Kyle Trask down)
        manager.addPlayerToDepthChart("QB", blaineGabbert, 1);

        List<Player> backups = manager.getBackups("QB", tomBrady);
        assertEquals(2, backups.size());
        assertEquals(blaineGabbert, backups.get(0)); 
        assertEquals(kyleTrask, backups.get(1));
    }

    @Test
    void testAddPlayer_NullOrOutOfBoundsDepth_AppendToEnd() {
        manager.addPlayerToDepthChart("QB", tomBrady, null);
        manager.addPlayerToDepthChart("QB", blaineGabbert, 99); 

        List<Player> backups = manager.getBackups("QB", tomBrady);
        assertEquals(1, backups.size());
        assertEquals(blaineGabbert, backups.get(0));
    }

    @Test
    void testRemovePlayer_ReturnListWithPlayer_RemoveFromChart() {
        manager.addPlayerToDepthChart("LWR", mikeEvans, 0);

        List<Player> removed = manager.removePlayerFromDepthChart("LWR", mikeEvans);

        assertEquals(1, removed.size());
        assertEquals(mikeEvans, removed.get(0));
        
        // verify no backups are present
        assertTrue(manager.getBackups("LWR", mikeEvans).isEmpty());
    }

    @Test
    void testRemovePlayer_NotFound_ReturnsEmptyList() {
        List<Player> removed = manager.removePlayerFromDepthChart("QB", tomBrady);
        assertTrue(removed.isEmpty(), "Removing a non-existent player should return an empty list.");
    }

    @Test
    void testGetBackups_ReturnCorrectSublist() {
        manager.addPlayerToDepthChart("QB", tomBrady, 0);
        manager.addPlayerToDepthChart("QB", blaineGabbert, 1);
        manager.addPlayerToDepthChart("QB", kyleTrask, 2);

        List<Player> backups = manager.getBackups("QB", blaineGabbert);
        assertEquals(1, backups.size());
        assertEquals(kyleTrask, backups.get(0));
    }

    @Test
    void testGetBackups_NoBackup_ReturnEmptyList() {
        manager.addPlayerToDepthChart("QB", tomBrady, 0);

        List<Player> backups = manager.getBackups("QB", tomBrady);
        assertTrue(backups.isEmpty(), "Player at the end of the chart should return an empty backups list.");
    }

    @Test
    void testGetBackups_EncapsulationSecurity() {
        manager.addPlayerToDepthChart("QB", tomBrady, 0);
        manager.addPlayerToDepthChart("QB", blaineGabbert, 1);

        // get backups and try to clear the returned list
        List<Player> backups = manager.getBackups("QB", tomBrady);
        backups.clear(); 

        // internal depth chart should remain completely unaffected
        List<Player> secureBackups = manager.getBackups("QB", tomBrady);
        assertFalse(secureBackups.isEmpty(), "Internal state leaked! Modifying the returned list destroyed internal data.");
        assertEquals(1, secureBackups.size());
    }

    @Test
    void testGetFullDepthChart_PrintsCorrectFormatAndSkipsEmpty() {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            // set up the depth chart
            manager.addPlayerToDepthChart("QB", tomBrady, 0);
            manager.addPlayerToDepthChart("QB", blaineGabbert, 1);
            manager.addPlayerToDepthChart("LWR", mikeEvans, 0);
            
            // add a player to TE, then remove them to test skip empty lists logic
            manager.addPlayerToDepthChart("TE", kyleTrask, 0);
            manager.removePlayerFromDepthChart("TE", kyleTrask);

            manager.getFullDepthChart();

            String output = outContent.toString();
            
            assertTrue(output.contains("QB - (#12, Tom Brady), (#11, Blaine Gabbert)"), 
                    "QB output was formatted incorrectly.");
            assertTrue(output.contains("LWR - (#13, Mike Evans)"), 
                    "LWR output was formatted incorrectly.");
            assertFalse(output.contains("TE -"), 
                    "Empty positions should be skipped entirely.");
            
        } finally {
            System.setOut(originalOut);
        }
    }
}
