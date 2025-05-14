package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sex {
    MALE("Male"),
    FEMALE("Female"),
    NOT_SPECIFIED("Not specified");

    private String value;
}
