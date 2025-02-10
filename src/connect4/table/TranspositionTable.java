package connect4.table;
import java.lang.instrument.*;

public class TranspositionTable {
    private final TableEntry[] entries;

    public TranspositionTable(int maxMB) {
        int byteSize = 32;
        int size = (maxMB * 1024 * 1024) / byteSize;

        entries = new TableEntry[size];
        System.out.println("Table of size " + entries.length);
    }

    public TableEntry get(long key) {
        int index = (int) (key % entries.length);

        TableEntry entry = entries[index];
        if(entry != null && entry.key == key) {
            return entries[index];
        }

        return null;
    }

    public void put(TableEntry entry) {
        int index = (int) (entry.key % entries.length);
        entries[index] = entry;
    }
}
