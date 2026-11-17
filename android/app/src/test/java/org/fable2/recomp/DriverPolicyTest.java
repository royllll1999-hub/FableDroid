package org.fable2.recomp;

import org.junit.Test;
import static org.junit.Assert.*;

public class DriverPolicyTest {
    @Test public void hardwareDefaults() {
        assertEquals("turnip27", DriverPolicy.defaultDriver("Adreno (TM) 740"));
        assertEquals("turnip27", DriverPolicy.defaultDriver("Adreno 750"));
        // Odin 3 fork: Adreno 8xx defaults to the bundled Turnip Gen8 V39 patched build.
        assertEquals("turnipgen8", DriverPolicy.defaultDriver("Adreno (TM) 830"));
        assertEquals("turnipgen8", DriverPolicy.defaultDriver("Adreno 840"));
        for (String gpu : new String[] { "Adreno 640", "Mali-G715", "", "Adreno 7400", "Adreno 8400" })
            assertEquals(gpu, "", DriverPolicy.defaultDriver(gpu));
        assertEquals("", DriverPolicy.defaultDriver(null));
    }
    @Test public void upgradesAndManualChoices() {
        assertEquals("turnipgen8", DriverPolicy.select(null, null, "Adreno (TM) 830"));
        assertEquals("turnipgen8", DriverPolicy.select(null, true, "Adreno (TM) 830"));
        assertEquals("turnipgen8", DriverPolicy.select("turnipgen8", null, "Adreno (TM) 740"));
        assertEquals("turnip27", DriverPolicy.select(null, null, "Adreno (TM) 740"));
        assertEquals("", DriverPolicy.select(null, false, "Adreno (TM) 740"));
        assertEquals("", DriverPolicy.select("", null, "Adreno (TM) 740"));
        assertEquals("turnip27", DriverPolicy.select("turnip27", null, "Adreno (TM) 830"));
        String downloaded = "download-" + "a".repeat(64);
        assertEquals(downloaded, DriverPolicy.select(downloaded, null, "Adreno (TM) 830"));
        assertEquals("", DriverPolicy.select("../bad", null, "Adreno (TM) 830"));
    }
    @Test public void recommendationsCoverOtherFamilies() {
        for (String gpu : new String[] {"Mali-G715", "Immortalis-G720", "PowerVR Rogue", "ANGLE Xclipse 940", "Unknown", ""}) {
            assertTrue(DriverPolicy.recommendation(gpu).contains("experimental"));
            assertFalse(DriverPolicy.matchingDownload(gpu, "Turnip_Gen8_V38.zip"));
            assertFalse(DriverPolicy.matchingDownload(gpu, "Turnip_v27.0.0-R1.zip"));
        }
        assertEquals("Mali", DriverPolicy.family("Mali-G715"));
        assertEquals("Immortalis", DriverPolicy.family("Immortalis-G720"));
        assertEquals("PowerVR", DriverPolicy.family("PowerVR Rogue"));
        assertEquals("Xclipse", DriverPolicy.family("ANGLE Xclipse 940"));
    }
    @Test public void downloadsMatchGenerationWithoutRecommendingPatchedVariants() {
        assertTrue(DriverPolicy.matchingDownload("Adreno (TM) 830", "Turnip_Gen8_V38.zip"));
        assertFalse(DriverPolicy.matchingDownload("Adreno (TM) 830", "Turnip_v27.0.0-R1.zip"));
        assertFalse(DriverPolicy.matchingDownload("Adreno (TM) 740", "Turnip_Gen8_V38.zip"));
        assertTrue(DriverPolicy.matchingDownload("Adreno (TM) 740", "Turnip_v27.0.0-R1.zip"));
        assertTrue(DriverPolicy.matchingDownload("Adreno (TM) 650", "Turnip_v27.0.0-R1.zip"));
        assertFalse(DriverPolicy.matchingDownload("Adreno (TM) 830", "Turnip_Gen8_V38_Patched.zip"));
        assertFalse(DriverPolicy.matchingDownload("Adreno (TM) 740", "Turnip_v27.0.0-R1-Oneui.zip"));
    }
}
