package com.example.mimimimetr.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с сессиями.
 */
@Service
public class SecurityService {
    /**
     * Получает идентификатор текущей сессии пользователя.
     * @param request HTTP-запрос, из которого извлекается сессия.
     * @return Идентификатор текущей сессии пользователя.
     */
    public String getCurrentSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getId();
    }
}
