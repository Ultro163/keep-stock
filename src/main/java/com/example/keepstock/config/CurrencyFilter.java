package com.example.keepstock.config;

import com.example.keepstock.common.CurrencyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {

    private final CurrencyProvider currencyProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/products")) {
            String currencyHeader = request.getHeader("currency");
            if (currencyHeader != null) {
                currencyProvider.setCurrency(currencyHeader);
            }
        }

        filterChain.doFilter(request, response);
    }
}
