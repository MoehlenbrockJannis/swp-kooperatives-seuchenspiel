package de.uol.swp.common.message.server_message;

import de.uol.swp.common.user.Session;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractServerMessageTest {

    AbstractServerMessage message  = new AbstractServerMessage();
    final List<Session> receiver = new ArrayList<>();

    @Test
    void setReceiverTest() {
        message.setReceiver(receiver);

        assertEquals(receiver, message.getReceiver());
    }

}
