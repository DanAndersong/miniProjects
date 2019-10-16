package DriversDatabase;

public interface TableOperations {
    void createTable();
    void createForeignKeys();
    void createExtraConstrains();
    void update();
    void drop();
    void readAll();
}
