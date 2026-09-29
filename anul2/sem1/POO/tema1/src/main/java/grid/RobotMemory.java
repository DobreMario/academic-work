package grid;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class representing the robot's memory.
 * Stores scanned entities and dictionary definitions.
 */
public final class RobotMemory {
    private static RobotMemory instance = null;
    private Map<String, List<String>> memory;
    private List<Componente> dictionary;

    private RobotMemory() {
        this.memory = new LinkedHashMap<>();
        this.dictionary = new ArrayList<>();
    }

    /**
     * Gets the singleton instance of RobotMemory.
     *
     * @return The instance.
     */
    public static RobotMemory getInstance() {
        if (instance == null) {
            instance = new RobotMemory();
        }
        return instance;
    }

    /**
     * Gets the memory map.
     *
     * @return Map of entity names to facts.
     */
    public Map<String, List<String>> getMemory() {
        return memory;
    }

    /**
     * Adds a scanned entity key to memory.
     *
     * @param key The entity name.
     */
    public void addScannedEntity(final String key) {
        memory.putIfAbsent(key, new ArrayList<>());
    }

    /**
     * Adds a fact to a specific entity.
     *
     * @param key  The entity name.
     * @param fact The fact string.
     */
    public void addFact(final String key, final String fact) {
        List<String> facts = memory.get(key);
        if (facts != null) {
            facts.add(fact);
        } else {
            System.err.println("ERROR: the object does not exist in memory.");
        }
    }

    /**
     * Checks if an entity has any facts.
     *
     * @param key The entity name.
     * @return true if facts exist.
     */
    public boolean haveFact(final String key) {
        List<String> facts = memory.get(key);
        if (facts != null) {
            return !facts.isEmpty();
        }
        return false;
    }

    /**
     * Checks if an entity has a specific fact.
     *
     * @param key  The entity name.
     * @param fact The fact to check.
     * @return true if the fact exists.
     */
    public boolean haveFact(final String key, final String fact) {
        List<String> facts = memory.get(key);
        if (facts != null) {
            return facts.contains(fact);
        }
        return false;
    }

    /**
     * Checks if an entity is in memory.
     *
     * @param key The entity name.
     * @return true if scanned.
     */
    public boolean isScanned(final String key) {
        return memory.containsKey(key);
    }

    /**
     * Adds an entity definition to the dictionary.
     *
     * @param name Entity name.
     * @param type Entity type.
     */
    public void addToDictionary(final String name, final String type) {
        if (!isInDictionary(name, type)) {
            dictionary.add(new Componente(name, type));
        }
    }

    /**
     * Checks if a name-type pair exists in the dictionary.
     *
     * @param name Entity name.
     * @param type Entity type.
     * @return true if exists.
     */
    public boolean isInDictionary(final String name, final String type) {
        for (Componente componente : dictionary) {
            if (componente.getName().equals(name) && componente.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a name exists in the dictionary.
     *
     * @param name Entity name.
     * @return true if exists.
     */
    public boolean isNameInDictionary(final String name) {
        for (Componente componente : dictionary) {
            if (componente.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a type exists in the dictionary.
     *
     * @param type Entity type.
     * @return true if exists.
     */
    public boolean isTypeInDictionary(final String type) {
        for (Componente componente : dictionary) {
            if (componente.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all memory and resets the singleton instance.
     */
    public void clearMemory() {
        memory.clear();
        memory = null;
        dictionary.clear();
        dictionary = null;
        instance = null;
    }

    /**
     * Internal class representing a dictionary component.
     */
    private static final class Componente {
        private final String name;
        private final String type;

        /**
         * Constructor.
         *
         * @param name Entity name.
         * @param type Entity type.
         */
        Componente(final String name, final String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * Gets the name.
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the type.
         *
         * @return Type.
         */
        public String getType() {
            return type;
        }
    }
}
