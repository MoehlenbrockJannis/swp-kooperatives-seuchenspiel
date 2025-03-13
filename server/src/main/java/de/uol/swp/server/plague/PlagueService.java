package de.uol.swp.server.plague;

import com.google.inject.Singleton;
import de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest;
import de.uol.swp.common.plague.response.RetrieveAllPlaguesResponse;
import de.uol.swp.server.AbstractService;
import jakarta.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Handles the plague requests sent by the client
 *
 * @see de.uol.swp.common.plague.Plague
 */
@Singleton
public class PlagueService extends AbstractService {

    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     */
    @Inject
    public PlagueService(EventBus bus) {
        super(bus);
    }

    /**
     * Handles a {@link RetrieveAllPlaguesRequest} from a client.
     * This sends back all {@link de.uol.swp.common.plague.Plague} objects from {@link Plagues} to the client.
     *
     * @param request the request to get the plague objects
     */
    @Subscribe
    public void onRetrieveAllPlaguesRequest(RetrieveAllPlaguesRequest request) {
        RetrieveAllPlaguesResponse response = new RetrieveAllPlaguesResponse(Plagues.getAllPlagues());
        response.initWithMessage(request);
        post(response);
    }
}
