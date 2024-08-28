package org.kt.hw.repository;

import org.kt.hw.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findAccountByUserId(Integer userId);

}
