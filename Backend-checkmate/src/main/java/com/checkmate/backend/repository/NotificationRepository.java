package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Kisi specific user ki notifications reverse order (nayi wali pehle) mein lane
    // ke liye
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Sirf wo notifications jo user ne abhi tak nahi dekhi hain
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Unread count check karne ke liye (Bell icon par red dot ke liye kaam aayega)
    long countByUserIdAndIsReadFalse(Long userId);
}
