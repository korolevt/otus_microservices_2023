package org.kt.hw.repository;

import org.kt.hw.entity.Account;
import org.kt.hw.entity.User;
import org.kt.hw.repository.utils.ShortUUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findAccountByOwnerId(Integer ownerId);

}
