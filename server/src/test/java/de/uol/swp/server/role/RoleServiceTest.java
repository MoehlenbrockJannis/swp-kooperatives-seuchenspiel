package de.uol.swp.server.role;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.request.RetrieveAllRolesRequest;
import de.uol.swp.common.role.response.RetrieveAllRolesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;
import java.util.stream.Collectors;

class RoleServiceTest extends EventBusBasedTest {

    private RoleCard firstRoleCard;
    private RoleCard secondRoleCard;
    @Mock
    private User firstUser;
    @Mock
    private User secondUser;
    @Mock
    private Lobby lobby;
    @Mock
    private Player player1;
    @Mock
    private Player player2;
    private RoleService roleService;
    private RoleManagement roleManagement;
    @Mock
    private EventBus eventBus;
    @Mock
    private LobbyManagement lobbyManagement;
    @Mock
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleManagement = new RoleManagement();

        roleService = new RoleService(eventBus, lobbyManagement, lobbyService, roleManagement);

        firstRoleCard = new RoleCard("TestRoleCard1", null, null);
        secondRoleCard = new RoleCard("TestRoleCard2", null, null);

        when(player1.getRole()).thenAnswer(invocation -> null);
        when(lobby.getPlayers()).thenReturn(Set.of(player1, player2));
    }

    @DisplayName("Roles are loaded into the set in RoleManagement, on which the entire role system is based")
    @Test
    void testGetAllRoles() {
        assertThat(roleManagement.getAllRoles()).isNotNull();
    }

    @DisplayName("Assigning a Role to a User in Lobby without a Message")
    @Test
    void testRoleAssignmentMethod() {
        UserPlayer userPlayer = new UserPlayer(firstUser);
        userPlayer.setRole(firstRoleCard);
        assertThat(userPlayer.getRole()).isEqualTo(firstRoleCard);
    }

    @DisplayName("Assigning a role to a user in the lobby and changing the role")
    @Test
    void testRoleChange() {
        UserPlayer userPlayer = new UserPlayer(firstUser);
        userPlayer.setRole(firstRoleCard);
        assertThat(userPlayer.getRole()).isEqualTo(firstRoleCard);
        userPlayer.setRole(secondRoleCard);
        assertThat(userPlayer.getRole()).isEqualTo(secondRoleCard);
        assertThat(userPlayer.getRole()).isNotEqualTo(firstRoleCard);
    }

    @DisplayName("Check whether a role can no longer be assigned if it has already been assigned")
    @Test
    void testIsRoleNotAvailableForPlayerInLobby() {
        when(player1.getRole()).thenReturn(firstRoleCard);

        boolean isAvailable = roleService.isRoleAvailableForPlayerInLobby(lobby, firstRoleCard);

        assertThat(isAvailable).isFalse();
    }

    @DisplayName("Check whether a role can be assigned")
    @Test
    void testIsRoleAvailableForPlayerInLobby() {
        when(player1.getRole()).thenReturn(firstRoleCard);

        boolean isAvailable = roleService.isRoleAvailableForPlayerInLobby(lobby, secondRoleCard);

        assertThat(isAvailable).isTrue();
    }

    @DisplayName("All roles are returned if no role has yet been assigned")
    @Test
    void testGetAvailableRolesInLobby_NoAssignedRoles() {
        Set<RoleCard> allRoles = roleManagement.getAllRoles();
        when(player1.getRole()).thenReturn(null);
        when(player2.getRole()).thenReturn(null);

        Set<RoleCard> availableRoles = roleService.getAvailableRolesInLobby(lobby);
        assertThat(allRoles).isEqualTo(availableRoles);
    }

    @DisplayName("Only the rolls that are actually still available are returned")
    @Test
    void testGetAvailableRolesInLobby_SomeAssignedRoles() {
        Set<RoleCard> allRoles = roleManagement.getAllRoles();

        RoleCard arztRole = new RoleCard("Arzt", null, null);
        when(player1.getRole()).thenReturn(arztRole);
        when(player2.getRole()).thenReturn(null);

        Set<RoleCard> availableRoles = roleService.getAvailableRolesInLobby(lobby);

        Set<RoleCard> expectedRoles = allRoles.stream()
                .filter(role -> !role.equals(arztRole))
                .collect(Collectors.toSet());

        assertThat(availableRoles).isEqualTo(expectedRoles);
    }

    @DisplayName("The method should send the required response the event bus")
    @Test
    void testOnRolesSendToClient() {
        RetrieveAllRolesRequest mockRequest = mock(RetrieveAllRolesRequest.class);
        Lobby mockLobby = mock(Lobby.class);
        when(mockRequest.getLobby()).thenReturn(mockLobby);

        roleService.onRolesSendToClient(mockRequest);

        verify(eventBus).post(any(RetrieveAllRolesResponse.class));
    }
}
