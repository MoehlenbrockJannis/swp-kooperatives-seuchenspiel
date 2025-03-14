package de.uol.swp.server.map;

import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.map.response.RetrieveOriginalGameMapTypeResponse;
import de.uol.swp.server.EventBusBasedTest;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapTypeServiceTest extends EventBusBasedTest {

    @BeforeEach
    void setUp() {
        EventBus eventBus = getBus();
        MapTypeService mapTypeService = new MapTypeService(eventBus);
    }

    @Test
    @DisplayName("Should react to a RetrieveOriginalGameMapTypeRequest and post a RetrieveOriginalGameMapTypeResponse")
    void onRetrieveOriginalGameMapTypeRequestTest() throws InterruptedException {
        RetrieveOriginalGameMapTypeRequest request = new RetrieveOriginalGameMapTypeRequest();
        postAndWait(request);

        assertThat(this.event)
                .isInstanceOf(RetrieveOriginalGameMapTypeResponse.class);
    }

    @Subscribe
    public void onEvent(RetrieveOriginalGameMapTypeResponse retrieveOriginalGameMapTypeResponse) {
        handleEvent(retrieveOriginalGameMapTypeResponse);
    }
}
