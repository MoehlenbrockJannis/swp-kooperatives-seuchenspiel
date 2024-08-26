package de.uol.swp.common.roles;

import de.uol.swp.common.actions.GeneralAction;
import de.uol.swp.common.actions.RoleAction;
import de.uol.swp.common.actions.service.Triggerable;
import lombok.*;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleAbility {


    private Map<Class<? extends GeneralAction>, Class<? extends RoleAction>> replacedActions;
    private List<Class<? extends RoleAction>> additionalActions;
    private List<Triggerable> triggerables;
}
