package de.uol.swp.common.card.event_card;

import lombok.NoArgsConstructor;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class EventCardFactory {

    private final Reflections reflections = new Reflections(EventCard.class.getPackageName());

    public List<EventCard> createEventCards () {
        List<EventCard> eventCards = new ArrayList<>();
        for (Class<? extends EventCard> eventCard : this.reflections.getSubTypesOf(EventCard.class)) {
            if(!Modifier.isAbstract(eventCard.getModifiers())) {
                eventCards.add(createEventCard(eventCard));
            }
        }
        return eventCards;
    }

    private EventCard createEventCard (Class<? extends EventCard> eventCard) {
        EventCard eventCardInstance = null;
        try {
            eventCardInstance = eventCard.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return eventCardInstance;
    }
}
