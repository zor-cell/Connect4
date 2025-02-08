package connect4.table;

public class TranspositionTable {
    private TableEntry[] entries;

    public TranspositionTable(int size) {
        entries = new TableEntry[size];
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
