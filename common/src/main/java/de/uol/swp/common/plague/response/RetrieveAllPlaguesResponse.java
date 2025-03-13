package de.uol.swp.common.plague.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.plague.Plague;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * This class receives all existing plagues from the backend in the set when a {@link de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest} request is executed.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RetrieveAllPlaguesResponse extends AbstractResponseMessage {
    private Set<Plague> plagueSet = new HashSet<>();

    public RetrieveAllPlaguesResponse(Set<Plague> plagueSet) {
        this.plagueSet.addAll(plagueSet);
    }
}
