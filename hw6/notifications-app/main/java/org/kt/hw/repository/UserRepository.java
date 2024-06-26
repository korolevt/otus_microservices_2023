package org.kt.hw.repository;

import org.kt.hw.repository.utils.ShortUUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.kt.hw.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
     User findByLoginEqualsAndPasswordEquals(String login, String password);

     // Создать нового пользователя
     public static User createUser(String login, String firstName, String lastName, String email) {
          return new User(
                  null,
                  login,
                  ShortUUID.shortUUID(), // generate password
                  firstName,
                  lastName,
                  email
          );
     }
}
