package com.checkmate.backend.service;

import com.checkmate.backend.dto.AccessLogEntryDto;
import com.checkmate.backend.entity.AccessLog;
import com.checkmate.backend.repository.AccessLogRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ActivityLogService {

    public static final String TYPE_TASK_UPDATED = "TASK_UPDATED";

    private final JdbcTemplate jdbcTemplate;
    private final AccessLogRepository accessLogRepository;

    public ActivityLogService(JdbcTemplate jdbcTemplate, AccessLogRepository accessLogRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.accessLogRepository = accessLogRepository;
    }

    @Transactional
    public void appendTaskUpdated(String userEmail, String resourceSummary, String clientIp) {
        if (userEmail == null || userEmail.isBlank()) {
            return;
        }
        AccessLog row = new AccessLog();
        row.setOccurredAt(LocalDateTime.now());
        row.setUserEmail(userEmail.trim());
        row.setActivityType(TYPE_TASK_UPDATED);
        row.setResourceSummary(resourceSummary);
        row.setIpAddress(clientIp != null && !clientIp.isBlank() ? clientIp : null);
        accessLogRepository.save(row);
    }

    public List<AccessLogEntryDto> fetchCombinedRecent(int limit) {
        if (limit < 1) {
            limit = 100;
        }
        if (limit > 500) {
            limit = 500;
        }

        String sql = """
                SELECT * FROM (
                    SELECT 'LOGIN'::text AS activity_code,
                           COALESCE(u.name, s.user_email) AS display_name,
                           s.logged_in_at AS occurred_at,
                           CAST(NULL AS text) AS resource_label,
                           s.ip_address AS ip_address
                    FROM user_auth_sessions s
                    LEFT JOIN app_users u ON LOWER(TRIM(u.email)) = LOWER(TRIM(s.user_email))
                    UNION ALL
                    SELECT 'LOGOUT', COALESCE(u.name, s.user_email), s.logged_out_at,
                           CAST(NULL AS text), s.ip_address
                    FROM user_auth_sessions s
                    LEFT JOIN app_users u ON LOWER(TRIM(u.email)) = LOWER(TRIM(s.user_email))
                    WHERE s.logged_out_at IS NOT NULL
                    UNION ALL
                    SELECT 'CHECKLIST_CREATED', COALESCE(u.name, c.created_by), c.created_at,
                           c.checklist_name, c.created_ip
                    FROM checklists c
                    LEFT JOIN app_users u ON LOWER(TRIM(u.email)) = LOWER(TRIM(c.created_by))
                    WHERE c.created_at IS NOT NULL
                    UNION ALL
                    SELECT 'TASK_COMPLETED', COALESCE(u.name, t.completed_by), t.completed_at,
                           COALESCE(t.title, '') || ' — ' || COALESCE(c.checklist_name, ''),
                           CAST(NULL AS varchar(45))
                    FROM tasks t
                    JOIN sections sec ON sec.id = t.section_id
                    JOIN checklists c ON c.id = sec.checklist_id
                    LEFT JOIN app_users u ON LOWER(TRIM(u.email)) = LOWER(TRIM(t.completed_by))
                    WHERE t.completed_at IS NOT NULL AND t.completed = true
                    UNION ALL
                    SELECT al.activity_type, COALESCE(u.name, al.user_email), al.occurred_at,
                           al.resource_summary, al.ip_address
                    FROM access_logs al
                    LEFT JOIN app_users u ON LOWER(TRIM(u.email)) = LOWER(TRIM(al.user_email))
                ) AS events
                WHERE events.occurred_at IS NOT NULL
                ORDER BY events.occurred_at DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AccessLogEntryDto dto = new AccessLogEntryDto();
            dto.setUsername(rs.getString("display_name"));
            dto.setActivityType(toDisplayActivity(rs.getString("activity_code")));
            String detail = rs.getString("resource_label");
            dto.setResourceDetail(detail != null && !detail.isBlank() ? detail : "—");
            Timestamp ts = rs.getTimestamp("occurred_at");
            if (ts != null) {
                dto.setOccurredAt(ts.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString());
            }
            String ip = rs.getString("ip_address");
            dto.setIpAddress(ip != null && !ip.isBlank() ? ip : "—");
            return dto;
        }, limit);
    }

    private static String toDisplayActivity(String code) {
        if (code == null) {
            return "—";
        }
        return switch (code) {
            case "LOGIN" -> "Login";
            case "LOGOUT" -> "Logout";
            case "CHECKLIST_CREATED" -> "Checklist created";
            case "TASK_COMPLETED" -> "Task Completed";
            case "TASK_UPDATED" -> "Task Updated";
            default -> code;
        };
    }
}
