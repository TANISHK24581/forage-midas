package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository,
                           TransactionRecordRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // ✅ REQUIRED for UserPopulator (DO NOT REMOVE)
    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    // ✅ Used by TransactionListener
    public void saveUsers(UserRecord sender, UserRecord recipient) {
        userRepository.save(sender);
        userRepository.save(recipient);
    }

    public void saveTransaction(TransactionRecord transaction) {
        transactionRepository.save(transaction);
    }
}
