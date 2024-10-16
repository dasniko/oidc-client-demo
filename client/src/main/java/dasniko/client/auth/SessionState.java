package dasniko.client.auth;

import jakarta.enterprise.context.SessionScoped;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@SessionScoped
public class SessionState implements Serializable {
}
