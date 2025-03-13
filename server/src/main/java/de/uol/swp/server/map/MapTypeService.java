package de.uol.swp.server.map;

import com.google.inject.Singleton;
import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.map.response.RetrieveOriginalGameMapTypeResponse;
import de.uol.swp.server.AbstractService;
import jakarta.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Handles the mapType requests sent by the client
 *
 * @see de.uol.swp.common.map.MapType
 */
@Singleton
public class MapTypeService extends AbstractService {

    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     */
    @Inject
    public MapTypeService(EventBus bus) {
        super(bus);
    }

    /**
     * Handles a {@link RetrieveOriginalGameMapTypeRequest} from a client.
     * This sends back the {@link de.uol.swp.common.map.MapType} for the original game (see {@link OriginalGameMapType}) to the client.
     *
     * @param request the request to get the {@link de.uol.swp.common.map.MapType} for the original game
     */
    @Subscribe
    public void onRetrieveOriginalGameMapTypeRequest(RetrieveOriginalGameMapTypeRequest request) {
        RetrieveOriginalGameMapTypeResponse response = new RetrieveOriginalGameMapTypeResponse(OriginalGameMapType.getMapType());
        response.initWithMessage(request);
        post(response);
    }
}
