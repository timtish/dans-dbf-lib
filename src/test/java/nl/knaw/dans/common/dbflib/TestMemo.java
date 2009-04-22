/*
 *  Copyright 2009
 *  Data Archiving and Networked Services (DANS), Netherlands.
 *
 *  This file is part of DANS DBF Library.
 *
 *  DANS DBF Library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DANS DBF Library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DANS DBF Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.knaw.dans.common.dbflib;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests reading and writing memo fields
 *
 * @author Vesa Åkerman
 */
public class TestMemo
{
    private static final int LENGTH_MEMO_BLOCK = 512;

    /**
     * DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws CorruptedTableException DOCUMENT ME!
     */
    @Test
    public void readMemo()
                  throws FileNotFoundException, IOException, CorruptedTableException
    {
        final File databaseDirectory = new File("src/test/resources/dbase3plus/types");
        final Database database = new Database(databaseDirectory);
        final Table t1 = database.addTable("MEMOTEST.DBF");
        final File memoFile = new File(databaseDirectory, "MEMOTEST.DBT");

        try
        {
            t1.open(IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = t1.recordIterator();

            Record r = recordIterator.next();
            assertEquals("m",
                         r.getStringValue("MEMO"));

            r = recordIterator.next();
            assertEquals(null,
                         r.getStringValue("MEMO"));

            r = recordIterator.next();
            assertEquals("This is a very long memo",
                         r.getStringValue("MEMO").substring(0, 24));

            r = recordIterator.next();

            assertEquals("1234567890",
                         r.getStringValue("MEMO").substring(0, 10));

            assertEquals(8, memoFile.length() / LENGTH_MEMO_BLOCK);
        }
        finally
        {
            t1.close();
        }
    }

/**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws CorruptedTableException DOCUMENT ME!
     */
    @Test
    public void writeMemo()
                   throws IOException, CorruptedTableException, ValueTooLargeException
    {
        final Ranges ignoredRangesDbf = new Ranges();
        ignoredRangesDbf.addRange(0x01, 0x03); // modified
        ignoredRangesDbf.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRangesDbf.addRange(0x4c, 0x4f); // field description "address in memory"

        final Ranges ignoredRangesDbt = new Ranges();
        ignoredRangesDbt.addRange(0x04, 0x1ff); // reserved/garbage

        UnitTestUtil.doCopyAndCompareTest("dbase3plus/types", "MEMOTEST", ignoredRangesDbf, ignoredRangesDbt);
    }
}