package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final DatabaseConduit databaseConduit;
    private final IncentiveClient incentiveClient;

    // ✅ SINGLE constructor (correct)
    public TransactionListener(UserRepository userRepository,
                               DatabaseConduit databaseConduit,
                               IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.databaseConduit = databaseConduit;
        this.incentiveClient = incentiveClient;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    public void listen(Transaction transaction) {

        Optional<UserRecord> senderOpt =
                userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt =
                userRepository.findById(transaction.getRecipientId());

        // ❌ invalid users
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // ❌ insufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // ✅ apply transaction
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // ✅ fetch incentive
        Incentive incentive = incentiveClient.fetchIncentive(transaction);
        if (incentive != null && incentive.getAmount() > 0) {
            recipient.setBalance(recipient.getBalance() + incentive.getAmount());
        }

        // ✅ persist changes
        databaseConduit.saveUsers(sender, recipient);

        TransactionRecord record =
                new TransactionRecord(sender, recipient, transaction.getAmount());

        git remote -v
        databaseConduit.saveTransaction(record);
    }
}
