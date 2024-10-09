package de.uol.swp.client.role;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.request.RetrieveAllRolesRequest;
import de.uol.swp.common.role.request.RoleAssignmentRequest;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@DisplayName("RoleService (Client) Unit Test")

class RoleServiceTest extends EventBusBasedTest {

    @Mock
    private EventBus eventBusMock;
    @Mock
    private LobbyPresenter lobbyPresenter;
    @Mock
    private Lobby lobby;
    @Mock
    private User user;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleService = new RoleService(eventBusMock);
        when(lobbyPresenter.getLobby()).thenReturn(lobby);}

    @DisplayName("A request to fill the ComboBox is sent to the server")
    @Test
    void testRetrieveAllRolesRequest() {
        roleService.sendRetrieveAllRolesRequest(lobby);
        verify(eventBusMock).post(any(RetrieveAllRolesRequest.class));
    }

    @DisplayName("A request to assign a role to a user is sent to the server.")
    @Test
    void testSendRoleAssignmentRequest() {
        RoleCard roleCard = mock(RoleCard.class);
        roleService.sendRoleAssignmentRequest(lobby, user, roleCard);
        verify(eventBusMock).post(any(RoleAssignmentRequest.class));
    }

}
