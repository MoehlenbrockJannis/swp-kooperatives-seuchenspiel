package de.uol.swp.server.role;

import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.simple.MoveAllyToAllyAction;
import de.uol.swp.common.action.simple.car.CarActionForAlly;
import de.uol.swp.common.action.simple.charter_flight.CharterFlightActionForAlly;
import de.uol.swp.common.action.simple.direct_flight.DirectFlightActionForAlly;
import de.uol.swp.common.action.simple.shuttle_flight.ShuttleFlightActionForAlly;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.util.RoleColors;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code RoleManagement} class manages and tracks the assignment of roles to users in a specific lobby.
 * It provides methods to retrieve available roles, assign roles to users, and manage user-role associations
 * within the context of a lobby.
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-06
 */
@Getter
public class RoleManagement {
    private final RoleCard logistiker = new RoleCard("Logistiker", RoleColors.LOGISTIKER_COLOR_PURPLE, new RoleAbility(Map.of(), List.of(CarActionForAlly.class, CharterFlightActionForAlly.class, DirectFlightActionForAlly.class, ShuttleFlightActionForAlly.class, MoveAllyToAllyAction.class), List.of()));
    private final RoleCard arzt = new RoleCard("Arzt", RoleColors.ARZT_COLOR_ORANGE, new RoleAbility(Map.of(), List.of(), List.of()));
    private final RoleCard betriebsexperte = new RoleCard("Betriebsexperte", RoleColors.BETRIEBSEXPERTE_COLOR_GREEN, new RoleAbility(Map.of(BuildResearchLaboratoryAction.class, ReducedCostBuildResearchLaboratoryAction.class), List.of(), List.of()));
    private final RoleCard wissenschaftler = new RoleCard("Wissenschaftler", RoleColors.WISSENSCHAFTLER_COLOR_GRAY, new RoleAbility(Map.of(), List.of(), List.of()));
    private final RoleCard forscherin = new RoleCard("Forscherin", RoleColors.FORSCHERIN_COLOR_BROWN, new RoleAbility(Map.of(), List.of(), List.of()));

    /**
     * Returns a set of all available roles that can be assigned to users.
     *
     * @return a set containing all {@link RoleCard} objects representing the roles
     */
    public Set<RoleCard> getAllRoles() {
        return Set.of(
                arzt,
                logistiker,
                betriebsexperte,
                wissenschaftler,
                forscherin
        );
    }

    /**
     * Assigns roles to players in a lobby.
     *
     * This method iterates through all players in the given lobby and assigns
     * a random, available role to each player who doesn't already have a role.
     *
     * @param lobby The lobby whose players should be assigned roles.
     */
    public void assignRolesToPlayers(final Lobby lobby) {
        final Set<Player> players = lobby.getPlayers();
        final List<RoleCard> availableRolesList = getAvailableRoles(players);
        for (final Player player : players) {
            if (player.getRole() == null) {
                final RoleCard roleCard = availableRolesList.remove(0);
                player.setRole(roleCard);
            }
        }
    }

    /**
     * Determines the available roles for a group of players.
     *
     * This method first identifies all roles that are already in use,
     * filters these out from the total set of all roles, and
     * returns a randomly shuffled list of the remaining roles.
     *
     * @param players The collection of players for which to determine available roles.
     * @return A shuffled list of RoleCard objects representing the available roles.
     */
    public List<RoleCard> getAvailableRoles(final Collection<Player> players) {
        final List<RoleCard> usedRoles = players.stream()
                .map(Player::getRole)
                .toList();
        final Set<RoleCard> allRoles = getAllRoles();
        final List<RoleCard> availableRolesList = allRoles.stream()
                .filter(role -> !usedRoles.contains(role))
                .collect(Collectors.toList());
        Collections.shuffle(availableRolesList);
        return availableRolesList;
    }
}
