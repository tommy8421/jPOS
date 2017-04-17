/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2017 jPOS Software SRL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.rc;

import static org.junit.Assert.*;

import org.jpos.core.ConfigurationException;
import org.jpos.core.SimpleConfiguration;
import org.jpos.util.Caller;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CMFTest {
    @Test
    public void testIRCCollitions() {
        Set<Integer> ircs = new HashSet<>();
        for (CMF cmf : CMF.values()) {
            if (ircs.contains(cmf.irc()))
                fail(String.format("CMF.%s uses duplicate irc %d", cmf.name(), cmf.irc()));
            ircs.add(cmf.irc());
        }
    }

    @Test
    public void testSuccess() {
        Result rc = new Result();
        rc.success(CMF.APPROVED, Caller.info(), "Approved");
        assertTrue(rc.isSuccess());
        rc.dump (System.out, "");
        assertNotNull(rc.success());
        rc.fail(CMF.GENERAL_DECLINE, Caller.info(), "Decline");
        assertFalse(rc.isSuccess());
        assertNull(rc.success());
        rc.dump (System.out, "");
    }

    @Test
    public void testInvalidSuccess() {
        Result rc = new Result();
        try {
            rc.success(CMF.GENERAL_DECLINE, Caller.info(), "Invalid Success");
            fail ("IllegalArgumentException should have been raised");
        } catch (IllegalArgumentException ignored) { }
    }

    @Test
    public void testCMFConverterOverride() throws ConfigurationException {
        CMFConverter c = new CMFConverter();
        SimpleConfiguration cfg = new SimpleConfiguration();
        cfg.put("10000","----,jPOS error message");
        c.setConfiguration(cfg);

        assertEquals("Standard RC", new SimpleRC("0000", "APPROVED"), c.convert(CMF.APPROVED));
        assertEquals("ResourceBundle override", new SimpleRC("ZZZZ", "General Decline"), c.convert(CMF.GENERAL_DECLINE));
        assertEquals("Configuration override", new SimpleRC("----", "jPOS error message"), c.convert(CMF.JPOS));
    }
}
