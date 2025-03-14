package de.uol.swp.common.card.event_card;

import lombok.NoArgsConstructor;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating instances of {@link EventCard}.
 * <p>
 * This class dynamically scans for all subclasses of {@link EventCard} in its package
 * and provides methods to create instances of these event cards.
 * </p>
 */
@NoArgsConstructor
public class EventCardFactory {

    private final Reflections reflections = new Reflections(EventCard.class.getPackageName());

    /**
     * Creates a list of all concrete {@link EventCard} subclasses.
     * <p>
     * The method scans the package for all non-abstract subclasses of {@link EventCard}
     * and creates one instance for each class.
     * </p>
     *
     * @return A list of instantiated {@link EventCard} objects.
     */
    public List<EventCard> createEventCards() {
        List<EventCard> eventCards = new ArrayList<>();
        for (Class<? extends EventCard> eventCard : this.reflections.getSubTypesOf(EventCard.class)) {
            if (!Modifier.isAbstract(eventCard.getModifiers())) {
                eventCards.add(createEventCard(eventCard));
            }
        }
        return eventCards;
    }

    /**
     * Creates an instance of the given {@link EventCard} class.
     * <p>
     * The method uses reflection to invoke the default constructor of the given class.
     * If instantiation fails due to any exception, it is logged via {@link #printStackTrace()}
     * and the method returns {@code null}.
     * </p>
     *
     * @param eventCard The {@link Class} of the {@link EventCard} to instantiate.
     * @return An instance of the specified {@link EventCard}, or {@code null} if instantiation fails.
     */
    private EventCard createEventCard(Class<? extends EventCard> eventCard) {
        EventCard eventCardInstance = null;
        try {
            eventCardInstance = eventCard.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return eventCardInstance;
    }
}