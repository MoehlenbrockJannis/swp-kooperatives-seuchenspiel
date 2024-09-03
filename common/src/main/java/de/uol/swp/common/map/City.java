package de.uol.swp.common.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class representing a city with all information about it
 *
 * @author Tom Weelborg
 * @since 2024-09-02
 */
@AllArgsConstructor
@Getter
public class City {
    private String name;
    private String information;
}
