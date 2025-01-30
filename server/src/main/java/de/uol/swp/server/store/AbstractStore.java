package de.uol.swp.server.store;


import com.google.common.hash.Hashing;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public abstract class AbstractStore {


    /**
     * Creates all stores that are available
     *
     * @param databaseIsAvailable boolean indicating if a database is available
     * @return Map containing all stores
     */

    public static Map<Class, AbstractStore> createStores(boolean databaseIsAvailable) {
        Map<Class, AbstractStore> storeMap = new HashMap<>();

        Set<Class<? extends AbstractStore>> subTypes = getSubStores(databaseIsAvailable);

        subTypes.forEach(subType -> storeMap.put(getStoreInterface(subType), createStoreInstance(subType)));

        return storeMap;
    }

    /**
     * Returns all subtypes of AbstractStore
     *
     * @param databaseIsAvailable boolean indicating if a database is available
     * @return Set of all subtypes of AbstractStore
     */
    private static Set<Class<? extends AbstractStore>> getSubStores(boolean databaseIsAvailable) {
        Reflections reflections = new Reflections(getServerPackagePath());
        return reflections.getSubTypesOf(AbstractStore.class)
                .stream()
                .filter(subType -> isValidSubType(databaseIsAvailable, subType))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the package path of the Server
     *
     * @return String containing the package path
     */
    private static String getServerPackagePath() {
        return AbstractStore.class.getPackage().getName().replace(".store", "");
    }

    /**
     * Checks if a given subType is valid
     *
     * @param databaseIsAvailable boolean indicating if a database is available
     * @param subType            the subType to check
     * @return boolean indicating if the subType is valid
     */
    private static boolean isValidSubType(boolean databaseIsAvailable, Class<? extends AbstractStore> subType) {
        if (Modifier.isAbstract(subType.getModifiers())) {
            return false;
        }
        if (MainMemoryBasedStore.class.isAssignableFrom(subType)) {
            return true;
        }
        if (databaseIsAvailable) {
            return DatabaseStore.class.isAssignableFrom(subType) && UserStore.class.isAssignableFrom(subType);
        } else {
            return MainMemoryBasedStore.class.isAssignableFrom(subType) && UserStore.class.isAssignableFrom(subType);
        }
    }

    /**
     * Creates an instance of a store
     *
     * @param subType  the store to create
     */
    private static AbstractStore createStoreInstance(Class<? extends AbstractStore> subType) {
        try {
            return subType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create store: " + e.getMessage());
        }
    }

    /**
     * Returns the interface of a store
     *
     * @param subType the store to get the interface for
     * @return the interface of the store
     */
    private static Class getStoreInterface(Class<? extends AbstractStore> subType) {
       Optional<Class<?>> interfaces = Arrays.stream(subType.getInterfaces())
                .filter(i -> i != MainMemoryBasedStore.class && i != DatabaseStore.class)
                .findFirst();

        return interfaces.orElse(null);
    }



    /**
     * Calculates the hash for a given String
     *
     * @implSpec the hash method used is sha256
     * @param toHash the String to calculate the hash for
     * @return String containing the calculated hash
     * @since 2019-09-04
     */
    protected String hash(String toHash){
        return Hashing.sha256()
                .hashString(toHash, StandardCharsets.UTF_8)
                .toString();
    }

}
