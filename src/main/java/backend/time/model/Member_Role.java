package backend.time.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Member_Role {
    GUEST("ROLE_GUEST"), USER("ROLE_USER"), MANAGER("ROLE_MANAGER");
    private final String key;

}
