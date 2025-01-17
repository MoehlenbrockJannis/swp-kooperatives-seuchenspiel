package de.uol.swp.server.approvable;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApprovableServiceTest extends EventBusBasedTest {
    private ApprovableService approvableService;
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        lobbyService = mock();

        approvableService = new ApprovableService(getBus(), lobbyService);

        doAnswer(invocation -> {
            post(invocation.getArguments()[1]);
            return null;
        }).when(lobbyService).sendToAllInLobby(any(), any());
    }

    private static Stream<Arguments> onApprovableRequestSource() {
        final Lobby lobby = mock();
        final Game game = mock();
        when(game.getLobby())
                .thenReturn(lobby);

        final Approvable approvable = mock();
        when(approvable.getGame())
                .thenReturn(game);

        return Stream.of(
                Arguments.of(ApprovableMessageStatus.OUTBOUND, approvable),
                Arguments.of(ApprovableMessageStatus.APPROVED, approvable),
                Arguments.of(ApprovableMessageStatus.REJECTED, approvable)
        );
    }

    @ParameterizedTest
    @MethodSource("onApprovableRequestSource")
    @DisplayName("Should react to an ApprovableRequest and post an ApprovableServerMessage with the same Status and Approvable")
    void onApprovableRequest(final ApprovableMessageStatus status, final Approvable approvable) throws InterruptedException {
        final ApprovableRequest approvableRequest = new ApprovableRequest(status, approvable);

        postAndWait(approvableRequest);

        assertInstanceOf(ApprovableServerMessage.class, event);

        final ApprovableServerMessage approvableServerMessage = (ApprovableServerMessage) event;
        assertThat(approvableServerMessage.getStatus())
                .isEqualTo(status);
        assertThat(approvableServerMessage.getApprovable())
                .isEqualTo(approvable);

        verify(lobbyService)
                .sendToAllInLobby(approvable.getGame().getLobby(), approvableServerMessage);
    }

    @Subscribe
    public void onEvent(final ApprovableServerMessage approvableServerMessage) {
        handleEvent(approvableServerMessage);
    }
}