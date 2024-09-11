package de.uol.swp.common.role;

import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.triggerable.Triggerable;
import lombok.*;

import java.util.List;
import java.util.Map;


@Getter
@AllArgsConstructor
@ToString
public class RoleAbility {
    private Map<Class<? extends GeneralAction>, Class<? extends RoleAction>> replacedActions;
    private List<Class<? extends RoleAction>> additionalActions;
    private List<Triggerable> triggerables;
}
