package org.kt.hw.repository;

import org.kt.hw.model.RegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.kt.hw.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
     User findByLoginEqualsAndPasswordEquals(String login, String password);
     User findByLoginEquals(String login);

     // Создать нового пользователя
     public static User createUser(RegisterRequest data) {
          return new User(
                  null,
                  data.getLogin(),
                  data.getPassword(), //ShortUUID.shortUUID(), // generate password
                  data.getFirstName(),
                  data.getLastName(),
                  data.getEmail()
          );
     }
}
