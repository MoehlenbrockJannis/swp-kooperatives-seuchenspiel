package de.uol.swp.server.store;


import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public abstract class AbstractStore {
    private final Random random = new Random();

    /**
     * Creates all stores that are available
     * <p>
     *
     * @param databaseIsAvailable boolean indicating if a database is available
     * @return Map containing all stores
     */

    public static Map<Class, AbstractStore> createStores(boolean databaseIsAvailable) {
        Map<Class, AbstractStore> storeMap = new HashMap<>();

        Set<Class<? extends ContentStore>> storeInterfaces = getSubStores(ContentStore.class, getServerPackagePath(), subType -> Modifier.isInterface(subType.getModifiers()));

        storeInterfaces.forEach(storeInterface -> {
            Optional<Class<AbstractStore>> subTypes = getStore(storeInterface, databaseIsAvailable);
            subTypes.ifPresent(store -> storeMap.put(storeInterface, createStoreInstance(store)));
        });

        return storeMap;
    }

    /**
     * Returns the store that should be used
     * <p>
     *
     * @param storeInterface      the interface of the store
     * @param databaseIsAvailable boolean indicating if a database is available
     * @return the store that should be used
     */
    private static Optional<Class<AbstractStore>> getStore(Class<? extends ContentStore> storeInterface, boolean databaseIsAvailable) {
        Set<Class<? extends ContentStore>> subTypes = getSubStores(storeInterface, storeInterface.getPackageName(), subType -> !Modifier.isAbstract(subType.getModifiers()));

        return findFirstMatchingStore(subTypes, subtype -> isDatabaseStore(subtype) && databaseIsAvailable)
                .or(() -> findFirstMatchingStore(subTypes, AbstractStore::isMainMemoryBasedStore));
    }

    /**
     * Returns the first store that matches the given filter
     * <p>
     *
     * @param subTypes the set of stores to search in
     * @param filter   the filter to apply
     * @param <U>      the type of the store
     * @return the first store that matches the given filter
     */
    private static <U extends AbstractStore> Optional<Class<U>> findFirstMatchingStore(Set<Class<? extends ContentStore>> subTypes, Predicate<Class<? extends ContentStore>> filter) {
        return subTypes.stream()
                .filter(filter)
                .map(subType -> (Class<U>) subType)
                .findFirst();
    }

    /**
     * Returns all sub stores of a given store type
     * <p>
     *
     * @param storeType   the store type
     * @param packagePath the package path to search in
     * @param filter      the filter to apply
     * @param <T>         the type of the store
     * @param <U>         the type of the sub store
     * @return the set of sub stores
     */
    private static <T, U extends T> Set<Class<? extends U>> getSubStores(final Class<? extends T> storeType, final String packagePath, Predicate<Class<U>> filter) {
        Reflections reflections = new Reflections(packagePath);
        return reflections.getSubTypesOf(storeType).stream()
                .map(subType -> (Class<U>) subType)
                .filter(filter)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the package path of the Server
     * <p>
     *
     * @return String containing the package path
     */
    private static String getServerPackagePath() {
        return AbstractStore.class.getPackage().getName().replace(".store", "");
    }

    /**
     * Checks if the given subType is a main memory-based store.
     * <p>
     *
     * @param subType the class to check
     * @return true if the subType is a main memory-based store, false otherwise
     */
    private static boolean isMainMemoryBasedStore(Class<? extends ContentStore> subType) {
        return MainMemoryBasedStore.class.isAssignableFrom(subType);
    }

    /**
     * Checks if the given subType is a database store.
     * <p>
     *
     * @param subType the class to check
     * @return true if the subType is a database store, false otherwise
     */
    private static boolean isDatabaseStore(Class<? extends ContentStore> subType) {
        return DatabaseStore.class.isAssignableFrom(subType);
    }

    /**
     * Creates an instance of a store
     * <p>
     *
     * @param subType the store to create
     */
    private static AbstractStore createStoreInstance(Class<? extends AbstractStore> subType) {
        try {
            return subType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create store: " + e.getMessage());
        }
    }

    /**
     * Generates a unique ID.
     *
     * @return A unique ID
     */
    protected int generateUniqueId() {
        Set<Integer> ids = getIds();
        int uniqueId;

        do {
            uniqueId = this.random.nextInt(100000);
        } while (ids.contains(uniqueId));
        return uniqueId;
    }

    protected abstract Set<Integer> getIds();
}
