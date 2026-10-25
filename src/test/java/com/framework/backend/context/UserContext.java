package com.framework.backend.context;

import com.framework.backend.model.User;
import lombok.Setter;

public class UserContext {

    @Setter
    private User currentUser;

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user has been registered in this scenario yet");
        }
        return currentUser;
    }
}
